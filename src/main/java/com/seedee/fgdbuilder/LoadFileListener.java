/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seedee.fgdbuilder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author cdani
 */
public class LoadFileListener implements ActionListener {

    private static final String fileExtension = "fgd";
    private static final String fileDotExtension = ".fgd";
    private String appTitle;
    private JMenuItem[] toggleableMenuItems;
    private File selectedFile;
    private DefaultListModel<String>[] entityListModel;
    private JTextArea previewTextArea;
    private JLabel statusLabel;
    
    public LoadFileListener(String title, JMenuItem[] menuItems, File file, DefaultListModel<String>[] listModel, JTextArea textArea, JLabel label) {
        appTitle = title;
        toggleableMenuItems = menuItems;
        selectedFile = file;
        entityListModel = listModel;
        previewTextArea = textArea;
        statusLabel = label;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem)) {
            return;
        }
        JMenuItem source = (JMenuItem) e.getSource();

        if (source.getText().equals("Reload") && selectedFile != null) {
            if (selectedFile.getName().endsWith(fileDotExtension))
                parseFgd(true);
            else {
                showFileError();
            }
        }
        else { //User clicked load
            try {
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Forge Game Data (*" + fileDotExtension + ")", fileExtension);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle(appTitle);
                fileChooser.setFileFilter(filter);
                fileChooser.setCurrentDirectory(new File("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Half-Life"));
                
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    if (!fileChooser.getSelectedFile().getName().endsWith(fileDotExtension)) {
                        showFileError();
                        return;
                    }
                    selectedFile = fileChooser.getSelectedFile();
                    parseFgd(false);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            } 
        }
    }
    
    public void showFileError() {
        statusLabel.setText("File must be a Forge Game Data (*" + fileDotExtension + ") file");
        JOptionPane.showMessageDialog(null, "File must be a Forge Game Data (*" + fileDotExtension + ") file", "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    public void parseFgd(boolean reloadingFile) {
        for (int i = 0; i < entityListModel.length; i++) {
            entityListModel[i].clear();
            previewTextArea.setText(null);
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("@BaseClass")) {
                    entityListModel[0].addElement(line);
                }
                if (line.trim().startsWith("@SolidClass")) {
                    entityListModel[1].addElement(line);
                }
                if (line.trim().startsWith("@PointClass")) {
                    entityListModel[2].addElement(line);
                }
            }
            
            toggleableMenuItems[0].setEnabled(true);
            toggleableMenuItems[1].setEnabled(true);
            
            if (reloadingFile)
                statusLabel.setText("Reoaded " + selectedFile.getName());
            else
                statusLabel.setText("Loaded " + selectedFile.getName());
        }
        catch (IOException ex) {
            if (reloadingFile)
                statusLabel.setText("Failed to reload " + selectedFile.getName());
            else
                statusLabel.setText("Failed to load " + selectedFile.getName());
            selectedFile = null;
            toggleableMenuItems[0].setEnabled(false);
            toggleableMenuItems[0].setEnabled(false);
            ex.printStackTrace();
        }
    }
}
