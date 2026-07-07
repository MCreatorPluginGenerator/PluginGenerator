package org.cdc.generator.utils;

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.generator.Generator;
import net.mcreator.generator.template.base.BaseDataModelProvider;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.util.ColorUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableType;
import org.cdc.generator.PluginMain;
import org.cdc.generator.elements.interfaces.IBlocklyCategoryElement;
import org.cdc.generator.init.ModElementTypes;
import org.cdc.generator.ui.elements.IQuickCreateImplModElement;
import org.cdc.generator.ui.elements.ISearchable;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.autocomplete.TemplateCompletion;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class Utils {

    public static boolean isNotPluginGenerator(Generator generator) {
        return !generator.getGeneratorConfiguration().getRaw().containsKey("is_plugin_maker");
    }

    public static List<String> getAllSupportedGenerators() {
        // to do more stable.
        return Generator.GENERATOR_CACHE.entrySet().stream().sorted(new JavaGeneratorFirstComparator())
                .map(Map.Entry::getKey).toList();
    }

    public static List<String> getAllVariableScope() {
        return Arrays.stream(VariableType.Scope.values()).map(a -> a.name().toLowerCase(Locale.ROOT)).toList()
                .reversed();
    }

    public static String[] getAllBuiltinColors() {
        return new String[] { Constants.NONE, Constants.BuiltInColors.BKY_TEXTS_HUE,
                Constants.BuiltInColors.BKY_LOGIC_HUE, Constants.BuiltInColors.BKY_MATH_HUE };
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
        if (lastSearchResult.size() != 1) {
            lastSearchResult.add(-1);
        }

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
        searchbar.registerKeyboardAction(a -> downSearch.doClick(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
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

    public static String nullToNoneOrNoneToNull(String none,boolean editingMode) {
        if (editingMode) {
            if (none == null){
                return Constants.NONE;
            }
            if (none.isBlank()) {
                return Constants.NONE;
            }
        } else {
            if (Constants.NONE.equals(none)){
                return null;
            }
        }
        return none;
    }

    public static File tryToFindCorePlugin() {
        var optional = PluginLoader.INSTANCE.getPlugins().stream().filter(a -> a.getID().equals("core")).findFirst();
        return optional.map(Plugin::getFile).orElse(null);
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
     *
     * @param provider  provider
     * @param generator generator of current mcreator. Do not need the specific generator.
     */
    public static void initCompletionWithGenerator(DefaultCompletionProvider provider, Generator generator) {
        new BaseDataModelProvider(generator).provide().forEach((key, value1) -> {
            provider.addCompletion(new BasicCompletion(provider, key, value1.getClass().getName()));
            for (Method method : value1.getClass().getMethods()) {
                if (method.getReturnType() != Void.TYPE) {
                    provider.addCompletion(
                            new ShorthandCompletion(provider, key + ".", key + "." + method.getName() + "(",
                                    method.getName()));
                }
            }
        });

        Stream.of("aiconditions", "boundingboxes", "mcelements", "mcitems", "procedures", "triggers").forEach(a -> provider.addCompletion(new ShorthandCompletion(provider, "in" + a, "<#include \"" + a + ".ftl\">")));

        provider.addCompletion(
                new TemplateCompletion(provider, "include", "include", "<#include \"${include}\">${cursor}"));
        provider.addCompletion(
                new TemplateCompletion(provider, "if", "if-template", "<#if ${condition}>${cursor}</#if>"));
        provider.addCompletion(new TemplateCompletion(provider, "list", "list-template",
                "<#list ${array} as ${element}>${cursor}</#if>"));
        provider.addCompletion(
                new TemplateCompletion(provider, "assign", "assign-template", "<#assign ${name}=${value}>"));
    }

    public static String convertColor(Color color) {
        if (color == null) {
            return "0";
        }
        return "\"" + ColorUtils.formatColor(color) + "\"";
    }

    public static List<String> getAllDatalistName() {
        return getAllDatalistName(false);
    }

    public static List<String> getAllDatalistName(boolean includeElement) {
        var list = new ArrayList<String>();
        if (includeElement) {
            for (MCreator openMCreator : PluginMain.getINSTANCE().getApplication().getOpenMCreators()) {
                for (ModElement modElement : openMCreator.getWorkspaceInfo()
                        .getElementsOfType(ModElementTypes.DATA_LIST.getRegistryName())) {
                    list.add(modElement.getRegistryName());
                }
            }
        }
        list.addAll(DataListLoader.getCache().keySet().stream().sorted().toList());
        return list;
    }

    public static <E extends IBlocklyCategoryElement> HashSet<String> getAllCategories(MCreator mcreator,
            BlocklyEditorType blocklyEditorType, Class<E> eClass, boolean appendBuiltin) {
        var getter = CompletableFuture.supplyAsync(() -> {
            var stringArrayList1 = new HashSet<String>();
            BlocklyLoader.INSTANCE.getBlockLoader(blocklyEditorType).getDefinedBlocks().values().forEach(a -> {
                if (a.getToolboxCategory() != null) {
                    stringArrayList1.add(a.getToolboxCategoryRaw());
                }
            });
            return stringArrayList1;
        });
        var stringArrayList = new HashSet<String>();
        for (ModElement element : mcreator.getWorkspace().getModElements()) {
            if (eClass.isInstance(element.getGeneratableElement())) {
                stringArrayList.add(element.getRegistryName());
            }
        }
        if (appendBuiltin) {
            stringArrayList.addAll(BlocklyLoader.getBuiltinCategories());
        }
        try {
            stringArrayList.addAll(getter.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return stringArrayList;
    }

    public static JComponent registerCreateImplShortCut(IQuickCreateImplModElement iQuickCreateImplModElement,
            JComponent panel) {
        var menus = L10N.menu("menus.simple_create_impl");
        for (String allSupportedGenerator : getAllSupportedGenerators()) {
            var menu = new JMenuItem(allSupportedGenerator);
            menu.addActionListener(a -> iQuickCreateImplModElement.createImpl(allSupportedGenerator,
                    StringUtils.uppercaseFirstLetter(allSupportedGenerator).replaceAll("[.-]", "")));
            menus.add(menu);
        }
        if (panel.getComponentPopupMenu() == null) {
            var popupMenu = new JPopupMenu();
            popupMenu.add(menus);
            panel.setComponentPopupMenu(popupMenu);
        } else {
            panel.getComponentPopupMenu().add(menus);
        }
        return panel;
    }
}
