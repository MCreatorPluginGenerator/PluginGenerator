package org.cdc.generator.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.tree.FilterTreeNode;
import net.mcreator.ui.component.tree.FilteredTreeModel;
import net.mcreator.ui.component.tree.JFileTree;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.util.TreeUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ReferencesFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.generator.ui.elements.AbstractConfigurationTableModElementGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/6/10
 */
public class ReferenceDock extends JPanel {

    private static final Logger LOG = LogManager.getLogger(ReferenceDock.class);

    private final FilteredTreeModel mods = new FilteredTreeModel(null);

    FilterTreeNode references = null;

    public final JFileTree tree = new JFileTree(mods);

    private JToolBar toolBar = new JToolBar();

    private MCreator mcreator;

    public ReferenceDock(MCreator mcreator) {
        setLayout(new BorderLayout(0, 0));

        this.mcreator = mcreator;

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(a -> reloadTree());
        toolBar.add(refresh);

        tree.addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased(MouseEvent e) {
                var selected = tree.getSelectionPath();
                if (selected != null && e.getClickCount() == 2) {
                    var element = mcreator.getWorkspace()
                            .getModElementByName(selected.getLastPathComponent().toString());
                    if (element != null) {
                        element.getType().getModElementGUI(mcreator, element, true).showView();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tree);
        add("Center", PanelUtils.northAndCenterElement(toolBar, scrollPane));
    }

    public void reloadTree() {
        tree.setSelectionPath(null);

        FilterTreeNode node = new FilterTreeNode(mcreator.getWorkspaceSettings().getModName());

        references = new FilterTreeNode(mcreator.getTabs().getCurrentTab().getText());

        if (mcreator.getTabs().getCurrentTab()
                .getContent() instanceof AbstractConfigurationTableModElementGUI<?> abstractConfigurationTableModElementGUI) {
            for (ModElement modElement : ReferencesFinder.searchModElementUsages(mcreator.getWorkspace(),
                    abstractConfigurationTableModElementGUI.getModElement())) {
                references.add(new FilterTreeNode(modElement.getName()));
            }
        }
        node.add(references);

        mods.setRoot(node);

        TreeUtils.expandAllNodes(tree,0,2);
    }
}
