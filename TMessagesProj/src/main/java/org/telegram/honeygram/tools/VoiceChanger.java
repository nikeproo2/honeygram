package org.telegram.honeygram.tools;

import org.telegram.honeygram.HoneyConfig;

/**
 * VoiceChanger - Real-time audio DSP modulation for voice messages.
 */
public class VoiceChanger {

    public enum Effect {
        NONE(1.0f, 1.0f),
        DEEP_BASS(0.7f, 0.95f),
        CHIPMUNK(1.5f, 1.05f),
        ROBOT(0.85f, 1.0f),
        FAST(1.0f, 1.3f);

        public final float pitch;
        public final float tempo;

        Effect(float pitch, float tempo) {
            this.pitch = pitch;
            this.tempo = tempo;
        }
    }

    public static Effect getCurrentEffect() {
        if (!HoneyConfig.getPrefs().getBoolean(HoneyConfig.KEY_VOICE_CHANGER_ENABLED, false)) {
            return Effect.NONE;
        }
        int effectIndex = HoneyConfig.getPrefs().getInt(HoneyConfig.KEY_VOICE_CHANGER_PITCH, 0);
        Effect[] effects = Effect.values();
        if (effectIndex >= 0 && effectIndex < effects.length) {
            return effects[effectIndex];
        }
        return Effect.NONE;
    }
}
