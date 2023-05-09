package org.powerimo.sqb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Condition {
    private String field;
    private ConditionType type;
    private String customConditionText;
    private Object value;
    private int sqlType;
    private String sqlTypeName;
    private Class<? extends ConditionResolver> resolverClass;
}
