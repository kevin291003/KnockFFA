package de.kevin.knockffa;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

    private final KnockFFA knockFFA;
    private final File configFile;
    private FileConfiguration configuration;

    private KnockFFA getKnockFFA() {
        return knockFFA;
    }

    public ConfigHandler(KnockFFA knockFFA, String config) {
        this.knockFFA = knockFFA;
        configFile = new File(getKnockFFA().getDataFolder(), config);
        createConfig(config);
    }

    private void createConfig(String config) {
        if (!configFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            configFile.getParentFile().mkdirs();
            getKnockFFA().saveResource(config, false);
        }

        configuration = new YamlConfiguration();
        try {
            configuration.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }


}
