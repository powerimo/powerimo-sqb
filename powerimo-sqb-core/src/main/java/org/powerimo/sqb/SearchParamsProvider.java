package org.powerimo.sqb;

import java.util.List;

public interface SearchParamsProvider {
     List<Condition> getConditions();
     String getOrderBy();
     Integer getLimit();
     Integer getLimitOffset();
}
