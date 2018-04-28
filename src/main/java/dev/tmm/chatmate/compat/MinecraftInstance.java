package dev.tmm.chatmate.compat;

import dev.tmm.chatmate.compat.base.CompatField;
import dev.tmm.chatmate.compat.base.CompatInstance;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MinecraftInstance extends CompatInstance {
    public MinecraftInstance(CompatInstance instance) {
        super(instance.getBaseInstance());
    }

    public EntityPlayerSPInstance getPlayer() {
        return new EntityPlayerSPInstance(this.get(playerField));
    }

    private static final CompatField playerField = new CompatField(MinecraftClass.instance, "field_71439_g", "thePlayer");
}
