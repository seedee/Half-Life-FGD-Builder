/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.seedee.fgdbuilder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

/**
 *
 * @author cdani
 */
public class FGDBuilder {

    private final String appTitle = "Half-Life FGD Builder";
    private File selectedFile;
    private final DefaultListModel<String>[] entityListModel = new DefaultListModel[3];
    private final JList<String> entityList;
    private final JScrollPane entityListScrollPane;
    private final JSplitPane entitySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private final JTextArea previewTextArea = new JTextArea();
    private final JLabel statusLabel = new JLabel("Ready");
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new FGDBuilder();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public FGDBuilder() {
        JFrame mainFrame = new JFrame(appTitle);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800,600);
        mainFrame.setLayout(new BorderLayout());
        
        //Menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createHelpMenu());
        mainFrame.setJMenuBar(menuBar);

        //Entity splitpane
        for (int i = 0; i < entityListModel.length; i++)
            entityListModel[i] = new DefaultListModel<>();
        entityList = new JList<>(entityListModel[0]);
        entityListScrollPane = new JScrollPane(entityList);
        entitySplitPane.setLeftComponent(entityListScrollPane);
        entitySplitPane.setRightComponent(new JPanel());
        entitySplitPane.setResizeWeight(.5);
        
        //Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Base Classes", entitySplitPane);
        tabbedPane.addTab("Solid Classes", null); //Dummy components to be swapped with entitySplitPane
        tabbedPane.addTab("Point Classes", null);
        
        JScrollPane fgdPreviewScrollPane = new JScrollPane(previewTextArea);
        previewTextArea.setEditable(false);
        tabbedPane.addTab("Preview", fgdPreviewScrollPane);
        tabbedPane.addChangeListener(new TabChangeListener(entitySplitPane, entityListModel, entityList));
        mainFrame.add(tabbedPane, BorderLayout.CENTER);
        
        //Status strip
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
        mainFrame.add(statusPanel, BorderLayout.SOUTH);
        
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem reloadMenuItem = createMenuItem("Reload", KeyEvent.VK_R, null, false);
        JMenuItem closeMenuItem = createMenuItem("Close", KeyEvent.VK_C, null, false);
        
        LoadFileListener loadFileListener = new LoadFileListener(appTitle, new JMenuItem[]{reloadMenuItem, closeMenuItem}, selectedFile, entityListModel, previewTextArea, statusLabel);
        reloadMenuItem.addActionListener(loadFileListener);
        
        fileMenu.add(createMenuItem("Load", KeyEvent.VK_L, loadFileListener, true));
        fileMenu.add(reloadMenuItem);
        fileMenu.add(reloadMenuItem);
        fileMenu.add(createMenuItem("Exit", KeyEvent.VK_E, (ActionEvent e) -> System.exit(0), true));
        
        return fileMenu;
    }
    
    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.add(createMenuItem("About " + appTitle, KeyEvent.VK_A, (ActionEvent e) -> System.out.println("Abouting"), true));
        
        return helpMenu;
    }
    
    private JMenuItem createMenuItem(String name, int mnemonic, ActionListener listener, boolean enabled) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.setMnemonic(mnemonic);
        menuItem.addActionListener(listener);
        menuItem.setEnabled(enabled);
        
        return menuItem;
    }
    
    /*@Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }*/
}
