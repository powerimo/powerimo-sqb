package org.powerimo.sqb;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProvidersConfig {
    protected Object sourceSearchParams;
    protected FromInfo fromInfo;
    protected SearchParamsProvider searchParamsProvider;
}
