package org.cdc.generator.utils.decorators;

import com.google.gson.GsonBuilder;
import net.mcreator.blockly.data.ExternalTrigger;
import net.mcreator.io.FileIO;
import net.mcreator.ui.FileOpener;
import net.mcreator.ui.MCreator;
import org.cdc.generator.elements.TriggerModElement;
import org.cdc.generator.utils.interfaces.ITrigger;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/9/19
 */
public class BlocklyTriggerDecorator implements ITrigger {
    private final ExternalTrigger trigger;
    private final MCreator mcreator;

    public BlocklyTriggerDecorator(ExternalTrigger trigger, MCreator mCreator){
        this.trigger = trigger;
        this.mcreator = mCreator;
    }

    @Override public List<TriggerModElement.Dependency> getDependencies() {
        if (trigger.dependencies_provided != null) {
            return trigger.dependencies_provided.stream().map(a-> new TriggerModElement.Dependency(a.getName(),a.getRawType())).toList();
        }
        return Collections.emptyList();
    }

    @Override public void openPreviewOrEdit() {
        var gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            var tempFile = File.createTempFile("preview", ".json");
            tempFile.deleteOnExit();
            FileIO.writeStringToFile(gson.toJson(trigger), tempFile);
            FileOpener.openFile(mcreator, tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public String getName() {
        return trigger.getName();
    }
}
