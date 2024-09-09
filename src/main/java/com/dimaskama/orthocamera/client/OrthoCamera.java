package com.dimaskama.orthocamera.client;

import com.dimaskama.orthocamera.client.config.ModConfig;
import com.dimaskama.orthocamera.client.config.ModConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;

import static com.dimaskama.orthocamera.client.OrthoCameraKeyBinding.*;

public class OrthoCamera implements ClientModInitializer {
    public static final String MOD_ID = "orthocamera";
    public static final Logger LOGGER = LogManager.getLogger("OrthoCamera");
    public static final ModConfig CONFIG = new ModConfig("config/orthocamera.json", "assets/orthocamera/default_config.json");
    private static final Text ENABLED_TEXT = Text.translatable("orthocamera.enabled");
    private static final Text DISABLED_TEXT = Text.translatable("orthocamera.disabled");
    private static final Text FIXED_TEXT = Text.translatable("orthocamera.fixed");
    private static final Text UNFIXED_TEXT = Text.translatable("orthocamera.unfixed");
    private static final float SCALE_MUL_INTERVAL = 1.1F;

    @Override
    public void onInitializeClient() {
        CONFIG.loadOrCreate();
        CONFIG.enabled &= CONFIG.save_enabled_state;
        OrthoCameraKeyBinding.register();
        ClientTickEvents.START_CLIENT_TICK.register(c -> CONFIG.tick());
        ClientTickEvents.END_CLIENT_TICK.register(this::handleInput);
        ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientStopping);
    }

    private void handleInput(MinecraftClient client) {
        boolean messageSent = false;
        while (TOGGLE_KEY.wasPressed()) {
            CONFIG.toggle();
            client.getMessageHandler().onGameMessage(
                    CONFIG.enabled ? ENABLED_TEXT : DISABLED_TEXT,
                    true
            );
            messageSent = true;
        }
        boolean on = CONFIG.enabled;
        boolean scaleChanged = false;
        while (SCALE_INCREASE_KEY.wasPressed()) {
            if (on) {
                CONFIG.setScaleX(CONFIG.scale_x * SCALE_MUL_INTERVAL);
                CONFIG.setScaleY(CONFIG.scale_y * SCALE_MUL_INTERVAL);
                CONFIG.setDirty(true);
                scaleChanged = true;
            }
        }
        while (SCALE_DECREASE_KEY.wasPressed()) {
            if (on) {
                CONFIG.setScaleX(CONFIG.scale_x / SCALE_MUL_INTERVAL);
                CONFIG.setScaleY(CONFIG.scale_y / SCALE_MUL_INTERVAL);
                CONFIG.setDirty(true);
                scaleChanged = true;
            }
        }
        if (scaleChanged && !messageSent) {
            client.getMessageHandler().onGameMessage(
                    Text.translatable(
                            "orthocamera.scale",
                            String.format("%.1f", CONFIG.scale_x), String.format("%.1f", CONFIG.scale_y)
                    ),
                    true
            );
            messageSent = true;
        }
        boolean fixPressed = false;
        while (FIX_CAMERA_KEY.wasPressed()) {
            fixPressed = true;
            CONFIG.setFixed(!CONFIG.fixed);
        }
        if (!messageSent && fixPressed) {
            client.getMessageHandler().onGameMessage(CONFIG.fixed ? FIXED_TEXT : UNFIXED_TEXT, true);
        }
        if (FIXED_CAMERA_ROTATE_LEFT_KEY.isPressed()) {
            CONFIG.setFixedYaw(CONFIG.fixed_yaw + CONFIG.fixed_rotate_speed_y);
        }
        if (FIXED_CAMERA_ROTATE_RIGHT_KEY.isPressed()) {
            CONFIG.setFixedYaw(CONFIG.fixed_yaw - CONFIG.fixed_rotate_speed_y);
        }
        if (FIXED_CAMERA_ROTATE_UP_KEY.isPressed()) {
            CONFIG.setFixedPitch(CONFIG.fixed_pitch + CONFIG.fixed_rotate_speed_x);
        }
        if (FIXED_CAMERA_ROTATE_DOWN_KEY.isPressed()) {
            CONFIG.setFixedPitch(CONFIG.fixed_pitch - CONFIG.fixed_rotate_speed_x);
        }
        boolean openScreen = false;
        while (OPEN_OPTIONS_KEY.wasPressed()) {
            openScreen = true;
        }
        if (openScreen) {
            client.setScreen(new ModConfigScreen(null));
        }
    }

    private void onClientStopping(MinecraftClient client) {
        if (CONFIG.isDirty()) {
            CONFIG.save();
        }
    }

    public static boolean isEnabled() {
        return CONFIG.enabled;
    }

    public static Matrix4f createOrthoMatrix(float delta, float minScale) {
        MinecraftClient client = MinecraftClient.getInstance();
        float width = Math.max(minScale, CONFIG.getScaleX(delta)
                * client.getWindow().getFramebufferWidth() / client.getWindow().getFramebufferHeight());
        float height = Math.max(minScale, CONFIG.getScaleY(delta));
        return new Matrix4f().setOrtho(
                -width, width,
                -height, height,
                CONFIG.min_distance, CONFIG.max_distance
        );
    }
}
