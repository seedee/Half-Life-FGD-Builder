/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seedee.fgdbuilder;

import com.bric.colorpicker.ColorPicker;
import com.bric.colorpicker.ColorPickerMode;
import com.bric.colorpicker.listeners.ColorListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author cdani
 */
public class OptionsView {
    
    private static OptionsView instance;
    private static final String FRAME_TITLE = "Options";
    private final JFrame optionsFrame = new JFrame(FRAME_TITLE);
    
    private final JCheckBox toolTipsCheckBox = new JCheckBox("Enabled");
    private final JLabel toolTipsDelayLabel = new JLabel("Delay (ms):");
    private final JSpinner toolTipsDelaySpinner = new JSpinner(new SpinnerNumberModel(100, 0, 5000, 1));
    
    private final JRadioButton[] themeRadioButtons = {
        new JRadioButton("Light"),
        new JRadioButton("Dark"),
    };
    
    private final ColorPicker accentColorPicker = new ColorPicker(true, false);
    
    private final JButton okButton = new JButton("OK");
    private final JButton cancelButton = new JButton("Cancel");
    
    private OptionsView(int[] offset) {
        optionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        optionsFrame.setTitle(FRAME_TITLE);
        optionsFrame.setPreferredSize(new Dimension(410, 370));
        optionsFrame.setResizable(false);
        optionsFrame.setAlwaysOnTop(true);
        optionsFrame.setLayout(new MigLayout("insets 0, gap 0, wrap 1, ltr", "[fill, grow]", "[fill, grow][]"));
        
        optionsFrame.add(createOptionsPanel());
        optionsFrame.add(createButtonsPanel(), "right");
        
        optionsFrame.pack();
        centerFrame(offset);
        optionsFrame.validate();
        optionsFrame.repaint();
        optionsFrame.setVisible(true);
    }
    
    public static OptionsView getInstance(int[] offset) {
        if (instance == null) {
            instance = new OptionsView(offset);
        }
        return instance;
    }
    
    public static void setInstance(OptionsView fgdBuilderOptions) {
        instance = fgdBuilderOptions;
    }
    
    public void addWindowListener(WindowAdapter adapter) {
        optionsFrame.addWindowListener(adapter);
    }
    
    public void addToolTipsListener(ItemListener listener) {
        toolTipsCheckBox.addItemListener(listener);
    }
    
    public void addToolTipsDelayListener(ChangeListener listener) {
        toolTipsDelaySpinner.addChangeListener(listener);
    }
    
    public void addThemeListener(ItemListener listener) {
        for (JRadioButton themeRadioButton : themeRadioButtons)
            themeRadioButton.addItemListener(listener);
    }
    
    public void addColorListener(ColorListener listener) {
        accentColorPicker.addColorListener(listener);
    }
    
    public void addOKListener(ActionListener listener) {
        okButton.addActionListener(listener);
    }
    
    public void addCancelListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }
    
    public final void centerFrame(int[] offset) {
        offset[0] -= optionsFrame.getWidth() / 2;
        offset[1] -= optionsFrame.getHeight() / 2;
        optionsFrame.setLocation(offset[0], offset[1]);
        optionsFrame.requestFocus();
    }
    
    public void closeFrame() {
        optionsFrame.dispatchEvent(new WindowEvent(optionsFrame, WindowEvent.WINDOW_CLOSING));
    }
    
    public void setToolTipsCheckBox(boolean hasToolTips) {
        toolTipsCheckBox.setSelected(hasToolTips);
    }
    
    public void setToolTipsDelaySpinner(int toolTipsDelay) {
        toolTipsDelaySpinner.setValue(toolTipsDelay);
    }
    
    public void enableToolTipsDelay(boolean enabled) {
        toolTipsDelayLabel.setEnabled(enabled);
        toolTipsDelaySpinner.setEnabled(enabled);
    }
    
    public void setThemeRadioButton(Model.LookAndFeel lookAndFeel) {
        switch(lookAndFeel) {
            case DARK -> themeRadioButtons[1].setSelected(true);
            default -> themeRadioButtons[0].setSelected(true);
        }
    }
    
    public void setAccentColor(Color accentColor) {
        accentColorPicker.setColor(accentColor);
    }
    
    private JPanel createOptionsPanel() {
        JPanel optionsPanel = new JPanel(new MigLayout("insets 5pt 5pt 0 5pt, ltr, wrap 2", "[right][grow]", "[][][grow, fill]"));
        
        optionsPanel.add(new JLabel("Tooltips:"));
        optionsPanel.add(toolTipsCheckBox, "split 3");
        optionsPanel.add(toolTipsDelayLabel);
        optionsPanel.add(toolTipsDelaySpinner);
        
        optionsPanel.add(new JLabel("Theme:"));
        ButtonGroup themeRadioButtonGroup = new ButtonGroup();
        themeRadioButtonGroup.add(themeRadioButtons[0]);
        themeRadioButtonGroup.add(themeRadioButtons[1]);
        optionsPanel.add(themeRadioButtons[0], "split 2");
        optionsPanel.add(themeRadioButtons[1]);
        
        accentColorPicker.setPreviewSwatchVisible(false);
        accentColorPicker.setMode(ColorPickerMode.BRIGHTNESS);
        accentColorPicker.setModeControlsVisible(false);
        optionsPanel.add(accentColorPicker, "span, left");
        return optionsPanel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new MigLayout("ltr, wrap 2", "[grow, right][]"));
        
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        
        return buttonsPanel;
    }
}
