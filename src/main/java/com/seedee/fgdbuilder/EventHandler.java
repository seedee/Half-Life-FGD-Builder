/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seedee.fgdbuilder;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author cdani
 */
public class EventHandler {
    
    private static final String FILE_EXTENSION = "fgd";
    private static final URI[] URIS;
    
    private final FGDBuilder fgdBuilder;
    private final EntityManager entityManager;
    
    static {
        try {
            URIS = new URI[] {
                new URI("https://developer.valvesoftware.com/wiki/FGD"),
                new URI("https://github.com/seedee/Half-Life-FGD-Builder/issues"),
                new URI("https://github.com/seedee/Half-Life-FGD-Builder/discussions")
            };
        }
        catch (final URISyntaxException e) {
            throw new RuntimeException("Error initializing URIs", e);
        }
    }
    
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
        fgdBuilder.addOptionsListener(this::optionsEventHandler);
        fgdBuilder.addLogListener(this::logEventHandler);
        fgdBuilder.addVdcListener(this::vdcEventHandler);
        fgdBuilder.addBugReportListener(this::bugReportEventHandler);
        fgdBuilder.addFeedbackListener(this::feedbackEventHandler);
        fgdBuilder.addAboutListener(this::aboutEventHandler);
        fgdBuilder.addEntityListTabListener(this::entityListTabEventHandler);
        fgdBuilder.addEntityListListener(this::entityListEventHandler);
        fgdBuilder.addJackCheckBoxListener(this::jackCheckBoxEventHandler);
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

