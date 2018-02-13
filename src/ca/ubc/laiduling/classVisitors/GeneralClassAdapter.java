package ca.ubc.laiduling.classVisitors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import ca.ubc.laiduling.util.AsmUtils;

public class GeneralClassAdapter extends ClassVisitor {

    protected String currentClassName;
    protected String superClassName;

    public GeneralClassAdapter(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.currentClassName = name;
        this.superClassName = superName;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

        // we are not dealing with ABSTRACT and NATIVE methods
        if (mv == null || (access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)) > 0) return mv;

        return new locationUpdateAdapter(Opcodes.ASM6, mv, currentClassName, access, name, desc);
    }

    // define a custom method visitor
    private class locationUpdateAdapter extends MethodVisitor {

        protected String className;

        protected locationUpdateAdapter(final int api, final MethodVisitor mv, final String className, final int access, final String name, final String desc) {
            super(api, mv);
            this.className = className;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if(owner.equals("android/location/LocationManager") && name.equals("requestLocationUpdates")){
                System.out.println(owner + "." + name + desc + " in " + className);
                AsmUtils.addPrintStackTrace(cv);
                AsmUtils.callPrintStackTrace(mv, className);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }
}
