package com.byionic.viamod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.version.VersionInterval;
import net.fabricmc.loader.impl.util.VersionUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class ViaModClient implements ClientModInitializer {
    private static boolean hasShownError = false;

    @Override
    public void onInitializeClient() {
        Path gameDir = FabricLoader.getInstance().getGameDir();
        Path viaModsDir = gameDir.resolve("viamods");

        if (!Files.exists(viaModsDir)) {
            try {
                Files.createDirectory(viaModsDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        List<String> failedMods = new ArrayList<>();
        String currentMcVersion = FabricLoader.getInstance().getGameVersion().getFriendlyString();

        File[] jars = viaModsDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars != null) {
            for (File jar : jars) {
                try (JarFile jarFile = new JarFile(jar)) {
                    ZipEntry entry = jarFile.getEntry("fabric.mod.json");
                    if (entry != null) {
                        String jsonContent = new String(jarFile.getInputStream(entry).readAllBytes());
                        // Simple parsing for 'depends' -> 'minecraft' version range
                        // In a real implementation, use a JSON library like Gson
                        if (isModVersionHigher(jsonContent, currentMcVersion)) {
                            String modName = extractModName(jsonContent);
                            failedMods.add(modName != null ? modName : jar.getName());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (!failedMods.isEmpty() && !hasShownError) {
            hasShownError = true;
            MinecraftClient client = MinecraftClient.getInstance();
            client.execute(() -> {
                Screen errorScreen = new ViaModScreen(client.currentScreen, failedMods);
                client.setScreen(errorScreen);
            });
        }
    }

    private boolean isModVersionHigher(String json, String currentVersion) {
        // Simplified logic: Check if the mod requires a version strictly greater than current
        // Real implementation requires parsing SemVer ranges from fabric.mod.json
        // This is a placeholder for the actual version comparison logic
        return false; 
    }

    private String extractModName(String json) {
        // Simplified extraction
        return null;
    }
}
