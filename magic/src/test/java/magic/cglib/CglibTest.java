package magic.cglib;

import common.UnderTestImpl;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.MethodInterceptor;
import org.testng.annotations.Test;

import javax.naming.OperationNotSupportedException;

import static common.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// http://mydailyjava.blogspot.com/2013/11/cglib-missing-manual.html
public class CglibTest {


    @Test
    public void test_enhancer_fixed_value() {
        UnderTestImpl realObject = new UnderTestImpl();
        UnderTestImpl proxyObject = (UnderTestImpl) Enhancer.create(
                UnderTestImpl.class,
                // all class methods will be overridden
                (FixedValue) () -> PONG);
        assertThat(realObject.ping()).isEqualTo(PING);
        assertThat(realObject.echo(ECHO)).isEqualTo(ECHO);

        assertThat(proxyObject.ping()).isEqualTo(PONG);
        // those methods are overridden as well :(
        assertThat(proxyObject.echo(ECHO)).isEqualTo(PONG);
        assertThat(proxyObject.toString()).isEqualTo(PONG);
        // try to guess, what will happen here?
//        proxyObject.equals(realObject);
    }

    @Test
    public void test_enhancer_invocation_handler() {
        UnderTestImpl realObject = new UnderTestImpl();
        UnderTestImpl proxyObject = (UnderTestImpl) Enhancer.create(
                UnderTestImpl.class,
                (InvocationHandler) (proxy, method, args) -> {
                    if (method.getDeclaringClass() != Object.class && method.getName().equals("ping")) {
                        return PONG;
                    }
                    if (method.getDeclaringClass() != Object.class && method.getName().equals("echo")) {
                        // let's return PING here
                        return PING;
                    }
                    throw new OperationNotSupportedException(MESSAGE);
                });
        assertThat(realObject.ping()).isEqualTo(PING);
        assertThat(realObject.echo(ECHO)).isEqualTo(ECHO);

        // now we have a bit more control, but it's still not perfect
        assertThat(proxyObject.ping()).isEqualTo(PONG);
        assertThat(proxyObject.echo(ECHO)).isEqualTo(PING);
        // in our proxy we have to override all methods or not to use them at all
        assertThatThrownBy(proxyObject::toString).hasMessageContaining(MESSAGE);
    }

    @Test
    public void test_enhancer_method_interceptor() {
        UnderTestImpl realObject = new UnderTestImpl();
        UnderTestImpl proxyObject = (UnderTestImpl) Enhancer.create(
                UnderTestImpl.class,
                (MethodInterceptor) (object, method, args, methodProxy) -> {
                    if (method.getDeclaringClass() != Object.class && method.getName().equals("ping")) {
                        return PONG;
                    }
                    return methodProxy.invokeSuper(object, args);
                });
        assertThat(realObject.ping()).isEqualTo(PING);
        assertThat(realObject.echo(ECHO)).isEqualTo(ECHO);

        // works like a charm :)
        assertThat(proxyObject.ping()).isEqualTo(PONG);
        assertThat(proxyObject.echo(ECHO)).isEqualTo(ECHO);
        assertThat(proxyObject.toString()).isEqualTo(TO_STRING);
    }


}
