package magic.javaassist;

import common.FinalUnderTestImpl;
import javassist.*;
import org.testng.annotations.Test;

import static common.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

public class JavaAssistTest {
    @Test
    public void override_final_class() throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException {
        ClassPool classPool = ClassPool.getDefault();
        // class which we are willing to adjust should not be loaded before modification!!!!
        CtClass ctClass = classPool.get("common.FinalUnderTestImpl");
        CtMethod ping = ctClass.getDeclaredMethod("ping");
        ping.setBody("{return \"" + PONG + "\";}");

        FinalUnderTestImpl adjustedFinalClass = (FinalUnderTestImpl) ctClass.toClass().newInstance();
        assertThat(adjustedFinalClass.ping()).isEqualTo(PONG);
        assertThat(adjustedFinalClass.echo(ECHO)).isEqualTo(ECHO);
        assertThat(adjustedFinalClass.toString()).isEqualTo(TO_STRING);
    }

    @Test
    public void some_additional_magic() {
        FinalUnderTestImpl finalUnderTest = new FinalUnderTestImpl();
        assertThat(finalUnderTest.ping()).isEqualTo(PONG);
        System.out.println("Why for the love of God it is '" + finalUnderTest.ping() + "'?");
    }


    @Test
    public void modifySystemClass() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass integerCtClass = pool.get("java.lang.Integer");
        CtField hiddenValueCtField = new CtField(CtClass.intType, "hiddenValue", integerCtClass);
        hiddenValueCtField.setModifiers(Modifier.PUBLIC);
        integerCtClass.addField(hiddenValueCtField);
        integerCtClass.writeFile(".");
        CtClass ccMyApp = pool.get("MyApp");
        CtMethod m = ccMyApp.getDeclaredMethod("main");
        m.insertBefore("{ getHiddenValue(); }");
        ccMyApp.writeFile();
        // cd magic
        // java -Xbootclasspath/p:. MyApp
    }
}
