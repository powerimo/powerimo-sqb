package org.powerimo.sqb;

import java.util.List;

public interface QueryDetails {
    void setFromPart(String value);
    String getWherePart();
    void setLimitPart(String value);
    List<QueryDetailParam> getQueryParams();
    void addWhere(String value);
    int getParamCounter();
}
