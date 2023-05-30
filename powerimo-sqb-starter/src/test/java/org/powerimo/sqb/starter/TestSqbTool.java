package org.powerimo.sqb.starter;

import org.junit.jupiter.api.Test;
import org.powerimo.sqb.QueryDetailParam;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestSqbTool {
    @Test
    void mapInstantTest() {
        Instant instant = Instant.now();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        QueryDetailParam param = QueryDetailParam.builder()
                .name("name1")
                .value(instant)
                .build();
        SqbTool.mapInstant(parameterSource, param);
        assertNotNull(parameterSource);
        var mapped = parameterSource.getValue("name1");
        assertNotNull(mapped);

    }

}
