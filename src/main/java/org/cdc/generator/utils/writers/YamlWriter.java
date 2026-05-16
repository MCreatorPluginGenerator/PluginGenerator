package org.cdc.generator.utils.writers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public enum YamlWriter implements IWriter {
    INSTANCE;

    @Override public String formatString(String str) {
        BufferedReader reader = new BufferedReader(new StringReader(str));

        StringBuilder result = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    if (!result.isEmpty()){
                        result.append(System.lineSeparator());
                    }
                    result.append(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result.toString();
    }
}
