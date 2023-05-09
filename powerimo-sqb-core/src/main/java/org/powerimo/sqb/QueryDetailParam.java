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
}
