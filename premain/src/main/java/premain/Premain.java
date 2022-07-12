package premain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @author baeldung
 */
public class Premain {

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        instrumentation.addTransformer(new PongClassFileTransformer());
    }

    public static class PongClassFileTransformer implements ClassFileTransformer {

        public static final String CLASS_NAME = "common/FinalUnderTestImpl";

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            try {
                if (className != null && className.equals(CLASS_NAME)) {
                    ClassPool classPool = ClassPool.getDefault();
                    CtClass ctClass = classPool.get(CLASS_NAME.replace("/", "."));
                    CtMethod ping = ctClass.getDeclaredMethod("ping");
                    ping.setBody("{return \"pong\";}");
                    return ctClass.toBytecode();
                }
            } catch (Throwable throwable) {
                return classfileBuffer;
            }
            return classfileBuffer;
        }
    }
}
