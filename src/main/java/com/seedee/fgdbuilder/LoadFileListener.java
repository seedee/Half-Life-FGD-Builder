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
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author cdani
 */
public class LoadFileListener implements ActionListener {

    private String appTitle;
    private File selectedFile;
    private DefaultListModel<String>[] entityListModel;
    private JLabel statusLabel;
    
    public LoadFileListener(String title, File file, DefaultListModel<String>[] listModel, JLabel label) {
        appTitle = title;
        selectedFile = file;
        entityListModel = listModel;
        statusLabel = label;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

            
        try {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Forge Game Data (*.fgd)", "fgd");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(appTitle);
            fileChooser.setFileFilter(filter);

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                if (!fileChooser.getSelectedFile().getName().endsWith(".fgd")) {
                    statusLabel.setText("File must be a Forge Game Data (*.fgd) file");
                    JOptionPane.showMessageDialog(null, "File must be a Forge Game Data (*.fgd) file", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                selectedFile = fileChooser.getSelectedFile();
                parseFgd();
            }
        } catch (Exception ex) {
            System.out.println(e);
        } 
    }
    
    private void parseFgd() {
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
            
            //fgdPreviewTextArea.setText(fgdContent.toString());
            statusLabel.setText("Loaded " + selectedFile.getName()); 
        }
        catch (IOException ex) {
            statusLabel.setText("Failed to load " + selectedFile.getName());
            ex.printStackTrace();
        }
    }
}
