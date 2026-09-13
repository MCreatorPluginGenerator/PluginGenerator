package org.cdc.generator.utils.builders;

import net.mcreator.ui.init.UIRES;

import javax.swing.*;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/9/13
 */
public class JButtonBuilder {
    private Icon icon;
    private String tooltipText;
    private String text;
    private boolean contentAreaFilled;
    private boolean opaque;

    public JButtonBuilder(){}

    public JButtonBuilder setIconFromUIRE(String identify){
        icon = UIRES.get(identify);
        return this;
    }

    public JButtonBuilder setTooltipText(String tooltipText){
        this.tooltipText = tooltipText;
        return this;
    }

    public JButtonBuilder setText(String text){
        this.text = text;
        return this;
    }

    public JButtonBuilder setContentAreaFilled(boolean b){
        this.contentAreaFilled = b;
        return this;
    }

    public JButtonBuilder setOpaque(boolean b){
        this.opaque = b;
        return this;
    }

    public JButton build(){
        var button = new JButton(text,icon);
        button.setToolTipText(tooltipText);
        button.setContentAreaFilled(contentAreaFilled);
        button.setOpaque(opaque);
        return button;
    }
}
