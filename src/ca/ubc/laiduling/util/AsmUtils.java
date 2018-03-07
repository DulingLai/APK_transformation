package ca.ubc.laiduling.util;

import android.util.Log;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;


public class AsmUtils {

    /*
    class related utils
     */
    public static void addClassStaticField(ClassVisitor cv, String fieldName, Class<?> typeClass){
        cv.visitField(Opcodes.ACC_PUBLIC| Opcodes.ACC_STATIC, fieldName, Type.getDescriptor(typeClass), null, null);
    }

    public static void setClassStaticFieldValue(MethodVisitor mv, String className, String fieldName, String typeSign) {
        mv.visitFieldInsn(179, className, fieldName, typeSign);
    }

    public static void getClassStaticFieldValue(MethodVisitor mv, String className, String fieldName, String typeSign) {
        mv.visitFieldInsn(178, className, fieldName, typeSign);
    }

    // add a system print out with custom message
    public static void addSystemPrintOut(MethodVisitor mv, String msg){
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System","out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn(msg);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    // add call to print stack trace
    public static void callPrintStackTrace(MethodVisitor mv, String className){
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, className, "printStackTrace", "()V", false);
    }

    // add start service to the original app
    public static void addStartService(MethodVisitor mv){
        mv.visitTypeInsn(NEW, "android/content/Intent");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(Type.getType("Lca/ubc/laiduling/util/dulingActivityAwareLocation;"));
        mv.visitMethodInsn(INVOKESPECIAL, "android/content/Intent", "<init>", "(Landroid/content/Context;Ljava/lang/Class;)V", false);
        mv.visitVarInsn(ASTORE, 2);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, "ca/ubc/laiduling/util/testBytecode", "startService", "(Landroid/content/Intent;)Landroid/content/ComponentName;", false);
        mv.visitInsn(POP);
        mv.visitInsn(RETURN);
    }

    public static void addLogLocationRequest(MethodVisitor mv){
        mv.visitLdcInsn("duling");
        mv.visitLdcInsn("location requested!");
        mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(Opcodes.POP);
    }

    // add print stack trace method
    public static void addPrintStackTrace(ClassVisitor cw){
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "printStackTrace", "()V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(24, l0);
        mv.visitTypeInsn(NEW, "java/lang/Throwable");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Throwable", "<init>", "()V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
        mv.visitVarInsn(ASTORE, 0);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLineNumber(25, l1);
        mv.visitVarInsn(ALOAD, 0);
        Label l2 = new Label();
        mv.visitJumpInsn(IFNULL, l2);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLineNumber(26, l3);
        mv.visitLdcInsn("Duling");
        mv.visitLdcInsn("++++++++++++++++ Start ++++++++++++++");
        mv.visitMethodInsn(INVOKESTATIC, "ca/ubc/laiduling/util/Utils", "sysPrint", "(Ljava/lang/String;Ljava/lang/String;)V", false);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLineNumber(27, l4);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 1);
        Label l5 = new Label();
        mv.visitLabel(l5);
        mv.visitFrame(Opcodes.F_APPEND, 2, new Object[]{"[Ljava/lang/StackTraceElement;", Opcodes.INTEGER}, 0, null);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ARRAYLENGTH);
        Label l6 = new Label();
        mv.visitJumpInsn(IF_ICMPGE, l6);
        Label l7 = new Label();
        mv.visitLabel(l7);
        mv.visitLineNumber(28, l7);
        mv.visitLdcInsn("Duling");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(AALOAD);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKESTATIC, "ca/ubc/laiduling/util/Utils", "sysPrint", "(Ljava/lang/String;Ljava/lang/String;)V", false);
        Label l8 = new Label();
        mv.visitLabel(l8);
        mv.visitLineNumber(27, l8);
        mv.visitIincInsn(1, 1);
        mv.visitJumpInsn(GOTO, l5);
        mv.visitLabel(l6);
        mv.visitLineNumber(30, l6);
        mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        mv.visitLdcInsn("Duling");
        mv.visitLdcInsn("++++++++++++++++ End ++++++++++++++");
        mv.visitMethodInsn(INVOKESTATIC, "ca/ubc/laiduling/util/Utils", "sysPrint", "(Ljava/lang/String;Ljava/lang/String;)V", false);
        mv.visitLabel(l2);
        mv.visitLineNumber(32, l2);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(RETURN);
        Label l9 = new Label();
        mv.visitLabel(l9);
        mv.visitLocalVariable("i", "I", null, l5, l6, 1);
        mv.visitLocalVariable("stackElement", "[Ljava/lang/StackTraceElement;", null, l1, l9, 0);
        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }
}
