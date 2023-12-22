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
import java.util.Arrays;
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
                fileChooser.setCurrentDirectory(new File("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Half-Life\\cstrike"));

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
            if (!(e.getSource() instanceof JMenuItem)) {
                return;
            }
            entityManager.clearEntityList();
            fgdBuilder.clearEntityListModel();
            entityManager.setFgdFile(null);
            fgdBuilder.enableFileMenuItems(false);
            fgdBuilder.setStatusLabel("Ready");
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
                default -> { return; }
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
            StringBuilder stringBuilder = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                
                if (trimmedLine.startsWith("@")) {
                    /*if (trimmedLine.endsWith("[]")) {*/
                        stringBuilder.append(trimmedLine + "\n");
                        createEntity(stringBuilder);
                        stringBuilder.setLength(0);
                    /*}
                    else {
                        stringBuilder.append(trimmedLine);
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(trimmedLine);
                            if (trimmedLine.contains("]"))
                                break;
                        }
                    }*/
                }
            }
            switch (fgdBuilder.getCurrentTab()) {
                case 0 -> fgdBuilder.updateEntityListModel(entityManager.getEntityList(EntityType.BASECLASS));
                case 1 -> fgdBuilder.updateEntityListModel(entityManager.getEntityList(EntityType.SOLIDCLASS));
                case 2 -> fgdBuilder.updateEntityListModel(entityManager.getEntityList(EntityType.POINTCLASS));
                default -> { return; }
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
    
    private void createEntity(StringBuilder stringBuilder) {
        String entityString = stringBuilder.substring(0, stringBuilder.indexOf("\n"));
        Matcher entClassMatcher = entityManager.getFgdPattern(0).matcher(entityString);
        Matcher nameMatcher = entityManager.getFgdPattern(1).matcher(entityString);
        
        if (!entClassMatcher.find() || !nameMatcher.find())
            return;
        
        Entity.Builder entityBuilder;
        switch (entClassMatcher.group(1)) {
            case "@BaseClass" -> entityBuilder = new Entity.Builder(EntityType.BASECLASS, nameMatcher.group(1));
            case "@SolidClass" -> entityBuilder = new Entity.Builder(EntityType.SOLIDCLASS, nameMatcher.group(1));
            case "@PointClass" -> entityBuilder = new Entity.Builder(EntityType.POINTCLASS, nameMatcher.group(1));
            default -> { return; }
        }
        
        Matcher descriptionMatcher = entityManager.getFgdPattern(2).matcher(entityString);
        
        if (descriptionMatcher.find())
            entityBuilder.setDescription(descriptionMatcher.group(1).replace("\"", "").trim());
        
        Matcher inheritsMatcher = entityManager.getFgdPattern(3).matcher(entityString);
        
        if (inheritsMatcher.find())
            entityBuilder.setInherits(inheritsMatcher.group(1).split(",\\s*"));
        
        Matcher sizeMatcher = entityManager.getFgdPattern(4).matcher(entityString);
        
        if (sizeMatcher.find()) {
            try {
                int[][] size = new int[2][3];
                String corners[] = new String[3];
                
                corners = sizeMatcher.group(1).split(",\\s*");

                for (int i = 0; i < corners.length; i++) {
                    String[] xyz = corners[i].split("\\s+");

                        for (int j = 0; j < xyz.length; j++)
                            size[i][j] = Integer.parseInt(xyz[j]);
                }
                entityBuilder.setSize(size);
            }
            catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        Matcher colorMatcher = entityManager.getFgdPattern(5).matcher(entityString);
        
        if (colorMatcher.find()) {
            try {
                short[] color = new short[3];
                String[] rgb = colorMatcher.group(1).split("\\s+");

                for (int i = 0; i < rgb.length; i++)
                    color[i] = Short.parseShort(rgb[i]);
                entityBuilder.setColor(color);
            }
            catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        Matcher spriteMatcher = entityManager.getFgdPattern(6).matcher(entityString);
        
        if (spriteMatcher.find())
            entityBuilder.setSprite(spriteMatcher.group(1).replace("\"", "").trim());
        
        Matcher decalMatcher = entityManager.getFgdPattern(7).matcher(entityString);
        boolean decal = false;
        
        if (decalMatcher.find())
            entityBuilder.setDecal(true);
        
        Matcher studioMatcher = entityManager.getFgdPattern(8).matcher(entityString);
        String studio = null;
        
        if (studioMatcher.find())
            entityBuilder.setStudio(studioMatcher.group(1).replace("\"", "").trim());
        
        Entity entity = entityBuilder.build();
        entityManager.addEntity(entity);
        entity.printData();
    }
}
