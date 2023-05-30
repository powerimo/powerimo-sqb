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
                }
                parameterSource.addValue(item.getName(), item.getValue());
            }
        }
        return parameterSource;
    }

    public static void mapInstant(@NonNull MapSqlParameterSource params, @NonNull QueryDetailParam param) {
        if (param.getValue() == null) {
            params.addValue(param.getName(), null);
            return;
        }
        Instant instantValue = (Instant) param.getValue();
        Timestamp t = Timestamp.from(instantValue);
        params.addValue(param.getName(), t);
    }

}
