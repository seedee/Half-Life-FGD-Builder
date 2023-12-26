/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seedee.fgdbuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author cdani
 */
public class Entity {

    private final EntityType entClass; // ^@.*?Class
    private final String name; // =\s*([^:|\n\]]+)
    private final String description; // :\s*"([^"\[]*)
    private final String url;
    private final String[] inherits; // base\(([^)]*)\)
    private final int[][] size; // size\(\s*([-+]?\d+)\s+([-+]?\d+)\s+([-+]?\d+)\s*,\s*([-+]?\d+)\s+([-+]?\d+)\s+([-+]?\d+)\s*\)
    private final short[] color; // color\(\s*([-+]?\d+)\s+([-+]?\d+)\s+([-+]?\d+)\s*\)
    private final String sprite; // iconsprite\("*([^"]*)"*\)
    private final boolean decal; // decal\(([^)]*)\)
    private final String studio; // studio\("*([^"]*)"*\)
    private final LinkedHashMap<String[], ArrayList<String>> propertyMap;
    
    private Entity(Builder entityBuilder) {
        this.entClass = entityBuilder.entClass;
        this.name = entityBuilder.name;
        this.description = entityBuilder.description;
        this.url = entityBuilder.url;
        this.inherits = entityBuilder.inherits;
        this.size = entityBuilder.size;
        this.color = entityBuilder.color;
        this.sprite = entityBuilder.sprite;
        this.decal = entityBuilder.decal;
        this.studio = entityBuilder.studio;
        this.propertyMap = entityBuilder.propertyMap;
    }
    
    public void printData() {
        System.out.println("Class: " + entClass);
        System.out.println("Name: " + name);
        System.out.println("Description: " + description);
        System.out.println("URL: " + url);
        System.out.println("Inherits: " + Arrays.toString(inherits));
        
        if (size != null)
            System.out.println("Size: " + Arrays.toString(size[0]) + ", " + Arrays.toString(size[1]));
        else
            System.out.println("Size: null");
        if (color != null)
            System.out.println("Color: " + Arrays.toString(color));
        else
            System.out.println("Color: null");
        System.out.println("Sprite: " + sprite);
        System.out.println("Decal: " + decal);
        System.out.println("Studiomodel: " + studio);
        
        if (propertyMap != null) {
            for (Map.Entry<String[], ArrayList<String>> entry : propertyMap.entrySet()) {
                System.out.print("-------- ");
                String[] property = entry.getKey();
                
                for (int i = 0; i < property.length; i++) {
                    System.out.print(property[i]);
                    
                    if (i < property.length - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
                ArrayList<String> propertyBody = entry.getValue();

                if (propertyBody != null) {
                    for (int i = 0; i < propertyBody.size(); i++) {
                        System.out.println("         " + propertyBody.get(i));
                    }
                }
            }
        }
        System.out.println();
    }
    
    public EntityType getEntityClass() {
        return entClass;
    }
    
    public static class Builder {

        private final EntityType entClass;
        private final String name;
        private String description;
        private String url;
        private String[] inherits;
        private int[][] size;
        private short[] color;
        private String sprite;
        private boolean decal;
        private String studio;
        private LinkedHashMap<String[], ArrayList<String>> propertyMap;
        
        public Builder(EntityType entClass, String name) {
            this.entClass = entClass;
            this.name = name;
        }
        
        public Builder setInherits(String[] inherits) {
            this.inherits = inherits;
            return this;
        }
        
        public Builder setSize(int[][] size) {
            this.size = size;
            return this;
        }
        
        public Builder setColor(short[] color) {
            this.color = color;
            return this;
        }
        
        public Builder setSprite(String sprite) {
            this.sprite = sprite;
            return this;
        }
        
        public Builder setDecal(boolean decal) {
            this.decal = decal;
            return this;
        }
        
        public Builder setStudio(String studio) {
            this.studio = studio;
            return this;
        }
        
        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }
        
        public Builder setURL(String url) {
            this.url = url;
            return this;
        }
        
        public Builder setProperties(LinkedHashMap<String[], ArrayList<String>> propertyMap) {
            this.propertyMap = propertyMap;
            return this;
        }
        
        public Entity build() {
            return new Entity(this);
        }
    }
}
