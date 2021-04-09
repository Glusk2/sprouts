package com.github.glusk2.sprouts.core.comb;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.glusk2.sprouts.core.geom.Polyline;

/**
 * Sprouts Initial Game State.
 * <p>
 * An initial state contains {@code sproutsCount} sprouts equally spread
 * apart on the edge of a circle that's in the center of the
 * {@code gameBounds} rectangle.
 * <p>
 * The graph is connected; all edges are cobweb edges; the graph has a single
 * face.
 * <p>
 * There are at most two vertices with {@code deg(v) = 1}. The remaining
 * vertices are vertices with {@code deg(v) = 2}.
 */
public final class SproutsInitialState implements SproutsGameState {
    /**
     * Scales the minimal {@code gameBound} dimension to pad the virtual
     * circle on which the sprouts are pinned.
     */
    private static final float DIMENSION_SCALE = .6f;
    /** Full circle, in degrees. */
    private static final int FULL_CIRCLE = 360;

    private int sproutsCount;
    private Rectangle gameBounds;

    public SproutsInitialState(
        final int sproutsCount,
        final Rectangle gameBounds
    ) {
        this.sproutsCount = sproutsCount;
        this.gameBounds = gameBounds;
    }

    @Override
    public Set<SproutsEdge> edges() {
        Vector2 center = gameBounds.getCenter(new Vector2());
        float minDimension =
            Math.min(
                gameBounds.getWidth(),
                gameBounds.getHeight()
            );
        float radius = (DIMENSION_SCALE * minDimension) / 2;
        Vector2 v0 = center.cpy().add(radius, 0);
        Vector2 clockPointer = v0.cpy().sub(center);

        Set<SproutsEdge> result = new HashSet<>();

        Vector2[] vertices = new Vector2[sproutsCount];
        for (int i = 0; i < sproutsCount; i++) {
            vertices[i] =
                center.cpy().add(
                    clockPointer.cpy().rotate(
                        1f * i / sproutsCount * FULL_CIRCLE
                    )
                );
        }

        for (int i = 0; i < sproutsCount - 1; i++) {
            SproutsEdge nextEdge = 
                new SproutsEdge(
                    new Polyline.WrappedList(vertices[i], vertices[i + 1]),
                    Color.BLACK, Color.BLACK
            );
            result.add(nextEdge);
            result.add(nextEdge.reversed());
        }
        return result;
    }
}
