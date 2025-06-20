package org.powerimo.sqb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SearchSource {
    String table() default "";
    String alias() default "";
    String resource() default "";
    String selectFromSql() default "";

}
