package org.powerimo.sqb;

import java.util.List;

public interface SearchParamsProvider {
     List<Condition> getConditions();
     TableSource getPrimaryTable();
     void setPrimaryTable(TableSource source);
     void primaryTable(String table, String alias);

     String getSelectFields();
     void setSelectFields(String fields);

     String getOrderBy();
     void setOrderBy(String orderBy);

     Integer getLimit();
     void setLimit(Integer value);
}
