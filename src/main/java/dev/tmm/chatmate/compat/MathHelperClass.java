package dev.tmm.chatmate.compat;

import dev.tmm.chatmate.compat.base.CompatClass;
import dev.tmm.chatmate.compat.base.CompatMethod;

public class MathHelperClass extends CompatClass {
    public static final MathHelperClass instance = new MathHelperClass();

    public static int ceil(float val) {
        return (int) instance.call(ceilMethod, val).getBaseInstance();
    }

    public static int floor(float val) {
        return (int) instance.call(floorMethod, val).getBaseInstance();
    }

    public static double clamp(double val, double min, double max) {
        return (double) instance.call(clampDoubleMethod, val, min, max).getBaseInstance();
    }

    public static int clamp(int val, int min, int max) {
        return (int) instance.call(clampIntMethod, val, min, max).getBaseInstance();
    }

    private static CompatMethod ceilMethod = new CompatMethod(instance, "func_76123_f", "ceiling_float_int", "ceil")
            .tryMapping(CompatUtility.clFloat);

    private static CompatMethod floorMethod = new CompatMethod(instance, "func_76141_d", "floor_float", "floor")
            .tryMapping(CompatUtility.clFloat);

    private static CompatMethod clampDoubleMethod = new CompatMethod(instance, "func_151237_a", "clamp_double", "clamp")
            .tryMapping(CompatUtility.clDouble, CompatUtility.clDouble, CompatUtility.clDouble);

    private static CompatMethod clampIntMethod = new CompatMethod(instance, "func_76125_a", "clamp_int", "clamp")
            .tryMapping(CompatUtility.clInt, CompatUtility.clInt, CompatUtility.clInt);

    private MathHelperClass() {
        super("net.minecraft.util.MathHelper", "net.minecraft.util.math.MathHelper");
    }
}
