package org.powerimo.sqb;

import lombok.Getter;
import lombok.Setter;
import org.powerimo.sqb.FromInfo;
import org.powerimo.sqb.SearchParamsProvider;
import org.powerimo.sqb.annotations.SearchSource;
import org.powerimo.sqb.std.FromResourceInfo;
import org.powerimo.sqb.std.FromStringInfo;
import org.powerimo.sqb.std.StdSearchSourceExtractor;

public class SearchAnnotationProcessor {

    public static ExtractedInfo extractInfo(Object searchParams) {
        ExtractedInfo extractedInfo = new ExtractedInfo();
        extractedInfo.sourceSearchParams = searchParams;

        if (searchParams.getClass().isAnnotationPresent(SearchSource.class)) {
            var annotation = (searchParams.getClass().getAnnotation(SearchSource.class));
            buildFromInfo(annotation, extractedInfo);
            buildSearchProvider(annotation, extractedInfo);
        }
        return extractedInfo;
    }

    public static void buildFromInfo(SearchSource annotation, ExtractedInfo extractedInfo) {
        extractedInfo.fromInfo = null;
        if (annotation.resource() != null) {
            extractedInfo.fromInfo = new FromResourceInfo(annotation.resource());
        } else if (annotation.selectFromSql() != null) {
            extractedInfo.fromInfo = new FromStringInfo()
                    .selectFrom(annotation.selectFromSql());
        } else if (annotation.table() != null) {
            var aliasPart = annotation.alias() != null ? " " + annotation.alias() : "";
            var fromPart = annotation.table() + aliasPart;
            extractedInfo.fromInfo = new FromStringInfo()
                    .select("select *")
                    .from(fromPart);
        }
    }

    public static void buildSearchProvider(SearchSource annotation, ExtractedInfo extractedInfo) {
        extractedInfo.searchParamsProvider = new StdSearchSourceExtractor(extractedInfo.sourceSearchParams);
    }

    @Getter
    @Setter
    public static class ExtractedInfo {
        private Object sourceSearchParams;
        private FromInfo fromInfo;
        private SearchParamsProvider searchParamsProvider;
    }
}
