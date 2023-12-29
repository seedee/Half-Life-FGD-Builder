/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.seedee.fgdbuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;

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
    private final JCheckBox jackFeaturesCheckBox = new JCheckBox("Enable J.A.C.K. Features");
    
    private final JPanel entityPanel = new JPanel(new GridBagLayout());
    private final JTextField entityNameTextField = new JTextField();
    private final JTextField entityDescriptionTextField = new JTextField();
    private final JTextField entityURLTextField = new JTextField();
    private final JTextField entityInheritsTextField = new JTextField();
    private final JButton entityInheritsAddButton = new JButton("Add");
    private final JButton entityInheritsRemoveButton = new JButton("Remove");
    private final DefaultListModel<String> entityInheritsListModel = new DefaultListModel();
    private final JList<String> entityInheritsList = new JList<>(entityInheritsListModel);
    private final JCheckBox entityFlagsAngleCheckBox = new JCheckBox("Angle");
    private final JCheckBox entityFlagsLightCheckBox = new JCheckBox("Light");
    private final JCheckBox entityFlagsPathCheckBox = new JCheckBox("Path");
    private final JCheckBox entityFlagsItemCheckBox = new JCheckBox("Item");
    private final JTextField entitySizeTextField = new JTextField();
    private final JTextField entityColorTextField = new JTextField();
    private final JTextField entitySpriteTextField = new JTextField();
    private final JButton entitySpriteBrowseButton = new JButton("Browse...");
    private final JCheckBox entityDecalCheckBox = new JCheckBox();
    private final JTextField entityStudioTextField = new JTextField("");
    private final JButton entityStudioBrowseButton = new JButton("Browse...");
    
    private final JLabel statusLabel = new JLabel("Ready");
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
        mainFrame.setSize(700,680);
        mainFrame.setLayout(new BorderLayout());
        
        mainFrame.setJMenuBar(createMenuBar());
        
        createEntityListTabs(); 
        mainFrame.add(entityListTabbedPane, BorderLayout.CENTER);
        
        splitPane.setResizeWeight(.1);
        
        createEntityTabs();
        createEntityPanel();
        rightPanel.add(entityTabbedPane, BorderLayout.CENTER);
        enableEditingPanels(false);
        
        rightPanel.add(createJackFeaturesPanel(), BorderLayout.SOUTH);
        
        mainFrame.add(createStatusPanel(), BorderLayout.SOUTH);
        
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
        ListSelectionListener[] listeners = entityList.getListSelectionListeners();
        return listeners;
    }
    
    public void removeEntityListListener(ListSelectionListener listener) {
        entityList.removeListSelectionListener(listener);
    }
    
    public int getCurrentTab() {
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
        setEnabledChildren(entityPanel, enabled);
    }
    
    public void setEnabledChildren(Container container, boolean enabled) {
        Component[] components = container.getComponents();
        
        for (Component component : components) {
            component.setEnabled(enabled);
            
            if (!enabled) {
                if (component instanceof JTextField textField)
                    textField.setText("");
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
        entityFlagsAngleCheckBox.setSelected(flags[0]);
        entityFlagsLightCheckBox.setSelected(flags[1]);
        entityFlagsPathCheckBox.setSelected(flags[2]);
        entityFlagsItemCheckBox.setSelected(flags[3]);
    }
    
    public void setEntitySize(int[][] size) {
        entitySizeTextField.setText(Arrays.toString(size[0]) + " " + Arrays.toString(size[1]));
    }
    
    public void setEntityColor(short[] color) {
        entityColorTextField.setText(Arrays.toString(color));
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
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        
        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy = 0;
        entityPanel.add(new JLabel("Name:"), c);
        
        c.gridy = 1;
        entityPanel.add(new JLabel("Description:"), c);
        
        c.gridy = 2;
        entityPanel.add(new JLabel("URL:"), c);
        
        c.anchor = GridBagConstraints.NORTHEAST;
        c.gridy = 3;
        entityPanel.add(new JLabel("Inherits:"), c);
        
        c.anchor = GridBagConstraints.EAST;
        c.gridy = 5;
        entityPanel.add(new JLabel("Flags:"), c);
        
        c.gridy = 6;
        entityPanel.add(new JLabel("Size:"), c);
        
        c.gridy = 7;
        entityPanel.add(new JLabel("Color:"), c);
        
        c.gridy = 8;
        entityPanel.add(new JLabel("Sprite:"), c);
        
        c.gridy = 9;
        entityPanel.add(new JLabel("Model:"), c);
        
        c.gridy = 10;
        entityPanel.add(new JLabel("Decal:"), c);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        c.gridwidth = 3;
        c.gridx = 1;
        c.gridy = 0;
        entityPanel.add(entityNameTextField, c);
        
        c.gridy = 1;
        entityPanel.add(entityDescriptionTextField, c);
        
        c.gridy = 2;
        entityPanel.add(entityURLTextField, c);
        
        c.gridwidth = 1;
        c.gridy = 3;
        entityPanel.add(entityInheritsTextField, c);
        
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 0.0;
        c.gridx = 2;
        entityPanel.add(entityInheritsAddButton, c);
        
        c.gridx = 3;
        entityPanel.add(entityInheritsRemoveButton, c);
        
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        c.gridwidth = 3;
        c.gridx = 1;
        c.gridy = 4;
        entityPanel.add(new JScrollPane(entityInheritsList), c);
        
        c.gridy = 5;
        JPanel flagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        entityFlagsAngleCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
        flagsPanel.add(entityFlagsAngleCheckBox);
        entityFlagsLightCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
        flagsPanel.add(entityFlagsLightCheckBox);
        entityFlagsPathCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
        flagsPanel.add(entityFlagsPathCheckBox);
        entityFlagsItemCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
        flagsPanel.add(entityFlagsItemCheckBox);
        entityPanel.add(flagsPanel, c);
        
        c.gridy = 6;
        entityPanel.add(entitySizeTextField, c);
        
        c.gridy = 7;
        entityPanel.add(entityColorTextField, c);

        c.gridwidth = 2;
        c.gridy = 8;
        entityPanel.add(entitySpriteTextField, c);
        
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridx = 3;
        entityPanel.add(entitySpriteBrowseButton, c);
        
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        c.gridwidth = 2;
        c.gridx = 1;
        c.gridy = 9;
        entityPanel.add(entityStudioTextField, c);
        
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridx = 3;
        entityPanel.add(entityStudioBrowseButton, c);
        
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        c.gridwidth = 2;
        c.gridx = 1;
        c.gridy = 10;
        entityPanel.add(entityDecalCheckBox, c);
        
        c.anchor = GridBagConstraints.NORTH;
        c.gridwidth = 3;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 11;
        c.weighty = 1.0;
        entityPanel.add(new JPanel(null), c);
    }
    
    private JPanel createJackFeaturesPanel() {
        JPanel jackFeaturesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jackFeaturesPanel.add(jackFeaturesCheckBox);
        
        jackFeaturesCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
        jackFeaturesCheckBox.setSelected(true);
        
        return jackFeaturesPanel;
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);
        
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        return statusPanel;
    }
}
