/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seedee.fgdbuilder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author cdani
 */
public class MainFrame extends JFrame {
    private JMenuItem aboutMenuItem;
    private File selectedFile;
    
    private JTextArea fgdPreview;
    private JLabel statusLabel = new JLabel("Ready");
    
    public MainFrame(String frameTitle) {
        super(frameTitle);
        aboutMenuItem = new JMenuItem("About " + frameTitle);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,600);
        setLayout(new BorderLayout());
        
        //Menu strip
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem loadMenuItem = new JMenuItem("Load");
        JMenuItem reloadMenuItem = new JMenuItem("Reload");
        JMenuItem closeMenuItem = new JMenuItem("Close");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        fileMenu.add(loadMenuItem);
        fileMenu.add(reloadMenuItem);
        fileMenu.add(closeMenuItem);
        fileMenu.add(exitMenuItem);
        
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.add(aboutMenuItem);

        loadMenuItem.setMnemonic(KeyEvent.VK_L);
        loadMenuItem.addActionListener((ActionEvent e) -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle(loadMenuItem.getName());

                FileNameExtensionFilter filter = new FileNameExtensionFilter("Forge Game Data (*.fgd)", "fgd");
                fileChooser.setFileFilter(filter);

                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    String selectedFileName = fileChooser.getSelectedFile().getName();
                    int i = selectedFileName.lastIndexOf(".");

                    if (i > 0) {
                        String extension = selectedFileName.substring(i + 1);

                        if (extension.equals("fgd")) {
                            selectedFile = fileChooser.getSelectedFile();
                            statusLabel.setText("Loading " + selectedFile.getName());
                            
                            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                                StringBuilder fgdContent = new StringBuilder();
                                String line;
                                
                                while ((line = reader.readLine()) != null) {
                                    fgdContent.append(line).append("\n");
                                }
                                
                                fgdPreview.setText(fgdContent.toString());
                                statusLabel.setText("Loaded " + selectedFile.getName());
                                
                            }
                            catch (IOException ex) {
                                statusLabel.setText("Failed to load " + selectedFile.getName());
                                ex.printStackTrace();
                            }
                        } else {
                            statusLabel.setText("File must be a Forge Game Data (*.fgd) file");
                            JOptionPane.showMessageDialog(null, "File must be a Forge Game Data (*.fgd) file", "Warning", JOptionPane.WARNING_MESSAGE); //Do on a separate thread
                        }
                    } else {
                        statusLabel.setText("File must be a Forge Game Data (*.fgd) file");
                        JOptionPane.showMessageDialog(null, "File must be a Forge Game Data (*.fgd) file", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex);
            } 
        });
        
        reloadMenuItem.setMnemonic(KeyEvent.VK_R);
        reloadMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Reloading");
            }
        });
        
        closeMenuItem.setMnemonic(KeyEvent.VK_C);
        closeMenuItem.addActionListener((ActionEvent e) -> {
            System.out.println("Closing");
        });
        
        exitMenuItem.setMnemonic(KeyEvent.VK_E);
        exitMenuItem.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        aboutMenuItem.addActionListener((ActionEvent e) -> {
            System.out.println("Abouting");
        });

        //Main content
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Base Classes", new JPanel());
        tabs.addTab("Solid Classes", new JPanel());
        tabs.addTab("Point Classes", new JPanel());
        
        fgdPreview = new JTextArea("test");
        fgdPreview.setEditable(false);
        
        JScrollPane fgdPreviewScroll = new JScrollPane(fgdPreview);
        
        JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabs, fgdPreviewScroll);
        mainPanel.setResizeWeight(.5);
        add(mainPanel, BorderLayout.CENTER);

        //Status strip
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public void createGUI() {
        
    }
    /*@Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }*/
}
