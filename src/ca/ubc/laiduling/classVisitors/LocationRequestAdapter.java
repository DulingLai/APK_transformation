package ca.ubc.laiduling.classVisitors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import ca.ubc.laiduling.util.AsmUtils;
import org.objectweb.asm.Type;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationRequestAdapter extends ClassVisitor {

    protected String currentClassName;
    protected String superClassName;

    public LocationRequestAdapter(final ClassVisitor cv) {
        super(Opcodes.ASM6, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.currentClassName = name;
        this.superClassName = superName;
        cv.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

        // we are not dealing with ABSTRACT and NATIVE methods
        return new locationMethodAdapter(mv, currentClassName, access, name, desc);
    }

    // define a custom method visitor
    class locationMethodAdapter extends MethodVisitor {

        protected String className;
        protected String methodName;
        protected Set<String> locationClasses = new HashSet<>();
        protected int access;
        protected boolean isGPS;

        protected locationMethodAdapter(final MethodVisitor mv, final String className, final int access, final String name, final String desc) {
            super(Opcodes.ASM6, mv);
            this.className = className;
            this.access = access;
            this.methodName = name;
            this.isGPS = false;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if(owner.equals("android/location/LocationManager") && name.equals("requestLocationUpdates")){
                // Here we check its parameter to see if it is using GPS
                System.out.println(owner + "." + name + desc + " in " + className);
                if(isGPS) {
                    System.out.println("GPS location requested!");
                    isGPS = false;
                    if(!locationClasses.contains(className)) {
                        locationClasses.add(className);
                        AsmUtils.addLogLocationRequest(mv);
                    } else{
                        AsmUtils.addLogLocationRequest(mv);
                    }
                    AsmUtils.addStartService(mv, className);
                } else {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                }
            }else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }

        @Override
        public void visitLdcInsn(Object cst) {
            super.visitLdcInsn(cst);
            if((cst instanceof String) && cst.toString().equals("gps")){
                isGPS = true;
            }
        }

        // let ASM compute Maxs for us, appear not working properly
//        @Override
//        public void visitEnd() {
//            mv.visitMaxs(0,0);
//            mv.visitEnd();
//        }
    }
}
