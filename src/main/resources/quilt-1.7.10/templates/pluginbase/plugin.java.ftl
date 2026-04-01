package ${package};

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.JavaPlugin;

public class PluginMain extends JavaPlugin{
    private static final Logger LOG = LogManager.getLogger("Demo Java Plugin");

    public PluginMain(Plugin plugin) {
        super(plugin);

        LOG.info("Demo java plugin was loaded");
    }
}