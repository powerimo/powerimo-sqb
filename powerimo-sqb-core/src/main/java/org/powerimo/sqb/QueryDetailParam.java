package org.powerimo.sqb;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryDetailParam {
    private String name;
    private Object value;
    private int sqlType;
    private String typeName;

    public static QueryDetailParam of(String paramName, Condition condition) {
        return QueryDetailParam.builder()
                .name(paramName)
                .value(condition.getValue())
                .sqlType(condition.getSqlType())
                .typeName(condition.getSqlTypeName())
                .build();
    }
}
