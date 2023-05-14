package org.powerimo.sqb;

public interface SimpleQueryBuilder {
    String prepareText(SearchParamsProvider searchParamsProvider);
    void prepare(SearchParamsProvider searchParamsProvider);
    void prepare(SearchParamsProvider searchParamsProvider, FromInfo fromInfo);
    String getQueryText();
    void config(ConditionResolverConfig config);
    SimpleQueryBuilder searchParams(SearchParamsProvider provider);
    SimpleQueryBuilder fromProvider(FromInfo provider);

}
