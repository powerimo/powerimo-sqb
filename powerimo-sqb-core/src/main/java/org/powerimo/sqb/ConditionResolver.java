package org.powerimo.sqb;

public interface ConditionResolver {

    void handleCondition(Condition condition, QueryDetails details);
}
