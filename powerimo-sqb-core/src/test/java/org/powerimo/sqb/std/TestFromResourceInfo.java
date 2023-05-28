package org.powerimo.sqb.std;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestFromResourceInfo {

    @Test
    void testCreation() throws IOException {
        FromResourceInfo provider = new FromResourceInfo("search1.sql");
        Assertions.assertEquals("select * from table1 t1 join table2 t2 on t1.id=t2.parent_id", provider.getSelectFromText());
    }
}
