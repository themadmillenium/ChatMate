package dev.tmm.chatmate.core;

public class ChatSound {
    private final String sound;
    private final float pitch;
    private final float volume;

    public ChatSound(String sound, float pitch, float volume) {
        this.sound = sound;
        this.pitch = pitch;
        this.volume = volume;
    }

    public String getName() {
        return sound;
    }

    public float getPitch() {
        return pitch;
    }

    public float getVolume() {
        return volume;
    }
}
