package org.cdc.js;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class JavaScriptBridge {
    public void setClipboard(String text) {
        System.out.println(text);
        StringSelection stringSelection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
    }

    public static String beautifyXml(String xml) {
        return xml.replaceAll("xmlns=\"https:(.+?)\" ","");
    }
}
