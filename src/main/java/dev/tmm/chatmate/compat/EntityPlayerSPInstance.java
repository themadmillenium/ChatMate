package dev.tmm.chatmate.compat;

import dev.tmm.chatmate.compat.base.CompatInstance;
import dev.tmm.chatmate.compat.base.CompatMethod;
import dev.tmm.chatmate.compat.base.CompatMethodArgumentTransformer;
import dev.tmm.chatmate.core.SoundRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EntityPlayerSPInstance extends CompatInstance {
    public EntityPlayerSPInstance(CompatInstance instance) {
        super(instance.getBaseInstance());
    }

    public void playSound(String key, float pitch, float volume) {
        call(playSoundMethod, SoundRegistry.getSound_NotNull(key), volume, pitch);
    }

    private static final CompatMethod playSoundMethod = new CompatMethod(CompatUtility.clEntityPlayerSP, "func_184185_a", "func_85030_a", "playSound")
            .tryMapping(CompatUtility.clString, CompatUtility.clFloat, CompatUtility.clFloat)

            .tryMapping(new CompatMethodArgumentTransformer() {
                public Object run(Method method, Object instance, Object... args) throws InvocationTargetException, IllegalAccessException {
                    return method.invoke(instance,
                            CompatUtility.clSoundEvent.newInstance(CompatUtility.ctorSoundEvent,
                                    CompatUtility.clResourceLocation.newInstance(CompatUtility.ctorResourceLocation,
                                            args[0]
                                    ).getBaseInstance()
                            ).getBaseInstance(), args[1], args[2]);
                }
            }, CompatUtility.clSoundEvent, CompatUtility.clFloat, CompatUtility.clFloat);
}