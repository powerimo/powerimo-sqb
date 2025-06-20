package org.powerimo.sqb.starter;

import lombok.NonNull;
import org.powerimo.sqb.QueryDetailParam;
import org.powerimo.sqb.QueryDetails;
import org.powerimo.sqb.SqbException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.Timestamp;
import java.time.Instant;

public class SqbTool {

    public static MapSqlParameterSource createNamedParams(@NonNull QueryDetails details) {
        if (details.getQueryParams() == null) {
            throw new SqbException("getQueryParams in QueryDetails is null");
        }

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        for (var item: details.getQueryParams()) {
            if (item.getTypeName() != null && !item.getTypeName().isEmpty()) {
                parameterSource.addValue(item.getName(), item.getValue(), item.getSqlType(), item.getTypeName());
            } else if (item.getSqlType() != 0) {
                parameterSource.addValue(item.getName(), item.getValue(), item.getSqlType());
            } else  {
                if (item.getValue() instanceof Instant) {
                    mapInstant(parameterSource, item);
                } else if (item.getValue() instanceof Enum) {
                    mapEnumValue(parameterSource, item);
                } else {
                    parameterSource.addValue(item.getName(), item.getValue());
                }
            }
        }
        return parameterSource;
    }

    public static void mapInstant(@NonNull MapSqlParameterSource parameterSource, @NonNull QueryDetailParam param) {
        if (param.getValue() == null) {
            parameterSource.addValue(param.getName(), null);
            return;
        }
        Instant instantValue = (Instant) param.getValue();
        Timestamp t = Timestamp.from(instantValue);
        parameterSource.addValue(param.getName(), t);
    }

    public static void mapEnumValue(@NonNull MapSqlParameterSource parameterSource, @NonNull QueryDetailParam param) {
        if (param.getValue() == null) {
            parameterSource.addValue(param.getName(), null);
            return;
        }
        String s = param.getValue().toString();
        parameterSource.addValue(param.getName(), s);
    }

}
