package org.cdc.generator.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.tree.FilterTreeNode;
import net.mcreator.ui.component.tree.FilteredTreeModel;
import net.mcreator.ui.component.tree.JFileTree;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.util.TreeUtils;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ReferencesFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/6/10
 */
public class InformationDock extends JPanel {

    private static final Logger LOG = LogManager.getLogger(InformationDock.class);

    private final FilteredTreeModel mods = new FilteredTreeModel(null);

    FilterTreeNode references = null;

    public final JFileTree tree = new JFileTree(mods);

    private JToolBar toolBar = new JToolBar();

    private MCreator mcreator;

    private final HashMap<String, ArrayList<String>> duplicatedElements = new HashMap<>();
    private final ArrayList<ModElement> notGenerate = new ArrayList<>();
    private boolean mcreatorPluginsWorkspace;

    public InformationDock(MCreator mcreator) {
        setLayout(new BorderLayout(0, 0));

        this.mcreator = mcreator;

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(a -> reloadTree());
        toolBar.add(refresh);

        tree.addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased(MouseEvent e) {
                var selected = tree.getSelectionPath();
                if (selected != null && e.getClickCount() == 2) {
                    var selectedComp = selected.getLastPathComponent();
                    if (selectedComp instanceof ModElementReferenceNode) {
                        var element = mcreator.getWorkspace().getModElementByName(selectedComp.toString());
                        if (element != null) {
                            element.getType().getModElementGUI(mcreator, element, true).showView();
                        }
                    }
                    if (selectedComp instanceof GradleTaskNode gradleTaskNode) {
                        mcreator.getGradleConsole().exec(gradleTaskNode.getUserObject().toString());
                        mcreator.showConsole();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tree);
        add("Center", PanelUtils.northAndCenterElement(toolBar, scrollPane));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topBar.setBackground(Theme.current().getAltBackgroundColor());
        topBar.add(ComponentUtils.setForeground(ComponentUtils.deriveFont(new JLabel("Controls"), 10f),
                Theme.current().getAltForegroundColor()));

        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.current().getBackgroundColor()),
                BorderFactory.createEmptyBorder(2, 5, 2, 0)));
        add("North", topBar);
    }

    public void reloadTree() {
        tree.setSelectionPath(null);

        FilterTreeNode node = new FilterTreeNode(mcreator.getWorkspaceSettings().getModName());

        if (mcreator.getTabs().getCurrentTab()
                .getContent() instanceof ModElementGUI<?> abstractConfigurationTableModElementGUI) {
            references = new FilterTreeNode(mcreator.getTabs().getCurrentTab().getText() + "'s");
            for (ModElement modElement : ReferencesFinder.searchModElementUsages(mcreator.getWorkspace(),
                    abstractConfigurationTableModElementGUI.getModElement())) {
                references.add(new ModElementReferenceNode(modElement.getName()));
            }
            node.add(references);
            node.add(new FilterTreeNode("!Right click your configuration area to show the helper menu."));
        }

        if (!duplicatedElements.isEmpty()) {
            var duplicatedElements = new FilterTreeNode("Duplicated elements");
            for (Map.Entry<String, ArrayList<String>> stringArrayListEntry : this.duplicatedElements.entrySet()) {
                var duplicatedElementUUID = new FilterTreeNode(stringArrayListEntry.getKey());
                for (String s : stringArrayListEntry.getValue()) {
                    duplicatedElementUUID.add(new ModElementReferenceNode(s));
                }
                duplicatedElements.add(duplicatedElementUUID);
            }
            node.add(duplicatedElements);
        }

        if (!notGenerate.isEmpty()) {
            var notGenerateElements = new FilterTreeNode("No generated elements");
            for (ModElement modElement : notGenerate) {
                notGenerateElements.add(new ModElementReferenceNode(modElement.getName()));
            }
            node.add(notGenerateElements);
        }

        if (mcreatorPluginsWorkspace) {
            var gradleTasks = new FilterTreeNode("Gradle tasks");
            gradleTasks.add(new GradleTaskNode("runMCreator"));
            node.add(gradleTasks);
        }

        mods.setRoot(node);

        TreeUtils.expandAllNodes(tree, 0, 2);
    }

    public HashMap<String, ArrayList<String>> getDuplicatedElements() {
        duplicatedElements.clear();
        return duplicatedElements;
    }

    public ArrayList<ModElement> getNotGenerate() {
        notGenerate.clear();
        return notGenerate;
    }

    public void setMcreatorPluginsWorkspace(boolean mcreatorPluginsWorkspace) {
        this.mcreatorPluginsWorkspace = mcreatorPluginsWorkspace;
    }

    private static class ModElementReferenceNode extends FilterTreeNode {

        public ModElementReferenceNode(Object userObject) {
            super(userObject);
        }
    }

    private static class GradleTaskNode extends FilterTreeNode {

        public GradleTaskNode(Object userObject) {
            super(userObject);
        }
    }
}
