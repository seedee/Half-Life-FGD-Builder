/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.seedee.fgdbuilder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author cdani
 */
public class MainView {

    private static final String FRAME_TITLE = "Half-Life FGD Builder";
    
    private final JFrame mainFrame = new JFrame(FRAME_TITLE);
            
    private JMenuItem loadMenuItem;
    private JMenuItem reloadMenuItem;
    private JMenuItem closeMenuItem;
    private JMenuItem exitMenuItem;
    private JMenuItem optionsMenuItem;
    private JMenuItem logMenuItem;
    private JMenuItem vdcMenuItem;
    private JMenuItem bugReportMenuItem;
    private JMenuItem feedbackMenuItem;
    private JMenuItem aboutMenuItem;
    
    private final JPanel leftPanel = new JPanel(new MigLayout("insets 5pt 5pt 5pt 0, ltr, wrap 3", "[fill, grow]", "[][fill, grow]"));
    private final JButton addEntityButton = new JButton("Add");
    private final JButton cutEntityButton = new JButton("Cut");
    private final JButton deleteEntityButton = new JButton("Delete");
    
    private final JTabbedPane entityListTabbedPane = new JTabbedPane();
    private final DefaultListModel<Entity> entityListModel = new DefaultListModel();
    private final JList<Entity> entityList = new JList<>(entityListModel);
    
    private final JPanel rightPanel = new JPanel(new BorderLayout());
    private final JTabbedPane entityTabbedPane = new JTabbedPane();
    
    private boolean editingPanelsEnabled;
    private final JPanel entityPanel = new JPanel(new MigLayout("insets 0, gap 0, ltr, wrap 1", "[fill, grow]"));
    private final JTextField entityNameTextField = new JTextField();
    private final JTextField entityDescriptionTextField = new JTextField();
    private final JTextField entityURLTextField = new JTextField();
    
    private final JTextField entityInheritsTextField = new JTextField();
    private final JButton entityInheritsAddButton = new JButton("+");
    private final JButton entityInheritsRemoveButton = new JButton("-");
    private final DefaultListModel<String> entityInheritsListModel = new DefaultListModel();
    private final JList<String> entityInheritsList = new JList<>(entityInheritsListModel);
    
    private final JPanel cuboidPanel = new JPanel();
    
    private final JCheckBox[] entityFlagsCheckBoxes = {
        new JCheckBox("Angle"),
        new JCheckBox("Light"),
        new JCheckBox("Path"),
        new JCheckBox("Item")
    };
    
    private final JCheckBox entitySizeCheckBox = new JCheckBox();
    private final JSpinner[] entitySizeSpinners = {
        new JSpinner(new SpinnerNumberModel(0, -512, 512, 1)),
        new JSpinner(new SpinnerNumberModel(0, -512, 512, 1)),
        new JSpinner(new SpinnerNumberModel(0, -512, 512, 1)),
        new JSpinner(new SpinnerNumberModel(0, -512, 512, 1)),
        new JSpinner(new SpinnerNumberModel(0, -512, 512, 1)),
        new JSpinner(new SpinnerNumberModel(0, -512, 512, 1))
    };
    
    private final JCheckBox entityColorCheckBox = new JCheckBox();
    private final JButton entityColorButton = new JButton("Choose...");
    private final JColorChooser entityColorChooser = new JColorChooser();
    
    private final JTextField entitySpriteTextField = new JTextField();
    private final JButton entitySpriteBrowseButton = new JButton("Browse...");
    
    private final JCheckBox entityDecalCheckBox = new JCheckBox();
    
    private final JTextField entityStudioTextField = new JTextField("");
    private final JButton entityStudioBrowseButton = new JButton("Browse...");
    
