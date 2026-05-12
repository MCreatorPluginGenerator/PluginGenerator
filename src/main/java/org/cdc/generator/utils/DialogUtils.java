package org.cdc.generator.utils;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.elementlist.special.CompactModElementListCellRenderer;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.variants.modmaker.ModMaker;
import net.mcreator.util.DesktopUtils;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.utils.factories.RSyntaxTextAreaFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DialogUtils {
    public static int showOptionPaneWithTextArea(RSyntaxTextArea jTextArea, Component parent, String title,
            Collection<?> lines) {
        RTextScrollPane jScrollPane = RSyntaxTextAreaFactory.createDefaultTextScrollPane(jTextArea, parent);
        jScrollPane.setBorder(BorderFactory.createTitledBorder("Lines"));
        if (!lines.isEmpty()) {
            jTextArea.setText(lines.stream().map(Object::toString).collect(Collectors.joining("\n")));
        }
        return JOptionPane.showConfirmDialog(parent, jScrollPane, title, JOptionPane.YES_NO_OPTION);
    }

    public static int showOptionPaneWithTextAreaAndToolBar(RSyntaxTextArea jTextArea, JToolBar toolbar,
            Component parent, String title, Collection<?> collections) {
        RTextScrollPane jScrollPane = RSyntaxTextAreaFactory.createDefaultTextScrollPane(jTextArea, parent);
        jScrollPane.setBorder(BorderFactory.createTitledBorder("Lines"));
        if (!collections.isEmpty()) {
            jTextArea.setText(collections.stream().map(Object::toString).collect(Collectors.joining("\n")));
        }
        return JOptionPane.showConfirmDialog(parent, PanelUtils.northAndCenterElement(toolbar, jScrollPane), title,
                JOptionPane.YES_NO_OPTION);
    }

    public static void showErrorElementDialog(MCreator mcreator, List<ModElement> problematicMods){
        JList<ModElement> problematicModsList = new JList<>(problematicMods.toArray(new ModElement[0]));
        problematicModsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        problematicModsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        problematicModsList.setFixedCellHeight(40);
        problematicModsList.setFixedCellWidth(200);
        problematicModsList.setVisibleRowCount(-1);
        problematicModsList.setCellRenderer(new CompactModElementListCellRenderer());

        JScrollPane sp = new JScrollPane(problematicModsList);
        sp.setPreferredSize(new Dimension(150, 140));
        sp.setBackground(Theme.current().getSecondAltBackgroundColor());
        problematicModsList.setBackground(Theme.current().getSecondAltBackgroundColor());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add("North", L10N.label("dialog.code_error.compilation_list"));
        wrapper.add("Center", sp);

        Object[] options = { L10N.t("dialog.code_error.show_in_workspace"), L10N.t("dialog.code_error.show_build_log"),
                L10N.t("gradle.errors.do_nothing"), L10N.t("action.support") };
        int n = JOptionPane.showOptionDialog(mcreator, wrapper, L10N.t("dialog.code_error.title"),
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        if (n == 0) {
            mcreator.getTabs().showTab(mcreator.workspaceTab);
            if (mcreator instanceof ModMaker modMaker)
                modMaker.getWorkspacePanel().setSearchTerm("f:err");
        } else if (n == 1) {
            mcreator.showConsole();
        } else if (n == 3) {
            DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/support");
        }
    }
}
