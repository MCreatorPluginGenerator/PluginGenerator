package org.cdc.generator.ui;

import com.google.common.io.Files;
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
import net.mcreator.util.image.ImageUtils;
import org.cdc.generator.elements.DataListModElement;
import org.cdc.generator.ui.elements.DataListModElementGUI;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResourcePanelIcons extends AbstractResourcePanel<File> {

    private File dataListIcon;
    private final List<File> files;

    public ResourcePanelIcons(WorkspacePanel workspacePanel, DataListModElementGUI dataListModElementGUI) {
        super(workspacePanel, new ResourceFilterModel<>(workspacePanel, File::getName), new Render(),
                JList.HORIZONTAL_WRAP);

        this.files = new ArrayList<>();

        addToolBarButton("workspace.textures.import", UIRES.get("16px.open"), event -> {
            var files = FileDialogs.getFileChooserDialog(workspacePanel.getMCreator(), FileChooserType.OPEN, false,
                    "*.png", new ExtensionFilter("PNG", "png"));
            if (files.length > 0) {
                var file = files[0];
                var name = file.getName();
                FileIO.copyFile(file, new File(dataListIcon, name.replace(Files.getNameWithoutExtension(name),
                        Files.getNameWithoutExtension(name).toUpperCase())));
                reloadElements();
            }
        });
        addToolBarButton("common.delete_selected", UIRES.get("16px.delete"), e -> {
            deleteCurrentlySelected();
            reloadElements();
        });
        addToolBarButton("workspace.datalisticons.as_entry", UIRES.get("16px.textures"), e -> {
            List<File> elements = elementList.getSelectedValuesList();
            for (File element : elements) {
                var fileName = Files.getNameWithoutExtension(element.getName());
                dataListModElementGUI.entries.add(
                        new DataListModElement.DataListEntry.Builder(fileName).setTexture(fileName).build());
            }

        });

        this.dataListIcon = new File(workspacePanel.getMCreator().getGenerator().getModAssetsRoot(), "datalists/icons");
    }

    @Override protected void deleteCurrentlySelected() {
        List<File> elements = elementList.getSelectedValuesList();
        elements.forEach(File::delete);
    }

    @Override public void reloadElements() {
        files.clear();
        List<File> selected = elementList.getSelectedValuesList();

        filterModel.removeAllElements();
        File[] mod_images = dataListIcon.listFiles();

        if (mod_images != null) {
            files.addAll(List.of(mod_images));
            filterModel.addAll(List.of(mod_images));
        }

        ListUtil.setSelectedValues(elementList, selected);
    }

    public List<File> getAllElements() {
        return Collections.unmodifiableList(files);
    }

    static class Render extends JLabel implements ListCellRenderer<File> {

        @Override
        public JLabel getListCellRendererComponent(JList<? extends File> list, File ma, int index, boolean isSelected,
                boolean cellHasFocus) {
            setOpaque(isSelected);
            setBackground(isSelected ? Theme.current().getAltBackgroundColor() : Theme.current().getBackgroundColor());
            setText(ma.getName());
            ComponentUtils.deriveFont(this, 11);
            setForeground(Theme.current().getForegroundColor());
            setVerticalTextPosition(BOTTOM);
            setHorizontalTextPosition(CENTER);
            setHorizontalAlignment(CENTER);

            setIcon(new ImageIcon(ImageUtils.resize(new ImageIcon(ma.getAbsolutePath()).getImage(), 145, 82)));

            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            return this;
        }

    }
}
