package org.powerimo.sqb.std;

import org.junit.jupiter.api.Test;
import org.powerimo.sqb.Condition;
import org.powerimo.sqb.ConditionType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestStdConditionResolver {
    private final StdConditionResolver resolver = new StdConditionResolver();

    private StdSimpleQueryBuilder createSqb() {
        var sqb = new StdSimpleQueryBuilder();
        sqb.setFromPart("from table1");
        return sqb;
    }

    @Test
    void testResolverEqual() {
        var sqb = createSqb();
        var condition = Condition.builder()
                .type(ConditionType.EQUAL)
                .value("aaa")
                .field("f1")
                .build();
        resolver.handleCondition(condition, sqb);

        assertNotNull(sqb.getQueryParams());
        assertEquals(1, sqb.getQueryParams().size());
        assertEquals("where f1=:p1", sqb.getWherePart());
    }

    @Test
    void testResolverNotEqual() {
        var sqb = createSqb();
        var condition = Condition.builder()
                .type(ConditionType.NOT_EQUAL)
                .value("aaa")
                .field("f1")
                .build();
        resolver.handleCondition(condition, sqb);

        assertNotNull(sqb.getQueryParams());
        assertEquals(1, sqb.getQueryParams().size());
        assertEquals("where f1<>:p1", sqb.getWherePart());
    }

    @Test
    void testResolverIsNull() {
        var sqb = createSqb();
        var condition = Condition.builder()
                .type(ConditionType.IS_NULL)
                .value("aaa")
                .field("f1")
                .build();
        resolver.handleCondition(condition, sqb);

        assertNotNull(sqb.getQueryParams());
        assertEquals(0, sqb.getQueryParams().size());
        assertEquals("where f1 is null", sqb.getWherePart());
    }

    @Test
    void testResolverIsNotNull() {
        var sqb = createSqb();
        var condition = Condition.builder()
                .type(ConditionType.IS_NOT_NULL)
                .value("aaa")
                .field("f1")
                .build();
        resolver.handleCondition(condition, sqb);

        assertNotNull(sqb.getQueryParams());
        assertEquals(0, sqb.getQueryParams().size());
        assertEquals("where f1 is not null", sqb.getWherePart());
    }

    @Test
    void testResolverInSql() {
        var sqb = createSqb();
        var condition = Condition.builder()
                .type(ConditionType.IN_SQL)
                .field("f1")
                .customConditionText("(select id from table2)")
                .build();
        resolver.handleCondition(condition, sqb);

        assertNotNull(sqb.getQueryParams());
        assertEquals(0, sqb.getQueryParams().size());
        assertEquals("where f1 in (select id from table2)", sqb.getWherePart());
    }

    @Test
    void testResolverInList() {
        var sqb = createSqb();
        List<String> list = new ArrayList<>();
        list.add("s1");
        list.add("s2");
        list.add("s3");
        var condition = Condition.builder()
                .type(ConditionType.IN_LIST)
                .field("f1")
                .value(list)
                .build();
        resolver.handleCondition(condition, sqb);

        assertNotNull(sqb.getQueryParams());
        assertEquals(3, sqb.getQueryParams().size());
        assertEquals("where f1 in (:p1,:p2,:p3)", sqb.getWherePart());
    }

    @Test
    void testResolverGreater() {
        var sqb = createSqb();
        var condition = Condition.builder()
                .type(ConditionType.GREATER)
                .value("1")
                .field("f1")
                .build();
        resolver.handleCondition(condition, sqb);

        assertNotNull(sqb.getQueryParams());
        assertEquals(1, sqb.getQueryParams().size());
        assertEquals("where f1>:p1", sqb.getWherePart());
    }

    @Test
    void testResolverGreaterOrEqual() {
        var sqb = createSqb();
        var condition = Condition.builder()
                .type(ConditionType.GREATER_OR_EQUAL)
                .value("1")
                .field("f1")
                .build();
        resolver.handleCondition(condition, sqb);

        assertNotNull(sqb.getQueryParams());
        assertEquals(1, sqb.getQueryParams().size());
        assertEquals("where f1>=:p1", sqb.getWherePart());
    }

    @Test
    void testResolverLess() {
        var sqb = createSqb();
        var condition = Condition.builder()
                .type(ConditionType.LESS)
                .value("1")
                .field("f1")
                .build();
        resolver.handleCondition(condition, sqb);

        assertNotNull(sqb.getQueryParams());
        assertEquals(1, sqb.getQueryParams().size());
        assertEquals("where f1<:p1", sqb.getWherePart());
    }

    @Test
    void testResolverLessOrEqual() {
        var sqb = createSqb();
        var condition = Condition.builder()
                .type(ConditionType.LESS_OR_EQUAL)
                .value("1")
                .field("f1")
                .build();
        resolver.handleCondition(condition, sqb);

        assertNotNull(sqb.getQueryParams());
        assertEquals(1, sqb.getQueryParams().size());
        assertEquals("where f1<=:p1", sqb.getWherePart());
    }

    @Test
    void testResolverCustom() {
        var sqb = createSqb();
        var condition = Condition.builder()
                .type(ConditionType.CUSTOM)
                .value("1")
                .field("f1")
                .customConditionText("_CUSTOM")
                .build();
        resolver.handleCondition(condition, sqb);

        assertNotNull(sqb.getQueryParams());
        assertEquals(0, sqb.getQueryParams().size());
        assertEquals("where f1_CUSTOM", sqb.getWherePart());
    }
}
