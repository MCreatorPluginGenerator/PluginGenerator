package org.cdc.js;

import com.google.j2objc.annotations.UsedByReflection;
import org.cdc.generator.utils.ElementsUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class JavaScriptBridge {
    @UsedByReflection public void setClipboard(String text) {
        System.out.println(text);
        StringSelection stringSelection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
    }

    @UsedByReflection public String beautifyXml(String xml) {
        return xml.replaceAll("xmlns=\"(https|http):(.+?)\" ", "");
    }

    @UsedByReflection
    public void uploadBlockNameAndCopyColor(String type){
        setClipboard(ElementsUtils.getExternalBlockColour(type, null));
    }

}
