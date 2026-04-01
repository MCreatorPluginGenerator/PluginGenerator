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
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class ResourcePanelModTypes extends AbstractResourcePanel<File> {

    private File mod_types;

    public ResourcePanelModTypes(WorkspacePanel workspacePanel) {
        super(workspacePanel, new ResourceFilterModel<>(workspacePanel
                , File::getName), new Render(), JList.HORIZONTAL_WRAP);

        addToolBarButton("workspace.textures.import",
                UIRES.get("16px.open"), event -> {
                    var files = FileDialogs.getFileChooserDialog(workspacePanel.getMCreator(), FileChooserType.OPEN, false, "*.png", new ExtensionFilter("PNG", "png"));
                    if (files.length > 0) {
                        var file = files[0];
                        FileIO.copyFile(file, new File(mod_types, file.getName()));
                        reloadElements();
                    }
                });
        addToolBarButton("common.delete_selected", UIRES.get("16px.delete"), e -> {
            deleteCurrentlySelected();
            reloadElements();
        });

        this.mod_types = new File(workspacePanel.getMCreator().getGenerator().getModAssetsRoot(), "themes/default_dark/images/mod_types");
    }

    @Override
    protected void deleteCurrentlySelected() {
        List<File> elements = elementList.getSelectedValuesList();
        elements.forEach(File::delete);
    }

    @Override
    public void reloadElements() {
        List<File> selected = elementList.getSelectedValuesList();

        filterModel.removeAllElements();
        File[] mod_images = mod_types.listFiles();

        if (mod_images != null)
            filterModel.addAll(List.of(mod_images));

        ListUtil.setSelectedValues(elementList, selected);
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
