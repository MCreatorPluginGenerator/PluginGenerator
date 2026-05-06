package org.cdc.generator.ui.preferences;

import net.mcreator.preferences.PreferencesSection;
import net.mcreator.preferences.entries.BooleanEntry;
import net.mcreator.preferences.entries.StringEntry;

public class PluginMakerPreference extends PreferencesSection {

    public static PluginMakerPreference INSTANCE;

    public StringEntry preferGenerator;
    public BooleanEntry searchIgnoreCase;
    public StringEntry defaultProcedureTooltip;

    public PluginMakerPreference(String preferencesIdentifier) {
        super(preferencesIdentifier);
        final String identifier = "plugin_generator";

        this.preferGenerator = addPluginEntry(identifier, new StringEntry("prefer_generator", "quilt-1.7.10", true));
        this.defaultProcedureTooltip = addPluginEntry(identifier,
                new StringEntry("default_procedure_tooltip", "Practice makes perfect", true));
        this.searchIgnoreCase = addPluginEntry(identifier, new BooleanEntry("search_ignore_case", true));
    }

    @Override public String getSectionKey() {
        return "plugin_maker";
    }
}
