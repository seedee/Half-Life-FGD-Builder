/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seedee.fgdbuilder;

import java.awt.Component;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author cdani
 */
public class TabChangeListener implements ChangeListener {
    
    private JTabbedPane tabbedPane;
    private Component swappableComponent;
    private DefaultListModel<String>[] entityListModel;
    private JList<String> entityList;
    
    public TabChangeListener(JTabbedPane tabs, Component component, DefaultListModel<String>[] listModel, JList<String> list) {
        tabbedPane = tabs;
        swappableComponent = component;
        entityListModel = listModel;
        entityList = list;
    }
    
@Override
    public void stateChanged(ChangeEvent e) {
        int newTabIndex = tabbedPane.getSelectedIndex();
        
        switch (newTabIndex) {
            case 0:
                entityList.setModel(entityListModel[0]);
                break;
            case 1:
                entityList.setModel(entityListModel[1]);
                break;
            case 2:
                entityList.setModel(entityListModel[2]);
                break;
            default:
                return;
        }
        int previousTabIndex = 0;
        
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getComponentAt(i) == swappableComponent)
                previousTabIndex = i;
        }
        if (previousTabIndex != newTabIndex) {
            tabbedPane.setComponentAt(previousTabIndex, null);
            tabbedPane.setComponentAt(newTabIndex, swappableComponent);
            tabbedPane.revalidate();
            tabbedPane.repaint();
        }
    }
}
