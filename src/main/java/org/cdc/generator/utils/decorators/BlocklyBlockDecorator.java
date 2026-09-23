package org.cdc.generator.utils.decorators;

import com.google.gson.GsonBuilder;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.io.FileIO;
import net.mcreator.ui.FileOpener;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import org.cdc.generator.elements.PluginProcedureModElement;
import org.cdc.generator.utils.interfaces.IProcedureBlock;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/9/19
 */
public class BlocklyBlockDecorator implements IProcedureBlock {
    private final ToolboxBlock toolboxBlock;
    private final MCreator mcreator;
    private final BlocklyEditorType blocklyEditorType;

    public BlocklyBlockDecorator(@NotNull ToolboxBlock toolboxBlock, MCreator mCreator,
            BlocklyEditorType blocklyEditorType) {
        this.toolboxBlock = toolboxBlock;
        this.mcreator = mCreator;
        this.blocklyEditorType = blocklyEditorType;
    }

    @Override public List<String> getInputs() {
        var test = toolboxBlock.getInputs();
        if (test == null) {
            return Collections.emptyList();
        }
        return test;
    }

    @Override public List<String> getFields() {
        var test = toolboxBlock.getFields();
        if (test == null) {
            return Collections.emptyList();
        }
        return test;
    }

    @Override public List<String> getStatements() {
        var test = toolboxBlock.getStatements();
        if (test == null) {
            return Collections.emptyList();
        }
        return test.stream().map(a -> a.name).toList();
    }

    @Override public List<PluginProcedureModElement.Dependency> getDependencies() {
        var test = toolboxBlock.getDependencies();
        if (test == null) {
            return Collections.emptyList();
        }
        return test.stream().map(a -> new PluginProcedureModElement.Dependency(a.name(), a.type())).toList();
    }

    @Override public void openPreviewOrEdit() {
        try {
            var tempFile = File.createTempFile("preview", ".json");
            tempFile.deleteOnExit();
            var gson = new GsonBuilder().setPrettyPrinting().create();
            FileIO.writeStringToFile(gson.toJson(toolboxBlock.getBlocklyJSON()), tempFile);
            FileOpener.openFile(mcreator, tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public String getName() {
        return toolboxBlock.getMachineName();
    }

    @Override public String getParentFolder() {
        return blocklyEditorType.registryName();
    }
}
