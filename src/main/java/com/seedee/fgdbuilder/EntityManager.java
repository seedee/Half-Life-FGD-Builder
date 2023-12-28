/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seedee.fgdbuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.regex.Pattern;

/**
 *
 * @author cdani
 */
public class EntityManager {
    
    private static final Pattern[] entityPatterns;
    private static final Pattern[] entityPropertyPatterns;
    private File fgdFile;
    private final EnumMap<EntityType, ArrayList<Entity>> entityListMap = new EnumMap<>(EntityType.class);
    
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
    
    public EntityManager() {
        for (EntityType entClass : EntityType.values())
            entityListMap.put(entClass, new ArrayList<>());
    }
    
    public void setFgdFile(File fgdFile) {
        this.fgdFile = fgdFile;
    }
    
    public File getFgdFile() {
        return fgdFile;
    }
    
    public ArrayList<Entity> getEntityList(EntityType entClass) {
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
