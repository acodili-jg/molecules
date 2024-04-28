package io.github.acodili_jg.molecules.level;

import io.github.acodili_jg.molecules.molecule.MoleculeRef;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public interface Level {
    void addMolecule(final MoleculeRef molecule);

    Set<? extends MoleculeRef> moleculeSet();

    double peekCollisionTime();

    default boolean hasNextCollisionBefore(final double time) {
        return time >= this.peekCollisionTime();
    }

    boolean consumeNextSimultaneousCollisions(CollisionConsumer action);

    static double calculateCollisionTime(final MoleculeRef lhs, final MoleculeRef rhs) {
        final var p1 = lhs.position();
        final var p2 = rhs.position();
        final var v1 = lhs.velocity();
        final var v2 = rhs.velocity();
        final var r1 = lhs.radius();
        final var r2 = rhs.radius();

        // This problem is solvable by transforming a two moving circles to a
        // moving point to a static circle problem.

        // The LHS will be *the* point and RHS circle will be *the* circle.
        // Translation is applied such that RHS is centered at the origin.
        final var px = p1.x() - p2.x();
        final var py = p1.y() - p2.y();

        // *The* circle is static, consider the particle's, perspective by
        // negating out it's velocity on both objects.
        final var vx = v1.x() - v2.x();
        final var vy = v1.y() - v2.y();

        // *The* point has no radius, collect both radii to *the* circle.
        final var r = r1 + r2;

        return collisionTimeOfParticleToCircle(px, py, vx, vy, r);
    }

    private static double collisionTimeOfParticleToCircle(
        // Position of the particle relative to the circle
        final double px,
        final double py,
        // Relative velocity of the particle to the circle
        final double vx,
        final double vy,
        // The radius of the circle
        final double r
    ) {
        // A circle centered at the origin can be described using:
        //     x^2 + y^2 = r^2
        // Where x and y can be substitued with the particle's next position:
        //     x = x_i + ut
        //     y = y_i + ut
        // The final formula would be:
        //     (u^2 + v^2) t^2 + (2 u x_i + 2 v y_i) t + (x_i^2 + y_i^2 - r^2) = 0
        final var a = vx * vx + vy * vy;
        final var b = 2 * (px * vx + py * vy);
        final var c = px * px + py * py - r * r;

        // To find the time t, rearrange the terms to create a quadtratic
        // equation in the form:
        //     a x^2 + b x + c
        // Where x is replaced with t. Substitute into the quadratic formula.
        final var t1 = quadraticFormula(a, b, c, false);
        final var t2 = quadraticFormula(a, b, c, true);
        final var t = Math.min(t1, t2);

        if (t >= 0.0) {
            // If both t_1 and t_2 is positive, the particle will collide
            // The least time is as if the particle entered the circle, the
            // greater time is as if the particle exited the circle.
            return t;
        } else {
            // If either t_1 or t_2 is nonpositive, the particle is coinciding
            // If both t_1 and t_2 are nonpositive, the particle wont collide
            return Double.POSITIVE_INFINITY;
        }
    }

    private static double quadraticFormula(
        // The coefficients
        final double a,
        final double b,
        final double c,
        // The sign of the root discriminant, `false` for positive and `true`
        // for negative
        final boolean k
    ) {
        if (k) {
            // \frac{-b - \sqrt{b^2 - 4ac}}{2a}
            return (-b - Math.sqrt(b * b - 4 * a * c)) / 2 / a;
        } else {
            // \frac{-b + \sqrt{b^2 - 4ac}}{2a}
            return (-b + Math.sqrt(b * b - 4 * a * c)) / 2 / a;
        }
    }
}
