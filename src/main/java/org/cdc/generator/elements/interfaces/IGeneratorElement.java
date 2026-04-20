package org.cdc.generator.elements.interfaces;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public interface IGeneratorElement {
    @UsedByReflection String getGeneratorName();

    default BufferedImage generateModElementPicture0() {
        var generatorName = getGeneratorName();
        var splits = generatorName.split("-");
        var flavor = splits[0];
        var version = splits[1].replace(".", "");
        if ("addon".equals(flavor)) {
            flavor = "bedrock";
        }
        var image = UIRES.get("16px." + flavor).getImage();
        var icon = new ImageIcon(image.getScaledInstance(48,48,Image.SCALE_SMOOTH));
        Font font = Theme.current().getConsoleFont().deriveFont(24f);

        // 先用临时 Graphics 计算文字尺寸
        BufferedImage tempImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImg.createGraphics();
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        int textWidth = fm.stringWidth(version);
        g2d.dispose();

        int width = 64;
        int height = 64;

        // 创建最终图片
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();

        // 绘制图片（居中）
        int iconX = (width - icon.getIconWidth()) / 2;
        g.drawImage(icon.getImage(), iconX, 0, icon.getImageObserver());

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 绘制文字（居中）
        g.setFont(font);
        g.setColor(Theme.current().getForegroundColor());
        int textX = (width - textWidth) / 2;
        int textY = icon.getIconHeight() + fm.getAscent() - 10;
        g.drawString(version, textX, textY);

        g.dispose();
        return result;
    }
}
