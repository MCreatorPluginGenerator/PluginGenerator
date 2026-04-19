package org.cdc.generator.elements.interfaces;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.ui.init.UIRES;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface IGeneratorElement {
    @UsedByReflection String getGeneratorName();

    default BufferedImage generateModElementPicture() {
        var generatorName = getGeneratorName();
        var flavor = generatorName.split("-")[0];
        if ("addon".equals(flavor)) {
            flavor = "bedrock";
        }
        var icon = UIRES.get("16px." + flavor);

        BufferedImage result = new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR);
        result.getGraphics().drawImage(icon.getImage(), 0, 0, icon.getImageObserver());
        return result;
    }
}
