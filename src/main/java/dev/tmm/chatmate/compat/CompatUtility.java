package dev.tmm.chatmate.compat;

import dev.tmm.chatmate.compat.base.*;
import dev.tmm.chatmate.core.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeVersion;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class CompatUtility {
    public static final CompatClass clString = new CompatClass(String.class);
    public static final CompatClass clDouble = new CompatClass(double.class);
    public static final CompatClass clFloat = new CompatClass(float.class);
    public static final CompatClass clInt = new CompatClass(int.class);
    public static final CompatClass clBoolean = new CompatClass(boolean.class);

    public static final CompatClass clBooleanWrapper = new CompatClass(Boolean.class);

    public static final CompatClass clMinecraft = new CompatClass("net.minecraft.client.Minecraft");

    public static final CompatClass clFontRenderer = new CompatClass("net.minecraft.client.gui.FontRenderer");
    public static final CompatClass clEntityPlayerSP = new CompatClass("net.minecraft.client.entity.EntityPlayerSP");

    public static final CompatClass clResourceLocation = new CompatClass("net.minecraft.util.ResourceLocation");
    public static final CompatConstructor ctorResourceLocation = new CompatConstructor(clResourceLocation).tryMapping(clString);

    public static final CompatClass clSoundEvent = new CompatClass("net.minecraft.util.SoundEvent");
    public static final CompatConstructor ctorSoundEvent = new CompatConstructor(clSoundEvent).tryMapping(clResourceLocation);

    public static final CompatClass clIChatComponent = new CompatClass("net.minecraft.util.IChatComponent", "net.minecraft.util.text.ITextComponent");

    public static final CompatClass clChatLine = new CompatClass("net.minecraft.client.gui.ChatLine");
    public static final CompatConstructor ctorChatLine = new CompatConstructor(clChatLine).tryMapping(clInt, clIChatComponent, clInt);

    public static final CompatClass clChatComponentText = new CompatClass("net.minecraft.util.text.TextComponentString", "net.minecraft.util.ChatComponentText");
    public static final CompatConstructor ctorChatComponentText = new CompatConstructor(clChatComponentText).tryMapping(clString);

    public static final CompatClass clStyle = new CompatClass("net.minecraft.util.ChatStyle", "net.minecraft.util.text.Style");
    public static final CompatConstructor ctorStyle = new CompatConstructor(clStyle).tryMapping();

    public static final CompatClass clChatColor = new CompatClass("net.minecraft.util.EnumChatFormatting", "net.minecraft.util.text.TextFormatting");

    public static final CompatClass clScaledResolution = new CompatClass("net.minecraft.client.gui.ScaledResolution");
    public static final CompatConstructor ctorScaledResolution = new CompatConstructor(clScaledResolution).tryMapping(new CompatConstructorArgumentTransformer() {
        public Object newInstance(Constructor constructor, Object... args) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            return constructor.newInstance(args[0]);
        }
    }, clMinecraft).tryMapping(clMinecraft, clInt, clInt);

    private static final CompatField versionField = new CompatField(clMinecraft, "launchedVersion", "field_110447_Z");

    public static MinecraftVersion getVersion(Minecraft mc) {
        String s = (String) versionField.get(mc);

        System.out.println(s);

        if (s.startsWith("1.8-")) {
            return MinecraftVersion.MC_1_8;
        } else if (s.startsWith("1.8.8-")) {
            return MinecraftVersion.MC_1_8_8;
        } else if (s.startsWith("1.8.9-")) {
            return MinecraftVersion.MC_1_8_9;
        } else if (s.startsWith("1.9-")) {
            return MinecraftVersion.MC_1_9;
        } else if (s.startsWith("1.9.4-")) {
            return MinecraftVersion.MC_1_9_4;
        } else if (s.startsWith("1.10-")) {
            return MinecraftVersion.MC_1_10;
        } else if (s.startsWith("1.10.2-")) {
            return MinecraftVersion.MC_1_10_2;
        } else if (s.startsWith("1.11-")) {
            return MinecraftVersion.MC_1_11;
        } else {
            return MinecraftVersion.UNSUPPORTED;
        }
    }

    public static void loadSounds(Minecraft mc) {
        switch (getVersion(mc)) {
            case MC_1_8:
            case MC_1_8_8:
            case MC_1_8_9:
                SoundRegistry.loadSounds_1_8();
                break;
            case MC_1_9:
            case MC_1_9_4:
                SoundRegistry.loadSounds_1_9();
                break;
            case MC_1_10:
            case MC_1_10_2:
                SoundRegistry.loadSounds_1_10();
                break;
            case MC_1_11:
                SoundRegistry.loadSounds_1_11();
                break;
        }
    }

    public static void throwWarning() {
        throw new UnsupportedOperationException("This field should not be set!");
    }
}
