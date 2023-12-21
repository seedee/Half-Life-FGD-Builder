/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seedee.fgdbuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 *
 * @author cdani
 */
public class EntityManager {
    
    private File fgdFile;
    private ArrayList<Entity>[] entityList = new ArrayList[3];
    private Pattern[] fgdPatterns;
    
    public EntityManager() {
        for (int i = 0; i < entityList.length; i++)
            entityList[i] = new ArrayList<>();
        
        fgdPatterns = new Pattern[] {
            //Pattern.compile("@.*?Class", Pattern.CASE_INSENSITIVE),
            Pattern.compile("=\\s*([^:\\n\\[\\]]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile(":(.*?)[\\[\\n]", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(base)\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(size)\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(color)\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(iconsprite)\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(decal)\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(studio)\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE),
        };
    }
    
    public void setFgdFile(File fgdFile) {
        this.fgdFile = fgdFile;
    }
    
    public File getFgdFile() {
        return fgdFile;
    }
    
    public ArrayList<Entity> getEntityList(EntityType entClass) {
        return entityList[entClass.ordinal()]; //Rewrite later to not use ordinal
    }
    
    public void clearEntityList() {
        for (int i = 0; i < entityList.length; i++)
            entityList[i].clear();
    }
    
    public Pattern getFgdPattern(int i) {
        return fgdPatterns[i];
    }
}
