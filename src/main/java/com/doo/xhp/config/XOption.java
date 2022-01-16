package com.doo.xhp.config;

import java.awt.*;

/**
 * 设置选项
 */
public class XOption {

    /**
     * enabled
     */
    public boolean enabled = true;

    /**
     * trigger
     */
    public TriggerEnum trigger = TriggerEnum.FOCUS;

    /**
     * trigger
     */
    public enum TriggerEnum {
        FOCUS("xhp.menu.trigger.focus"),
        DISTANCE("xhp.menu.trigger.distance");

        public final String key;

        TriggerEnum(String key) {
            this.key = key;
        }

        public static TriggerEnum get(Integer index) {
            return values()[index % values().length];
        }
    }

    /**
     * tips
     */
    public boolean tips = true;

    /**
     * tips color
     */
    public int tipsColor = Color.BLUE.getRGB();

    /**
     * tips location x, y
     */
    public int[] tipsLocation = {0, 0};

    /**
     * 名字
     */
    public boolean name = true;

    /**
     * 血量
     */
    public boolean hp = true;

    /**
     * 血量可视化
     */
    public boolean visualization = true;

    /**
     * 血条还是图片
     */
    public StyleEnum style = StyleEnum.BAR;

    /**
     * 样式枚举
     */
    public enum StyleEnum {
        BAR("xhp.menu.style.bar"),
        ICON("xhp.menu.style.icon"),
        FENCE("xhp.menu.style.fence");

        public final String key;

        StyleEnum(String key) {
            this.key = key;
        }

        public static StyleEnum get(Integer index) {
            return values()[index % values().length];
        }
    }

    /**
     * 中立生物血条颜色
     */
    public Integer friendColor = Color.ORANGE.getRGB();

    /**
     * 敌对生物血条颜色
     */
    public Integer mobColor = Color.RED.getRGB();

    /**
     * 伤害颜色
     */
    public Integer damageColor = Color.RED.getRGB();

    /**
     * 暴击伤害颜色
     */
    public Integer criticDamageColor = Color.MAGENTA.getRGB();

    /**
     * 血条长度
     */
    public int barLength = 20;

    /**
     * 血条高度
     */
    public int barHeight = 3;

    /**
     * 伤害
     */
    public boolean damage = true;

    /**
     * 距离
     */
    public int distance = 128;

    /**
     * 缩放比例
     */
    public float scale = 0.02F;

    /**
     * 基础高度
     */
    public int height = 0;

    /**
     * 忽略盔甲架
     */
    public boolean ignoreArmorStandEntity = true;
}