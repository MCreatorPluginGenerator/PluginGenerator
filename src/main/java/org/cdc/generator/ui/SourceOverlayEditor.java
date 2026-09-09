package org.cdc.generator.ui;  // 根据你的包结构调整

import net.mcreator.io.FileIO;
import net.mcreator.io.FileWatcher;
import net.mcreator.io.tree.FileNode;
import net.mcreator.io.tree.FileTree;
import net.mcreator.ui.FileOpener;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.browser.action.NewFolderAction;
import net.mcreator.ui.component.CodePreviewPanel;
import net.mcreator.ui.component.ImagePreviewPanel;
import net.mcreator.ui.component.JFileBreadCrumb;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.tree.FilterTreeNode;
import net.mcreator.ui.component.tree.FilteredTreeModel;
import net.mcreator.ui.component.tree.JFileTree;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.util.TreeUtils;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.dialogs.imageeditor.NewImageDialog;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.views.editor.image.metadata.MetadataManager;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.util.DesktopUtils;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;
import org.cdc.framework.annotaion.AIGenerated;
import org.cdc.generator.ui.renderer.FileTreeDirectoryAndFileCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 通用源覆盖编辑器。
 * 支持两种源模式：
 * 1. ZIP 文件：将 ZIP 包内容作为原始内容。
 * 2. 目录：将指定目录内容作为原始内容（普通路径访问）。
 * <p>
 * 覆盖目录 overlayRoot 用于存放用户自定义覆盖文件。
 * 提供树形浏览、预览（图片/文本）、编辑、导入、删除覆盖等功能。
 */
@AIGenerated public class SourceOverlayEditor extends JPanel implements IReloadableFilterable {

    private static final List<String> TEXT_EXTENSIONS = List.of("json", "txt", "svg", "css", "html", "xml");

    private final MCreator mcreator;
    private final Workspace workspace;
    private final File source;          // 源（ZIP 文件或目录）
    private final String root;
    private final File overlayRoot;     // 覆盖根目录
    @Nullable private final Supplier<String> filterProvider;

    private final JFileTree tree;
    private final FilteredTreeModel model = new FilteredTreeModel(new FilterTreeNode(""));
    private final JFileBreadCrumb breadCrumb;
    private final JPanel previewPanel = new JPanel(new GridLayout());

    private SourceEntryNode selectedNode = null;

    private final JLabel originalLabel = new JLabel("Original");
    private final JLabel overrideLabel = new JLabel("Override");

    private final JButton editButton;
    private final JButton importButton;
    private final JButton deleteButton;

    private List<DefaultMutableTreeNode> preSearchState = null;

    // ------------------------------------------------------------------------
    // 构造器
    // ------------------------------------------------------------------------

