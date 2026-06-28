package com.byionic.viamod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.stream.Collectors;

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
        
        // FIX: Use Environment Variable passed by GitHub Actions
        String currentMcVersion = System.getenv("MINECRAFT_VERSION");
        if (currentMcVersion == null) {
            // Fallback for local development if env var is missing
            currentMcVersion = "1.20.1"; 
        }

        List<Path> jars;
        try {
            jars = Files.list(viaModsDir)
                    .filter(path -> path.getFileName().toString().endsWith(".jar"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (Path jarPath : jars) {
            try (JarFile jarFile = new JarFile(jarPath.toFile())) {
                ZipEntry entry = jarFile.getEntry("fabric.mod.json");
                if (entry != null) {
                    String jsonContent = new String(jarFile.getInputStream(entry).readAllBytes());
                    if (isModVersionHigher(jsonContent, currentMcVersion)) {
                        String modName = extractModName(jsonContent);
                        failedMods.add(modName != null ? modName : jarPath.getFileName().toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        // TODO: Implement actual JSON parsing and SemVer comparison
        return false; 
    }

    private String extractModName(String json) {
        // TODO: Implement JSON parsing to extract "name" or "id"
        return null;
    }
}
