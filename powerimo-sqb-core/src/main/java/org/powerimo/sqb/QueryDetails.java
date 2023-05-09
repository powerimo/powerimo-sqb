package org.powerimo.sqb;

import java.util.List;

public interface QueryDetails {
    String getSelectPart();
    void setSelectPart(String value);
    String getFromPart();
    void setFromPart(String value);
    String getWherePart();
    void setWherePart(String value);
    String getOrderPart();
    void setOrderPart(String value);
    String getLimitPart();
    void setLimitPart(String value);
    List<QueryDetailParam> getQueryParams();

    void addWhere(String value);
    int getParamCounter();
}
