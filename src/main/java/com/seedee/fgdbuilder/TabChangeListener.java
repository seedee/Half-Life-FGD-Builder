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
    
    private Component swappableComponent;
    private DefaultListModel<String>[] entityListModel;
    private JList<String> entityList;
    
    public TabChangeListener(Component component, DefaultListModel<String>[] listModel, JList<String> list) {
        swappableComponent = component;
        entityListModel = listModel;
        entityList = list;
    }
    
@Override
    public void stateChanged(ChangeEvent e) {
        if (!(e.getSource() instanceof JTabbedPane)) {
            return;
        }
        JTabbedPane source = (JTabbedPane) e.getSource();
        
        int newTabIndex = source.getSelectedIndex();
        
        switch (newTabIndex) {
            case 0 -> entityList.setModel(entityListModel[0]);
            case 1 -> entityList.setModel(entityListModel[1]);
            case 2 -> entityList.setModel(entityListModel[2]);
            default -> {
                    return;
            }
        }
        int previousTabIndex = 0;
        
        for (int i = 0; i < source.getTabCount(); i++) {
            if (source.getComponentAt(i) == swappableComponent)
                previousTabIndex = i;
        }
        if (previousTabIndex != newTabIndex) {
            source.setComponentAt(previousTabIndex, null);
            source.setComponentAt(newTabIndex, swappableComponent);
            source.revalidate();
            source.repaint();
        }
    }
}
