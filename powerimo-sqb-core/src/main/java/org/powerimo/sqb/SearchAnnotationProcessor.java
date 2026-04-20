package org.powerimo.sqb;

import org.powerimo.sqb.annotations.SearchSource;
import org.powerimo.sqb.std.FromResourceInfo;
import org.powerimo.sqb.std.FromStringInfo;
import org.powerimo.sqb.std.StdSearchSourceExtractor;

public class SearchAnnotationProcessor {

    public static ProvidersConfig extractInfo(Object searchParams) {
        ProvidersConfig extractedInfo = new ProvidersConfig();
        extractedInfo.sourceSearchParams = searchParams;

        if (searchParams.getClass().isAnnotationPresent(SearchSource.class)) {
            var annotation = (searchParams.getClass().getAnnotation(SearchSource.class));
            buildFromInfo(annotation, extractedInfo);
            buildSearchProvider(annotation, extractedInfo);
        }

        if (extractedInfo.getSearchParamsProvider() == null) {
            if (searchParams instanceof SearchParamsProvider) {
                extractedInfo.setSearchParamsProvider((SearchParamsProvider) searchParams);
            }
        }

        return extractedInfo;
    }

    public static void buildFromInfo(SearchSource annotation, ProvidersConfig extractedInfo) {
        extractedInfo.fromInfo = null;
        if (annotation.resource() != null && !annotation.resource().isEmpty()) {
            extractedInfo.fromInfo = new FromResourceInfo(annotation.resource());
        } else if (annotation.selectFromSql() != null && !annotation.selectFromSql().isEmpty()) {
            extractedInfo.fromInfo = new FromStringInfo()
                    .selectFrom(annotation.selectFromSql());
        } else if (annotation.table() != null && !annotation.table().isEmpty()) {
            var aliasPart = annotation.alias() != null && !annotation.alias().isEmpty() ? " " + annotation.alias() : "";
            var fromPart = annotation.table() + aliasPart;
            extractedInfo.fromInfo = new FromStringInfo()
                    .select("select *")
                    .from(fromPart);
        }
    }

    public static void buildSearchProvider(SearchSource annotation, ProvidersConfig extractedInfo) {
        extractedInfo.searchParamsProvider = new StdSearchSourceExtractor(extractedInfo.sourceSearchParams);
    }

}
