/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seedee.fgdbuilder;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.regex.Pattern;

/**
 *
 * @author cdani
 */
public class Model {
    
    private static final Pattern[] entityPatterns;
    private static final Pattern[] entityPropertyPatterns;
    
    private boolean hasToolTips;
    private boolean newHasToolTips;
    private int toolTipsDelay;
    private int newToolTipsDelay;
    
    enum LookAndFeel {
        LIGHT,
        DARK,
    }
    private LookAndFeel lookAndFeel;
    private LookAndFeel newLookAndFeel;
    private Color accentColor;
    private Color newAccentColor;
    
    private File fgdFile;
    private final EnumMap<Entity.Class, ArrayList<Entity>> entityListMap = new EnumMap<>(Entity.Class.class);
    
    static {
        entityPatterns = new Pattern[] {
            Pattern.compile("(^@.*?Class)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("=\\s*([^:\\n\\[\\]]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile(":\\s*\"([^\"\\[]*)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("base\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("flags\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("size\\(\\s*([-+]?\\d+)\\s+([-+]?\\d+)\\s+([-+]?\\d+)\\s*,\\s*([-+]?\\d+)\\s+([-+]?\\d+)\\s+([-+]?\\d+)\\s*\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("color\\(\\s*([-+]?\\d+)\\s+([-+]?\\d+)\\s+([-+]?\\d+)\\s*\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("iconsprite\\(\"*([^\"]*)\"*\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("decal\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("studio\\(\"*([^\"]*)\"*\\)", Pattern.CASE_INSENSITIVE)
        };
        
        entityPropertyPatterns = new Pattern[] {
            Pattern.compile("(.+)\\((.+)\\)", Pattern.CASE_INSENSITIVE)
        };
    }
    
    public Model() {
        loadOptions();
        newHasToolTips = hasToolTips;
        newToolTipsDelay = toolTipsDelay;
        newLookAndFeel = lookAndFeel;
        newAccentColor = accentColor;
        
        for (Entity.Class entClass : Entity.Class.values())
            entityListMap.put(entClass, new ArrayList<>());
    }
    
    public void loadOptions() {
        try (BufferedReader reader = new BufferedReader(new FileReader("options.csv"))) {
            char delimiter = ',';
            String line;

            while ((line = reader.readLine()) != null) {
                String[] options = line.split(String.valueOf(delimiter));
                
                if (options.length != 6) {
                    loadDefaultOptions();
                    return;
                }
                hasToolTips = Boolean.parseBoolean(options[0]);
                toolTipsDelay = Math.max(0, Math.min(5000, Integer.parseInt(options[1])));
                lookAndFeel = LookAndFeel.valueOf(options[2]);
                int red = Math.max(0, Math.min(255, Integer.parseInt(options[3])));
                int green = Math.max(0, Math.min(255, Integer.parseInt(options[4])));
                int blue = Math.max(0, Math.min(255, Integer.parseInt(options[5])));
                accentColor = new Color(red, green, blue);
            }
        }
        catch (IOException | IllegalArgumentException e) {
            loadDefaultOptions();
        }
    }
    
    public void loadDefaultOptions() {
        hasToolTips = true;
        toolTipsDelay = 100;
        lookAndFeel = LookAndFeel.LIGHT;
        accentColor = Color.decode("#2180d9");
    }
    
    public void saveOptions(boolean hasToolTips, int toolTipsDelay, LookAndFeel lookAndFeel, Color accentColor) {
        this.hasToolTips = hasToolTips;
        this.toolTipsDelay = toolTipsDelay;
        this.lookAndFeel = lookAndFeel;
        this.accentColor = accentColor;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("options.csv"))) {
            char delimiter = ',';
            writer.write(String.valueOf(hasToolTips));
            writer.write(delimiter);
            writer.write(String.valueOf(toolTipsDelay));
            writer.write(delimiter);
            writer.write(String.valueOf(lookAndFeel));
            writer.write(delimiter);
            writer.write(String.valueOf(accentColor.getRed()));
            writer.write(delimiter);
            writer.write(String.valueOf(accentColor.getGreen()));
            writer.write(delimiter);
            writer.write(String.valueOf(accentColor.getBlue()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean hasToolTips() {
        return hasToolTips;
    }
    
    public void proposeNewHasToolTips(boolean newHasToolTips) {
        this.newHasToolTips = newHasToolTips;
    }
    
    public boolean getProposedHasToolTips() {
        return newHasToolTips;
    }
    
    public int getToolTipsDelay() {
        return toolTipsDelay;
    }
    
    public void proposeNewToolTipsDelay(int newToolTipsDelay) {
        this.newToolTipsDelay = newToolTipsDelay;
    }
    
    public int getProposedToolTipsDelay() {
        return newToolTipsDelay;
    }
    
    public LookAndFeel getLookAndFeel() {
        return lookAndFeel;
    }
    
    public void proposeNewLookAndFeel(LookAndFeel newLookAndFeel) {
        this.newLookAndFeel = newLookAndFeel;
    }
    
    public LookAndFeel getProposedLookAndFeel() {
        return newLookAndFeel;
    }
    
    public Color getAccentColor() {
        return accentColor;
    }
    
    public void proposeNewAccentColor(Color newAccentColor) {
        this.newAccentColor = newAccentColor;
    }
    
    public Color getProposedAccentColor() {
        return newAccentColor;
    }
    
    public void setFgdFile(File fgdFile) {
        this.fgdFile = fgdFile;
    }
    
    public File getFgdFile() {
        return fgdFile;
    }
    
    public ArrayList<Entity> getEntityList(Entity.Class entClass) {
        return entityListMap.get(entClass);
    }
    
    public void clearEntityList() {
        for (ArrayList<Entity> entityList : entityListMap.values())
            entityList.clear();
    }
    
    public void addEntity(Entity entity) {
        ArrayList<Entity> entityList = entityListMap.get(entity.getEntityClass());
        entityList.add(entity);
    }
    
    public Pattern getEntityPattern(int i) {
        return entityPatterns[i];
    }
    
    public Pattern getEntityPropertyPattern(int i) {
        return entityPropertyPatterns[i];
    }
}
