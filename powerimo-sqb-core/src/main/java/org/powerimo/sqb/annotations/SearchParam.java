package org.powerimo.sqb.annotations;

import org.powerimo.sqb.ConditionResolver;
import org.powerimo.sqb.ConditionType;
import org.powerimo.sqb.std.StdConditionResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SearchParam {
    String fieldName() default "";
    boolean ignoreNulls() default true;
    int sqlType() default 0;
    String sqlTypeName() default "";
    ConditionType conditionType() default ConditionType.EQUAL;
    Class<? extends ConditionResolver> resolver() default StdConditionResolver.class;
    String customConditionText() default "";
}