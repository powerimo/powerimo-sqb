package org.powerimo.sqb.starter;

import lombok.NonNull;
import org.powerimo.sqb.FromInfo;
import org.powerimo.sqb.QueryDetailParam;
import org.powerimo.sqb.SearchAnnotationProcessor;
import org.powerimo.sqb.SearchParamsProvider;
import org.powerimo.sqb.std.StdSearchSourceExtractor;
import org.powerimo.sqb.std.StdSimpleQueryBuilder;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;

public class Sqb {
    private final StdSimpleQueryBuilder queryBuilder;
    private final Object params;
    private SearchParamsProvider searchParamsProvider;
    private FromInfo fromInfo;

    public Sqb(Object params, FromInfo fromProvider) {
        this.queryBuilder = new StdSimpleQueryBuilder();
        this.params = params;
        this.fromInfo = fromProvider;
        initSearchProvider();
        prepare();
    }

    public Sqb(Object params) {
        this.queryBuilder = new StdSimpleQueryBuilder();
        this.params = params;
        initSearchProvider();
        prepare();
    }

    protected void initSearchProvider() {
        if (params instanceof SearchParamsProvider) {
            searchParamsProvider = (SearchParamsProvider) params;
        } else {
            searchParamsProvider = new StdSearchSourceExtractor(params);
        }
    }

    protected void prepare() {
        var info = SearchAnnotationProcessor.extractInfo(params);
        if (fromInfo != null)
            info.setFromInfo(fromInfo);
        queryBuilder.prepare(info.getFromInfo(), info.getSearchParamsProvider());
    }

    public String getQuery() {
        return queryBuilder.getQueryText();
    }

    public MapSqlParameterSource getNamedParams() {
        return SqbTool.createNamedParams(queryBuilder);
    }

    public Query createJpaQuery(@NonNull EntityManager entityManager, @NonNull Class<?> tClass) {
        var query = entityManager.createNativeQuery(this.getQuery(), tClass);
        for (var item : getDetailParams()) {
            query.setParameter(item.getName(), item.getValue());
        }
        return query;
    }

    public <T> List<T> executeJpaQuery(@NonNull EntityManager entityManager, @NonNull Class<T> tClass) {
        var query = createJpaQuery(entityManager, tClass);
        return (List<T>) query.getResultList();
    }

    public List<QueryDetailParam> getDetailParams() {
        return queryBuilder.getQueryParams();
    }

}