    /**
     * @param mcreator       MCreator 实例
     * @param source         源文件（ZIP 文件）或源目录
     * @param overlayRoot    覆盖根目录（必须存在或可创建）
     * @param filterProvider 过滤器提供者（可选）
     */
    public SourceOverlayEditor(MCreator mcreator, File source, String root, File overlayRoot,
            @Nullable Supplier<String> filterProvider) {
        super(new BorderLayout());
        setOpaque(false);

        this.mcreator = mcreator;
        this.workspace = mcreator.getWorkspace();
        this.source = source;
        this.root = root;
        this.overlayRoot = overlayRoot;
        this.filterProvider = filterProvider;

        if (!source.exists()) {
            throw new IllegalArgumentException("Source does not exist: " + source.getAbsolutePath());
        }
        if (!overlayRoot.exists()) {
            overlayRoot.mkdirs();
        }

        // 标签样式
        originalLabel.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
        ComponentUtils.deriveFont(originalLabel, 13);
        overrideLabel.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
        ComponentUtils.deriveFont(overrideLabel, 13);

        // ---- 文件树 ----
        tree = new JFileTree(model);
        tree.setCellRenderer(new FileTreeDirectoryAndFileCellRenderer());

        JScrollPane treeScroll = new JScrollPane(tree);
        treeScroll.setCorner(JScrollPane.LOWER_RIGHT_CORNER, new JPanel());
        treeScroll.setCorner(JScrollPane.LOWER_LEFT_CORNER, new JPanel());

        // ---- 文件夹操作工具栏 ----
        TransparentToolBar folderBar = new TransparentToolBar();

        JPopupMenu createMenu = new JPopupMenu();
        JMenuItem createJson = new JMenuItem("New JSON file");
        createJson.addActionListener(e -> {
            File currentFolder = getCurrentFolder();
            if (currentFolder != null) {
                String fileName = JOptionPane.showInputDialog(mcreator, "Enter JSON file name:");
                if (fileName != null && !fileName.isBlank()) {
                    fileName = fileName.replaceAll("[^a-zA-Z0-9_/.-]", "_");
                    File target = new File(currentFolder, fileName + (fileName.contains(".") ? "" : ".json"));
                    FileIO.writeStringToFile("", target);
                    reloadElements();
                }
            }
        });
        createMenu.add(createJson);

        JMenuItem createPng = new JMenuItem("New image file");
        createPng.addActionListener(e -> {
            File currentFolder = getCurrentFolder();
            if (currentFolder != null) {
                String fileName = JOptionPane.showInputDialog(mcreator, "Enter image file name:");
                if (fileName != null && !fileName.isBlank()) {
                    fileName = fileName.replaceAll("[^a-zA-Z0-9_/.-]", "_");
                    File target = new File(currentFolder, fileName + (fileName.contains(".") ? "" : ".png"));
                    ImageMakerView view = new ImageMakerView(mcreator);
                    new NewImageDialog(mcreator, view).setVisible(true);
                    view.setSaveLocation(target);
                    reloadElements();
                }
            }
        });
        createMenu.add(createPng);

        JButton addFile = createToolBarButton("Add file", UIRES.get("16px.add"));
        addFile.addActionListener(e -> {
            if (getCurrentFolder() != null) {
                createMenu.show(addFile, 5, addFile.getHeight() + 5);
            }
        });
        folderBar.add(addFile);

        JButton addFolder = createToolBarButton("Add folder", UIRES.get("16px.directory"));
        addFolder.addActionListener(e -> {
            File currentFolder = getCurrentFolder();
            if (currentFolder != null) {
                File folderToMake = NewFolderAction.openCreateFolderDialog(mcreator, currentFolder);
                if (folderToMake != null) {
                    folderToMake.mkdirs();
                    reloadElements();
                }
            }
        });
        folderBar.add(addFolder);


        // ---- 面包屑 ----
        breadCrumb = new JFileBreadCrumb(mcreator, overlayRoot, overlayRoot);

        // ---- 文件操作工具栏 ----
        TransparentToolBar fileBar = new TransparentToolBar();

        editButton = createToolBarButton("Edit", UIRES.get("16px.edit"));
        editButton.addActionListener(e -> editOrCreateOverride());
        fileBar.add(editButton);

        importButton = createToolBarButton("Import", UIRES.get("16px.open"));
        importButton.addActionListener(e -> importOverride());
        fileBar.add(importButton);

        deleteButton = createToolBarButton("Delete", UIRES.get("16px.delete"));
        deleteButton.addActionListener(e -> deleteOverride());
        fileBar.add(deleteButton);

        // ---- 预览面板 ----
        previewPanel.setOpaque(false);

        // ---- 布局 ----
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                PanelUtils.northAndCenterElement(folderBar, treeScroll),
                PanelUtils.northAndCenterElement(fileBar, PanelUtils.centerAndSouthElement(previewPanel, breadCrumb)));
        splitPane.setDividerLocation(320);
        splitPane.setOpaque(false);
        splitPane.setBackground(Theme.current().getBackgroundColor());

        add("Center", splitPane);

        // ---- 事件绑定 ----
        tree.addTreeSelectionListener(e -> {
            if (tree.getLastSelectedPathComponent() instanceof FilterTreeNode node
                    && node.getUserObject() instanceof FileNode<?> fileNode) {
                if (fileNode.getObject() instanceof SourceEntryNode entry) {
                    if (fileNode.incrementalPath.endsWith(entry.path)) {
                        setSelectedEntry(entry);
                    } else {
                        setSelectedEntry(new SourceEntryNode(fileNode.incrementalPath, true, null, null,
                                new File(overlayRoot, fileNode.incrementalPath)));
                    }
                }
            }
        });

