package org.powerimo.sqb;

import org.junit.jupiter.api.Test;
import org.powerimo.sqb.annotations.SearchParam;
import org.powerimo.sqb.annotations.SearchSource;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

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
        var is = this.getClass().getClassLoader().getResourceAsStream("test_sap.sql");
        String resourceText;
        try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8)) {
            resourceText = scanner.useDelimiter("\\A").next();
        }
        assertEquals(resourceText, info.getFromInfo().getSelectFromText());
        assertEquals(1, info.getSearchParamsProvider().getConditions().size());
    }

}
