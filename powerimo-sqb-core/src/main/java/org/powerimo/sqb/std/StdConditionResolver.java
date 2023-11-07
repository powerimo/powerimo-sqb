package org.powerimo.sqb.std;

import org.powerimo.sqb.*;

import java.util.List;

public class StdConditionResolver implements ConditionResolver {
    private Condition condition;
    public QueryDetails details;

    @Override
    public void handleCondition(Condition condition, QueryDetails details) {
        this.condition = condition;
        this.details = details;

        switch (condition.getType()) {
            case EQUAL:
                conditionEqual();
                break;
            case NOT_EQUAL:
                conditionNotEqual();
                break;
            case CUSTOM:
                conditionCustom();
                break;
            case IS_NULL:
                conditionIsNull();
                break;
            case IS_NOT_NULL:
                conditionIsNotNull();
                break;
            case LESS:
                conditionLess();
                break;
            case LESS_OR_EQUAL:
                conditionLessOrEqual();
                break;
            case GREATER:
                conditionGreater();
                break;
            case GREATER_OR_EQUAL:
                conditionGreaterOrEqual();
                break;
            case IN_LIST:
                conditionInList();
                break;
            case IN_SQL:
                conditionInSql();
                break;
            case LIKE:
                conditionLike();
                break;
            default:
                throw new SqbException("Not supported condition type: " + condition.getType());
        }
    }

    protected String getNextParamName() {
        int n = details.getParamCounter();
        return "p" + n;
    }

    protected String inlineParamName(String name) {
        return ":" + name;
    }

    protected void conditionEqual() {
        var name = getNextParamName();
        details.addWhere(condition.getField() + "=" + inlineParamName(name));
        details.getQueryParams().add(QueryDetailParam.of(name, condition));
    }

    protected void conditionNotEqual() {
        var name = getNextParamName();
        details.addWhere(condition.getField() + "<>" + inlineParamName(name));
        details.getQueryParams().add(QueryDetailParam.of(name, condition));
    }

    protected void conditionCustom() {
        details.addWhere(condition.getField() + condition.getCustomConditionText());
    }

    protected void conditionIsNull() {
        details.addWhere(condition.getField() + " is null");
    }

    protected void conditionIsNotNull() {
        details.addWhere(condition.getField() + " is not null");
    }

    protected void conditionGreater() {
        var name = getNextParamName();
        details.addWhere(condition.getField() + ">" + inlineParamName(name));
        details.getQueryParams().add(QueryDetailParam.of(name, condition));
    }

    protected void conditionGreaterOrEqual() {
        var name = getNextParamName();
        details.addWhere(condition.getField() + ">=" + inlineParamName(name));
        details.getQueryParams().add(QueryDetailParam.of(name, condition));
    }

    protected void conditionLess() {
        var name = getNextParamName();
        details.addWhere(condition.getField() + "<" + inlineParamName(name));
        details.getQueryParams().add(QueryDetailParam.of(name, condition));
    }

    protected void conditionLessOrEqual() {
        var name = getNextParamName();
        details.addWhere(condition.getField() + "<=" + inlineParamName(name));
        details.getQueryParams().add(QueryDetailParam.of(name, condition));
    }

    protected void conditionLike() {
        var name = getNextParamName();
        details.addWhere(condition.getField() + " like " + inlineParamName(name));
        details.getQueryParams().add(QueryDetailParam.of(name, condition));
    }

    protected void conditionInSql() {
        details.addWhere(condition.getField() + " in " + condition.getCustomConditionText());
    }

    protected void conditionInList() {
        if (condition.getValue() == null) {
            details.addWhere(condition.getField() + " in (select null)");
            return;
        }

        if (condition.getValue() instanceof List) {
            List<?> list = (List<?>) condition.getValue();
            boolean isListOfObjects = true;
            for (Object element : list) {
                if (!(element instanceof Object)) {
                    isListOfObjects = false;
                    break;
                }
            }
            if (!isListOfObjects)
                throw new SqbException("Condition type is IN_LIST but the value is not a List of Object. Field=" + condition.getField());

            StringBuilder sbTmp = new StringBuilder(condition.getField() + " in (");
            var first = true;
            for (Object element : list) {
                if (element instanceof Object) {

                    var name = getNextParamName();

                    if (!first) {
                        sbTmp.append(",").append(inlineParamName(name));
                    } else {
                        sbTmp.append(inlineParamName(name));
                        first = false;
                    }

                    var param = QueryDetailParam.builder()
                            .name(name)
                            .value(element)
                            .sqlType(condition.getSqlType())
                            .typeName(condition.getSqlTypeName())
                            .build();
                    details.getQueryParams().add(param);
                }
            }
            sbTmp.append(")");
            details.addWhere(sbTmp.toString());
        } else
            throw new SqbException("Condition type is IN_LIST but the value is not a List. Field=" + condition.getField());
    }

}
