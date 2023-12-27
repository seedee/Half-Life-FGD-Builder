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
        initializeEventListeners();
    }
    
    private void initializeEventListeners() {
        fgdBuilder.addLoadListener(this::loadEventHandler);
        fgdBuilder.addReloadListener(this::reloadEventHandler);
        fgdBuilder.addCloseListener(this::closeEventHandler);
        fgdBuilder.addExitListener(this::exitEventHandler);
        fgdBuilder.addAboutListener(this::aboutEventHandler);
        fgdBuilder.addTabListener(this::tabEventHandler);
    }
    
    private void loadEventHandler(ActionEvent e) {
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
    }
    
    private void reloadEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem))
            return;
        if (entityManager.getFgdFile() != null) {
            if (entityManager.getFgdFile().getName().endsWith("." + FILE_EXTENSION))
                parseEntities(true);
            else {
                showFileError();
            }
        }
    }
    
    private void closeEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem))
            return;
        entityManager.clearEntityList();
        fgdBuilder.clearEntityListModel();
        entityManager.setFgdFile(null);
        fgdBuilder.enableFileMenuItems(false);
        fgdBuilder.setStatusLabel("Ready");
    }
    
    private void exitEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem))
            return;
        System.exit(0);
    }
    
    private void aboutEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem))
            return;
        System.out.println("About");
    }
    
    private void tabEventHandler(ChangeEvent e) {
        if (!(e.getSource() instanceof JTabbedPane))
            return;
        JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
        int newTabIndex = tabbedPane.getSelectedIndex();

        if (!refreshEntityTab(newTabIndex))
            return;
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
                if (line.trim().indexOf('[') != -1 && line.trim().indexOf(']') != -1)
                    continue;
                boolean startOfProperties = false;
                int syntaxIndex = 0;
                reader.mark(100);
                
                while (true) {
                    String propertyLine = reader.readLine();
                    
                    if (propertyLine == null) { //If EOF before properties block
                        System.out.println("======== Syntax error in properties for " + nameMatcher.group(1).trim() + ", discarding");
                        createEntity(line.trim(), null);
                        entityProperties.setLength(0);
                        reader.reset();
                        break;
                    }
                    entClassMatcher.reset(propertyLine.trim());
                    
                    if (entClassMatcher.find()) { //If new entity found before syntaxIndex is 0
                        System.out.println("======== Syntax error in properties for " + nameMatcher.group(1).trim() + ", discarding");
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
            if (!refreshEntityTab(fgdBuilder.getCurrentTab()))
                return;
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
    
    private LinkedHashMap<String[], ArrayList<String[]>> parseEntityProperties(StringBuilder entityProperties) {
        if (entityProperties.toString().trim().replace("\n", "").equals("[]"))
            return null;
        entityProperties.delete(0, entityProperties.indexOf("\n") + 1);
        entityProperties.delete(entityProperties.lastIndexOf("\n"), entityProperties.length());
        LinkedHashMap<String[], ArrayList<String[]>> entityPropertyMap = new LinkedHashMap<>();
        ArrayList<String> propertyLines = new ArrayList<>(Arrays.asList(entityProperties.toString().split("\n")));
        Matcher keyMatcher = entityManager.getEntityPropertyPattern(0).matcher("");
        
        for (int i = 0; i < propertyLines.size(); i++) {
            String[] propertyParts = splitPropertyLines(propertyLines.get(i));
            
            if (propertyParts.length > 4) //If invalid property
                continue;
            String keyName;
            String keyType;
            keyMatcher.reset(propertyParts[0]);

            if (!keyMatcher.find())
                continue;
            keyName = keyMatcher.group(1);
            keyType = keyMatcher.group(2);
            String lastPropertyPart = propertyParts[propertyParts.length - 1];
            boolean foundPropertyBody = false;
            
            if (lastPropertyPart.endsWith("=")) {
                if (i == propertyLines.size() - 1)
                    break;
                propertyParts[propertyParts.length - 1] = lastPropertyPart.substring(0, lastPropertyPart.length() - 1).trim();

                if (keyName.equalsIgnoreCase("spawnflags") && keyType.equalsIgnoreCase("flags") || keyType.equalsIgnoreCase("choices"))
                    foundPropertyBody = true;
            }
            String keySmartEditName = null;
            String keyDefaultValue = null;
            String keyDescription = null;
            
            if (propertyParts.length > 1 && !(keyName.equalsIgnoreCase("spawnflags") && !keyType.equalsIgnoreCase("flags"))) {
                if (!propertyParts[1].isBlank())
                    keySmartEditName = propertyParts[1];

                if (propertyParts.length > 2) {
                    if (!propertyParts[2].isBlank())
                        keyDefaultValue = propertyParts[2];

                    if (propertyParts.length > 3) {
                        if (!propertyParts[3].isBlank())
                            keyDescription = propertyParts[3];
                    }
                }
            }
            String[] entityProperty = new String[5];
            entityProperty[0] = keyName;
            entityProperty[1] = keyType;
            entityProperty[2] = keySmartEditName;
            entityProperty[3] = keyDefaultValue;
            entityProperty[4] = keyDescription;
            
            if (foundPropertyBody)
                entityPropertyMap.put(entityProperty, parseEntityPropertyBody(propertyLines, keyMatcher, keyName, keyType, i));
            else
                entityPropertyMap.put(entityProperty, null);
        }
        return entityPropertyMap;
    }
    
    private String[] splitPropertyLines(String propertyLine) {
        String[] propertyParts = propertyLine.split(":");
            
        for (int i = 0; i < propertyParts.length; i++) {
            propertyParts[i] = propertyParts[i].replace("\"", "").trim();
        }
        return propertyParts;
    }
    
    private ArrayList<String[]> parseEntityPropertyBody(ArrayList<String> propertyLines, Matcher keyMatcher, String keyName, String keyType, int startIndex) {
        ArrayList<String[]> entityPropertyBody = new ArrayList<>();
        boolean foundOpeningTag = false;
                
        for (int i = startIndex + 1; i < propertyLines.size(); i++) {
            if (propertyLines.get(i).startsWith("[")) {
                foundOpeningTag = true;
                continue;
            }
            if (!foundOpeningTag)
                continue;
            keyMatcher.reset(propertyLines.get(i));
            
            if (keyMatcher.find()) {
                return null;
            }
            if (propertyLines.get(i).equals("]")) {
                return entityPropertyBody;
            }
            String[] propertyBodyParts = splitPropertyLines(propertyLines.get(i));
            
            if (keyName.equalsIgnoreCase("spawnflags") && keyType.equalsIgnoreCase("flags") && propertyBodyParts.length == 3) {
                entityPropertyBody.add(propertyBodyParts);
            }
            else if (keyType.equalsIgnoreCase("choices") && propertyBodyParts.length == 2) {
                entityPropertyBody.add(propertyBodyParts);
            } 
        }
        return entityPropertyBody;
    }
    
    private void createEntity(String entityString, LinkedHashMap<String[], ArrayList<String[]>> entityPropertyMap) {
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
        setEntityDescriptionandURL(entityBuilder, entityManager.getEntityPattern(2).matcher(entityString));
        setEntityInherits(entityBuilder, entityManager.getEntityPattern(3).matcher(entityString));
        setEntitySize(entityBuilder, entityManager.getEntityPattern(4).matcher(entityString));
        setEntityColor(entityBuilder, entityManager.getEntityPattern(5).matcher(entityString));
        setEntitySprite(entityBuilder, entityManager.getEntityPattern(6).matcher(entityString));
        setEntityDecal(entityBuilder, entityManager.getEntityPattern(7).matcher(entityString));
        setEntityStudio(entityBuilder, entityManager.getEntityPattern(8).matcher(entityString));
        
        if (entityPropertyMap != null)
            entityBuilder.setProperties(entityPropertyMap);
        Entity entity = entityBuilder.build();
        entityManager.addEntity(entity);
        entity.printData();
    }
    
    private void setEntityDescriptionandURL(Entity.Builder entityBuilder, Matcher matcher) {
        if (matcher.find())
            entityBuilder.setDescription(matcher.group(1).trim());
        if (matcher.find())
            entityBuilder.setURL(matcher.group(1).trim());
    }
    
    private void setEntityInherits(Entity.Builder entityBuilder, Matcher matcher) {
        if (matcher.find())
            entityBuilder.setInherits(matcher.group(1).split(",\\s*"));
    }
    
    private void setEntitySize(Entity.Builder entityBuilder, Matcher matcher) {
        if (matcher.find()) {
            try {
                int[][] size = new int[2][3];
                
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 3; j++)
                        size[i][j] = Integer.parseInt(matcher.group(i * 3 + j + 1));
                }
                entityBuilder.setSize(size);
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void setEntityColor(Entity.Builder entityBuilder, Matcher matcher) {
        if (matcher.find()) {
            try {
                short[] color = new short[3];

                for (int i = 0; i < 3; i++)
                    color[i] = Short.parseShort(matcher.group(i + 1));
                entityBuilder.setColor(color);
            }
            catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void setEntitySprite(Entity.Builder entityBuilder, Matcher matcher) {
        if (matcher.find())
            entityBuilder.setSprite(matcher.group(1).trim());
    }
    
    private void setEntityDecal(Entity.Builder entityBuilder, Matcher matcher) {
        if (matcher.find())
            entityBuilder.setDecal(true);
    }
    
    private void setEntityStudio(Entity.Builder entityBuilder, Matcher matcher) {
        if (matcher.find())
            entityBuilder.setStudio(matcher.group(1).trim());
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

