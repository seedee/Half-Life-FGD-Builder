/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.seedee.fgdbuilder;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author cdani
 */
public class FGDBuilder {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame("Half-Life FGD Builder");
                mainFrame.createGUI();
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
