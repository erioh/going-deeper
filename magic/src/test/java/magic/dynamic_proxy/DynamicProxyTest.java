package magic.dynamic_proxy;

import common.InterfaceUnderTest;
import org.testng.annotations.Test;

import javax.naming.OperationNotSupportedException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

// https://www.baeldung.com/java-dynamic-proxies
public class DynamicProxyTest {
    @Test
    public void test_proxy() {
        String result = "Hello There Obi-Wan Kenobi.";
        InterfaceUnderTest helloWorld = (InterfaceUnderTest) Proxy.newProxyInstance(
                this.getClass().getClassLoader(), new Class<?>[]{InterfaceUnderTest.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("ping")) {
                        return result;
                    }
                    throw new OperationNotSupportedException();
                });
        assertThat(helloWorld.ping()).isEqualTo(result);
    }

    @Test
    public void useless_proxy() {
        A a = (A) Proxy.newProxyInstance(
                this.getClass().getClassLoader(), new Class<?>[]{A.class}, new TestIH());
        assertThat(a.method()).isEqualTo("A");
        B b = (B) Proxy.newProxyInstance(
                this.getClass().getClassLoader(), new Class<?>[]{B.class}, new TestIH());
        assertThat(a.method()).isEqualTo("A");
        assertThat(b.method()).isEqualTo("B");
    }

    public static class TestIH implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (proxy instanceof A)
                return "A";
            if (proxy instanceof B) {
                return "B";
            }
            throw new OperationNotSupportedException();
        }
    }

    public interface A {
        String method();
    }

    public interface B {
        String method();
    }
}
