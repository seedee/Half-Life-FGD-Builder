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
    private EntityType entClass;
    private String name;
    private String description;
    private String inherits;
    private int[][] size;
    private short[] color;
    private String sprite;
    private String decal;
    private String studio;
    
    private Entity(Builder entityBuilder) {
        entClass = entityBuilder.entClass;
        name = entityBuilder.name;
        description = entityBuilder.description;
        inherits = entityBuilder.inherits;
        size = entityBuilder.size;
        color = entityBuilder.color;
        sprite = entityBuilder.sprite;
        decal = entityBuilder.decal;
        studio = entityBuilder.studio;
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
            entClass = entityClass;
            name = entityName;
        }

        public Builder setInherits(String entityInherits) {
            inherits = entityInherits;
            return this;
        }

        public Builder setSize(int[][] entitySize) {
            size = entitySize;
            return this;
        }

        public Builder setColor(short[] entityColor) {
            color = entityColor;
            return this;
        }

        public Builder setSprite(String entitySprite) {
            sprite = entitySprite;
            return this;
        }

        public Builder setDecal(String entityDecal) {
            decal = entityDecal;
            return this;
        }

        public Builder setStudio(String entityStudio) {
            studio = entityStudio;
            return this;
        }

        public Builder setDescription(String entityDescription) {
            description = entityDescription;
            return this;
        }

        public Entity build() {
            return new Entity(this);
        }
    }
}
