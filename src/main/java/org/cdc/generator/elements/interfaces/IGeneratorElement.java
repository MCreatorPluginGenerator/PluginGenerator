package org.cdc.generator.elements.interfaces;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface IGeneratorElement {
    @UsedByReflection String getGeneratorName();

    default BufferedImage generateModElementPicture() {
        var generatorName = getGeneratorName();
        var splits = generatorName.split("-");
        var flavor = splits[0];
        var version = splits[1].replace(".", "");
        if ("addon".equals(flavor)) {
            flavor = "bedrock";
        }
        var icon = UIRES.get("16px." + flavor);
        Font font = Theme.current().getFont().deriveFont(12f).deriveFont(Font.BOLD);

        // 先用临时 Graphics 计算文字尺寸
        BufferedImage tempImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImg.createGraphics();
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        int textWidth = fm.stringWidth(version);
        int textHeight = fm.getHeight();
        g2d.dispose();

        // 计算最终图片尺寸
        int width = Math.max(icon.getIconWidth(), textWidth);
        int height = icon.getIconHeight() + textHeight;

        int middle = Math.max(width,height);

        width = middle;
        height = middle;

        // 创建最终图片
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();

        // 绘制图片（居中）
        int iconX = (width - icon.getIconWidth()) / 2;
        g.drawImage(icon.getImage(), iconX, 0, icon.getImageObserver());

        // 绘制文字（居中）
        g.setFont(font);
        g.setColor(Theme.current().getForegroundColor());
        int textX = (width - textWidth) / 2;
        int textY = icon.getIconHeight() + fm.getAscent();
        g.drawString(version, textX, textY);

        g.dispose();
        return result;
    }
}
