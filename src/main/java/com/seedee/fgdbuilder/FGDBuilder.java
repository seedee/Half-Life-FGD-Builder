/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.seedee.fgdbuilder;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
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
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author cdani
 */
public class FGDBuilder {

    private static final String APP_TITLE = "Half-Life FGD Builder";
    
    private final JFrame mainFrame = new JFrame(APP_TITLE);
            
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
    
    private final JTabbedPane entityListTabbedPane = new JTabbedPane();
    private final DefaultListModel<Entity> entityListModel = new DefaultListModel();
    private final JList<Entity> entityList = new JList<>(entityListModel);
    private final JTextArea previewTextArea = new JTextArea();
    
    private final JPanel rightPanel = new JPanel(new BorderLayout());
    private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(entityList), rightPanel);
    
    private final JTabbedPane entityTabbedPane = new JTabbedPane();
    
    private boolean editingPanels;
    private final JPanel entityPanel = new JPanel(new MigLayout("insets 0, gap 0, wrap 1, ltr", "[grow]"));
    private final JTextField entityNameTextField = new JTextField();
    private final JTextField entityDescriptionTextField = new JTextField();
    private final JTextField entityURLTextField = new JTextField();
    
    private final JTextField entityInheritsTextField = new JTextField();
    private final JButton entityInheritsAddButton = new JButton("Add");
    private final JButton entityInheritsRemoveButton = new JButton("Remove");
    private final DefaultListModel<String> entityInheritsListModel = new DefaultListModel();
    private final JList<String> entityInheritsList = new JList<>(entityInheritsListModel);
    
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
    
    private boolean jackFeatures;
    private final JCheckBox jackCheckBox = new JCheckBox("Enable J.A.C.K. Features");
    
    private final JLabel statusLabel = new JLabel("Ready");
    
    public static void main(String[] args) {
        FlatLightLaf.setup();
        
        SwingUtilities.invokeLater(() -> {
            try {
                FGDBuilder fgdBuilder = new FGDBuilder();
                EntityManager entityManager = new EntityManager();
                new EventHandler(fgdBuilder, entityManager);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public FGDBuilder() {
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800,600);
        mainFrame.setLayout(new BorderLayout());
        
        mainFrame.setJMenuBar(createMenuBar());
        
        createEntityListTabs(); 
        mainFrame.add(entityListTabbedPane, BorderLayout.CENTER);
        
        createEntityTabs();
        createEntityPanel();
        ToolTipManager.sharedInstance().setInitialDelay(100);
        rightPanel.add(entityTabbedPane, BorderLayout.CENTER);
        enableEditingPanels(false);
        
        rightPanel.add(createJackPanel(), BorderLayout.SOUTH);
        
        mainFrame.add(createStatusPanel(), BorderLayout.SOUTH);
        
        mainFrame.setLocationRelativeTo(null);
        mainFrame.validate();
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }
    
    public int showFileChooserOpenDialog (JFileChooser fileChooser) {
        return fileChooser.showOpenDialog(mainFrame);
    }
    
    public int showConfirmDialog(String message, String title, int optionType, int messageType) {
        return JOptionPane.showConfirmDialog(mainFrame, message, title, optionType, messageType);
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

    public void enableFileMenuItems(boolean enabled) {
        reloadMenuItem.setEnabled(enabled);
        closeMenuItem.setEnabled(enabled);
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
        
        for (Entity entity : list) {
            entityListModel.addElement(entity);
        }
    }
    
    public void clearEntityListModel() {
        enableEditingPanels(false);
        entityList.clearSelection();
        entityListModel.clear();
    }
    
    public void enableEditingPanels(boolean enabled) {
        editingPanels = enabled;
        setEnabledChildren(entityPanel, enabled);
        enableJackFeatures(jackFeatures);
        enableToolTips(enabled);
    }
    
    public void setEnabledChildren(Container container, boolean enabled) {
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
        jackFeatures = enabled;
        
        if (!editingPanels)
            return;
        entityURLTextField.setEnabled(enabled);
        
        for (JCheckBox entityFlagsCheckBox : entityFlagsCheckBoxes) {
            entityFlagsCheckBox.setEnabled(enabled);
        }
    }
    
    public void setEntityName(String name) {
        entityNameTextField.setText(name);
    }
    
    public void setEntityDescription(String description) {
        entityDescriptionTextField.setText(description);
    }
    
    public void setEntityURL(String url) {
        entityURLTextField.setText(url);
    }
    
    public void setEntityInherits(String[] inherits) {
        entityInheritsList.clearSelection();
        entityInheritsListModel.clear();
        
        if (inherits != null)
            entityInheritsListModel.addAll(Arrays.asList(inherits));
    }
    
    public void setEntityFlags(boolean[] flags) {
        entityFlagsCheckBoxes[0].setSelected(flags[0]);
        entityFlagsCheckBoxes[1].setSelected(flags[1]);
        entityFlagsCheckBoxes[2].setSelected(flags[2]);
        entityFlagsCheckBoxes[3].setSelected(flags[3]);
    }
    
    public void setEntitySize(boolean hasSize, int[][] size) {
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
    }
    
    public void setEntityColor(boolean hasColor, short[] color) {
        entityColorCheckBox.setSelected(hasColor);
        
        if (hasColor) {
            entityColorChooser.setColor(color[0], color[1], color[2]);
            return;
        }
        entityColorChooser.setColor(220, 30, 220);
    }
    
    public void setEntitySprite(String sprite) {
        entitySpriteTextField.setText(sprite);
    }
    
    public void setEntityDecal(boolean decal) {
        entityDecalCheckBox.setSelected(decal);
    }
    
    public void setEntityStudio(String studio) {
        entityStudioTextField.setText(studio);
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
        
        feedbackMenuItem = createMenuItem("Send Feedback", KeyEvent.VK_S, true);
        helpMenu.add(feedbackMenuItem);
        helpMenu.addSeparator();
        
        aboutMenuItem = createMenuItem("About " + APP_TITLE, KeyEvent.VK_A, true);
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
        entityListTabbedPane.addTab("Base Classes", splitPane);
        entityListTabbedPane.addTab("Solid Classes", null); //Dummy components to be swapped with entitySplitPane
        entityListTabbedPane.addTab("Point Classes", null);
        
        JScrollPane fgdPreviewScrollPane = new JScrollPane(previewTextArea);
        previewTextArea.setEditable(false);
        entityListTabbedPane.addTab("Preview", fgdPreviewScrollPane);
    }
    
    private void createEntityTabs() {
        entityTabbedPane.addTab("Entity", new JScrollPane(entityPanel));
        entityTabbedPane.addTab("Properties", new JPanel());
        entityTabbedPane.addTab("Flags", new JPanel());
    }
    
    private void createEntityPanel() {
        int labelColumnWidth = 40;
        JPanel group1 = new JPanel(new MigLayout("insets 5pt 5pt 0 5pt, wrap 2, ltr", "[" + labelColumnWidth + "pt, right][grow]"));
        group1.add(new JLabel("Name:"));
        group1.add(entityNameTextField, "grow, span");
        
        group1.add(new JLabel("Description:"));
        group1.add(entityDescriptionTextField, "grow, span");

        group1.add(new JLabel("URL:"));
        group1.add(entityURLTextField, "grow, span");
        entityPanel.add(group1, "grow");
                
        JPanel group2 = new JPanel(new MigLayout("insets 5pt 5pt 0 5pt, ltr", "[" + labelColumnWidth + "pt, right][grow][]", "[][60pt]"));
        group2.add(new JLabel("Inherits:"));
        group2.add(entityInheritsTextField, "grow");
        group2.add(entityInheritsAddButton, "split 2");
        group2.add(entityInheritsRemoveButton);
        group2.add(new JScrollPane(entityInheritsList), "cell 1 1, grow, span");
        entityPanel.add(group2, "grow");
        
        JPanel group3 = new JPanel(new MigLayout("insets 5pt 5pt 0 5pt, ltr, wrap 4", "[" + labelColumnWidth + "pt, right][][][grow]"));
        group3.add(new JLabel("Flags:"));
        group3.add(entityFlagsCheckBoxes[0], "split 4, span 2");
        group3.add(entityFlagsCheckBoxes[1]);
        group3.add(entityFlagsCheckBoxes[2]);
        group3.add(entityFlagsCheckBoxes[3]);
        group3.add(new JPanel(), "span 1 4");
        
        for (JSpinner entitySizeSpinner : entitySizeSpinners)
            entitySizeSpinner.setSize(new Dimension(20, entitySizeSpinner.getPreferredSize().height));
        group3.add(new JLabel("Size:"));
        group3.add(entitySizeCheckBox);
        group3.add(new JLabel("X1:"), "split 6");
        group3.add(entitySizeSpinners[0]);
        group3.add(new JLabel("Y1:"));
        group3.add(entitySizeSpinners[1]);
        group3.add(new JLabel("Z1:"));
        group3.add(entitySizeSpinners[2]);
        group3.add(new JLabel("X2:"), "cell 2 2, split 6");
        group3.add(entitySizeSpinners[3]);
        group3.add(new JLabel("Y2:"));
        group3.add(entitySizeSpinners[4]);
        group3.add(new JLabel("Z2:"));
        group3.add(entitySizeSpinners[5]);

        group3.add(new JLabel("Color:"));
        group3.add(entityColorCheckBox);
        group3.add(entityColorButton);
        entityPanel.add(group3, "grow");
        
        JPanel group4 = new JPanel(new MigLayout("insets 5pt 5pt 0 5pt, wrap 3, ltr", "[" + labelColumnWidth + "pt, right][grow][]"));
        group4.add(new JLabel("Sprite:"));
        group4.add(entitySpriteTextField, "grow");
        group4.add(entitySpriteBrowseButton);
        
        group4.add(new JLabel("Model:"));
        group4.add(entityStudioTextField, "grow");
        group4.add(entityStudioBrowseButton);
        
        group4.add(new JLabel("Decal:"));
        group4.add(entityDecalCheckBox);
        entityPanel.add(group4, "grow");
    }
    
    private void enableToolTips(boolean enabled) {
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
        
        if (!enabled) {
            for (JComponent component : components)
                component.setToolTipText(null);
            return;
        }
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
