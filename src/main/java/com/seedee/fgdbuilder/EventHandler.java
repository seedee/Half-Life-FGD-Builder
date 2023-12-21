/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seedee.fgdbuilder;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author cdani
 */
public class EventHandler {
    
    private FGDBuilder fgdBuilder;
    private EntityManager entityManager;
    private static final String extension = "fgd";
    private static final String dotExtension = ".fgd";
    
    public EventHandler(FGDBuilder fgdBuilder, EntityManager entityManager) {
        this.fgdBuilder = fgdBuilder;
        this.entityManager = entityManager;
        
        fgdBuilder.addLoadListener((ActionEvent e) -> {
            if (!(e.getSource() instanceof JMenuItem)) {
                return;
            }

            try {
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Forge Game Data (*" + dotExtension + ")", extension);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Load");
                fileChooser.setFileFilter(filter);
                fileChooser.setCurrentDirectory(new File("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Half-Life"));

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    if (!fileChooser.getSelectedFile().getName().endsWith(dotExtension)) {
                        showFileError();
                        return;
                    }
                    entityManager.setFgdFile(fileChooser.getSelectedFile());
                    parseFgd(false);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        fgdBuilder.addReloadListener((ActionEvent e) -> {
            if (!(e.getSource() instanceof JMenuItem)) {
                return;
            }

            if (entityManager.getFgdFile() != null) {
                if (entityManager.getFgdFile().getName().endsWith(dotExtension))
                    parseFgd(true);
                else {
                    showFileError();
                }
            }
        });
        
        fgdBuilder.addCloseListener((ActionEvent e) -> {
        
        });
        
        fgdBuilder.addExitListener((ActionEvent e) -> {
            System.exit(0);
        });
        
        fgdBuilder.addAboutListener((ActionEvent e) -> {
        
        });
        
        fgdBuilder.addTabListener((ChangeEvent e) -> {
            if (!(e.getSource() instanceof JTabbedPane)) {
                    return;
            }
            JTabbedPane tabbedPane = (JTabbedPane) e.getSource();

            int newTabIndex = tabbedPane.getSelectedIndex();

            switch (newTabIndex) {
                case 0 -> fgdBuilder.updateEntityListModel(entityManager.getEntityList(EntityType.BASECLASS));
                case 1 -> fgdBuilder.updateEntityListModel(entityManager.getEntityList(EntityType.SOLIDCLASS));
                case 2 -> fgdBuilder.updateEntityListModel(entityManager.getEntityList(EntityType.POINTCLASS));
                default -> {
                    return;
                }
            }
            
            JSplitPane entitySplitPane = fgdBuilder.getEntitySplitPane();
            int previousTabIndex = 0;
            
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (tabbedPane.getComponentAt(i) == entitySplitPane)
                    previousTabIndex = i;
            }
            if (previousTabIndex != newTabIndex) {
                tabbedPane.setComponentAt(previousTabIndex, null);
                tabbedPane.setComponentAt(newTabIndex, entitySplitPane);
                tabbedPane.revalidate();
                tabbedPane.repaint();
            }
        });
    }
    
    private void showFileError() {
        fgdBuilder.setStatusLabel("File must be a Forge Game Data (*" + dotExtension + ") file");
        JOptionPane.showMessageDialog(null, "File must be a Forge Game Data (*" + dotExtension + ") file", "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    private void parseFgd(boolean reloading) {
        entityManager.clearEntityList();
        fgdBuilder.clearEntityListModel();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(entityManager.getFgdFile()))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim().toUpperCase();
                
                for (EntityType entClass : EntityType.values()) {
                    if (trimmedLine.startsWith("@" + entClass.name())) {
                        createEntity(line.trim(), entClass);
                        break;
                    }
                }
            }
            switch (fgdBuilder.getCurrentTab()) {
                case 0 -> fgdBuilder.updateEntityListModel(entityManager.getEntityList(EntityType.BASECLASS));
                case 1 -> fgdBuilder.updateEntityListModel(entityManager.getEntityList(EntityType.SOLIDCLASS));
                case 2 -> fgdBuilder.updateEntityListModel(entityManager.getEntityList(EntityType.POINTCLASS));
                default -> {
                    return;
                }
            }
            fgdBuilder.enableFileMenuItems(true);
            
            if (reloading)
                fgdBuilder.setStatusLabel("Reloaded " + entityManager.getFgdFile());
            else
                fgdBuilder.setStatusLabel("Loaded " + entityManager.getFgdFile());
        }
        catch (IOException e) {
            entityManager.setFgdFile(null);
            fgdBuilder.enableFileMenuItems(false);
            if (reloading)
                fgdBuilder.setStatusLabel("Failed to reload " + entityManager.getFgdFile());
            else
                fgdBuilder.setStatusLabel("Failed to load " + entityManager.getFgdFile());
            e.printStackTrace();
        }
    }
    
    private void createEntity(String declaration, EntityType entClass) {
        Matcher nameMatcher = entityManager.getFgdPattern(0).matcher(declaration);
        
        if (nameMatcher.find()) {
            Entity entity = new Entity.Builder(entClass, nameMatcher.group(1)).build();
            System.out.println(nameMatcher.group(1));
            entityManager.getEntityList(entClass).add(entity);
        }
    }
}
