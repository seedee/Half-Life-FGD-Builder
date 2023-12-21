/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seedee.fgdbuilder;

import java.util.Arrays;

/**
 *
 * @author cdani
 */
public class Entity {

    private EntityType entClass; // @.*?Class
    private String name; // =\s*([^:|\n\]]+)
    private String description; // :(.*?)[\[\n]
    private String[] inherits; // base\(([^)]*)\)
    private int[][] size; // size\(([^)]*)\)
    private short[] color; // color\(([^)]*)\)
    private String sprite; // iconsprite\(([^)]*)\)
    private boolean decal; // decal\(([^)]*)\)
    private String studio; // studio\(([^)]*)\)
    
    private Entity(Builder entityBuilder) {
        this.entClass = entityBuilder.entClass;
        this.name = entityBuilder.name;
        this.description = entityBuilder.description;
        this.inherits = entityBuilder.inherits;
        this.size = entityBuilder.size;
        this.color = entityBuilder.color;
        this.sprite = entityBuilder.sprite;
        this.decal = entityBuilder.decal;
        this.studio = entityBuilder.studio;
    }
    
    public void printData() {
        System.out.println("Class: " + entClass);
        System.out.println("Name: " + name);
        System.out.println("Description: " + description);
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
        System.out.println();
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public EntityType getEntityClass() {
        return entClass;
    }
    
    public static class Builder {
        //Required
        private EntityType entClass;
        private String name;
        
        //Optional
        private String description;
        private String[] inherits;
        private int[][] size;
        private short[] color;
        private String sprite;
        private boolean decal;
        private String studio;
        
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

        public Entity build() {
            return new Entity(this);
        }
    }
}