        tree.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editOrCreateOverride();
                }
            }
        });

        editButton.setEnabled(false);
        importButton.setEnabled(false);
        deleteButton.setEnabled(false);

        // 文件变化监听（覆盖目录）
        FileWatcher watcher = mcreator.getGenerator().getFileWatcher();
        watcher.addListener(changes -> SwingUtilities.invokeLater(() -> {
            boolean needReload = changes.stream()
                    .anyMatch(c -> c.file().getAbsolutePath().startsWith(overlayRoot.getAbsolutePath()));
            if (needReload) {
                changes.stream().filter(c -> c.file().getName().endsWith(".png")).forEach(c -> {
                    try {
                        new ImageIcon(c.file().getAbsolutePath()).getImage().flush();
                    } catch (Exception ignored) {
                    }
                });
                reloadElements();
            }
        }));

        // 初始加载
        reloadElements();
    }

    // 便捷构造（无过滤器）
    public SourceOverlayEditor(MCreator mcreator, File source, String root, File overlayRoot) {
        this(mcreator, source, root, overlayRoot, null);
    }

    // ------------------------------------------------------------------------
    // 核心数据模型
    // ------------------------------------------------------------------------

    /**
     * 表示一个源条目（可以是 ZIP 中的条目或目录中的文件）。
     *
     * @param zipEntry   若源为 ZIP 则非空（文件夹为 null）
     * @param sourceFile 若源为目录则指向源文件
     */
    public record SourceEntryNode(String path, boolean folder, ZipEntry zipEntry, File sourceFile, File overrideFile) {

        public String getName() {return new File(path).getName();}

        public String getExtension() {return FilenameUtils.getExtension(path).toLowerCase(Locale.ROOT);}

        public boolean hasOverride() {return overrideFile != null && overrideFile.exists();}

        @Override public @NotNull String toString() {
            return "SourceEntryNode{" + "path='" + path + '\'' + ", folder=" + folder + ", zipEntry=" + zipEntry
                    + ", sourceFile=" + sourceFile + ", overrideFile=" + overrideFile + '}';
        }
    }

    // ------------------------------------------------------------------------
    // IReloadableFilterable 实现
    // ------------------------------------------------------------------------

    private ZipFile zipFile;

    @Override public void reloadElements() {
        FilterTreeNode root = new FilterTreeNode("");
        FileTree<SourceEntryNode> fileTree = new FileTree<>(new FileNode<>("", ""));
        Set<String> names = new HashSet<>();

        if (source.isDirectory()) {
            // 目录模式：递归遍历所有文件
            var start = source.toPath().resolve(this.root);
            try (var walker = Files.walk(start)) {
                walker.forEach(path -> {
                    boolean isDirectory = Files.isDirectory(path);
                    String relativePath = start.relativize(path).toString().replace('\\', '/');
                    File overrideFolder = new File(overlayRoot, relativePath);
                    SourceEntryNode node = new SourceEntryNode(relativePath, isDirectory, null, path.toFile(),
                            overrideFolder);
                    names.add(relativePath);
                    if (isDirectory) {
                        fileTree.addElement(relativePath + "/", node);
                    } else {
                        fileTree.addElement(relativePath, node);
                    }
                });
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mcreator, "Failed to walk directory: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // ZIP 模式：遍历 ZIP 条目
            try {
                zipFile = new ZipFile(source);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (entry.getName().startsWith(this.root)) {
                        var isDir = entry.isDirectory();
                        String relativePath = entry.getName().substring(this.root.length() + 1);
                        File overrideFile = new File(overlayRoot, relativePath);
                        SourceEntryNode node = new SourceEntryNode(relativePath, isDir, entry, null, overrideFile);
                        names.add(relativePath);
                        if (isDir) {
                            fileTree.addElement(relativePath + "/", node);
                        } else {
                            fileTree.addElement(relativePath, node);
                        }
                        // 若覆盖文件存在，监听其所在文件夹
                        if (overrideFile.isFile()) {
                            File parent = overrideFile.getParentFile();
                            if (parent != null) {
                                workspace.getGenerator().getFileWatcher().watchFolder(parent);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mcreator, "Failed to read source: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        try (var walker = Files.walk(overlayRoot.toPath())) {
            walker.forEach(path -> {
                boolean isDirectory = Files.isDirectory(path);
                String relativePath = overlayRoot.toPath().relativize(path).toString().replace('\\', '/');
                File overrideFolder = new File(overlayRoot, relativePath);
                SourceEntryNode node = new SourceEntryNode(relativePath, isDirectory, null, path.toFile(),
                        overrideFolder);
                if (!names.contains(relativePath)) {
                    if (isDirectory) {
                        fileTree.addElement(relativePath + "/", node);
                    } else {
                        fileTree.addElement(relativePath, node);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JFileTree.addFileNodeToRoot(root, fileTree.root());
        model.setRoot(root);
        model.refilter();

        if (selectedNode != null) {
            AtomicReference<SourceEntryNode> newEntry = new AtomicReference<>(null);
            TreeUtils.selectNodeByUserObject(tree, entry -> {
                if (entry.getObject() instanceof SourceEntryNode selectedEntry2) {
                    boolean match = selectedEntry2.path.equals(selectedNode.path);
                    if (match) {
                        newEntry.set(selectedEntry2);
                    }
                    return match;
                }
                return false;
            }, FileNode.class);
            setSelectedEntry(newEntry.get());
        }

    }

    @Override public void refilterElements() {
        if (filterProvider != null) {
            String filter = filterProvider.get();
            if (filter.length() >= 3) {
                if (preSearchState == null)
                    preSearchState = TreeUtils.getExpansionState(tree);
                model.setFilter(filter);
                SwingUtilities.invokeLater(() -> TreeUtils.expandAllNodes(tree, 0, tree.getRowCount()));
            } else {
                model.setFilter("");
                if (preSearchState != null) {
                    TreeUtils.setExpansionState(tree, preSearchState);
                    preSearchState = null;
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // 选择与预览
    // ------------------------------------------------------------------------

    private void setSelectedEntry(@Nullable SourceEntryNode node) {
        selectedNode = node;
        previewPanel.removeAll();

        editButton.setEnabled(false);
        importButton.setEnabled(false);
        deleteButton.setEnabled(false);

        if (node != null) {
            breadCrumb.reloadPath(node.overrideFile());

            if (node.folder()) {
                importButton.setEnabled(true);
                // 文件夹不启用编辑/删除
            } else {
                String ext = node.getExtension();
                boolean isText = TEXT_EXTENSIONS.contains(ext);
                boolean isImage = "png".equals(ext);
                boolean isFont = "ttf".equals(ext);

                if (isImage || isText) {
                    editButton.setEnabled(true);
                    editButton.setText(node.hasOverride() ? "Edit Override" : "Create Override");
                } else {
                    editButton.setEnabled(false);
                }

                importButton.setEnabled(true);
                if (node.hasOverride()) {
                    deleteButton.setEnabled(true);
                }

                // 预览
                if (isImage) {
                    showImagePreview(node);
                } else if (isText) {
                    showTextPreview(node);
                } else if (isFont){
                    showFontPreview(node);
                } else {
                    previewPanel.add(new JLabel("Preview not supported for ." + ext));
                }
            }
        }

        previewPanel.revalidate();
        previewPanel.repaint();
    }

    // 读取原始内容（处理 ZIP 或目录）
    private byte[] readSourceContent(SourceEntryNode node) throws IOException {
        if (node.zipEntry() != null) {
            // ZIP 模式
            if (zipFile != null) {
                ZipEntry entry = node.zipEntry;
                try (InputStream is = zipFile.getInputStream(entry)) {
                    return is.readAllBytes();
                }
            }
        } else if (node.sourceFile() != null && node.sourceFile().exists()) {
            // 目录模式
            try (FileInputStream fis = new FileInputStream(node.sourceFile())) {
                return fis.readAllBytes();
            }
        }
        return null;
    }

    private void showImagePreview(SourceEntryNode node) {
        Image originalImage = null;
        try {
            byte[] data = readSourceContent(node);
            if (data != null) {
                originalImage = ImageIO.read(new ByteArrayInputStream(data));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageIcon originalIcon = originalImage != null ? new ImageIcon(originalImage) : null;
        ImageIcon overrideIcon = null;
        if (node.hasOverride()) {
            overrideIcon = new ImageIcon(node.overrideFile().getAbsolutePath());
        }

        if (originalIcon != null && overrideIcon != null) {
            ImagePreviewPanel origPanel = new ImagePreviewPanel(originalIcon);
            ImagePreviewPanel ovrPanel = new ImagePreviewPanel(overrideIcon);
            previewPanel.add(PanelUtils.gridElements(1, 2, PanelUtils.northAndCenterElement(originalLabel, origPanel),
                    PanelUtils.northAndCenterElement(overrideLabel, ovrPanel)));
        } else if (originalIcon != null) {
            previewPanel.add(new ImagePreviewPanel(originalIcon));
        } else if (overrideIcon != null) {
            previewPanel.add(new ImagePreviewPanel(overrideIcon));
        }
    }

    /**
     * 预览字体文件（TTF/OTF），显示字体名称、样式和示例文本。
     */
    private void showFontPreview(SourceEntryNode node) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 读取原始字体
        Font originalFont = null;
        try {
            byte[] data = readSourceContent(node);
            if (data != null) {
                originalFont = Font.createFont(Font.TRUETYPE_FONT, new ByteArrayInputStream(data));
            }
        } catch (Exception ignored) {}

        // 读取覆盖字体
        Font overrideFont = null;
        if (node.hasOverride()) {
            try {
                overrideFont = Font.createFont(Font.TRUETYPE_FONT, node.overrideFile);
            } catch (Exception ignored) {}
        }

        int row = 0;
        if (originalFont != null) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            JLabel originalTitle = new JLabel(originalLabel.getText());
            originalTitle.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
            panel.add(originalTitle, gbc);
            row++;

            // 字体信息
            gbc.gridy = row;
            gbc.gridwidth = 2;
            String info = String.format("Name: %s, Style: %s, Size: %d",
                    originalFont.getName(),
                    originalFont.isBold() ? "Bold" : (originalFont.isItalic() ? "Italic" : "Plain"),
                    originalFont.getSize());
            JLabel infoLabel = new JLabel(info);
            infoLabel.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
            panel.add(infoLabel, gbc);
            row++;

            // 示例文本
            gbc.gridy = row;
            gbc.gridwidth = 2;
            JTextArea example = new JTextArea("The quick brown fox jumps over the lazy dog.\n0123456789!@#$%^&*() 你好中文");
            example.setFont(originalFont.deriveFont(24f)); // 使用适当大小
            example.setEditable(false);
            example.setBackground(Theme.current().getBackgroundColor());
            example.setForeground(Theme.current().getForegroundColor());
            example.setBorder(BorderFactory.createLineBorder(Theme.current().getForegroundColor()));
            panel.add(example, gbc);
            row++;
        }

        if (overrideFont != null) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 2;
            JLabel overrideTitle = new JLabel(overrideLabel.getText());
            overrideTitle.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
            panel.add(overrideTitle, gbc);
            row++;

            gbc.gridy = row;
            gbc.gridwidth = 2;
            String info = String.format("Name: %s, Style: %s, Size: %d",
                    overrideFont.getName(),
                    overrideFont.isBold() ? "Bold" : (overrideFont.isItalic() ? "Italic" : "Plain"),
                    overrideFont.getSize());
            JLabel infoLabel = new JLabel(info);
            infoLabel.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
            panel.add(infoLabel, gbc);
            row++;

            gbc.gridy = row;
            gbc.gridwidth = 2;
            JTextArea example = new JTextArea("The quick brown fox jumps over the lazy dog.\n0123456789!@#$%^&*() 你好中文");
            example.setFont(overrideFont.deriveFont(24f));
            example.setEditable(false);
            example.setBackground(Theme.current().getBackgroundColor());
            example.setForeground(Theme.current().getForegroundColor());
            example.setBorder(BorderFactory.createLineBorder(Theme.current().getForegroundColor()));
            panel.add(example, gbc);
        }

        if (originalFont == null && overrideFont == null) {
            panel.add(new JLabel("Cannot preview font."));
        }

        previewPanel.add(panel);
    }

    private void showTextPreview(SourceEntryNode node) {
        String originalText = null;
        try {
            byte[] data = readSourceContent(node);
            if (data != null) {
                originalText = new String(data, StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {
        }

        String overrideText = null;
        if (node.hasOverride()) {
            overrideText = FileIO.readFileToString(node.overrideFile());
        }

        File file = node.overrideFile();
        if (originalText != null && overrideText != null) {
            CodePreviewPanel origPanel = new CodePreviewPanel(originalText, file);
            CodePreviewPanel ovrPanel = new CodePreviewPanel(overrideText, file);
            previewPanel.add(PanelUtils.gridElements(1, 2, PanelUtils.northAndCenterElement(originalLabel, origPanel),
                    PanelUtils.northAndCenterElement(overrideLabel, ovrPanel)));
        } else if (originalText != null) {
            previewPanel.add(new CodePreviewPanel(originalText, file));
        } else if (overrideText != null) {
            previewPanel.add(new CodePreviewPanel(overrideText, file));
        }
    }

    // ------------------------------------------------------------------------
    // 操作：编辑/创建覆盖
    // ------------------------------------------------------------------------

    private void editOrCreateOverride() {
        if (selectedNode == null || selectedNode.folder())
            return;

        mcreator.getWorkspace().getHistoryManager().checkpoint("source_edit", selectedNode.getName());

        File overrideFile = selectedNode.overrideFile();
        if (overrideFile.exists()) {
            FileOpener.openFile(mcreator, overrideFile);
            return;
        }

        // 创建新的覆盖文件
        String ext = selectedNode.getExtension();
        if ("ttf".equals(ext)) {
            FileIO.writeStringToFile("",overrideFile);
            DesktopUtils.openSafe(overrideFile,true);
        } if ("png".equals(ext)) {
            ImageMakerView view = new ImageMakerView(mcreator);
            new NewImageDialog(mcreator, view).setVisible(true);
            view.setSaveLocation(overrideFile);
            reloadElements();
        } else if (TEXT_EXTENSIONS.contains(ext)) {
            FileIO.writeStringToFile("", overrideFile);
            FileOpener.openFile(mcreator, overrideFile);
            reloadElements();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    // ------------------------------------------------------------------------
    // 操作：导入覆盖
    // ------------------------------------------------------------------------

    private void importOverride() {
        if (selectedNode == null)
            return;

        File target;
        target = selectedNode.overrideFile();

        String[] extensions;
        if (selectedNode.folder()) {
            extensions = new String[] { "png", "json", "ttf", "txt", "css", "svg", "xml" };
        } else {
            extensions = new String[] { selectedNode.getExtension() };
        }

        if (!selectedNode.folder() && target.exists()) {
            int res = JOptionPane.showConfirmDialog(mcreator, "Override file already exists. Overwrite?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (res != JOptionPane.YES_OPTION)
                return;
        }

        File[] sources;
        if (selectedNode.folder()) {
            sources = FileDialogs.getMultiOpenDialog(mcreator, extensions);
        } else {
            File src = FileDialogs.getOpenDialog(mcreator, extensions);
            sources = src != null ? new File[] { src } : null;
        }

        if (sources != null) {
            for (File src : sources) {
                if (selectedNode.folder()) {
                    FileIO.copyFile(src, new File(target, src.getName()));
                } else {
                    if (!selectedNode.getExtension().equalsIgnoreCase(FilenameUtils.getExtension(src.getName()))) {
                        continue;
                    }
                    FileIO.copyFile(src, target);
                }
            }
            reloadElements();
        }
    }

    // ------------------------------------------------------------------------
    // 操作：删除覆盖
    // ------------------------------------------------------------------------

    private void deleteOverride() {
        if (selectedNode == null || !selectedNode.hasOverride())
            return;

        File toDelete = selectedNode.overrideFile();
        int option = JOptionPane.showConfirmDialog(mcreator,
                "Delete override file/folder:\n" + toDelete.getAbsolutePath(), "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            if (toDelete.isDirectory()) {
                FileIO.deleteDir(toDelete);
            } else {
                if (toDelete.getName().endsWith(".png")) {
                    File meta = MetadataManager.getMetadataFile(workspace, toDelete);
                    if (meta.exists())
                        meta.delete();
                }
                toDelete.delete();
            }
            reloadElements();
        }
    }

    // ------------------------------------------------------------------------
    // 辅助方法
    // ------------------------------------------------------------------------

    @Nullable private File getCurrentFolder() {
        if (selectedNode == null) {
            return overlayRoot;
        }
        if (selectedNode.folder()) {
            return new File(overlayRoot, selectedNode.path);
        }
        return selectedNode.overrideFile().getParentFile();
    }

    private static JButton createToolBarButton(String tooltip, Icon icon) {
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        return button;
    }

    /**
     * 外部导入文件（如拖放）。
     */
    public void importExternalFile(File file) {
        if (selectedNode == null)
            return;
        if (selectedNode.folder()) {
            File target = new File(selectedNode.overrideFile(), file.getName());
            FileIO.copyFile(file, target);
            reloadElements();
        } else {
            if (!selectedNode.getExtension().equalsIgnoreCase(FilenameUtils.getExtension(file.getName()))) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            FileIO.copyFile(file, selectedNode.overrideFile());
            reloadElements();
        }
    }

    public void refresh() {
        reloadElements();
    }
}