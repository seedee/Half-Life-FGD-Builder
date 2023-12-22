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
    
    private File fgdFile;
    private final EnumMap<EntityType, ArrayList<Entity>> entityListMap = new EnumMap<>(EntityType.class);
    private final Pattern[] fgdPatterns;
    
    public EntityManager() {
        for (EntityType entClass : EntityType.values())
            entityListMap.put(entClass, new ArrayList<>());
        
        fgdPatterns = new Pattern[] {
            Pattern.compile("(@.*?Class)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("=\\s*([^:\\n\\[\\]]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile(":(.*?)[\\[\\n]", Pattern.CASE_INSENSITIVE),
            Pattern.compile("base\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("size\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("color\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("iconsprite\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("decal\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("studio\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE),
        };
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
    
    public Pattern getFgdPattern(int i) {
        return fgdPatterns[i];
    }
}
