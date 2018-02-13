package ca.ubc.laiduling.runner;

import ca.ubc.laiduling.classVisitors.GeneralClassAdapter;
import ca.ubc.laiduling.data.ClassHierarchy;
import ca.ubc.laiduling.data.Constants;
import ca.ubc.laiduling.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;


public class instrumenter {


    public static void main(String[] args) {

        File apkFile = new File(Constants.INPUT_APK_DIR+Constants.APP_NAME+".apk");
        Utils.unzipApk(apkFile, Constants.DECOMPILED_INPUT_DIR);

//        // Collect all class files
//        File inputClassFolder = new File(DECOMPILED_INPUT_DIR + Constants.APP_NAME);
//        Collection<File> classFiles = FileUtils.listFiles(inputClassFolder, new RegexFileFilter(Constants.CLASS_FILE_REGEX), DirectoryFileFilter.DIRECTORY);
//
//        // process each class files
//        try {
//            for(File inputClassFile:classFiles) {
//                // read the class file
//                byte[] classBytes = FileUtils.readFileToByteArray(inputClassFile);
//                final ClassReader classReader = new ClassReader(classBytes);
//
//                // create an instance of class writer
//                final ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
//
//                // create a class visitor
//                ClassVisitor classVisitor = new GeneralClassAdapter(Opcodes.ASM6, classWriter);
//
//                classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);
//
//                // write output
//                byte[] outputClassBytes = classWriter.toByteArray();
//                File instrumentedClassFolder = new File(Constants.INSTRUMENTED_OUTPUT_DIR + Constants.APP_NAME);
//                File instrumentedClassFile = new File(inputClassFile.getPath().replace(inputClassFolder.getPath(), instrumentedClassFolder.getPath()));
//                FileUtils.writeByteArrayToFile(instrumentedClassFile, outputClassBytes);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // create the instrumented apk file
//        Utils.packApk(Constants.APP_NAME);
//        Utils.deployApk(Constants.APP_NAME);
    }
}
