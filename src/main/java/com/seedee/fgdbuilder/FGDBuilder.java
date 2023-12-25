/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.seedee.fgdbuilder;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;

/**
 *
 * @author cdani
 */
public class FGDBuilder {

    private static final String APP_TITLE = "Half-Life FGD Builder";
    private JMenuItem loadMenuItem;
    private JMenuItem reloadMenuItem;
    private JMenuItem closeMenuItem;
    private JMenuItem exitMenuItem;
    private JMenuItem aboutMenuItem;
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final DefaultListModel<Entity> entityListModel = new DefaultListModel();
    private final JList<Entity> entityList = new JList<>(entityListModel);
    private final JPanel entityPanel = new JPanel();
    private final JSplitPane entitySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(entityList), entityPanel);
    private final JTextArea previewTextArea = new JTextArea();
    private final JLabel statusLabel = new JLabel("Ready");
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
        JFrame mainFrame = new JFrame(APP_TITLE);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800,600);
        mainFrame.setLayout(new BorderLayout());
        
        //Menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createHelpMenu());
        mainFrame.setJMenuBar(menuBar);
                
        //Tabs
        tabbedPane.addTab("Base Classes", entitySplitPane);
        tabbedPane.addTab("Solid Classes", null); //Dummy components to be swapped with entitySplitPane
        tabbedPane.addTab("Point Classes", null);
        
        JScrollPane fgdPreviewScrollPane = new JScrollPane(previewTextArea);
        previewTextArea.setEditable(false);
        tabbedPane.addTab("Preview", fgdPreviewScrollPane);
        
        mainFrame.add(tabbedPane, BorderLayout.CENTER);
        
        //Entity splitpane
        entitySplitPane.setResizeWeight(.1);
        
        //Entity panel
        entityPanel.setLayout(new BoxLayout(entityPanel, BoxLayout.Y_AXIS));
        
        //Status strip
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
        mainFrame.add(statusPanel, BorderLayout.SOUTH);

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
    
    public void addAboutListener(ActionListener listener) {
        aboutMenuItem.addActionListener(listener);
    }
    
    public void addTabListener(ChangeListener listener) {
        tabbedPane.addChangeListener(listener);
    }
    
    public int getCurrentTab() {
        return tabbedPane.getSelectedIndex();
    }
    
    public JSplitPane getEntitySplitPane() {
        return entitySplitPane;
    }
    
    public void updateEntityListModel(ArrayList<Entity> list) {
        if (list == null)
            return;
        entityListModel.clear();
        
        for (Entity entity : list) {
            entityListModel.addElement(entity);
        }
    }
    
    public void clearEntityListModel() {
        entityListModel.clear();
    }
    
    public void setStatusLabel(String text) {
        statusLabel.setText(text);
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
    
    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        aboutMenuItem = createMenuItem("About " + APP_TITLE, KeyEvent.VK_E, true);
        helpMenu.add(aboutMenuItem);
        
        return helpMenu;
    }
    
    private JMenuItem createMenuItem(String name, int mnemonic, boolean enabled) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.setMnemonic(mnemonic);
        menuItem.setEnabled(enabled);
        
        return menuItem;
    }
}
