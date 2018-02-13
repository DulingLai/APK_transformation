package ca.ubc.laiduling.util;

import ca.ubc.laiduling.data.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import com.googlecode.dex2jar.tools.Dex2jarCmd;

import static org.apache.commons.io.IOUtils.copy;

public class Utils {

    public static ArrayList<String> allDexList = new ArrayList();
    public static ArrayList<String> errorDexList = new ArrayList();

    // decompile the APK
    public static boolean unzipApk(File srcApkFile, String targetDir){
        try{
            long time = System.currentTimeMillis();
            System.out.println("Step 1. =====> Decompile APK: " + srcApkFile.getAbsolutePath());
            FileUtils.decompressDexFile(srcApkFile.getAbsolutePath(), targetDir);
            System.out.println("Step 1. Completed: decompile APK =====> time elapsed: " + (System.currentTimeMillis() - time) / 1000L + "s\n\n");
            return true;
        }catch(Throwable e){
            System.out.println("Step 1. Failed: decompile APK failed =====> exist!: " + e.toString());
            return false;
        }
    }

    // decompile dex files
    public static boolean decompileDexFiles(String unZipDir, String outJarDir)
    {
        try
        {
            long time = System.currentTimeMillis();
            System.out.println("Step 2. =====> decompile dex files to class files");
            File apkDir = new File(unZipDir);
            File[] listFile = apkDir.listFiles();
            for (File file : listFile)
                if (file.getAbsolutePath().endsWith(".dex")) {
                    System.out.println("Working on: " + file.getName());

                    boolean isValid = FileUtils.isValidDexFile(file.getAbsolutePath());
                    if (!isValid) {
                        System.out.println("Invalid dex file, skipped!");
                    }
                    else {
                        long time1 = System.currentTimeMillis();
                        allDexList.add(file.getName());
                        Dex2jarCmd.dexPath = file.getParentFile().getAbsolutePath();
                        Dex2jarCmd.dexFileName = file.getName();
                        Dex2jarCmd.outJarPath = outJarDir;
                        Dex2jarCmd.outJarFileName = dexName2JarName(file.getName());
                        try {
                            Dex2jarCmd.main(new String[0]);
                        } catch (Throwable e) {
                            System.out.println(file.getAbsolutePath() + ",处理失败！");
                            errorDexList.add(file.getName());
                        }
                        System.out.println("处理完" + file.getName() + "===耗时:" + (System.currentTimeMillis() - time1) / 1000L + "s\n");
                    }
                }
            System.out.println("添加日志信息结束===耗时:" + (System.currentTimeMillis() - time) / 1000L + "s\n\n");
            return true;
        } catch (Throwable e) {
            System.out.println("添加日志代码到dex文件中失败，退出！:" + e.toString()); }
        return false;
    }

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


    public static void decompileApk(String apkName){
        runScript(Constants.SCRIPTS_DIR + "asmPrepare " + apkName);
    }

    public static void packApk(String apkName){
        runScript(Constants.SCRIPTS_DIR + "asmPack " + Constants.APP_NAME);
    }

    public static void deployApk(String apkName){
        runScript(Constants.SCRIPTS_DIR + "deploy " + Constants.APP_NAME);
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
        }
        catch (IOException e) {
            System.out.println("Exception happened:");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
