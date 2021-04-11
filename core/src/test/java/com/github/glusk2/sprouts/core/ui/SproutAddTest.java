package com.github.glusk2.sprouts.core.ui;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.glusk2.sprouts.core.comb.PresetVertex;
import com.github.glusk2.sprouts.core.comb.SproutsEdge;
import com.github.glusk2.sprouts.core.comb.SproutsGameState;
import com.github.glusk2.sprouts.core.comb.Vertex;
import com.github.glusk2.sprouts.core.geom.Polyline;

import org.junit.Test;

/** A test class for SproutAdd. */
public class SproutAddTest {
    /**
     * Checks if the middle sprout, placed on the intersection between the new
     * move and existing cobweb edge, is added to the combinatorial
     * representation correctly.
     * <p>
     * This is just a simple, provisionary test. There are 2 sprouts,
     * connected by a straight line cobweb segment. A move that crosses the
     * cobweb edge must split it (in other words, remove the original
     * cobweb edge).
     * <p>
     * Middle sprout must not be added too close to the cobweb intersection
     * point. Because of that, the updated state will have an additional pair
     * of directed edges.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void correctlyPlacesNewSproutOnCobwebMoveIntersection() {
        Vertex v1 = new PresetVertex(new Vector2(-50, 0));
        Vertex v2 = new PresetVertex(new Vector2(50, 0));

        SproutsEdge e1 = new SproutsEdge(
            new Polyline.WrappedList(v1.position(), v2.position()),
            v1.color(), v2.color()
        );

        SproutsGameState nextState = new SproutAdd(
            () -> new HashSet<>(Arrays.asList(e1, e1.reversed())),
            10,
            16,
            v1,
            Arrays.asList(
                v1.position(),
                new Vector2(-20, 20),
                new Vector2(0, 40),
                new Vector2(20, 1),
                new Vector2(0, -40),
                new Vector2(-20, -20),
                v1.position()
            ),
            new Rectangle(-100, -100, 1000, 1000)
        ).touchUp(new Vector2(10, 0)).gameState();

        assertThat(
            nextState.edges().size(),
            is(8)
        );
        assertThat(
            nextState.edges(),
            not(hasItems(e1, e1.reversed()))
        );
    }
}
