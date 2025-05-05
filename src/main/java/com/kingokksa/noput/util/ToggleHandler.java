package com.kingokksa.noput.util;

import net.minecraftforge.fml.loading.FMLPaths;
import java.nio.file.Files;
import java.nio.file.Path;

public class ToggleHandler {
    private static boolean enabled = true;
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("nojbput_state.txt");

    static {
        try {
            if (Files.exists(CONFIG_PATH)) {
                enabled = Boolean.parseBoolean(Files.readString(CONFIG_PATH));
            }
        } catch (Exception e) {
            enabled = true;
        }
    }

    public static void toggle() {
        enabled = !enabled;
        try {
            Files.writeString(CONFIG_PATH, Boolean.toString(enabled));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        ToggleHandler.enabled = enabled;
        try {
            Files.writeString(CONFIG_PATH, Boolean.toString(enabled));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}