package ca.ubc.laiduling.util;

import ca.ubc.laiduling.classVisitors.LocationRequestAdapter;
import ca.ubc.laiduling.data.Constants;

import java.io.*;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import static junit.framework.Assert.assertTrue;


public class Utils {

    private static boolean enableMultidex = false;

    // Step 1. decompile the APK
    public static boolean decompileApkFile(String apkName) {
        try {
            long time = System.currentTimeMillis();
            System.out.println("Step 1. =====> Decompile APK: " + Constants.APP_NAME);
            decompileApk(apkName);
            System.out.println("Step 1. Completed: decompile APK =====> time elapsed: " + (System.currentTimeMillis() - time) / 1000L + "s\n\n");
            return true;
        } catch (Throwable e) {
            System.out.println("Step 1. Failed: decompile APK failed =====> exit!: " + e.toString());
            return false;
        }
    }


    // Step 2&3. iterate through all class files
    public static void iterateClassFiles(String apkName) {
        // Step 2. collect all class files
        long time = System.currentTimeMillis();
        System.out.println("Step 2. =====> Collect class files");
        File inputClassFolder = new File(Constants.DECOMPILED_INPUT_DIR + apkName);
        Collection<File> classFiles = FileUtils.listFiles(inputClassFolder, new RegexFileFilter(Constants.CLASS_FILE_REGEX), DirectoryFileFilter.DIRECTORY);
        int totalClassFile = classFiles.size();
        System.out.println("Step 2. Completed: collected " + totalClassFile + " class files =====> time elapsed: " + (System.currentTimeMillis() - time) / 1000L + "s\n\n");

        // Step 3. iterate through all class files
        try {
            long start = System.currentTimeMillis();
            System.out.println("Step 3. =====> iterate through class files");
            ProgressBar bar = new ProgressBar();        // here we use a progress bar to indicate our progress
            int counter = 0;
            for (File inputClassFile : classFiles) {
                bar.update(counter, totalClassFile);
                // read the class file
                byte[] classBytes = FileUtils.readFileToByteArray(inputClassFile);
                final ClassReader classReader = new ClassReader(classBytes);

                // create an instance of class writer
                final ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);

                // create a class visitor
                ClassVisitor classVisitor = new LocationRequestAdapter(classWriter);

                classReader.accept(classVisitor,ClassReader.SKIP_DEBUG);

                byte[] outputClassBytes = classWriter.toByteArray();

                // verify the instrumented class file
//                checkGeneratedClass(outputClassBytes);
                final ClassReader verifyCR = new ClassReader(outputClassBytes);
//                StringWriter sw = new StringWriter();
//                PrintWriter pw = new PrintWriter(sw);
//                CheckClassAdapter.verify(verifyCR, false, pw);
//                if(sw.toString().length()!=0){
//                    System.err.println("Verify Bytecode Error!");
//                    System.err.println(sw);
//                    throw new IllegalStateException("Bytecode Verification Failed!");
//                }
                verifyCR.accept(new CheckClassAdapter(new ClassWriter(0)),0);

                // Write the output class file
                File instrumentedClassFolder = new File(Constants.INSTRUMENTED_OUTPUT_DIR + Constants.APP_NAME);
                File instrumentedClassFile = new File(inputClassFile.getPath().replace(inputClassFolder.getPath(), instrumentedClassFolder.getPath()));
                FileUtils.writeByteArrayToFile(instrumentedClassFile, outputClassBytes);
                counter++;
            }
            System.out.println("Step 3. Completed =====> time elapsed: " + (System.currentTimeMillis() - start) / 1000L + "s\n\n");
        } catch (Throwable e) {
            System.out.println("Step 3. Failed: iterate class files failed =====> exit!: " + e.toString());
            e.printStackTrace();
        }
    }


    // Step 4. pack modified class files to a jar file
    public static boolean packClassFiles(String apkName) {
        try {
            long time = System.currentTimeMillis();
            System.out.println("Step 4. =====> Pack class files to a jar file.");
            FileUtils.copyFileToDirectory(new File(Constants.BASE_DIR+"out/production/APK_transformation/ca/ubc/laiduling/util/dulingActivityAwareLocation.class"), new File(Constants.INSTRUMENTED_OUTPUT_DIR + Constants.APP_NAME + "/ca/ubc/laiduling/util/"));
            packApk(apkName);
            System.out.println("Step 4. Completed: Pack class files =====> time elapsed: " + (System.currentTimeMillis() - time) / 1000L + "s\n\n");
            return true;
        } catch (Throwable e) {
            System.out.println("Step 4. Failed: Pack class files failed =====> exit!: " + e.toString());
            return false;
        }
    }

    // Step 5. convert jar to dex files
    public static boolean jar2Dex(String outJarDir, String rootPath, String apkName) {
        try {
            System.out.println("Step 5. =====> convert jar to dex files");
            long time = System.currentTimeMillis();
            enableMultidex = checkMultiDex(Constants.INPUT_APK_DIR+apkName+".apk");

            File file = new File(outJarDir);
            System.out.println("Working on: " + file.getName());

            if(enableMultidex) {
                String[] cmd = {"--dex", "--multi-dex", "--output=" + rootPath, file.getAbsolutePath()};
                try {
                    System.out.println("Multidex mode enabled!");
                    com.android.dx.command.Main.main(cmd);
                } catch (Throwable e) {
                    System.out.println("Failed to execute dx tool in multidex mode!");
                    System.out.println(e.toString());
                    return false;
                }
            }else{
                String[] cmd = {"--dex", "--output=" + rootPath + File.separator + "classes.dex", file.getAbsolutePath()};
                try {
                    com.android.dx.command.Main.main(cmd);
                } catch (Throwable e) {
                    System.out.println("Failed to execute dx tool in single dex mode!");
                    System.out.println(e.toString());
                    return false;
                }
            }
            System.out.println("File: " + file.getName() + " completed.");
        System.out.println("Step 5. Completed =====> time elapsed:" + (System.currentTimeMillis() - time) / 1000L + "s\n\n");
        return true;
        } catch (Throwable e) {
            System.out.println("Step 5. Failed，exit:" + e.toString());
        }
        return false;
    }

    // Step 6. copy the original apk and remove the signature
    public static boolean removeSignatureFromApk(String apkName) {
        try {
            long time = System.currentTimeMillis();
            System.out.println("Step 6. =====> remove signature info.");
            removeSignature(apkName);
            System.out.println("Step 6. Completed: remove signature info =====> time elapsed: " + (System.currentTimeMillis() - time) / 1000L + "s\n\n");
            return true;
        } catch (Throwable e) {
            System.out.println("Step 6. Failed: remove signature info =====> exit!: " + e.toString());
            return false;
        }
    }

    // Step 7. modify manifest of the original apk
    public static boolean modifyManifest(String apkName) {
        try {
            long time = System.currentTimeMillis();
            System.out.println("Step 7. =====> modify manifest of the original apk.");
            unpackApk(apkName);
            System.out.println("Modify the Manifest file and press Enter to continue");
            try{System.in.read();}
            catch(Exception e){System.out.println("Error while waiting for user input");}
            System.out.println("Step 7. modify manifest of the original apk =====> time elapsed: " + (System.currentTimeMillis() - time) / 1000L + "s\n\n");
            return true;
        } catch (Throwable e) {
            System.out.println("Step 7. Failed: modify manifest of the original apk =====> exit!: " + e.toString());
            return false;
        }
    }

    // Step 8. add dex files to the apk
    public static boolean addDexToApk(String aaptCmdDir, String unZipDir, String srcApkPath, String apkName) {
        try {
            System.out.println("Step 8. =====> add dex file to the source apk");
            long time = System.currentTimeMillis();
            File aaptFile = new File(aaptCmdDir);
            File classDir = new File(unZipDir);
            File[] classListFile = classDir.listFiles();
            for (File file : classListFile) {
                if (file.getName().endsWith(".dex")) {
                    String cmd = aaptFile.getAbsolutePath() + " remove " + srcApkPath;
                    cmd = cmd + " " + file.getName();
                    runScript(cmd);
                }
            }

            addDex(apkName);
            System.out.println("Step 8. Completed ====> time elapsed: " + (System.currentTimeMillis() - time) / 1000L + "s\n\n");
            return true;
        } catch (Throwable e) {
            System.out.println("Step 8. Failed，exit!: " + e.toString());
        }
        return false;
    }

    // Step 9. zipalign and sign the apk!
    public static boolean signApk(String apkName) {
        try {
            System.out.println("Step 9. =====> signing apk file: " + apkName);
            long time = System.currentTimeMillis();
            signApkFile(apkName);
            System.out.println("Step 9. Completed =====> time elapsed: " + (System.currentTimeMillis() - time) / 1000L + "s\n\n");
            return true;
        } catch (Throwable e) {
            System.out.println("Step 9. failed to sign the apk，exit:" + e.toString());
        }
        return false;
    }

