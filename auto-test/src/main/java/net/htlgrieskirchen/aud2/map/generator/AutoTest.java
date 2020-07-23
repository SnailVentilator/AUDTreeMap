package net.htlgrieskirchen.aud2.map.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface AutoTest {
    Class<?> testInterface();
    Class<?> shouldImplementation();
    Class<?> testImplementation();
    Class<?>[] genericArguments();
}
