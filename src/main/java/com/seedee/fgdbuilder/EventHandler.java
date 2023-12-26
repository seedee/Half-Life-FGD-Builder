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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
    private static final String FILE_EXTENSION = "fgd";
    
    public EventHandler(FGDBuilder fgdBuilder, EntityManager entityManager) {
        this.fgdBuilder = fgdBuilder;
        this.entityManager = entityManager;
        
        fgdBuilder.addLoadListener((ActionEvent e) -> {
            if (!(e.getSource() instanceof JMenuItem))
                return;
            try {
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Forge Game Data (*." + FILE_EXTENSION + ")", FILE_EXTENSION);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Load");
                fileChooser.setFileFilter(filter);
                fileChooser.setCurrentDirectory(new File("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Half-Life\\cstrike"));

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    if (!fileChooser.getSelectedFile().getName().endsWith("." + FILE_EXTENSION)) {
                        showFileError();
                        return;
                    }
                    entityManager.setFgdFile(fileChooser.getSelectedFile());
                    parseEntities(false);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        fgdBuilder.addReloadListener((ActionEvent e) -> {
            if (!(e.getSource() instanceof JMenuItem))
                return;
            if (entityManager.getFgdFile() != null) {
                if (entityManager.getFgdFile().getName().endsWith("." + FILE_EXTENSION))
                    parseEntities(true);
                else {
                    showFileError();
                }
            }
        });
        
        fgdBuilder.addCloseListener((ActionEvent e) -> {
            if (!(e.getSource() instanceof JMenuItem))
                return;
            entityManager.clearEntityList();
            fgdBuilder.clearEntityListModel();
            entityManager.setFgdFile(null);
            fgdBuilder.enableFileMenuItems(false);
            fgdBuilder.setStatusLabel("Ready");
        });
        
        fgdBuilder.addExitListener((ActionEvent e) -> {
            if (!(e.getSource() instanceof JMenuItem))
                return;
            System.exit(0);
        });
        
        fgdBuilder.addAboutListener((ActionEvent e) -> {
            if (!(e.getSource() instanceof JMenuItem))
                return;
            System.out.println("About");
        });
        
        fgdBuilder.addTabListener((ChangeEvent e) -> {
            if (!(e.getSource() instanceof JTabbedPane))
                return;
            JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
            int newTabIndex = tabbedPane.getSelectedIndex();

            if (!refreshEntityTab(newTabIndex)) return;
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
        fgdBuilder.setStatusLabel("File must be a Forge Game Data (*." + FILE_EXTENSION + ") file");
        JOptionPane.showMessageDialog(null, "File must be a Forge Game Data (*." + FILE_EXTENSION + ") file", "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    private void parseEntities(boolean reloading) {
        entityManager.clearEntityList();
        fgdBuilder.clearEntityListModel();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(entityManager.getFgdFile()))) {
            String line;
            StringBuilder entityProperties = new StringBuilder();
            Matcher entClassMatcher = entityManager.getEntityPattern(0).matcher("");
            Matcher nameMatcher = entityManager.getEntityPattern(1).matcher("");

            while ((line = reader.readLine()) != null) {
                entClassMatcher.reset(line.trim());
                nameMatcher.reset(line.trim());
                
                if (!entClassMatcher.find() || !nameMatcher.find())
                    continue;
                if (line.trim().indexOf('[') != -1 && line.trim().indexOf(']') != -1) {
                    createEntity(line.trim(), null);
                    continue;
                }
                boolean startOfProperties = false;
                int syntaxIndex = 0;
                reader.mark(100);
                
                while (true) {
                    String propertyLine = reader.readLine();
                    
                    if (propertyLine == null) { //If EOF before properties block
                        createEntity(line.trim(), null);
                        entityProperties.setLength(0);
                        reader.reset();
                        break;
                    }
                    entClassMatcher.reset(propertyLine.trim());
                    
                    if (entClassMatcher.find()) { //If new entity found before properties block
                        createEntity(line.trim(), null);
                        entityProperties.setLength(0);
                        reader.reset();
                        break;
                    }
                    if (propertyLine.trim().indexOf('[') != -1 && !startOfProperties)
                        startOfProperties = true;
                    if (startOfProperties) {
                        if (!propertyLine.trim().isEmpty() && !propertyLine.trim().startsWith("//"))
                            entityProperties.append(propertyLine.trim()).append("\n");
                        for (int i = 0; i < propertyLine.trim().length(); i++) {
                            switch (propertyLine.trim().charAt(i)) {
                                case '[' -> syntaxIndex++;
                                case ']' -> syntaxIndex--;
                            }
                        }
                        if (syntaxIndex == 0) {
                            if (entityProperties.charAt(entityProperties.length() - 1) == '\n')
                                entityProperties.setLength(entityProperties.length() - 1);
                            createEntity(line.trim(), parseEntityProperties(entityProperties));
                            entityProperties.setLength(0);
                            break;
                        }
                    }
                }
            }
            if (!refreshEntityTab(fgdBuilder.getCurrentTab())) return;
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
    
    private LinkedHashMap<String[], ArrayList<String>> parseEntityProperties(StringBuilder entityProperties) {
        if (entityProperties.toString().trim().replace("\n", "").equals("[]"))
            return null;
        entityProperties.delete(0, entityProperties.indexOf("\n") + 1);
        entityProperties.delete(entityProperties.lastIndexOf("\n"), entityProperties.length());
        LinkedHashMap<String[], ArrayList<String>> entityPropertyMap = new LinkedHashMap<>();
        ArrayList<String> propertyLines = new ArrayList<>(Arrays.asList(entityProperties.toString().split("\n")));
        Matcher keyMatcher = entityManager.getEntityPropertyPattern(0).matcher("");
        
        for (int i = 0; i < propertyLines.size(); i++) {
            String[] propertyParts = propertyLines.get(i).split(":");
            
            for (int j = 0; j < propertyParts.length; j++) {
                propertyParts[j] = propertyParts[j].trim();
            }
            if (propertyParts.length > 4) //Invalid property
                continue;
            String keyName;
            String keyType;
            keyMatcher.reset(propertyParts[0]);

            if (!keyMatcher.find())
                continue;
            keyName = keyMatcher.group(1);
            keyType = keyMatcher.group(2);
            String lastPart = propertyParts[propertyParts.length - 1];
            boolean startOfPropertyBody = false;
            
            if (lastPart.endsWith("=")) {
                if (i == propertyLines.size() - 1)
                    break;
                propertyParts[propertyParts.length - 1] = lastPart.substring(0, lastPart.length() - 1).trim();

                if (keyName.equalsIgnoreCase("spawnflags") && keyType.equalsIgnoreCase("flags") || keyType.equalsIgnoreCase("choices"))
                    startOfPropertyBody = true;
            }
            String keySmartEditName = null;
            String keyDefaultValue = null;
            String keyDescription = null;
            
            if (propertyParts.length > 1 && !(keyName.equalsIgnoreCase("spawnflags") && !keyType.equalsIgnoreCase("flags"))) {
                if (!propertyParts[1].replace("\"", "").isBlank())
                    keySmartEditName = propertyParts[1].replace("\"", "");

                if (propertyParts.length > 2) {
                    if (!propertyParts[2].replace("\"", "").isBlank())
                        keyDefaultValue = propertyParts[2].replace("\"", "");

                    if (propertyParts.length > 3) {
                        if (!propertyParts[3].replace("\"", "").isBlank())
                            keyDescription = propertyParts[3].replace("\"", "");
                    }
                }
            }
            String[] entityProperty = new String[5];
            entityProperty[0] = keyName;
            entityProperty[1] = keyType;
            entityProperty[2] = keySmartEditName;
            entityProperty[3] = keyDefaultValue;
            entityProperty[4] = keyDescription;
            
            entityPropertyMap.put(entityProperty, null);
                    
            if (startOfPropertyBody) {
                ArrayList<String> entityPropertyBody = new ArrayList<>();
                boolean foundOpeningTag = false;
                
                for (int j = i + 1; j < propertyLines.size(); j++) {
                    if (propertyLines.get(j).startsWith("[")) {
                        foundOpeningTag = true;
                        continue;
                    }
                    if (!foundOpeningTag)
                        continue;
                    keyMatcher.reset(propertyLines.get(j));
                    
                    if (keyMatcher.find()) {
                        entityPropertyBody = null;
                        break;
                    }
                    if (propertyLines.get(j).equals("]")) {
                        break;
                    }
                    entityPropertyBody.add(propertyLines.get(j));
                }
                entityPropertyMap.put(entityProperty, entityPropertyBody);
            }
        }
        return entityPropertyMap;
    }
    
    private void createEntity(String entityString, LinkedHashMap<String[], ArrayList<String>> entityPropertyMap) {
        Matcher entClassMatcher = entityManager.getEntityPattern(0).matcher(entityString);
        Matcher nameMatcher = entityManager.getEntityPattern(1).matcher(entityString);
        
        if (!entClassMatcher.find() || !nameMatcher.find())
            return;
        
        Entity.Builder entityBuilder;
        switch (entClassMatcher.group(1)) {
            case "@BaseClass" -> entityBuilder = new Entity.Builder(EntityType.BASECLASS, nameMatcher.group(1));
            case "@SolidClass" -> entityBuilder = new Entity.Builder(EntityType.SOLIDCLASS, nameMatcher.group(1));
            case "@PointClass" -> entityBuilder = new Entity.Builder(EntityType.POINTCLASS, nameMatcher.group(1));
            default -> { return; }
        }
        
        Matcher descriptionandURLMatcher = entityManager.getEntityPattern(2).matcher(entityString);

        if (descriptionandURLMatcher.find())
            entityBuilder.setDescription(descriptionandURLMatcher.group(1).trim());
        if (descriptionandURLMatcher.find())
            entityBuilder.setURL(descriptionandURLMatcher.group(1).trim());
        
        Matcher inheritsMatcher = entityManager.getEntityPattern(3).matcher(entityString);
        
        if (inheritsMatcher.find())
            entityBuilder.setInherits(inheritsMatcher.group(1).split(",\\s*"));
        
        Matcher sizeMatcher = entityManager.getEntityPattern(4).matcher(entityString);
        
        if (sizeMatcher.find()) {
            try {
                int[][] size = new int[2][3];
                
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 3; j++)
                        size[i][j] = Integer.parseInt(sizeMatcher.group(i * 3 + j + 1));
                }
                entityBuilder.setSize(size);
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        Matcher colorMatcher = entityManager.getEntityPattern(5).matcher(entityString);
        
        if (colorMatcher.find()) {
            try {
                short[] color = new short[3];

                for (int i = 0; i < 3; i++)
                    color[i] = Short.parseShort(colorMatcher.group(i + 1));
                entityBuilder.setColor(color);
            }
            catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        Matcher spriteMatcher = entityManager.getEntityPattern(6).matcher(entityString);
        
        if (spriteMatcher.find())
            entityBuilder.setSprite(spriteMatcher.group(1).trim());
        
        Matcher decalMatcher = entityManager.getEntityPattern(7).matcher(entityString);
        
        if (decalMatcher.find())
            entityBuilder.setDecal(true);
        
        Matcher studioMatcher = entityManager.getEntityPattern(8).matcher(entityString);
        
        if (studioMatcher.find())
            entityBuilder.setStudio(studioMatcher.group(1).trim());
        
        if (entityPropertyMap != null)
            entityBuilder.setProperties(entityPropertyMap);
        
        Entity entity = entityBuilder.build();
        entityManager.addEntity(entity);
        entity.printData();
    }
    
    private boolean refreshEntityTab(int tabIndex) {
        switch (tabIndex) {
            case 0 -> fgdBuilder.updateEntityListModel(entityManager.getEntityList(EntityType.BASECLASS));
            case 1 -> fgdBuilder.updateEntityListModel(entityManager.getEntityList(EntityType.SOLIDCLASS));
            case 2 -> fgdBuilder.updateEntityListModel(entityManager.getEntityList(EntityType.POINTCLASS));
            default -> { return false; }
        }
        return true;
    }
}

