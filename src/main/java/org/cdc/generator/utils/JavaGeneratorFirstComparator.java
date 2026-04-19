package org.cdc.generator.utils;

import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;

import java.util.Comparator;
import java.util.Map;

public class JavaGeneratorFirstComparator implements Comparator<Map.Entry<String, GeneratorConfiguration>> {
    @Override
    public int compare(Map.Entry<String, GeneratorConfiguration> o1, Map.Entry<String, GeneratorConfiguration> o2) {
        int first = getComparateNumber(o1.getValue());
        int second = getComparateNumber(o2.getValue());
        return second - first;
    }

    private int getComparateNumber(GeneratorConfiguration generatorConfiguration){
        return generatorConfiguration.getGeneratorFlavor().getBaseLanguage() == GeneratorFlavor.BaseLanguage.JAVA?1:0;
    }
}
