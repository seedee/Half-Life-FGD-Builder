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

    private EntityType entClass; // ^@.*?Class
    private String name; // =\s*([^:|\n\]]+)
    private String description; // :\s*"([^"\[]*)
    private String url;
    private String[] inherits; // base\(([^)]*)\)
    private String[] flags; // flags\(([^)]*)\)
    private int[][] size; // size\(\s*([-+]?\d+)\s+([-+]?\d+)\s+([-+]?\d+)\s*,\s*([-+]?\d+)\s+([-+]?\d+)\s+([-+]?\d+)\s*\)
    private short[] color; // color\(\s*([-+]?\d+)\s+([-+]?\d+)\s+([-+]?\d+)\s*\)
    private String sprite; // iconsprite\("*([^"]*)"*\)
    private boolean decal; // decal\(([^)]*)\)
    private String studio; // studio\("*([^"]*)"*\)
    private LinkedHashMap<String[], ArrayList<String[]>> propertyMap;
    
    public Entity(EntityType entClass, String name) {
        this.entClass = entClass;
        this.name = name;
    }
    
    public void setClass(EntityType entClass) {
        this.entClass = entClass;
    }
    
    public EntityType getEntityClass() {
        return entClass;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public void setInherits(String[] inherits) {
        this.inherits = inherits;
    }
    
    public void setFlags(String[] flags) {
        this.flags = flags;
    }
    
    public void setSize(int[][] size) {
        this.size = size;
    }
    
    public void setColor(short[] color) {
        this.color = color;
    }
    
    public void setSprite(String sprite) {
        this.sprite = sprite;
    }
    
    public void setDecal(boolean decal) {
        this.decal = decal;
    }
    
    public void setStudio(String studio) {
        this.studio = studio;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setURL(String url) {
        this.url = url;
    }
    
    public void setProperties(LinkedHashMap<String[], ArrayList<String[]>> propertyMap) {
        this.propertyMap = propertyMap;
    }
    
    public void printData() {
        System.out.println("Class: " + entClass);
        System.out.println("Name: " + name);
        System.out.println("Description: " + description);
        System.out.println("URL: " + url);
        System.out.println("Inherits: " + Arrays.toString(inherits));
        System.out.println("Flags: " + Arrays.toString(flags));
        
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
            for (Map.Entry<String[], ArrayList<String[]>> entry : propertyMap.entrySet()) {
                System.out.print("-------- ");
                String[] property = entry.getKey();
                
                for (int i = 0; i < property.length; i++) {
                    System.out.print(property[i]);
                    
                    if (i < property.length - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
                ArrayList<String[]> propertyBody = entry.getValue();

                if (propertyBody != null) {
                    for (int i = 0; i < propertyBody.size(); i++) {
                        System.out.println("         " + Arrays.toString(propertyBody.get(i)));
                    }
                }
            }
        }
        System.out.println();
    }
}
