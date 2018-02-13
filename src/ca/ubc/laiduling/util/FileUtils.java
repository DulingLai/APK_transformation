package ca.ubc.laiduling.util;

import ca.ubc.laiduling.data.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    public FileUtils() {}

    private static final byte[] DEX_MAGIC = { 100, 101, 120, 10, 48, 51, 53 };

    public static boolean isValidDexFile(String dexFile) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(dexFile));
            byte[] magic = new byte[8];
            fis.read(magic, 0, 8);
            boolean isValid = true;
            for (int i = 0; i < 8; i++) {
                if (DEX_MAGIC[i] == magic[i]) {
                    isValid &= true;
                } else {
                    isValid &= false;
                }
            }
            return isValid;
        }
        catch (Exception localException1) {}finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (Exception localException3) {}
            }
        }
        return false;
    }



    public static boolean fileCopy(String src, String des)
    {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(des);
            int len = 0;
            byte[] buffer = new byte['⠀'];
            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        } catch (Exception e) {
            System.out.println("拷贝文件失败:" + e.toString());
            return false;
        } finally {
            try {
                if (fis != null) fis.close();
                if (fos != null) fos.close();
            } catch (Exception e) {
                System.out.println("拷贝文件失败:" + e.toString());
                return false;
            }
        }
        return true;
    }

    public static void zip(String zipFileName, File inputFile) throws Exception
    {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        BufferedOutputStream bo = new BufferedOutputStream(out);
        zip(out, inputFile, inputFile.getName(), bo);
        bo.close();
        out.close();
    }

    public static void unJar(File src, File desDir) throws FileNotFoundException, IOException
    {
        JarInputStream jarIn = new JarInputStream(new BufferedInputStream(new FileInputStream(src)));
        if (!desDir.exists()) desDir.mkdirs();
        byte[] bytes = new byte['Ѐ'];
        for (;;)
        {
            ZipEntry entry = jarIn.getNextJarEntry();
            if (entry == null) break;
            File desTemp = new File(desDir.getAbsoluteFile() + File.separator + entry.getName());
            if (entry.isDirectory()) {
                if (!desTemp.exists())
                    desTemp.mkdirs();
            } else {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(desTemp));
                int len = jarIn.read(bytes, 0, bytes.length);
                while (len != -1) {
                    out.write(bytes, 0, len);
                    len = jarIn.read(bytes, 0, bytes.length);
                }
                out.flush();
                out.close();
            }
            jarIn.closeEntry();
        }


        java.util.jar.Manifest manifest = jarIn.getManifest();
        if (manifest != null) {
            File manifestFile = new File(desDir.getAbsoluteFile() + File.separator + "META-INF/MANIFEST.MF");
            if (!manifestFile.getParentFile().exists()) manifestFile.getParentFile().mkdirs();
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(manifestFile));
            manifest.write(out);
            out.close();
        }
        jarIn.close();
    }

    private static void zip(ZipOutputStream out, File f, String base, BufferedOutputStream bo) throws Exception {
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            if (fl.length == 0) {
                out.putNextEntry(new ZipEntry(base + "/"));
            }
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + "/" + fl[i].getName(), bo);
            }
        } else {
            out.putNextEntry(new ZipEntry(base));
            System.out.println(base);
            FileInputStream in = new FileInputStream(f);
            BufferedInputStream bi = new BufferedInputStream(in);
            int b;
            while ((b = bi.read()) != -1) {
                bo.write(b);
            }
            bi.close();
            in.close();
        }
    }

    public static void copy(InputStream input, OutputStream output) throws IOException
    {

        byte[] BUFFER = new byte[4194304];
        int bytesRead; while ((bytesRead = input.read(BUFFER)) != -1) {
        output.write(BUFFER, 0, bytesRead);
    }
    }

    public static void addFileToZipFile(String fileName, String zipFileName, String newZipFileName)
    {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len = 0;
            byte[] buffer = new byte['Ѐ'];
            while ((len = fis.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }

            ZipFile war = new ZipFile(zipFileName);
            ZipOutputStream append = new ZipOutputStream(new FileOutputStream(newZipFileName));

            Enumeration<? extends ZipEntry> entries = war.entries();
            while (entries.hasMoreElements()) {
                ZipEntry e = (ZipEntry)entries.nextElement();
                append.putNextEntry(e);
                if (!e.isDirectory()) {
                    copy(war.getInputStream(e), append);
                }
                append.closeEntry();
            }
            ZipEntry e = new ZipEntry(Constants.UTIL_CLASS_NAME.replace(".", "/") + ".class");
            append.putNextEntry(e);
            append.write(bos.toByteArray());
            append.closeEntry();

            war.close();
            append.close();
        }
        catch (Exception localException) {}
    }

    public static void decompressDexFile(String zipPath, String targetPath) throws IOException { File file = new File(zipPath);
        if (!file.isFile()) {
            throw new FileNotFoundException("Dex file does not exist!");
        }
        if ((targetPath == null) || ("".equals(targetPath))) {
            targetPath = file.getParent();
        }

        ZipFile zipFile = new ZipFile(file);
        Enumeration<? extends ZipEntry> files = zipFile.entries();
        ZipEntry entry = null;
        File outFile = null;
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;
        while (files.hasMoreElements()) {
            entry = (ZipEntry)files.nextElement();
            outFile = new File(targetPath + File.separator + entry.getName());

            if (entry.isDirectory()) {
                outFile.mkdirs();
            }
            else if ((entry.getName().endsWith(".dex")) || (entry.getName().startsWith("META-INF")))
            {
                if (!outFile.getParentFile().exists()) {
                    outFile.getParentFile().mkdirs();
                }
                outFile.createNewFile();

                if (outFile.canWrite())
                {
                    try
                    {
                        bin = new BufferedInputStream(zipFile.getInputStream(entry));
                        bout = new BufferedOutputStream(new FileOutputStream(outFile));
                        byte[] buffer = new byte['Ѐ'];
                        int readCount = -1;
                        while ((readCount = bin.read(buffer)) != -1) {
                            bout.write(buffer, 0, readCount);
                        }
                    } finally {
                        try {
                            bin.close();
                            bout.flush();
                            bout.close();
                        }
                        catch (Exception localException) {}
                    }
                }
            }
        }
    }


    public static boolean deleteFile(String fileName)
    {
        File file = new File(fileName);

        if ((file.exists()) && (file.isFile())) {
            if (file.delete()) {
                return true;
            }
            return false;
        }

        return false;
    }


    public static boolean deleteDirectory(String dir)
    {
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);

        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;

        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            return false;
        }

        if (dirFile.delete()) {
            return true;
        }
        return false;
    }
}