    private final JPanel entityPropertiesPanel = new JPanel(new MigLayout("insets 0, gap 0, ltr, wrap 1", "[fill, grow]", "[][180pt][]"));
    private final DefaultTableModel entityPropertiesTableModel = new DefaultTableModel(
        new Object[]{"Key", "Type", "SmartEdit Name", "Default Value", "Description"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable entityPropertiesTable = new JTable(entityPropertiesTableModel);
    
    
    
    private final JPanel entityFlagsPanel = new JPanel(new MigLayout("insets 0, gap 0, ltr, wrap 1", "[fill, grow]"));
    
    
    
    
    
    private boolean jackFeaturesEnabled;
    private final JCheckBox jackCheckBox = new JCheckBox("Enable J.A.C.K. Features");
    
    private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
    private final JTextArea previewTextArea = new JTextArea();
    
    private final JLabel statusLabel = new JLabel("Ready");

    public MainView() {
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setPreferredSize(new Dimension(880, 650));
        mainFrame.setLayout(new BorderLayout());
        
        mainFrame.setJMenuBar(createMenuBar());
        
        //Left panel
        leftPanel.add(addEntityButton);
        leftPanel.add(cutEntityButton);
        leftPanel.add(deleteEntityButton);
        leftPanel.setPreferredSize(new Dimension(0, 0));
        leftPanel.add(new JScrollPane(entityList), "span");
        
        //Right panel
        createEntityPanel(40);
        entityTabbedPane.addTab("Entity", new JScrollPane(entityPanel));
        
        createEntityPropertiesPanel(40);
        entityTabbedPane.addTab("Properties", new JScrollPane(entityPropertiesPanel));
        
        createEntityFlagsPanel(40);
        entityTabbedPane.addTab("Flags", new JScrollPane(entityFlagsPanel));
        
        enableEditingPanels(false);
        rightPanel.add(entityTabbedPane, BorderLayout.CENTER);
        rightPanel.add(createJackPanel(), BorderLayout.SOUTH);
        
        //Split pane
        createEntityListTabs();
        mainFrame.add(entityListTabbedPane, BorderLayout.CENTER);
        
        //Status
        mainFrame.add(createStatusPanel(), BorderLayout.SOUTH);
        
        setToolTips();
        ToolTipManager.sharedInstance().setReshowDelay(0);
        enableToolTips(false);
        
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.validate();
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }
    
    public void addLoadListener(ActionListener listener) {
        loadMenuItem.addActionListener(listener);
    }
    
    public void addReloadListener(ActionListener listener) {
        reloadMenuItem.addActionListener(listener);
    }
    
    public void addCloseListener(ActionListener listener) {
        closeMenuItem.addActionListener(listener);
    }
    
    public void addExitListener(ActionListener listener) {
        exitMenuItem.addActionListener(listener);
    }

    public void addOptionsListener(ActionListener listener) {
        optionsMenuItem.addActionListener(listener);
    }
        
    public void addLogListener(ActionListener listener) {
        logMenuItem.addActionListener(listener);
    }
    
    public void addVdcListener(ActionListener listener) {
        vdcMenuItem.addActionListener(listener);
    }
    
    public void addBugReportListener(ActionListener listener) {
        bugReportMenuItem.addActionListener(listener);
    }
    
    public void addFeedbackListener(ActionListener listener) {
        feedbackMenuItem.addActionListener(listener);
    }
    
    public void addAboutListener(ActionListener listener) {
        aboutMenuItem.addActionListener(listener);
    }
    
    public void addAddEntityListener(ActionListener listener) {
        addEntityButton.addActionListener(listener);
    }
    
    public void addCutEntityListener(ActionListener listener) {
        cutEntityButton.addActionListener(listener);
    }
    
    public void addDeleteEntityListener(ActionListener listener) {
        deleteEntityButton.addActionListener(listener);
    }
    
    public void addEntityListTabListener(ChangeListener listener) {
        entityListTabbedPane.addChangeListener(listener);
    }
    
    public void addEntityListListener(ListSelectionListener listener) {
        entityList.addListSelectionListener(listener);
    }
    
    public ListSelectionListener[] getEntityListListeners() {
        return entityList.getListSelectionListeners();
    }
    
    public void removeEntityListListener(ListSelectionListener listener) {
        entityList.removeListSelectionListener(listener);
    }
    
    public void addJackCheckBoxListener(ItemListener listener) {
        jackCheckBox.addItemListener(listener);
    }
    
    public ItemListener[] getJackCheckBoxListeners() {
        return jackCheckBox.getItemListeners();
    }
    
    public void removeJackCheckBoxListener(ItemListener listener) {
        jackCheckBox.removeItemListener(listener);
    }
    
    public int[] getFrameOffset() {
        int[] offset = {
            mainFrame.getX() + mainFrame.getWidth() / 2,
            mainFrame.getY() + mainFrame.getHeight() / 2
        };
        return offset;
    }
    
    public int showConfirmDialog(String message, String title, int optionType, int messageType) {
        return JOptionPane.showConfirmDialog(mainFrame, message, title, optionType, messageType);
    }
        
    public int showFileChooserOpenDialog (JFileChooser fileChooser) {
        return fileChooser.showOpenDialog(mainFrame);
    }
    
    public void enableFileMenuItems(boolean enabled) {
        reloadMenuItem.setEnabled(enabled);
        closeMenuItem.setEnabled(enabled);
    }
    
    public int getCurrentEntityListTab() {
        return entityListTabbedPane.getSelectedIndex();
    }
    
    public JSplitPane getSplitPane() {
        return splitPane;
    }
    
    public void updateEntityListModel(ArrayList<Entity> list) {
        if (list == null)
            return;
        clearEntityListModel();
        
        for (Entity entity : list)
            entityListModel.addElement(entity);
    }
    
    public void clearEntityListModel() {
        enableEditingPanels(false);
        entityList.clearSelection();
        entityListModel.clear();
    }
    
    public final void enableEditingPanels(boolean enabled) {
        editingPanelsEnabled = enabled;
        setEnabledChildren(entityPanel, enabled);
        setEnabledChildren(entityPropertiesTable, enabled);
        enableJackFeatures(jackFeaturesEnabled);
    }
    
    public void setEnabledChildren(Container container, boolean enabled) {
        container.setEnabled(enabled);
        Component[] components = container.getComponents();
        
        for (Component component : components) {
            component.setEnabled(enabled);
            
            if (!enabled) {
                if (component instanceof JTextField textField && !(component.getParent().getParent() instanceof JSpinner))
                    textField.setText("");
                if (component instanceof JSpinner spinner) {
                    SpinnerNumberModel spinnerModel = (SpinnerNumberModel) spinner.getModel();
                    spinnerModel.setValue(0);
                }
                if (component instanceof JList list) {
                    DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
                    listModel.clear();
                }
                if (component instanceof JCheckBox checkBox)
                    checkBox.setSelected(false);
            }
            if (component instanceof Container childContainer)
                setEnabledChildren(childContainer, enabled);
        }
    }
    
    public void enableJackFeatures(boolean enabled) {
        jackFeaturesEnabled = enabled;
        
        if (!editingPanelsEnabled)
            return;
        entityURLTextField.setEnabled(enabled);
        
        for (JCheckBox entityFlagsCheckBox : entityFlagsCheckBoxes) {
            entityFlagsCheckBox.setEnabled(enabled);
        }
    }
    
    public final void enableToolTips(boolean hasToolTips) {
        ToolTipManager.sharedInstance().setEnabled(editingPanelsEnabled && hasToolTips);
    }
    
    public void setToolTipsDelay(int toolTipsDelay) {
        ToolTipManager.sharedInstance().setInitialDelay(toolTipsDelay);
    }
    
    public void setEntity(String name, String description, String url, String[] inherits, boolean[] flags, boolean hasSize, int[][] size, boolean hasColor, short[] color, String sprite, boolean decal, String studio) {
        entityNameTextField.setText(name);
        entityDescriptionTextField.setText(description);
        entityURLTextField.setText(url);
        entityInheritsList.clearSelection();
        entityInheritsListModel.clear();
        
        if (inherits != null)
            entityInheritsListModel.addAll(Arrays.asList(inherits));
        entityFlagsCheckBoxes[0].setSelected(flags[0]);
        entityFlagsCheckBoxes[1].setSelected(flags[1]);
        entityFlagsCheckBoxes[2].setSelected(flags[2]);
        entityFlagsCheckBoxes[3].setSelected(flags[3]);
        entitySizeCheckBox.setSelected(hasSize);
        
        for (JSpinner entitySizeSpinner : entitySizeSpinners)
            entitySizeSpinner.setEnabled(hasSize);
        if (hasSize) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 3; j++) {
                    entitySizeSpinners[(i * 3) + j].setValue(size[i][j]);
                }
            }
            return;
        }
        for (int i = 0; i < 6; i++)
            entitySizeSpinners[i].setValue(0);
        entityColorCheckBox.setSelected(hasColor);
        
