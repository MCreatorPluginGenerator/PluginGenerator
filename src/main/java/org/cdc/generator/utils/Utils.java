package org.cdc.generator.utils;

import net.mcreator.generator.Generator;
import net.mcreator.generator.template.base.BaseDataModelProvider;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.util.ColorUtils;
import net.mcreator.workspace.elements.VariableType;
import org.cdc.generator.ui.elements.ISearchable;
import org.cdc.generator.utils.interfaces.ITypeProvider;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.*;
import java.util.List;

public class Utils {

    public static boolean isNotPluginGenerator(Generator generator) {
        return !generator.getGeneratorConfiguration().getRaw().containsKey("is_plugin_maker");
    }

    public static Set<String> getAllSupportedGenerators() {
        return Generator.GENERATOR_CACHE.keySet();
    }

    public static Set<String> getAllSupportedVariableTypes() {
        // remove duplicated strs.
        var set = new HashSet<String>();
        ITypeProvider.serviceLoader.stream().forEach(a -> {
            set.addAll(a.get().provide());
        });
        return set;
    }

    public static List<String> getAllVariableScope() {
        return Arrays.stream(VariableType.Scope.values()).map(a -> a.name().toLowerCase(Locale.ROOT)).toList()
                .reversed();
    }

    public static List<String> getMappingResult(String generator, String datalist, String name) {
        var memory = Generator.GENERATOR_CACHE.get(generator).getMappingLoader().getMapping(datalist);
        if (memory != null) {
            if (memory.containsKey(name)) {
                var oe = memory.get(name);
                return convertYamlToList(oe);
            }
        }
        return null;
    }

    public static List<String> convertYamlToList(Object oe) {
        if (oe instanceof String str) {
            return new ArrayList<>(List.of(str));
        } else if (oe instanceof List<?> list) {
            return new ArrayList<>(list.stream().map(Object::toString).toList());
        } else {
            return new ArrayList<>(List.of(Objects.toString(oe)));
        }
    }

    public static JPanel initSearchComponent(ArrayList<Integer> lastSearchResult, ISearchable searchable) {
        VTextField searchbar = new VTextField();
        ComponentUtils.deriveFont(searchbar, 16);
        searchbar.setOpaque(false);
        searchbar.setBorder(BorderFactory.createEmptyBorder());
        searchbar.setToolTipText("You can use \"=\" to filter type like name=name");

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.setOpaque(false);
        searchbar.setCustomDefaultMessage("enter to search");
        searchbar.setValidator(() -> {
            if (lastSearchResult.size() == 1) {
                return new ValidationResult(ValidationResult.Type.ERROR, "No results");
            }
            return ValidationResult.PASSED;
        });
        JCheckBox ignoreCase = new JCheckBox("Ignore case");
        ignoreCase.setSelected(Rules.SearchRules.isIgnoreCase());
        ignoreCase.addActionListener(e -> {
            Rules.SearchRules.setIgnoreCase(ignoreCase.isSelected());
            searchable.doSearch(splitSearch(searchbar.getText()));
        });
        JButton upSearch = new JButton(UIRES.get("18px.up"));
        upSearch.setToolTipText("0/0");
        upSearch.setOpaque(false);
        JButton downSearch = new JButton(UIRES.get("18px.down"));
        downSearch.setToolTipText("0/0");
        downSearch.setOpaque(false);
        buttons.add(ignoreCase);
        buttons.add(upSearch);
        buttons.add(downSearch);
        searchbar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {
                searchable.doSearch(splitSearch(searchbar.getText()));
                searchbar.getValidationStatus();
            }

            @Override public void removeUpdate(DocumentEvent e) {
                searchable.doSearch(splitSearch(searchbar.getText()));
                searchbar.getValidationStatus();
            }

            @Override public void changedUpdate(DocumentEvent e) {
                searchable.doSearch(splitSearch(searchbar.getText()));
                searchbar.getValidationStatus();
            }
        });
        searchbar.registerKeyboardAction(a -> {
            downSearch.doClick();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
        downSearch.addActionListener(a -> {
            var index = lastSearchResult.getFirst() + 1;
            if (index >= lastSearchResult.size()) {
                index = 1;
            }
            if (index >= lastSearchResult.size()) {
                return;
            }
            searchable.showSearch(lastSearchResult.get(index));
            lastSearchResult.set(0, index);
            downSearch.setToolTipText(index + "/" + (lastSearchResult.size() - 1));
        });
        upSearch.addActionListener(a -> {
            var index = lastSearchResult.getFirst() - 1;
            if (index < 1) {
                index = lastSearchResult.size() - 1;
            }
            searchable.showSearch(lastSearchResult.get(index));
            lastSearchResult.set(0, index);
            upSearch.setToolTipText(index + "/" + (lastSearchResult.size() - 1));
        });
        var panel = PanelUtils.centerAndEastElement(searchbar, buttons);
        var dimension = tryToGetTextFieldSize();
        dimension.width *= 2;
        panel.setMaximumSize(dimension);
        panel.setOpaque(true);
        return panel;
    }

    public static Dimension tryToGetTextFieldSize() {
        var dimen = Toolkit.getDefaultToolkit().getScreenSize();
        return new Dimension(dimen.width / 6, dimen.height / 30);
    }

    public static String nullToNoneOrNoneToNull(String none) {
        if (none == null) {
            return "None";
        }
        return none.equals("None") ? null : none;
    }

    public static File tryToFindCorePlugin() {
        return PluginLoader.INSTANCE.getPlugins().stream().filter(a -> a.getID().equals("core")).findFirst().get()
                .getFile();
    }

    public static Map.Entry<String, String> splitSearch(String text) {
        if (text.contains("=")) {
            var sp = text.split("=", 2);
            return sp.length == 2 ?
                    Map.entry(sp[0], Rules.SearchRules.applyIgnoreCaseRule(sp[1])) :
                    Map.entry("", Rules.SearchRules.applyIgnoreCaseRule(sp[0]));
        }
        return Map.entry("", Rules.SearchRules.applyIgnoreCaseRule(text));
    }

    /**
     * add the base data variables
     * @param provider provider
     * @param generator generator of current mcreator. Do not need the specific generator.
     */
    public static void initCompletionWithGenerator(DefaultCompletionProvider provider, Generator generator) {
        new BaseDataModelProvider(generator).provide().forEach((key, value) -> {
            provider.addCompletion(new BasicCompletion(provider, "${" + key, value.getClass().getName()));
        });
    }

    public static String convertColor(Color color) {
        if (color == null) {
            return "0";
        }

        float[] hsbvals = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbvals);

        double hue = Math.ceil(hsbvals[0] * 360);
        double saturation = Math.ceil(hsbvals[1] * 100);
        double brightness = Math.ceil(hsbvals[2] * 100);

        if (saturation == Rules.defaultHsvSaturation && brightness == Rules.defaultHsvValue) {
            return String.valueOf((int) hue);
        }
        return "\"" + ColorUtils.formatColor(color) + "\"";
    }
}
