/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seedee.fgdbuilder;

import com.bric.colorpicker.models.ColorModel;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author cdani
 */
public class Controller {
    
    private static final String FILE_EXTENSION = "fgd";
    private static final URI[] URIS;
    static {
        try {
            URIS = new URI[] {
                new URI("https://developer.valvesoftware.com/wiki/FGD"),
                new URI("https://github.com/seedee/Half-Life-FGD-Builder/issues/new?assignees=seedee&labels=bug&projects=&template=bug_report.md&title=BUG"),
                new URI("https://github.com/seedee/Half-Life-FGD-Builder/issues/new?assignees=seedee&labels=enhancement&projects=&template=feature_request.md&title=%5BREQUEST%5D")
            };
        }
        catch (final URISyntaxException e) {
            throw new RuntimeException("Error initializing URIs", e);
        }
    }
    private final MainView mainView;
    private OptionsView optionsView;
    private final Model model;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Model model = new Model();
                FlatLaf lookAndFeel;
                
                switch(model.getLookAndFeel()) {
                    case DARK -> lookAndFeel = new FlatDarkLaf();
                    default -> lookAndFeel = new FlatLightLaf();
                }
                Color accentColor = model.getAccentColor();
                String accentColorHex = String.format("#%02x%02x%02x", accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue());  
                lookAndFeel.setExtraDefaults(Collections.singletonMap("@accentColor", accentColorHex));
                FlatLaf.setup(lookAndFeel);
                MainView mainView = new MainView();
                new Controller(model, mainView);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public Controller(Model model, MainView mainView) {
        this.model = model;
        this.mainView = mainView;
        this.mainView.enableToolTips(model.hasToolTips());
        this.mainView.setToolTipsDelay(model.getToolTipsDelay());
        
        this.mainView.addLoadListener(this::loadEventHandler);
        this.mainView.addReloadListener(this::reloadEventHandler);
        this.mainView.addCloseListener(this::closeEventHandler);
        this.mainView.addExitListener(this::exitEventHandler);
        this.mainView.addOptionsListener(this::optionsEventHandler);
        this.mainView.addLogListener(this::logEventHandler);
        this.mainView.addVdcListener(this::vdcEventHandler);
        this.mainView.addBugReportListener(this::bugReportEventHandler);
        this.mainView.addFeedbackListener(this::feedbackEventHandler);
        this.mainView.addAboutListener(this::aboutEventHandler);
        
        this.mainView.addEntityListTabListener(this::entityListTabEventHandler);
        this.mainView.addEntityListListener(this::entityListEventHandler);
        
        this.mainView.addAddEntityListener(this::addEntityEventHandler);
        this.mainView.addCutEntityListener(this::cutEntityEventHandler);
        this.mainView.addDeleteEntityListener(this::deleteEntityEventHandler);
        
        this.mainView.addEntityPropertiesTableListener(this::entityPropertiesTableEventHandler);
        this.mainView.addEntityPropertiesChoicesTableListener(this::entityPropertiesChoicesTableEventHandler);
        this.mainView.addJackCheckBoxListener(this::jackCheckBoxEventHandler);
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

            if (mainView.showFileChooserOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
                if (!fileChooser.getSelectedFile().getName().endsWith("." + FILE_EXTENSION)) {
                    model.setFgdFile(null);
                    showFileError();
                    return;
                }
                model.setFgdFile(fileChooser.getSelectedFile());
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
        if (model.getFgdFile() != null) {
            if (model.getFgdFile().getName().endsWith("." + FILE_EXTENSION))
                parseEntities(true);
            else {
                model.setFgdFile(null);
                showFileError();
            }
        }
    }
    
    private void showFileError() {
        mainView.setStatusLabel("File must be a Forge Game Data (*." + FILE_EXTENSION + ") file");
        JOptionPane.showMessageDialog(null, "File must be a Forge Game Data (*." + FILE_EXTENSION + ") file", "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    private void closeEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem))
            return;
        model.clearEntityList();
        mainView.clearEntityListModel();
        mainView.enableToolTips(false);
        mainView.enableFileMenuItems(false);
        mainView.setStatusLabel("Closed " + model.getFgdFile());
        model.setFgdFile(null);
    }
    
    private void exitEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem))
            return;
        System.exit(0);
    }
    
    private void optionsEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JMenuItem))
            return;
        optionsView = OptionsView.getInstance(mainView.getFrameOffset());
        optionsView.centerFrame(mainView.getFrameOffset()); //Centers if options is already open
        optionsView.setToolTipsCheckBox(model.hasToolTips());
        optionsView.setToolTipsDelaySpinner(model.getToolTipsDelay());
        optionsView.enableToolTipsDelay(model.hasToolTips());
        optionsView.setThemeRadioButton(model.getLookAndFeel());
        optionsView.setAccentColor(model.getAccentColor());
        
        optionsView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                OptionsView.setInstance(null);
            }
        });
        optionsView.addToolTipsListener(this::optionsToolTipsEventHandler);
        optionsView.addToolTipsDelayListener(this::optionsToolTipsDelayEventHandler);
        optionsView.addThemeListener(this::optionsThemeEventHandler);
        optionsView.addColorListener(this::optionsColorEventHandler);
        optionsView.addOKListener(this::optionsOKEventHandler);
        optionsView.addCancelListener(this::optionsCancelEventHandler);
    }
    
    private void optionsToolTipsEventHandler(ItemEvent e) {
        if (!(e.getSource() instanceof JCheckBox))
            return;
        model.proposeNewHasToolTips(e.getStateChange() == ItemEvent.SELECTED);
        optionsView.enableToolTipsDelay(e.getStateChange() == ItemEvent.SELECTED);
    }
    
    private void optionsToolTipsDelayEventHandler(ChangeEvent e) {
        if (!(e.getSource() instanceof JSpinner))
            return;
        JSpinner toolTipsDelaySpinner = (JSpinner) e.getSource();
        model.proposeNewToolTipsDelay((int) toolTipsDelaySpinner.getValue());
    }
    
    private void optionsThemeEventHandler(ItemEvent e) {
        if (!(e.getSource() instanceof JRadioButton))
            return;
        if (e.getStateChange() != ItemEvent.SELECTED)
            return;
        JRadioButton themeRadioButton = (JRadioButton) e.getSource();
        
        switch (themeRadioButton.getText()) {
            case "Light" -> model.proposeNewLookAndFeel(Model.LookAndFeel.LIGHT);
            case "Dark" -> model.proposeNewLookAndFeel(Model.LookAndFeel.DARK);
        }
    }
    
    private void optionsColorEventHandler(ColorModel e) {
        model.proposeNewAccentColor(e.getColor());
    }
    
    private void optionsOKEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JButton))
            return;
        model.saveOptions(model.getProposedHasToolTips(), model.getProposedToolTipsDelay(), model.getProposedLookAndFeel(), model.getProposedAccentColor());
        mainView.enableToolTips(model.hasToolTips());
        mainView.setToolTipsDelay(model.getToolTipsDelay());
        FlatLaf lookAndFeel;
        
        switch(model.getLookAndFeel()) {
            case DARK -> lookAndFeel = new FlatDarkLaf();
            default -> lookAndFeel = new FlatLightLaf();
        }
        Color accentColor = model.getAccentColor();
        String accentColorHex = String.format("#%02x%02x%02x", accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue());  
        lookAndFeel.setExtraDefaults(Collections.singletonMap("@accentColor", accentColorHex));
        FlatLaf.setup(lookAndFeel);
        FlatLaf.updateUI();
        mainView.enableEditingPanels(mainView.isEnabledEditingPanels());
        
        optionsView.closeFrame();
    }
    
    private void optionsCancelEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JButton))
            return;
        model.proposeNewHasToolTips(model.hasToolTips());
        model.proposeNewToolTipsDelay(model.getToolTipsDelay());
        model.proposeNewLookAndFeel(model.getLookAndFeel());
        model.proposeNewAccentColor(model.getAccentColor());
        
        optionsView.closeFrame();
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
                mainView.setStatusLabel("Unable to open URL: Desktop API is not supported on the current platform");
                return;
            }
            if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                mainView.setStatusLabel("Unable to open URL: Browsing is not supported on this platform");
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

        if (!refreshEntityList(newTabIndex))
            return;
        JSplitPane splitPane = mainView.getSplitPane();
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
        mainView.clearEditingPanelSelections();
        
        JList<Entity> entityList = (JList<Entity>) e.getSource();
        
        if (entityList.getSelectedIndices().length != 1) {
            mainView.enableEditingPanels(false);
            mainView.enableToolTips(false);
            return;
        }
        mainView.enableEditingPanels(true);
        mainView.enableToolTips(model.hasToolTips());
        Entity selectedEntity = entityList.getSelectedValue();
        mainView.updateEntityPanel(
                selectedEntity.toString(),
                selectedEntity.getDescription(),
                selectedEntity.getURL(),
                selectedEntity.getInherits(),
                selectedEntity.getFlags(),
                selectedEntity.hasSize(),
                selectedEntity.getSize(),
                selectedEntity.hasColor(),
                selectedEntity.getColor(),
                selectedEntity.getSprite(),
                selectedEntity.isDecal(),
                selectedEntity.getStudio());
        LinkedHashMap<String[], ArrayList<String[]>> selectedEntityProperties = selectedEntity.getProperties();
        
        if (selectedEntityProperties == null) {
            mainView.updateEntityPropertiesTable(null);
            return;
        }
        mainView.updateEntityPropertiesTable(selectedEntityProperties.keySet().toArray(new String[0][0]));
    }
    
    private void addEntityEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JButton))
            return;
        createEntity();
        refreshEntityList(mainView.getCurrentEntityListTab());
        //mainView.enableFileMenuItems(true);
        mainView.setStatusLabel("Added new Base Class entity");
    }
    
    private void cutEntityEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JButton))
            return;
        System.out.println("cut");
    }
    
    private void deleteEntityEventHandler(ActionEvent e) {
        if (!(e.getSource() instanceof JButton))
            return;
        System.out.println("del");
    }
    
    private void entityPropertiesTableEventHandler(ListSelectionEvent e) {
        if (!(e.getSource() instanceof ListSelectionModel))
            return;
        ListSelectionModel entityPropertiesTableListModel = (ListSelectionModel) e.getSource();
        if (entityPropertiesTableListModel.getValueIsAdjusting() || entityPropertiesTableListModel.getSelectedIndices().length < 1 || !mainView.isEnabledEditingPanels())
            return;
        if (entityPropertiesTableListModel.getSelectedIndices().length != 1) {
            mainView.enablePropertiesEditingPanel(false);
            mainView.toggleEntityPropertiesChoicesPanel(false);
            return;
        }
        mainView.enablePropertiesEditingPanel(true);
        int row = entityPropertiesTableListModel.getSelectedIndices()[0];
        mainView.updateEntityPropertiesEditingPanel(mainView.getSelectedEntityProperty(row));
    }
    
    private void entityPropertiesChoicesTableEventHandler(ListSelectionEvent e) {
        if (!(e.getSource() instanceof DefaultListSelectionModel))
            return;
        if (e.getValueIsAdjusting())
            return;
        System.out.println("asdf");
    }
    
    private void jackCheckBoxEventHandler(ItemEvent e) {
        if (!(e.getSource() instanceof JCheckBox))
            return;
        if (e.getStateChange() != ItemEvent.SELECTED)
            mainView.enableJackFeatures(false);
        else {
            ItemListener[] listeners = mainView.getJackCheckBoxListeners();
        
            for (ItemListener listener : listeners) {
                mainView.removeJackCheckBoxListener(listener);
            }
            int result = mainView.showConfirmDialog(
                    "Saving FGDs with this option will break compatibility with Valve Hammer Editor.",
                    "Enable J.A.C.K. Features",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );
            JCheckBox jackCheckBox = (JCheckBox) e.getSource();
            
            if (result != JOptionPane.OK_OPTION) {
                jackCheckBox.setSelected(false);
                mainView.addJackCheckBoxListener(this::jackCheckBoxEventHandler);
                return;
            }
            jackCheckBox.setSelected(true);
            mainView.addJackCheckBoxListener(this::jackCheckBoxEventHandler);
            mainView.enableJackFeatures(true);
        }
    }
    
    private void parseEntities(boolean reloading) {
        model.clearEntityList();
        mainView.clearEntityListModel();
        mainView.enableToolTips(false);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(model.getFgdFile()))) {
            String line;
            StringBuilder entityProperties = new StringBuilder();
            Matcher entClassMatcher = model.getEntityPattern(0).matcher("");
            Matcher nameMatcher = model.getEntityPattern(1).matcher("");

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
                    
                    if (entClassMatcher.find()) { //If new entity found before syntaxIndex is 0
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
            refreshEntityList(mainView.getCurrentEntityListTab());
            mainView.enableFileMenuItems(true);
            
            if (reloading)
                mainView.setStatusLabel("Reloaded " + model.getFgdFile());
            else
                mainView.setStatusLabel("Loaded " + model.getFgdFile());
        }
        catch (IOException e) {
            model.setFgdFile(null);
            mainView.enableFileMenuItems(false);
            
            if (reloading)
                mainView.setStatusLabel("Failed to reload " + model.getFgdFile());
            else
                mainView.setStatusLabel("Failed to load " + model.getFgdFile());
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
        Matcher keyMatcher = model.getEntityPropertyPattern(0).matcher("");
        
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
            keyType = keyMatcher.group(2).toLowerCase();
            String lastPropertyPart = propertyParts[propertyParts.length - 1];
            boolean foundPropertyBody = false;
            
            if (lastPropertyPart.endsWith("=")) {
                if (i == propertyLines.size() - 1)
                    break;
                propertyParts[propertyParts.length - 1] = lastPropertyPart.substring(0, lastPropertyPart.length() - 1).trim();

                if (keyName.equalsIgnoreCase("spawnflags") && keyType.equals("flags") || keyType.equals("choices"))
                    foundPropertyBody = true;
            }
            String keySmartEditName = null;
            String keyDefaultValue = null;
            String keyDescription = null;
            
            if (propertyParts.length > 1 && !(keyName.equalsIgnoreCase("spawnflags") && !keyType.equals("flags"))) {
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
        String[] propertyParts = propertyLine.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            
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
            
            if (keyMatcher.find())
                return null;
            if (propertyLines.get(i).equals("]"))
                return entityPropertyBody;
            String[] propertyBodyParts = splitPropertyLines(propertyLines.get(i));
            
            if ((keyName.equalsIgnoreCase("spawnflags") && keyType.equals("flags") && propertyBodyParts.length == 3) || (keyType.equals("choices") && propertyBodyParts.length == 2))
                entityPropertyBody.add(propertyBodyParts);
        }
        return entityPropertyBody;
    }
    
    private void createEntity(String entityString, LinkedHashMap<String[], ArrayList<String[]>> entityPropertyMap) {
        Matcher entClassMatcher = model.getEntityPattern(0).matcher(entityString);
        Matcher nameMatcher = model.getEntityPattern(1).matcher(entityString);
        
        if (!entClassMatcher.find() || !nameMatcher.find())
            return;
        
        Entity entity;
        switch (entClassMatcher.group(1)) {
            case "@BaseClass" -> entity = new Entity(Entity.Class.BASECLASS, nameMatcher.group(1).trim());
            case "@SolidClass" -> entity = new Entity(Entity.Class.SOLIDCLASS, nameMatcher.group(1).trim());
            case "@PointClass" -> entity = new Entity(Entity.Class.POINTCLASS, nameMatcher.group(1).trim());
            default -> { return; }
        }
        setEntityDescriptionAndURL(entity, model.getEntityPattern(2).matcher(entityString));
        setEntityInherits(entity, model.getEntityPattern(3).matcher(entityString));
        setEntityFlags(entity, model.getEntityPattern(4).matcher(entityString));
        setEntitySize(entity, model.getEntityPattern(5).matcher(entityString));
        setEntityColor(entity, model.getEntityPattern(6).matcher(entityString));
        setEntitySprite(entity, model.getEntityPattern(7).matcher(entityString));
        setEntityDecal(entity, model.getEntityPattern(8).matcher(entityString));
        setEntityStudio(entity, model.getEntityPattern(9).matcher(entityString));
        
        if (entityPropertyMap != null)
            entity.setProperties(entityPropertyMap);
        model.addEntity(entity);
        entity.printData();
    }
    
    private void createEntity() {
        model.addEntity(new Entity(Entity.Class.BASECLASS, "classname"));
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
    
    private boolean refreshEntityList(int tabIndex) {
        ListSelectionListener[] listeners = mainView.getEntityListListeners();
        
        for (ListSelectionListener listener : listeners)
            mainView.removeEntityListListener(listener);
        switch (tabIndex) {
            case 0 -> mainView.updateEntityListModel(model.getEntityList(Entity.Class.BASECLASS));
            case 1 -> mainView.updateEntityListModel(model.getEntityList(Entity.Class.SOLIDCLASS));
            case 2 -> mainView.updateEntityListModel(model.getEntityList(Entity.Class.POINTCLASS));
            default -> {
                mainView.addEntityListListener(this::entityListEventHandler);
                return false;
            }
        }
        mainView.addEntityListListener(this::entityListEventHandler);
        return true;
    }
}
