package com.dimaskama.orthocamera.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class OrthoCameraKeyBinding {
    public static final KeyBinding TOGGLE_KEY = createKeybinding("toggle", GLFW.GLFW_KEY_Z);
    public static final KeyBinding SCALE_INCREASE_KEY = createKeybinding("scale_increase", GLFW.GLFW_KEY_EQUAL);
    public static final KeyBinding SCALE_DECREASE_KEY = createKeybinding("scale_decrease", GLFW.GLFW_KEY_MINUS);
    public static final KeyBinding OPEN_OPTIONS_KEY = createKeybinding("options", GLFW.GLFW_KEY_RIGHT_BRACKET);
    public static final KeyBinding FIX_CAMERA_KEY = createKeybinding("fix_camera", GLFW.GLFW_KEY_LEFT_BRACKET);
    public static final KeyBinding FIXED_CAMERA_ROTATE_UP_KEY = createKeybinding("fixed_camera_rotate_up", GLFW.GLFW_KEY_KP_8);
    public static final KeyBinding FIXED_CAMERA_ROTATE_DOWN_KEY = createKeybinding("fixed_camera_rotate_down", GLFW.GLFW_KEY_KP_2);
    public static final KeyBinding FIXED_CAMERA_ROTATE_LEFT_KEY = createKeybinding("fixed_camera_rotate_left", GLFW.GLFW_KEY_KP_4);
    public static final KeyBinding FIXED_CAMERA_ROTATE_RIGHT_KEY = createKeybinding("fixed_camera_rotate_right", GLFW.GLFW_KEY_KP_6);

    public static void register() {
        KeyBindingHelper.registerKeyBinding(TOGGLE_KEY);
        KeyBindingHelper.registerKeyBinding(SCALE_INCREASE_KEY);
        KeyBindingHelper.registerKeyBinding(SCALE_DECREASE_KEY);
        KeyBindingHelper.registerKeyBinding(OPEN_OPTIONS_KEY);
        KeyBindingHelper.registerKeyBinding(FIX_CAMERA_KEY);
        KeyBindingHelper.registerKeyBinding(FIXED_CAMERA_ROTATE_UP_KEY);
        KeyBindingHelper.registerKeyBinding(FIXED_CAMERA_ROTATE_DOWN_KEY);
        KeyBindingHelper.registerKeyBinding(FIXED_CAMERA_ROTATE_LEFT_KEY);
        KeyBindingHelper.registerKeyBinding(FIXED_CAMERA_ROTATE_RIGHT_KEY);
    }

    private static KeyBinding createKeybinding(String name, int key) {
        return new KeyBinding(
                "orthocamera.key." + name,
                InputUtil.Type.KEYSYM,
                key,
                OrthoCamera.MOD_ID
        );
    }
}
