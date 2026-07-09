package org.cdc.generator.ui;

import net.mcreator.minecraft.DataListEntry;
import net.mcreator.ui.validation.component.VComboBox;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SearchableComboBox<T> extends VComboBox<T> implements KeyListener {

    public SearchableComboBox() {
        init();
    }

    private void init() {
        addKeyListener(this);

        this.getEditor().getEditorComponent().addKeyListener(this);
    }

    private boolean canSearch() {
        return isPopupVisible();
    }

    @Override public void keyTyped(KeyEvent e) {
        // 不处理
    }

    @Override public void keyPressed(KeyEvent e) {

        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) {
            e.consume();
            showSearchDialog();
            return;
        }

        // 不可编辑模式：字母/数字/常用符号触发搜索
        if (canSearch()) {
            char ch = e.getKeyChar();
            if (Character.isLetterOrDigit(ch) || ch == '_' || ch == '-' || ch == ':' || ch == ' ') {
                e.consume();
                showSearchDialog();
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                e.consume();
                showSearchDialog();
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {
    }

    private void showSearchDialog() {
        if (isPopupVisible()) {
            setPopupVisible(false);
        }

        // 从模型中获取所有条目
        List<T> allEntries = new ArrayList<>();
        ComboBoxModel<T> model = getModel();
        for (int i = 0; i < model.getSize(); i++) {
            allEntries.add(model.getElementAt(i));
        }

        SearchDialog dialog = new SearchDialog(SwingUtilities.getWindowAncestor(this), allEntries);
        dialog.setVisible(true);

        T selected = dialog.getSelectedValue();
        if (selected != null) {
            setSelectedItem(selected);
        }
    }

    // ==================== 内部搜索对话框 ====================

    private class SearchDialog extends JDialog {

        private final List<T> allEntries;
        private T selectedValue = null;

        private final DefaultListModel<T> listModel = new DefaultListModel<>();
        private final JList<T> list = new JList<>(listModel);
        private final JTextField searchField = new JTextField(20);

        public SearchDialog(Window parent, List<T> entries) {
            super(parent, "Search", ModalityType.APPLICATION_MODAL);
            this.allEntries = new ArrayList<>(entries);

            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(true);
            initUI();
            pack();
            setLocationRelativeTo(parent);
            updateList("");
        }

        private void initUI() {
            setLayout(new BorderLayout(10, 10));

            JPanel topPanel = new JPanel(new BorderLayout(5, 5));
            topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

            JLabel label = new JLabel("Search:");
            topPanel.add(label, BorderLayout.WEST);
            topPanel.add(searchField, BorderLayout.CENTER);

            searchField.getDocument().addDocumentListener(new DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) {filterList();}

                @Override public void removeUpdate(DocumentEvent e) {filterList();}

                @Override public void changedUpdate(DocumentEvent e) {filterList();}

                private void filterList() {updateList(searchField.getText());}
            });

            list.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    String display;
                    if (value instanceof DataListEntry dle) {
                        display = dle.getReadableName();
                    } else {
                        display = value != null ? value.toString() : "";
                    }
                    return super.getListCellRendererComponent(list, display, index, isSelected, cellHasFocus);
                }
            });

            JScrollPane scrollPane = new JScrollPane(list);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            topPanel.add(scrollPane, BorderLayout.SOUTH);
            add(topPanel, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");

            okButton.addActionListener(e -> {
                T selected = list.getSelectedValue();
                if (selected != null)
                    selectedValue = selected;
                dispose();
            });
            cancelButton.addActionListener(e -> dispose());

            list.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        T selected = list.getSelectedValue();
                        if (selected != null) {
                            selectedValue = selected;
                            dispose();
                        }
                    }
                }
            });

            list.addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        T selected = list.getSelectedValue();
                        if (selected != null) {
                            selectedValue = selected;
                            dispose();
                        }
                    }
                }
            });

            bottomPanel.add(okButton);
            bottomPanel.add(cancelButton);
            add(bottomPanel, BorderLayout.SOUTH);

            addWindowListener(new WindowAdapter() {
                @Override public void windowClosed(WindowEvent e) {searchField.setText("");}
            });

            addComponentListener(new ComponentAdapter() {
                @Override public void componentShown(ComponentEvent e) {searchField.requestFocusInWindow();}
            });

            getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW);
        }

        private void updateList(String searchText) {
            listModel.clear();
            String lower = searchText.toLowerCase(Locale.ENGLISH);
            List<T> filtered = allEntries.stream().filter(item -> {
                String display;
                if (item instanceof DataListEntry dle) {
                    display = dle.getReadableName();
                } else {
                    display = item != null ? item.toString() : "";
                }
                return display.toLowerCase(Locale.ENGLISH).contains(lower);
            }).collect(Collectors.toList());
            for (T item : filtered) {
                listModel.addElement(item);
            }
            if (listModel.getSize() > 0) {
                list.setSelectedIndex(0);
            }
        }

        public T getSelectedValue() {return selectedValue;}
    }
}