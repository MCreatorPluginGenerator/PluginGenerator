package org.cdc.generator.utils.writers;

import net.mcreator.util.yaml.YamlUtil;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.constructor.ConstructYamlNull;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.resolver.FailsafeScalarResolver;
import org.snakeyaml.engine.v2.resolver.ScalarResolver;
import org.snakeyaml.engine.v2.schema.Schema;

import java.util.HashMap;
import java.util.Map;

public enum YamlWriter implements IWriter {
    INSTANCE;

    @Override public String formatString(String str) {
        LoadSettings loadSettings = YamlUtil.getSimpleLoadSettings();
        Load load = new Load(loadSettings);

        Object parsed = load.loadFromString(str);

        final Map<Tag, ConstructNode> tagConstructors = new HashMap<>();
        tagConstructors.put(Tag.NULL, new ConstructYamlNull());

        final ScalarResolver scalarResolver = new FailsafeScalarResolver();

        final Schema schema = new Schema() {
            @Override public ScalarResolver getScalarResolver() {
                return scalarResolver;
            }

            @Override public Map<Tag, ConstructNode> getSchemaTagConstructors() {
                return tagConstructors;
            }
        };

        DumpSettings dumpSettings = DumpSettings.builder()
                .setIndent(2)
                .setDefaultFlowStyle(FlowStyle.BLOCK)
                .setDefaultScalarStyle(ScalarStyle.PLAIN)
                .setSchema(schema)
                .build();
        Dump dump = new Dump(dumpSettings);

        return dump.dumpToString(parsed);
    }
}
