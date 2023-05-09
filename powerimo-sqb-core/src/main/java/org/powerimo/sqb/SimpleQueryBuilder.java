package org.powerimo.sqb;

public interface SimpleQueryBuilder {
    String prepareText(SearchParamsProvider searchParamsProvider);
    void prepare(SearchParamsProvider searchParamsProvider);
    String getQueryText();
    void config(ConditionResolverConfig config);
}