//    // decompile dex files
//    public static boolean decompileDexFiles(String unZipDir, String outJarDir)
//    {
//        try
//        {
//            long time = System.currentTimeMillis();
//            System.out.println("Step 2. =====> decompile dex files to class files");
//            File apkDir = new File(unZipDir);
//            File[] listFile = apkDir.listFiles();
//            for (File file : listFile)
//                if (file.getAbsolutePath().endsWith(".dex")) {
//                    System.out.println("Working on: " + file.getName());
//
//                    boolean isValid = FileUtils.isValidDexFile(file.getAbsolutePath());
//                    if (!isValid) {
//                        System.out.println("Invalid dex file, skipped!");
//                    }
//                    else {
//                        long time1 = System.currentTimeMillis();
//                        allDexList.add(file.getName());
//                        Dex2jarCmd.dexPath = file.getParentFile().getAbsolutePath();
//                        Dex2jarCmd.dexFileName = file.getName();
//                        Dex2jarCmd.outJarPath = outJarDir;
//                        Dex2jarCmd.outJarFileName = dexName2JarName(file.getName());
//                        try {
//                            Dex2jarCmd.main(new String[0]);
//                        } catch (Throwable e) {
//                            System.out.println(file.getAbsolutePath() + ",处理失败！");
//                            errorDexList.add(file.getName());
//                        }
//                        System.out.println("处理完" + file.getName() + "===耗时:" + (System.currentTimeMillis() - time1) / 1000L + "s\n");
//                    }
//                }
//            System.out.println("添加日志信息结束===耗时:" + (System.currentTimeMillis() - time) / 1000L + "s\n\n");
//            return true;
//        } catch (Throwable e) {
//            System.out.println("添加日志代码到dex文件中失败，退出！:" + e.toString()); }
//        return false;
//    }

