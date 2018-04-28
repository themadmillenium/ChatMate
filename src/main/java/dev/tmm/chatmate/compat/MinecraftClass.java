package dev.tmm.chatmate.compat;

import dev.tmm.chatmate.compat.base.CompatClass;
import dev.tmm.chatmate.compat.base.CompatMethod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MinecraftClass extends CompatClass {
    public static final MinecraftClass instance = new MinecraftClass();

    public static MinecraftInstance getMinecraft() {
        return new MinecraftInstance(instance.call(getMinecraftMethod));
    }

    private static CompatMethod getMinecraftMethod = new CompatMethod(instance, "func_71410_x", "getMinecraft").tryMapping();

    private MinecraftClass() {
        super("net.minecraft.client.Minecraft");
    }
}
