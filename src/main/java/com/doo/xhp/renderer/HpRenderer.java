package com.doo.xhp.renderer;

import com.doo.xhp.XHP;
import com.doo.xhp.config.XOption;
import com.doo.xhp.util.HpUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.awt.*;

public class HpRenderer {

    private static final Identifier HEART_ID = new Identifier(XHP.ID, "textures/heart/heart.png");
    private static final Identifier YELLOW_HEART_ID = new Identifier(XHP.ID, "textures/heart/yellow_heart.png");
    private static final Identifier EMPTY_HEART_ID = new Identifier(XHP.ID, "textures/heart/empty_heart.png");

    private static final int EMPTY_COLOR = Color.DARK_GRAY.getRGB();

    public static void render(MatrixStack matrixStack, LivingEntity e, Entity camera) {
        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        if (world == null) {
            return;
        }
        // 基本参数
        int id = e.getEntityId();
        float health = e.getHealth();
        float scale = HpUtil.getScale(e.isBaby());
        int y = -HpUtil.getShowY((float) (e.getHeight() - e.getHeightOffset()), e.isBaby());
        long time = world.getTime();
        boolean isFriend = !(e instanceof HostileEntity)
                && !HpUtil.isAttacker(id, camera.getEntityId(), time)
                || e.isTeammate(camera);
        int color = isFriend ? XHP.XOption.friendColor : XHP.XOption.mobColor;
        // 矩阵操作
        matrixStack.push();
        // 缩小倍数
        matrixStack.scale(scale, scale, scale);
        // 始终正对玩家
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-camera.yaw));
        // 翻转
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
        // 画生命值
        boolean isIcon = XOption.StyleEnum.ICON == XHP.XOption.style;
        if (XHP.XOption.hp) {
            drawText(matrixStack, client, y, color, String.format("%.1f", health));
            y = (int) (y - HpUtil.BASE_HEIGHT / (isIcon ? 1 : 2.5));
        }
        // 画伤害
        if (XHP.XOption.damage) {
            HpUtil.get(id).forEach(d -> {
                if (time - d.time > 20) {
                    return;
                }
                DrawableHelper.drawCenteredString(matrixStack, client.textRenderer, String.format("%.1f", d.damage),
                        d.x, -d.y, d.rgb);
            });
        }
        // 画图片
        if (XHP.XOption.visualization) {
            float healScale = Math.min(health / e.getMaxHealth(), 1);
            RenderSystem.enableDepthTest();
            switch (XHP.XOption.style) {
                case BAR:
                    drawBar(matrixStack, y, color, healScale);
                    break;
                case ICON:
                    drawIcon(matrixStack, client, y, isFriend ? YELLOW_HEART_ID : HEART_ID, healScale);
                    break;
                case FENCE:
                    drawFence(matrixStack, client, y, color, healScale);
                default:
            }
            RenderSystem.disableDepthTest();
            y = y - HpUtil.BASE_HEIGHT / 3;
        }
        // 画名字
        if (XHP.XOption.name) {
            drawText(matrixStack, client, y, color, e.getDisplayName().getString());
        }
        // 矩阵操作退栈
        matrixStack.pop();
    }

    /**
     * 画名字
     *
     * @param matrixStack 矩阵
     * @param client 客户端
     * @param y y坐标
     * @param color 颜色
     * @param string 名字
     */
    private static void drawText(MatrixStack matrixStack, MinecraftClient client, int y, int color, String string) {
        matrixStack.push();
        matrixStack.scale(0.5F, 0.5F, 0.5F);
        DrawableHelper.drawCenteredString(matrixStack, client.textRenderer, string, 0, y * 2, color);
        matrixStack.pop();
    }

    /**
     * 画图标
     *
     * @param matrixStack 矩阵
     * @param client      客户端
     * @param y           坐标y
     * @param texture     需要画的icon
     * @param healScale   比例
     */
    private static void drawIcon(MatrixStack matrixStack, MinecraftClient client, int y, Identifier texture, float healScale) {
        matrixStack.push();
        int x = -HpUtil.HEALTH / 2;
        int healWidth = (int) (healScale * HpUtil.HEALTH);
        TextureManager textureManager = client.getTextureManager();
        // 空血槽
        if (healWidth < HpUtil.HEALTH) {
            textureManager.bindTexture(EMPTY_HEART_ID);
            DrawableHelper.drawTexture(matrixStack, x + healWidth, y, healWidth, 0,
                    HpUtil.HEALTH - healWidth, HpUtil.HEALTH, HpUtil.HEALTH, HpUtil.HEALTH);
        }
        // 血槽
        textureManager.bindTexture(texture);
        DrawableHelper.drawTexture(matrixStack, x, y, 0, 0,
                healWidth, HpUtil.HEALTH, HpUtil.HEALTH, HpUtil.HEALTH);
        matrixStack.pop();
    }

    /**
     * 画血条
     *
     * @param matrixStack 矩阵
     * @param y           坐标y
     * @param color       颜色
     * @param healScale   比例
     */
    private static void drawBar(MatrixStack matrixStack, int y, int color, float healScale) {
        matrixStack.push();
        // 总长度
        int len = XHP.XOption.barLength;
        // 当前血量长度
        int healLen = (int) (healScale * len);
        int x1 = -(len / 2);
        int x2 = x1 + healLen;
        int y2 = y - XHP.XOption.barHeight;
        DrawableHelper.fill(matrixStack, x1, y, x2, y2, color);
        if (healLen < len) {
            DrawableHelper.fill(matrixStack, x2, y, x1 + len, y2, EMPTY_COLOR);
        }
        matrixStack.pop();
    }

    /**
     * 画栅栏
     *
     * @param matrixStack 矩阵
     * @param client      客户端
     * @param y           坐标y
     * @param color       颜色
     * @param healScale   比例
     */
    private static void drawFence(MatrixStack matrixStack, MinecraftClient client, int y, int color, float healScale) {
        matrixStack.push();
        matrixStack.scale(0.5F, 0.5F, 0.5F);
        String fenceStr = "||||||||||||||||||||";
        int len = (int) (fenceStr.length() * healScale);
        LiteralText heal = new LiteralText(fenceStr.substring(0, len));
        LiteralText empty = new LiteralText(fenceStr.substring(len));
        heal.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
        empty.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(EMPTY_COLOR)));
        DrawableHelper.drawCenteredText(matrixStack, client.textRenderer, heal.append(empty), 0, y * 2, color);
        matrixStack.pop();
    }
}
