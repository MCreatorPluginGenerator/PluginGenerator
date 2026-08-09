package org.cdc.generator.utils;

import net.mcreator.Launcher;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import org.cdc.generator.utils.decorators.WorkspaceDecorator;
import org.cdc.generator.utils.factories.RSyntaxTextAreaFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

    public static List<String> showMultipleYesOrNoDialog(Component parentComponent,String title,boolean defaultSelect,String... options){
        JPanel selectionPanel = new JPanel(new GridLayout(0,1));
        HashMap<String,JRadioButton> map = new HashMap<>();
        for (String option : options) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING,10,5));
            panel.add(new JLabel(option));
            ButtonGroup buttonGroup = new ButtonGroup();

            JRadioButton yes = new JRadioButton(UIManager.getString("OptionPane.yesButton.textAndMnemonic"));
            JRadioButton no = new JRadioButton(UIManager.getString("OptionPane.noButton.textAndMnemonic"));
            buttonGroup.add(yes);
            buttonGroup.add(no);
            if (defaultSelect){
                yes.setSelected(true);
            } else {
                no.setSelected(true);
            }
            panel.add(yes);
            panel.add(no);
            map.put(option,yes);
            selectionPanel.add(panel);
        }
        var result = JOptionPane.showConfirmDialog(parentComponent,selectionPanel,title,JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.CANCEL_OPTION){
            return List.of();
        }
        var resultList = new ArrayList<String>();
        map.forEach((problem,value)->{
            if (value.isSelected()){
                resultList.add(problem);
            }
        });
        return resultList;
    }

    public static void initWorkspace(MCreator mcreator, WorkspaceDecorator workspaceDecorator){
        String appendCurrent = L10N.t("dialogs.init.support_current_version", Launcher.version.majorlong + "");
        String injectMCreatorLibraries = L10N.t("dialogs.init.inject_current_mcreator_libraries");
        var list = DialogUtils.showMultipleYesOrNoDialog(mcreator,"Initializing",true,appendCurrent,injectMCreatorLibraries);
        if (list.contains(appendCurrent)){
            workspaceDecorator.addSupportedVersion(Launcher.version.majorlong);
        }
        if (list.contains(injectMCreatorLibraries)){
            workspaceDecorator.injectMCreatorLibraries(new File(""));
        }
    }
}
