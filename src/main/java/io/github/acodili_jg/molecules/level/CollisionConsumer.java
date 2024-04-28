package io.github.acodili_jg.molecules.level;

import java.util.UUID;
import org.joml.Vector2dc;

@FunctionalInterface
public interface CollisionConsumer {
    void accept(double time, UUID uuid, Vector2dc velocity);
}
