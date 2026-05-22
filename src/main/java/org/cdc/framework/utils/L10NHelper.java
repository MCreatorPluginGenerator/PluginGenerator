package org.cdc.framework.utils;

public class L10NHelper {
	public static String getProcedureKey(String procedureName){
		return "blockly.block." + procedureName;
	}

	public static String getTriggerKey(String triggerName){
		return "trigger." + triggerName;
	}

	public static String getWarningKey(String warningKey){
		return "blockly.warning." + warningKey;
	}
}

