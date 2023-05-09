package org.powerimo.sqb;

import lombok.Data;

@Data
public class TableSource {
    private String table;
    private String alias;
    private String joinText;
}
