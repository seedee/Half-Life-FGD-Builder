/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seedee.fgdbuilder;

/**
 *
 * @author cdani
 */
public class Entity {

    private EntityType entClass; // @.*?Class
    private String name; // =\s*([^:|\n\]]+)
    private String description; // :(.*?)[\[\n]
    private String inherits; // (base)\(([^)]*)\)
    private int[][] size; // (size)\(([^)]*)\)
    private short[] color; // (color)\(([^)]*)\)
    private String sprite; // (iconsprite)\(([^)]*)\)
    private String decal; // (decal)\(([^)]*)\)
    private String studio; // (studio)\(([^)]*)\)

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
        System.out.println("Inherits: " + inherits);
        System.out.println("Size: " + size);
        System.out.println("Color: " + color);
        System.out.println("Sprite: " + sprite);
        System.out.println("Decal: " + decal);
        System.out.println("Studio: " + studio);
        System.out.println();
        
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static class Builder {
        //Required
        private EntityType entClass;
        private String name;
        
        //Optional
        private String description;
        private String inherits;
        private int[][] size;
        private short[] color;
        private String sprite;
        private String decal;
        private String studio;
        
        public Builder(EntityType entityClass, String entityName) {
            this.entClass = entityClass;
            this.name = entityName;
        }

        public Builder setInherits(String entityInherits) {
            this.inherits = entityInherits;
            return this;
        }

        public Builder setSize(int[][] entitySize) {
            this.size = entitySize;
            return this;
        }

        public Builder setColor(short[] entityColor) {
            this.color = entityColor;
            return this;
        }

        public Builder setSprite(String entitySprite) {
            this.sprite = entitySprite;
            return this;
        }

        public Builder setDecal(String entityDecal) {
            this.decal = entityDecal;
            return this;
        }

        public Builder setStudio(String entityStudio) {
            this.studio = entityStudio;
            return this;
        }

        public Builder setDescription(String entityDescription) {
            this.description = entityDescription;
            return this;
        }

        public Entity build() {
            return new Entity(this);
        }
    }
}
