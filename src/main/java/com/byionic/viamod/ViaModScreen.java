package com.byionic.viamod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ViaModScreen extends Screen {
    private final Screen parent;
    private final List<String> failedMods;

    public ViaModScreen(Screen parent, List<String> failedMods) {
        super(Text.literal("ViaMod Manager"));
        this.parent = parent;
        this.failedMods = failedMods != null ? failedMods : List.of();
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> {
            if (this.client != null) {
                this.client.setScreen(this.parent);
            }
        }).dimensions(this.width / 2 - 50, this.height / 2 + 40, 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // FIX: 1.20.1 signature only takes DrawContext
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        String title = failedMods.isEmpty() ? "ViaMod Manager" : "Compatibility Error";
        context.drawCenteredTextWithShadow(this.textRenderer, title, this.width / 2, 20, 0xFFFFFF);

        if (failedMods.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer, "No incompatible mods found.", this.width / 2, this.height / 2 - 10, 0x55FF55);
        } else {
            context.drawCenteredTextWithShadow(this.textRenderer, "The following mods could not be loaded:", this.width / 2, this.height / 2 - 40, 0xFF5555);
            int yOffset = this.height / 2 - 20;
            for (String mod : failedMods) {
                context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(mod).formatted(Formatting.RED), this.width / 2, yOffset, 0xFFFFFF);
                yOffset += 12;
            }
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}
