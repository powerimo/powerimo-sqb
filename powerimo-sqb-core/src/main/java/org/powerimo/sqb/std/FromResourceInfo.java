package org.powerimo.sqb.std;

import org.powerimo.sqb.FromInfo;
import org.powerimo.sqb.SqbException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FromResourceInfo implements FromInfo {
    private final String resourceText;

    public FromResourceInfo(String resourcePath) {
        InputStream inputStream = FromResourceInfo.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }

        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            resourceText = scanner.useDelimiter("\\A").next();
        }
    }

    @Override
    public String getSelectText() {
        throw new SqbException("Method does not supported");
    }

    @Override
    public String getFromText() {
        throw new SqbException("Method does not supported");
    }

    @Override
    public String getSelectFromText() {
        return resourceText;
    }
}
