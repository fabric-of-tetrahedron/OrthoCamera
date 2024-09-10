package com.dimaskama.orthocamera.client.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * ModConfig 类
 * 这个类负责管理模组的配置选项，包括正交相机的各种设置。
 * 它继承自 JsonConfig 类，可能用于将配置保存为 JSON 格式。
 */
public class ModConfig extends JsonConfig {
    /** 最小缩放比例 */
    public static final float MIN_SCALE = 0.01F;
    /** 最大缩放比例 */
    public static final float MAX_SCALE = 10000.0F;

    /** 标记配置是否被修改 */
    private transient boolean dirty;
    /** 上一帧的 X 轴缩放值 */
    private transient float prevScaleX;
    /** 上一帧的 Y 轴缩放值 */
    private transient float prevScaleY;
    /** 上一帧的固定偏航角 */
    private transient float prevFixedYaw;
    /** 上一帧的固定俯仰角 */
    private transient float prevFixedPitch;
    /** 上一个视角模式 */
    private transient Perspective prevPerspective;

    /** 是否启用模组 */
    public boolean enabled = false;
    /** 是否保存启用状态 */
    public boolean save_enabled_state;
    /** X 轴缩放比例 */
    public float scale_x = 3.0F;
    /** Y 轴缩放比例 */
    public float scale_y = 3.0F;
    /** 最小距离 */
    public float min_distance = -1000.0F;
    /** 最大距离 */
    public float max_distance = 1000.0F;
    /** 是否固定视角 */
    public boolean fixed = false;
    /** 固定偏航角 */
    public float fixed_yaw = 0.0F;
    /** 固定俯仰角 */
    public float fixed_pitch = 0.0F;
    /** 固定视角 Y 轴旋转速度 */
    public float fixed_rotate_speed_y = 3.0F;
    /** 固定视角 X 轴旋转速度 */
    public float fixed_rotate_speed_x = 3.0F;
    /** 是否自动切换到第三人称视角 */
    public boolean auto_third_person = true;

    /**
     * 构造函数
     * @param path 配置文件路径
     * @param defaultPath 默认配置文件路径
     */
    public ModConfig(String path, String defaultPath) {
        super(path, defaultPath);
    }

    /**
     * 设置配置是否被修改
     * @param dirty 是否被修改
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * 检查配置是否被修改
     * @return 是否被修改
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * 每帧更新，保存上一帧的值
     */
    public void tick() {
        prevScaleX = scale_x;
        prevScaleY = scale_y;
        prevFixedYaw = fixed_yaw;
        prevFixedPitch = fixed_pitch;
    }

    /**
     * 获取插值后的 X 轴缩放比例
     * @param delta 插值系数
     * @return 插值后的 X 轴缩放比例
     */
    public float getScaleX(float delta) {
        return MathHelper.lerp(delta, prevScaleX, scale_x);
    }

    /**
     * 获取插值后的 Y 轴缩放比例
     * @param delta 插值系数
     * @return 插值后的 Y 轴缩放比例
     */
    public float getScaleY(float delta) {
        return MathHelper.lerp(delta, prevScaleY, scale_y);
    }

    /**
     * 获取插值后的固定偏航角
     * @param delta 插值系数
     * @return 插值后的固定偏航角
     */
    public float getFixedYaw(float delta) {
        return MathHelper.lerpAngleDegrees(delta, prevFixedYaw, fixed_yaw);
    }

    /**
     * 获取插值后的固定俯仰角
     * @param delta 插值系数
     * @return 插值后的固定俯仰角
     */
    public float getFixedPitch(float delta) {
        return MathHelper.lerpAngleDegrees(delta, prevFixedPitch, fixed_pitch);
    }

    /**
     * 设置 X 轴缩放比例
     * @param scale 新的缩放比例
     */
    public void setScaleX(float scale) {
        scale = MathHelper.clamp(scale, MIN_SCALE, MAX_SCALE);
        if (scale != scale_x) {
            scale_x = scale;
            setDirty(true);
        }
    }

    /**
     * 设置 Y 轴缩放比例
     * @param scale 新的缩放比例
     */
    public void setScaleY(float scale) {
        scale = MathHelper.clamp(scale, MIN_SCALE, MAX_SCALE);
        if (scale != scale_y) {
            scale_y = scale;
            setDirty(true);
        }
    }

    /**
     * 设置固定偏航角
     * @param yaw 新的偏航角
     */
    public void setFixedYaw(float yaw) {
        if (yaw < 0) yaw = 360 + yaw;
        yaw = yaw % 360;
        if (yaw != fixed_yaw) {
            fixed_yaw = yaw;
            setDirty(true);
        }
    }

    /**
     * 设置固定俯仰角
     * @param pitch 新的俯仰角
     */
    public void setFixedPitch(float pitch) {
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        if (pitch != fixed_pitch) {
            fixed_pitch = pitch;
            setDirty(true);
        }
    }

    /**
     * 设置是否固定视角
     * @param fixed 是否固定
     */
    public void setFixed(boolean fixed) {
        this.fixed = fixed;
        if (fixed) {
            Entity entity = MinecraftClient.getInstance().getCameraEntity();
            if (entity != null) {
                setFixedYaw(entity.getYaw() + 180);
                prevFixedYaw = fixed_yaw;
                setFixedPitch(entity.getPitch());
                prevFixedPitch = fixed_pitch;
            }
        }
        setDirty(true);
    }

    /**
     * 切换正交相机启用状态
     */
    public void toggle() {
        enabled = !enabled;
        if (auto_third_person) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (enabled) {
                prevPerspective = client.options.getPerspective();
                client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            } else if (prevPerspective != null) {
                client.options.setPerspective(prevPerspective);
            }
        }
        setDirty(true);
    }
}
