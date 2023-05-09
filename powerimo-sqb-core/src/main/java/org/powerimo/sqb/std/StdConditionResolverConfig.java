package org.powerimo.sqb.std;

import lombok.Getter;
import lombok.Setter;
import org.powerimo.sqb.Condition;
import org.powerimo.sqb.ConditionResolver;
import org.powerimo.sqb.ConditionResolverConfig;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class StdConditionResolverConfig implements ConditionResolverConfig {
    private final HashMap<Class, ConditionResolver> resolvers = new HashMap<>();
    private ConditionResolver defaultResolver;

    public StdConditionResolverConfig() {
        this.defaultResolver = new StdConditionResolver();
    }

    public StdConditionResolverConfig(ConditionResolver defaultResolver) {
        this.defaultResolver = defaultResolver;
    }

    @Override
    public Map<Class, ConditionResolver> conditionResolvers() {
        return resolvers;
    }

    @Override
    public void registerConditionResolver(Class cls, ConditionResolver resolver) {
        resolvers.put(cls, resolver);
    }

    @Override
    public void defaultConditionResolver(ConditionResolver resolver) {
        this.defaultResolver = resolver;
    }

    @Override
    public ConditionResolver getResolver(Class cls) {
        var resolver = resolvers.get(cls);
        return resolver != null ? resolver : defaultResolver;
    }

    @Override
    public ConditionResolver getResolver(Condition condition) {
        if (condition.getValue() != null) {
            return getResolver(condition.getValue().getClass());
        }
        return defaultResolver;
    }
}
