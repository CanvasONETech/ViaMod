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
        super(Text.literal("ViaMod Compatibility Error"));
        this.parent = parent;
        this.failedMods = failedMods;
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(ButtonWidget.builder(Text.literal("OK"), button -> {
            this.client.setScreen(parent);
        }).dimensions(this.width / 2 - 50, this.height / 2 + 20, 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, "The following mods could not be loaded:", this.width / 2, this.height / 2 - 40, 0xFFFFFF);
        
        int yOffset = this.height / 2 - 20;
        for (String mod : failedMods) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(mod).formatted(Formatting.RED), this.width / 2, yOffset, 0xFFFFFF);
            yOffset += 10;
        }

        super.render(context, mouseX, mouseY, delta);
    }
}