        if (hasColor) {
            entityColorChooser.setColor(color[0], color[1], color[2]);
            return;
        }
        entityColorChooser.setColor(220, 30, 220);
        entitySpriteTextField.setText(sprite);
        entityDecalCheckBox.setSelected(decal);
        entityStudioTextField.setText(studio);
    }
    
    public void setEntityProperties(String[][] entityProperties) {
        entityPropertiesTableModel.setRowCount(0);
        
        if (entityProperties == null)
            return;
        for (String[] entityProperty : entityProperties)
            entityPropertiesTableModel.addRow(entityProperty);
    }
    
    public void setStatusLabel(String text) {
        statusLabel.setText(text);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createToolsMenu());
        menuBar.add(createHelpMenu());
        
        return menuBar;
    }
    
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F); //Check convention for mnemonics later
        
        loadMenuItem = createMenuItem("Load", KeyEvent.VK_L, true);
        fileMenu.add(loadMenuItem);
        
        reloadMenuItem = createMenuItem("Reload", KeyEvent.VK_R, false);
        fileMenu.add(reloadMenuItem);
        
        closeMenuItem = createMenuItem("Close", KeyEvent.VK_C, false);
        fileMenu.add(closeMenuItem);
        
        exitMenuItem = createMenuItem("Exit", KeyEvent.VK_E, true);
        fileMenu.add(exitMenuItem);
        
        return fileMenu;
    }
    
    private JMenu createToolsMenu() {
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic(KeyEvent.VK_T);
        
        optionsMenuItem = createMenuItem("Options", KeyEvent.VK_O, true);
        toolsMenu.add(optionsMenuItem);
        
        logMenuItem = createMenuItem("View Log", KeyEvent.VK_V, true);
        toolsMenu.add(logMenuItem);
        
        return toolsMenu;
    }
    
    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        vdcMenuItem = createMenuItem("View FGD documentation", KeyEvent.VK_V, true);
        helpMenu.add(vdcMenuItem);
        
        bugReportMenuItem = createMenuItem("Report a Bug", KeyEvent.VK_R, true);
        helpMenu.add(bugReportMenuItem);
        
        feedbackMenuItem = createMenuItem("Request a Feature", KeyEvent.VK_S, true);
        helpMenu.add(feedbackMenuItem);
        helpMenu.addSeparator();
        
        aboutMenuItem = createMenuItem("About " + FRAME_TITLE, KeyEvent.VK_A, true);
        helpMenu.add(aboutMenuItem);
        
        return helpMenu;
    }
    
    private JMenuItem createMenuItem(String name, int mnemonic, boolean enabled) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.setMnemonic(mnemonic);
        menuItem.setEnabled(enabled);
        
        return menuItem;
    }
    
    private void createEntityListTabs() {
        splitPane.setDividerSize(8);
        entityListTabbedPane.addTab("Base Classes", splitPane);
        entityListTabbedPane.addTab("Solid Classes", null); //Dummy components to be swapped with splitPane
        entityListTabbedPane.addTab("Point Classes", null);
        
        JScrollPane fgdPreviewScrollPane = new JScrollPane(previewTextArea);
        previewTextArea.setEditable(false);
        entityListTabbedPane.addTab("Preview", fgdPreviewScrollPane);
    }
    
    private void createEntityPanel(int labelColumnWidth) {
        JPanel group1 = new JPanel(new MigLayout("insets 5pt 5pt 0 5pt, wrap 2, ltr", "[" + labelColumnWidth + "pt, right][fill, grow]"));
        group1.add(new JLabel("Name:"));
        group1.add(entityNameTextField);
        
        group1.add(new JLabel("Description:"));
        group1.add(entityDescriptionTextField);

        group1.add(new JLabel("URL:"));
        group1.add(entityURLTextField);
        entityPanel.add(group1);
                
        JPanel group2 = new JPanel(new MigLayout("insets 5pt 5pt 0 5pt, wrap 4, ltr", "[" + labelColumnWidth + "pt, right][fill, grow][][]", "[][60pt]"));
        group2.add(new JLabel("Inherits:"));
        group2.add(entityInheritsTextField);
        group2.add(entityInheritsAddButton);
        group2.add(entityInheritsRemoveButton);
        group2.add(new JScrollPane(entityInheritsList), "grow, span");
        entityPanel.add(group2);
        
        JPanel group3 = new JPanel(new MigLayout("insets 5pt 5pt 0 5pt, wrap 4, ltr", "[" + labelColumnWidth + "pt, right][][][fill, grow]"));
        group3.add(new JLabel("Flags:"));
        group3.add(entityFlagsCheckBoxes[0], "split 4, span 2");
        group3.add(entityFlagsCheckBoxes[1]);
        group3.add(entityFlagsCheckBoxes[2]);
        group3.add(entityFlagsCheckBoxes[3]);
        group3.add(cuboidPanel, "span 1 4");
        
        for (JSpinner entitySizeSpinner : entitySizeSpinners)
            entitySizeSpinner.setSize(new Dimension(20, entitySizeSpinner.getPreferredSize().height));
        group3.add(new JLabel("Size:"));
        group3.add(entitySizeCheckBox);
        group3.add(new JLabel("X1:"), "split 6");
        int spinnerMaxWidth = 35;
        group3.add(entitySizeSpinners[0], "wmax " + spinnerMaxWidth + "pt");
        group3.add(new JLabel("Y1:"));
        group3.add(entitySizeSpinners[1], "wmax " + spinnerMaxWidth + "pt");
        group3.add(new JLabel("Z1:"));
        group3.add(entitySizeSpinners[2], "wmax " + spinnerMaxWidth + "pt");
        group3.add(new JLabel("X2:"), "cell 2 2, split 6");
        group3.add(entitySizeSpinners[3], "wmax " + spinnerMaxWidth + "pt");
        group3.add(new JLabel("Y2:"));
        group3.add(entitySizeSpinners[4], "wmax " + spinnerMaxWidth + "pt");
        group3.add(new JLabel("Z2:"));
        group3.add(entitySizeSpinners[5], "wmax " + spinnerMaxWidth + "pt");
        
        group3.add(new JLabel("Color:"));
        group3.add(entityColorCheckBox);
        group3.add(entityColorButton);
        entityPanel.add(group3);
        
        JPanel group4 = new JPanel(new MigLayout("insets 5pt, wrap 3, ltr", "[" + labelColumnWidth + "pt, right][fill, grow][]"));
        group4.add(new JLabel("Sprite:"));
        group4.add(entitySpriteTextField);
        group4.add(entitySpriteBrowseButton);
        
        group4.add(new JLabel("Model:"));
        group4.add(entityStudioTextField);
        group4.add(entityStudioBrowseButton);
        
        group4.add(new JLabel("Decal:"));
        group4.add(entityDecalCheckBox);
        entityPanel.add(group4);
    }
    
    private void createEntityPropertiesPanel(int labelColumnWidth) {
        JPanel group1 = new JPanel(new MigLayout("insets 5pt 5pt 0 5pt, wrap 2, ltr"));
        group1.add(new JButton("+"));
        group1.add(new JButton("-"));
        entityPropertiesPanel.add(group1);
        
        JPanel group2 = new JPanel(new MigLayout("insets 5pt 0 0 5pt, wrap 1, ltr", "[fill, grow]"));
        group2.add(new JScrollPane(entityPropertiesTable));
        entityPropertiesPanel.add(group2);
        
        JPanel group3 = new JPanel(new MigLayout("insets 5pt, wrap 2, ltr", "[right][fill, grow]"));
        group3.add(new JLabel("Key:"));
        group3.add(new JTextField());
        
        group3.add(new JLabel("Type:"));
        JComboBox typeComboBox = new JComboBox();
        typeComboBox.setEditable(true);
        typeComboBox.addItem("string");
        typeComboBox.addItem("integer");
        typeComboBox.addItem("color255");
        typeComboBox.addItem("studio");
        typeComboBox.addItem("sprite");
        typeComboBox.addItem("sound");
        typeComboBox.addItem("choices");
        typeComboBox.addItem("target_source");
        typeComboBox.addItem("target_destination");
        typeComboBox.addItem("sky");
        group3.add(typeComboBox);
        
        group3.add(new JLabel("SmartEdit Name:"));
        group3.add(new JTextField());
        
        group3.add(new JLabel("Default Value:"));
        group3.add(new JTextField());
        
        group3.add(new JLabel("Description:"));
        group3.add(new JTextField());
        entityPropertiesPanel.add(group3);
    }
    
    private void createEntityFlagsPanel(int labelColumnWidth) {
        
    }
    
    private void setToolTips() {
        JComponent[] components = {
            entityNameTextField,
            entityDescriptionTextField,
            entityURLTextField,
            entityInheritsTextField,
            entityFlagsCheckBoxes[0],
            entityFlagsCheckBoxes[1],
            entityFlagsCheckBoxes[2],
            entityFlagsCheckBoxes[3],
            entitySizeCheckBox,
            entityColorCheckBox,
            entitySpriteTextField,
            entityStudioTextField,
            entityDecalCheckBox
        };
        
        String[] toolTips = {
            "Classname of the entity.",
            "Description visible in the entity's help window.",
            "URL to the documentation, shown as <a href=\"#\">Read more...</a> in the entity's help window.",
            "Base classes that the entity inherits properties from.",
            "Renders an arrow in the 3D view indicating the entity angles.",
            "Renders the entity as an octahedron instead of a cuboid.<br>Also renders in the 3D view if no model or sprite present.",
            "Allows the entity to be hidden with <b>View > Hide Paths</b>,<br>even if classname is not prefixed with <coAde>path_</code>.",
            "Allows the entity to be hidden with <b>View > Hide Items</b>,<br>even if classname is not prefixed with <code>item_</code>.",
            "Sets the entity size. Each set of coordinates represent opposite vertices of a cuboid.",
            "Sets the entity wireframe color in 2D views,<br>and 3D view if no model or sprite present.",
            "Path to the sprite.",
            "Path to the model. In J.A.C.K, this supports formats such as BSP, MD2, MD3, etc.",
            "Renders decals on nearby surfaces in the 3D view.<br>This requires a <code>texture</code> keyvalue to work."
        };
        
        for (int i = 0; i < components.length; i++)
            components[i].setToolTipText("<html>" + toolTips[i] + "</html>");
    }
    
    private JPanel createJackPanel() {
        JPanel jackPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jackCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
        jackPanel.add(jackCheckBox);
        
        return jackPanel;
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
        
        return statusPanel;
    }
}