            if (fgdBuilder.showFileChooserOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
                if (!fileChooser.getSelectedFile().getName().endsWith("." + FILE_EXTENSION)) {
                    entityManager.setFgdFile(null);
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
                entityManager.setFgdFile(null);
                showFileError();
            }
        }
    }
    
    private void showFileError() {
        fgdBuilder.setStatusLabel("File must be a Forge Game Data (*." + FILE_EXTENSION + ") file");
        JOptionPane.showMessageDialog(null, "File must be a Forge Game Data (*." + FILE_EXTENSION + ") file", "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    private void closeEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem))
            return;
        entityManager.clearEntityList();
        fgdBuilder.clearEntityListModel();
        fgdBuilder.enableFileMenuItems(false);
        fgdBuilder.setStatusLabel("Closed " + entityManager.getFgdFile());
        entityManager.setFgdFile(null);
    }
    
    private void exitEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem))
            return;
        System.exit(0);
    }
    
    private void optionsEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem))
            return;
        System.out.println("Options");
    }
    
    private void logEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem))
            return;
        System.out.println("Log");
    }
    
    private void vdcEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem))
            return;
         openURL(URIS[0]);
    }
    
    private void bugReportEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem))
            return;
        openURL(URIS[1]);
    }
    
    private void feedbackEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem))
            return;
        openURL(URIS[2]);
    }
    
    
    private void openURL(URI uri) {
        try {
            if (!Desktop.isDesktopSupported()) { //Todo: implement linux support
                fgdBuilder.setStatusLabel("Unable to open URL: Desktop API is not supported on the current platform");
                return;
            }
            if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                fgdBuilder.setStatusLabel("Unable to open URL: Browsing is not supported on this platform");
                return;
            }
            Desktop.getDesktop().browse(uri);
        }
        catch (UnsupportedOperationException | IOException e) {
            e.printStackTrace();
        }
    }
    
    private void aboutEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem))
            return;
        System.out.println("About");
    }
    
    private void entityListTabEventHandler(ChangeEvent e) {
        if (!(e.getSource() instanceof JTabbedPane))
            return;
        JTabbedPane entityListTabbedPane = (JTabbedPane) e.getSource();
        int newTabIndex = entityListTabbedPane.getSelectedIndex();

        if (!refreshEntityListTab(newTabIndex))
            return;
        JSplitPane splitPane = fgdBuilder.getSplitPane();
        int previousTabIndex = 0;
        
        for (int i = 0; i < entityListTabbedPane.getTabCount(); i++) {
            if (entityListTabbedPane.getComponentAt(i) == splitPane)
                previousTabIndex = i;
        }
        if (previousTabIndex != newTabIndex) {
            entityListTabbedPane.setComponentAt(previousTabIndex, null);
            entityListTabbedPane.setComponentAt(newTabIndex, splitPane);
            entityListTabbedPane.revalidate();
            entityListTabbedPane.repaint();
        }
    }
    
    private void entityListEventHandler(ListSelectionEvent e) {
        if (!(e.getSource() instanceof JList))
            return;
        if (e.getValueIsAdjusting())
            return;
        JList<Entity> entityList = (JList<Entity>) e.getSource();
        
        if (entityList.getSelectedIndices().length != 1) {
            fgdBuilder.enableEditingPanels(false);
            return;
        }
        fgdBuilder.enableEditingPanels(true);
        Entity selectedEntity = entityList.getSelectedValue();
        fgdBuilder.setEntityName(selectedEntity.toString());
        fgdBuilder.setEntityDescription(selectedEntity.getDescription());
        fgdBuilder.setEntityURL(selectedEntity.getURL());
        fgdBuilder.setEntityInherits(selectedEntity.getInherits());
        fgdBuilder.setEntityFlags(selectedEntity.getFlags());
        fgdBuilder.setEntitySize(selectedEntity.hasSize(), selectedEntity.getSize());
        fgdBuilder.setEntityColor(selectedEntity.hasColor(), selectedEntity.getColor());
        fgdBuilder.setEntitySprite(selectedEntity.getSprite());
        fgdBuilder.setEntityDecal(selectedEntity.isDecal());
        fgdBuilder.setEntityStudio(selectedEntity.getStudio());
    }
    
    private void jackCheckBoxEventHandler(ItemEvent e) {
        if (!(e.getSource() instanceof JCheckBox))
            return;
        if (e.getStateChange() != ItemEvent.SELECTED)
            fgdBuilder.enableJackFeatures(false);
        else {
            ItemListener[] listeners = fgdBuilder.getJackCheckBoxListeners();
        
            for (ItemListener listener : listeners) {
                fgdBuilder.removeJackCheckBoxListener(listener);
            }
            int result = fgdBuilder.showConfirmDialog(
                    "Saving FGDs with this option will break compatibility with Valve Hammer Editor.",
                    "Enable J.A.C.K. Features",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );
            JCheckBox jackCheckBox = (JCheckBox) e.getSource();
            
            if (result != JOptionPane.OK_OPTION) {
                jackCheckBox.setSelected(false);
                fgdBuilder.addJackCheckBoxListener(this::jackCheckBoxEventHandler);
                return;
            }
            jackCheckBox.setSelected(true);
            fgdBuilder.addJackCheckBoxListener(this::jackCheckBoxEventHandler);
            fgdBuilder.enableJackFeatures(true);
        }
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
            if (!refreshEntityListTab(fgdBuilder.getCurrentEntityListTab()))
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
        
        Entity entity;
        switch (entClassMatcher.group(1)) {
            case "@BaseClass" -> entity = new Entity(EntityType.BASECLASS, nameMatcher.group(1).trim());
            case "@SolidClass" -> entity = new Entity(EntityType.SOLIDCLASS, nameMatcher.group(1).trim());
            case "@PointClass" -> entity = new Entity(EntityType.POINTCLASS, nameMatcher.group(1).trim());
            default -> { return; }
        }
        setEntityDescriptionAndURL(entity, entityManager.getEntityPattern(2).matcher(entityString));
        setEntityInherits(entity, entityManager.getEntityPattern(3).matcher(entityString));
        setEntityFlags(entity, entityManager.getEntityPattern(4).matcher(entityString));
        setEntitySize(entity, entityManager.getEntityPattern(5).matcher(entityString));
        setEntityColor(entity, entityManager.getEntityPattern(6).matcher(entityString));
        setEntitySprite(entity, entityManager.getEntityPattern(7).matcher(entityString));
        setEntityDecal(entity, entityManager.getEntityPattern(8).matcher(entityString));
        setEntityStudio(entity, entityManager.getEntityPattern(9).matcher(entityString));
        
        if (entityPropertyMap != null)
            entity.setProperties(entityPropertyMap);
        entityManager.addEntity(entity);
    }
    
    private void setEntityDescriptionAndURL(Entity entity, Matcher matcher) {
        if (matcher.find())
            entity.setDescription(matcher.group(1).trim());
        if (matcher.find())
            entity.setURL(matcher.group(1).trim());
    }
    
    private void setEntityInherits(Entity entity, Matcher matcher) {
        if (matcher.find())
            entity.setInherits(matcher.group(1).split(",\\s*"));
    }
    
    private void setEntityFlags(Entity entity, Matcher matcher) {
        if (matcher.find())
            entity.setFlags(matcher.group(1).split(",\\s*"));
    }
    
    private void setEntitySize(Entity entity, Matcher matcher) {
        if (matcher.find()) {
            try {
                int[][] size = new int[2][3];
                
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 3; j++)
                        size[i][j] = Integer.parseInt(matcher.group(i * 3 + j + 1));
                }
                entity.enableSize(true);
                entity.setSize(size);
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void setEntityColor(Entity entity, Matcher matcher) {
        if (matcher.find()) {
            try {
                short[] color = new short[3];

                for (int i = 0; i < 3; i++)
                    color[i] = Short.parseShort(matcher.group(i + 1));
                entity.enableColor(true);
                entity.setColor(color);
            }
            catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void setEntitySprite(Entity entity, Matcher matcher) {
        if (matcher.find())
            entity.setSprite(matcher.group(1).trim());
    }
    
    private void setEntityDecal(Entity entity, Matcher matcher) {
        if (matcher.find())
            entity.enableDecal(true);
    }
    
    private void setEntityStudio(Entity entity, Matcher matcher) {
        if (matcher.find())
            entity.setStudio(matcher.group(1).trim());
    }
    
    private boolean refreshEntityListTab(int tabIndex) {
        ListSelectionListener[] listeners = fgdBuilder.getEntityListListeners();
        
        for (ListSelectionListener listener : listeners) {
            fgdBuilder.removeEntityListListener(listener);
        }
        switch (tabIndex) {
            case 0 -> fgdBuilder.updateEntityListModel(entityManager.getEntityList(EntityType.BASECLASS));
            case 1 -> fgdBuilder.updateEntityListModel(entityManager.getEntityList(EntityType.SOLIDCLASS));
            case 2 -> fgdBuilder.updateEntityListModel(entityManager.getEntityList(EntityType.POINTCLASS));
            default -> {
                fgdBuilder.addEntityListListener(this::entityListEventHandler);
                return false;
            }
        }
        fgdBuilder.addEntityListListener(this::entityListEventHandler);
        return true;
    }
}

