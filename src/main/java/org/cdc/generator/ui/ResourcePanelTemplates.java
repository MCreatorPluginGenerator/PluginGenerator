package org.cdc.generator.ui;

import net.mcreator.io.FileIO;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.ListUtil;
import net.mcreator.ui.dialogs.file.ExtensionFilter;
import net.mcreator.ui.dialogs.file.FileChooserType;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.ui.workspace.resources.AbstractResourcePanel;
import net.mcreator.ui.workspace.resources.ResourceFilterModel;
import org.apache.logging.log4j.core.util.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class ResourcePanelTemplates extends AbstractResourcePanel<File> {

    private File templatesFile;

    public ResourcePanelTemplates(WorkspacePanel workspacePanel) {
        super(workspacePanel, new ResourceFilterModel<>(workspacePanel, File::getName), new Render(),
                JList.HORIZONTAL_WRAP);

        addToolBarButton("workspace.textures.import", UIRES.get("16px.open"), event -> {
            var files = FileDialogs.getFileChooserDialog(workspacePanel.getMCreator(), FileChooserType.OPEN, false, "",
                    new ExtensionFilter("Templates", "png", "ptpl", "aitpl", "cmdtpl", "ftpl", "json"));
            if (files.length > 0) {
                var file = files[0];
                switch (FileUtils.getFileExtension(file)) {
                case "aitpl", "cmdtpl", "ftpl", "ptpl" -> FileIO.copyFile(file,
                        new File(templatesFile, FileUtils.getFileExtension(file) + "/" + file.getName()));
                case "png" -> FileIO.copyFile(file, new File(templatesFile, "textures/" + file.getName()));
                case "json" -> FileIO.copyFile(file, new File(templatesFile, "animations/" + file.getName()));
                }
                reloadElements();
            }
        });
        addToolBarButton("common.delete_selected", UIRES.get("16px.delete"), e -> {
            deleteCurrentlySelected();
            reloadElements();
        });

        this.templatesFile = new File(workspacePanel.getMCreator().getGenerator().getModAssetsRoot(), "templates");
    }

    @Override protected void deleteCurrentlySelected() {
        List<File> elements = elementList.getSelectedValuesList();
        elements.forEach(File::delete);
    }

    @Override public void reloadElements() {
        List<File> selected = elementList.getSelectedValuesList();

        filterModel.removeAllElements();
        Stream<Path> templates;
        if (templatesFile.isDirectory()) {
            try {
                templates = Files.walk(templatesFile.toPath(), 2);
                filterModel.addAll(templates.map(Path::toFile).filter(File::isFile).toList());

                ListUtil.setSelectedValues(elementList, selected);
                templates.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class Render extends JLabel implements ListCellRenderer<File> {

        @Override
        public JLabel getListCellRendererComponent(JList<? extends File> list, File ma, int index, boolean isSelected,
                boolean cellHasFocus) {
            setOpaque(isSelected);
            setBackground(isSelected ? Theme.current().getAltBackgroundColor() : Theme.current().getBackgroundColor());
            setText(ma.getPath());
            ComponentUtils.deriveFont(this, 11);
            setForeground(Theme.current().getForegroundColor());
            setVerticalTextPosition(BOTTOM);
            setHorizontalTextPosition(CENTER);
            setHorizontalAlignment(CENTER);

            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            return this;
        }

    }
}
