package org.powerimo.sqb;

import org.junit.jupiter.api.Test;
import org.powerimo.sqb.annotations.SearchParam;
import org.powerimo.sqb.annotations.SearchSource;

import static org.junit.jupiter.api.Assertions.*;

public class TestSearchAnnotationProcessor {

    @SearchSource(resource = "test_sap.sql")
    public static class TestParams {
        @SearchParam(fieldName = "t1.search_field1")
        private String searchField1 = "aaa";
        @SearchParam(fieldName = "t2.search_field2")
        private Integer searchField2;
    }

    @Test
    void testExtract() {
        var info = SearchAnnotationProcessor.extractInfo(new TestParams());

        assertNotNull(info);
        assertNotNull(info.getFromInfo());
        assertNotNull(info.getSearchParamsProvider());
        String s = "select *\r\n" +
                "from table1 t1\r\n" +
                "    join table2 t2 on t1.parent_id=t2.id";
        assertEquals(s, info.getFromInfo().getSelectFromText());
        assertEquals(1, info.getSearchParamsProvider().getConditions().size());
    }

}
