package org.cdc.generator.ui;

import net.mcreator.io.tree.FileNode;
import net.mcreator.io.tree.FileTree;
import net.mcreator.io.zip.ZipIO;
import net.mcreator.ui.FileOpener;
import net.mcreator.ui.component.tree.FilterTreeNode;
import net.mcreator.ui.component.tree.FilteredTreeModel;
import net.mcreator.ui.component.tree.JFileTree;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.util.DesktopUtils;
import org.apache.commons.io.FilenameUtils;
import org.cdc.generator.ui.renderer.FileTreeDirectoryAndFileCellRenderer;
import org.cdc.generator.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

import static org.cdc.generator.utils.Constants.ARCHIVE_EXTENSIONS;

public class ResourcePanelCorePack extends JPanel implements IReloadableFilterable {

    private final JFileTree tree;
    private final FilteredTreeModel model;

    private File parent;

    public ResourcePanelCorePack(WorkspacePanel workspacePanel) {
        super(new BorderLayout());
        this.model = new FilteredTreeModel(new FilterTreeNode(""));
        this.tree = new JFileTree(model);
        tree.setCellRenderer(new FileTreeDirectoryAndFileCellRenderer());

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyName = new JMenuItem(L10N.t("workspace.resources.tab.core_pack.menus.copy_name"));
        copyName.addActionListener(_ -> {
            if (tree.getSelectionPath() != null) {
                var content = new StringSelection(
                        tree.getSelectionPath().getLastPathComponent().toString().split("\\.")[0]);
                tree.getToolkit().getSystemClipboard().setContents(content, content);
                tree.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS).execute(() -> {
                    tree.setCursor(Cursor.getDefaultCursor());
                });
            }
        });
        popupMenu.add(copyName);
        JMenuItem openInExplorer = new JMenuItem(L10N.t("workspace.resources.tab.core_pack.menus.open_in_explorer"));
        openInExplorer.addActionListener(_ -> {
            if (tree.getLastSelectedPathComponent() != null) {
                FilterTreeNode selection = (FilterTreeNode) tree.getLastSelectedPathComponent();
                if (selection.getUserObject() instanceof FileNode<?> fileNode) {
                    DesktopUtils.openSafe(new File(parent, fileNode.incrementalPath), true);
                } else {
                    DesktopUtils.openSafe(new File(parent,
                            Arrays.stream(selection.getPath()).filter(a -> a instanceof FilterTreeNode)
                                    .map(a -> ((FilterTreeNode) a).getUserObject().toString())
                                    .collect(Collectors.joining(File.separator))), true);
                }
            }
        });
        popupMenu.add(openInExplorer);

        tree.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    if (tree.getLastSelectedPathComponent() != null
                            && tree.getLastSelectedPathComponent() instanceof FilterTreeNode node
                            && node.getChildCount() == 0) {
                        if (node.getUserObject() instanceof FileNode<?> fileNode) {
                            try {
                                FileOpener.openFile(workspacePanel.getMCreator(),
                                        generatePreviewFile(new File(parent,fileNode.incrementalPath).toString()));
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                }
            }
        });

        tree.setComponentPopupMenu(popupMenu);

        JScrollPane scrollPane = new JScrollPane(tree);

        this.add("Center", scrollPane);
    }

    @Override public void reloadElements() {
        if (parent != null) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            FilterTreeNode root = new FilterTreeNode("");

            FileTree<String> fileTree = new FileTree<>(new FileNode<>("", ""));
            parent = Utils.tryToFindCorePlugin();
            if (parent.isFile() && parent.getName().endsWith(".zip")) {
                ZipIO.iterateZip(parent, entry -> fileTree.addElement(entry.getName()), true);
            } else {
                try (var walker = Files.walk(parent.toPath())) {
                    walker.forEach(a -> {
                        if (Files.isRegularFile(a)) {
                            fileTree.addElement(a.toString().replace('\\', '/').replace("./plugins/mcreator-core", ""),
                                    a.toString());
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            JFileTree.addFileNodeToRoot(root, fileTree.root());

            model.setRoot(root);
            model.refilter();
        });
    }

    @Override public void refilterElements() {

    }

    private File generatePreviewFile(String path) throws IOException {
        var s = split(path);
        var temp = File.createTempFile("preview","."+ FilenameUtils.getExtension(path));
        temp.deleteOnExit();
        if (s.length == 1){
            Files.copy(Path.of(path),temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return temp;
        } else if (s.length == 2){
            ZipFile zipFile = new ZipFile(s[0]);
            var ent = zipFile.getEntry(s[1]);
            var input = zipFile.getInputStream(ent);
            Files.copy(input,temp.toPath(),StandardCopyOption.REPLACE_EXISTING);
            zipFile.close();
            return temp;
        }
        return temp;
    }

    private String[] split(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            throw new IllegalArgumentException("路径不能为空");
        }

        String lower = rawPath.toLowerCase(Locale.ROOT);

        // 从左到右扫描，找到第一个“压缩包扩展名 + 路径分隔符”的位置
        for (int i = 0; i < rawPath.length(); i++) {
            for (String ext : ARCHIVE_EXTENSIONS) {
                if (lower.startsWith(ext, i)) {
                    int afterExt = i + ext.length();

                    // 必须是 .zip\ 或 .zip/ 或 .zip 结尾，避免把 foo.zip.txt 误判
                    if (afterExt == rawPath.length() || rawPath.charAt(afterExt) == '\\'
                            || rawPath.charAt(afterExt) == '/') {

                        String archivePath = rawPath.substring(0, afterExt);
                        String entryPath = rawPath.substring(afterExt);

                        // 去掉 entryPath 开头的一个分隔符
                        if (entryPath.startsWith("\\") || entryPath.startsWith("/")) {
                            entryPath = entryPath.substring(1);
                        }

                        // 压缩包内部路径统一使用 /
                        entryPath = entryPath.replace('\\', '/');

                        return new String[] { archivePath, entryPath };
                    }
                }
            }
        }

        // 没有找到压缩包边界，整个路径视为普通文件
        return new String[] { rawPath };
    }
}
