/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seedee.fgdbuilder;

import java.awt.Component;
import javax.swing.JPanel;
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
    
    public TabChangeListener(JTabbedPane tabs, Component component) {
        tabbedPane = tabs;
        swappableComponent = component;
    }
    
@Override
    public void stateChanged(ChangeEvent e) {
        int newTabIndex = tabbedPane.getSelectedIndex();
        if (newTabIndex == 3) {
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
