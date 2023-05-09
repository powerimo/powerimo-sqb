package org.powerimo.sqb;

import java.util.Map;

public interface ConditionResolverConfig {
    Map<Class, ConditionResolver> conditionResolvers();
    void registerConditionResolver(Class cls, ConditionResolver resolver);
    void defaultConditionResolver(ConditionResolver resolver);
    ConditionResolver getResolver(Class cls);
    ConditionResolver getResolver(Condition condition);
}
