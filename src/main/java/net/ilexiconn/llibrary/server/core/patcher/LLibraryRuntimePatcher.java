package net.ilexiconn.llibrary.server.core.patcher;

import net.ilexiconn.llibrary.client.lang.LanguageHandler;
import net.ilexiconn.llibrary.server.asm.InsnPredicate;
import net.ilexiconn.llibrary.server.asm.RuntimePatcher;
import net.ilexiconn.llibrary.server.world.TickRateHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHandSide;

import java.util.List;
import java.util.Map;

public class LLibraryRuntimePatcher extends RuntimePatcher {
    @Override
    public void onInit() {
        this.patchClass(Locale.class)
            .patchMethod("loadLocaleDataFiles", IResourceManager.class, List.class, void.class)
                .apply(Patch.BEFORE, new InsnPredicate.Method(String.class, "format", String.class, Object[].class, String.class), method -> {
                    method.field(GETSTATIC, LanguageHandler.class, "INSTANCE", LanguageHandler.class);
                    method.var(ALOAD, 4).var(ALOAD, 0);
                    method.field(GETFIELD, Locale.class, "properties", Map.class);
                    method.method(INVOKEVIRTUAL, LanguageHandler.class, "addRemoteLocalizations", String.class, Map.class, void.class);
                }).pop();

        this.patchClass(ModelPlayer.class)
            .patchMethod("setRotationAngles", 6, float.class, Entity.class, void.class)
                .apply(Patch.BEFORE, new InsnPredicate.Op().opcode(InsnPredicate.RETURNING), method -> {
                    method.var(ALOAD, 0).var(ALOAD, 7).var(FLOAD, 1, 6);
                    method.method(INVOKESTATIC, LLibraryHooks.class, "setRotationAngles", ModelPlayer.class, Entity.class, 6, float.class, void.class);
                }).pop()
            .patchMethod("render", Entity.class, 6, float.class, void.class)
                .apply(Patch.BEFORE, new InsnPredicate.Op().opcode(InsnPredicate.RETURNING), method -> {
                    method.var(ALOAD, 0, 1).var(FLOAD, 2, 7);
                    method.method(INVOKESTATIC, LLibraryHooks.class, "renderModel", ModelPlayer.class, Entity.class, 6, float.class, void.class);
                }).pop()
            .patchMethod("<init>", float.class, boolean.class, void.class)
                .apply(Patch.BEFORE, new InsnPredicate.Op().opcode(InsnPredicate.RETURNING), method -> {
                    method.var(ALOAD, 0);
                    method.method(INVOKESTATIC, LLibraryHooks.class, "constructModel", ModelPlayer.class, void.class);
                }).pop();

        this.patchClass(RenderPlayer.class)
            .patchMethod("renderLeftArm", AbstractClientPlayer.class, void.class)
                .apply(Patch.REPLACE, method -> {
                    method.var(ALOAD, 0, 1);
                    method.field(GETSTATIC, EnumHandSide.class, "LEFT", EnumHandSide.class);
                    method.method(INVOKESTATIC, LLibraryHooks.class, "renderArm", RenderPlayer.class, AbstractClientPlayer.class, EnumHandSide.class, void.class);
                    method.node(RETURN);
                }).pop()
            .patchMethod("renderRightArm", AbstractClientPlayer.class, void.class)
                .apply(Patch.REPLACE, method -> {
                    method.var(ALOAD, 0, 1);
                    method.field(GETSTATIC, EnumHandSide.class, "RIGHT", EnumHandSide.class);
                    method.method(INVOKESTATIC, LLibraryHooks.class, "renderArm", RenderPlayer.class, AbstractClientPlayer.class, EnumHandSide.class, void.class);
                    method.node(RETURN);
                }).pop()
            .patchMethod("<init>", RenderManager.class, boolean.class, void.class)
                .apply(Patch.BEFORE, new InsnPredicate.Op().opcode(InsnPredicate.RETURNING), method -> {
                    method.var(ALOAD, 0).var(ALOAD, 0).var(ALOAD, 0);
                    method.method(INVOKEVIRTUAL, RenderPlayer.class, "getMainModel", ModelPlayer.class);
                    method.var(ALOAD, 0);
                    method.field(GETFIELD, RenderPlayer.class, "smallArms", boolean.class);
                    method.method(INVOKESTATIC, LLibraryHooks.class, "assignModel", RenderPlayer.class, ModelPlayer.class, boolean.class, ModelPlayer.class);
                    method.field(PUTFIELD, RenderLivingBase.class, "mainModel", ModelBase.class);
                }).pop();

        this.patchClass(MinecraftServer.class)
            .patchMethod("run", void.class)
                .apply(Patch.REPLACE_NODE, new InsnPredicate.Ldc().cst(50L), method -> {
                    method.field(GETSTATIC, TickRateHandler.class, "INSTANCE", TickRateHandler.class);
                    method.method(INVOKEVIRTUAL, TickRateHandler.class, "getTickRate", long.class);
                }).pop();
    }
}