//
//    public static boolean addClassFileToJar(String classFilePath, String jarName, String outJarDir){
//        try{
//            int index = outJarDir.lastIndexOf(File.separator);
//            String path = outJarDir.substring(0, index+1);
//            File oldZipFile = new File(path + jarName);
//            File newZipFile = new File(path + "tmp_" + jarName);
//            // add the class files to the jar file
//            addFileToZipFile(classFilePath, oldZipFile.getAbsolutePath(), newZipFile.getAbsolutePath());
//            oldZipFile.delete();
//            newZipFile.renameTo(oldZipFile);
//            return true;
//        }catch (Throwable e){
//            return false;
//        }
//    }

//    private static void addFileToZipFile(String fileName, String zipFileName, String newZipFileName){
//        try{
//            FileInputStream fis = new FileInputStream(fileName);
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            int len = 0;
//            byte[] buffer = new byte[1024];
//            while((len=fis.read(buffer)) > 0){
//                bos.write(buffer, 0, len);
//            }
//
//            // read war.zip and write to append.zip
//            ZipFile war = new ZipFile(zipFileName);
//            ZipOutputStream append = new ZipOutputStream(new FileOutputStream(newZipFileName));
//            // copy the content from existing war
//            Enumeration<? extends ZipEntry> entries = war.entries();
//            while (entries.hasMoreElements()){
//                ZipEntry e = entries.nextElement();
//                append.putNextEntry(e);
//                if(!e.isDirectory()) copy(war.getInputStream(e), append);
//                append.closeEntry();
//            }
//            ZipEntry e = new ZipEntry(AsmUtils.getClassFileName());
//            append.putNextEntry(e);
//            append.write(bos.toByteArray());
//            append.closeEntry();
//        }catch(Exception e){
//
//        }
//    }


    private static void decompileApk(String apkName) {
        runScript(Constants.SCRIPTS_DIR + "asmPrepare " + apkName);
    }

    private static void packApk(String apkName) {
        runScript(Constants.SCRIPTS_DIR + "asmPack " + apkName);
    }

    public static void deployApk(String apkName) {
        runScript(Constants.SCRIPTS_DIR + "deploy " + apkName);
    }

    private static void removeSignature(String apkName) { runScript(Constants.SCRIPTS_DIR + "removeSignature " + apkName); }

    private static void signApkFile(String apkName) {
        runScript(Constants.SCRIPTS_DIR + "signApk " + apkName);
    }

    private static void unpackApk(String apkName) { runScript(Constants.SCRIPTS_DIR + "unpackApk " + apkName); }

    private static void addDex(String apkName){
        runScript(Constants.SCRIPTS_DIR + "addDex " + apkName);
    }

    private static void runScript(String cmd) {
        String s = null;
        try {
            System.out.println("Running " + cmd);
            Process p = Runtime.getRuntime().exec(cmd);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Standard output:");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Standard error:");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            System.out.println("Exception happened:");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static boolean checkMultiDex(String zipPath) throws IOException {
        File file = new File(zipPath);
        if (!file.isFile()) {
            throw new FileNotFoundException("file not exist!");
        }

        ZipFile zipFile = new ZipFile(file);
        Enumeration<? extends ZipEntry> files = zipFile.entries();
        int count = 0;
        ZipEntry entry = null;
        while (files.hasMoreElements()) {
            entry = (ZipEntry) files.nextElement();
            if (entry.getName().endsWith(".dex")) {
                count++;
            }
        }
        if (count > 1) {
            System.out.println("There are "+count+" dex files ---> enabling multidex support");
            return true;
        } else {
            System.out.println("There are only "+count+" dex file ---> multidex disabled");
            return false;
        }
    }
}

/**
 * Ascii progress meter. On completion this will reset itself,
 * so it can be reused
 * <br /><br />
 * 100% ################################################## |
 */
class ProgressBar {
    private StringBuilder progress;

    /**
     * initialize progress bar properties.
     */
    public ProgressBar() {
        init();
    }

    /**
     * called whenever the progress bar needs to be updated.
     * that is whenever progress was made.
     *
     * @param done an int representing the work done so far
     * @param total an int representing the total work
     */
    public void update(int done, int total) {
        char[] workchars = {'|', '/', '-', '\\'};
        String format = "\r%3d%% %s %c";

        int percent = (++done * 100) / total;
        int extrachars = (percent / 2) - this.progress.length();

        while (extrachars-- > 0) {
            progress.append('#');
        }

        System.out.printf(format, percent, progress,
                workchars[done % workchars.length]);

        if (done == total) {
            System.out.flush();
            System.out.println();
            init();
        }
    }

    private void init() {
        this.progress = new StringBuilder(60);
    }
}
