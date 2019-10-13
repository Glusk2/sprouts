package com.github.glusk2.sprouts.core.comb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import org.junit.Test;

/** Test class for {@code PresetRotations}. */
public final class PresetRotationsTest {
    /** Checks the ordering of the DirectedEdges produced. */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void checksClockwiseEdgeOrder() {
        List<CompoundEdge> edges =
            new PresetRotations(
                new PresetVertex(
                    Vector2.Zero,
                    "v"
                )
            ).with(
                new StraightLineEdge(
                    new PresetVertex(
                        new Vector2(2, 2),
                        "0"
                    )
                ),
                new StraightLineEdge(
                    new PresetVertex(
                        new Vector2(0, 3),
                        "1"
                    )
                ),
                new StraightLineEdge(
                    new PresetVertex(
                        new Vector2(-4, 1),
                        "2"
                    )
                ),
                new StraightLineEdge(
                    new PresetVertex(
                        new Vector2(-1, 2),
                        "3"
                    )
                ),
                new StraightLineEdge(
                    new PresetVertex(
                        new Vector2(3, -1),
                        "4"
                    )
                ),
                new StraightLineEdge(
                    new PresetVertex(
                        new Vector2(-1, -2),
                        "5"
                    )
                )
            ).edges();

        List<String> labels = new ArrayList<String>();
        for (CompoundEdge edge : edges) {
            labels.add(edge.direction().to().label());
        }

        List<String> actual = new ArrayList<String>(labels);
        actual.addAll(labels);

        assertThat(actual).containsSubsequence(
            "1", "0", "4", "5", "2", "3"
        );
    }

    /** Finds the next DirectedEdge of an empty LocalRotations object. */
    @Test
    public void findsNextEdgeOfEmptyLocalRotations() {
        assertEquals(
            new CompoundEdge.Wrapped(
                new StraightLineEdge(
                    new PresetVertex(
                        Vector2.Zero
                    ),
                    new PresetVertex(
                        new Vector2(1, 1)
                    )
                )
            ),
            new PresetRotations(
                new PresetVertex(
                    Vector2.Zero
                )
            ).next(
                new StraightLineEdge(
                    new PresetVertex(
                        new Vector2(1, 1)
                    )
                )
            )
        );
    }

    /**
     * Checks that the implementation can differentiate between the
     * DirectedEdges that end in the same Vertex.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void differentiatesBetweenEdgesThatEndInTheSameVertex() {
        assertThat(
            new PresetRotations(
                new PresetVertex(Vector2.Zero)
            ).with(
                new StraightLineEdge(
                    new PresetVertex(new Vector2(10, 10))
                ),
                new StraightLineEdge(
                    new PresetVertex(new Vector2(3, 4)),
                    new PresetVertex(new Vector2(10, 10))
                )
            ).edges()
        ).hasSize(2);
    }

    /**
     * Checks that the implementation can find the <em>next</em> CompoundEdge
     * of two different DirectedEdges that share the same direction.
     */
    @Test
    public void findsNextForTheSameDirection() {
        assertThat(
            new PresetRotations(
                new PresetVertex(Vector2.Zero)
            ).with(
                new StraightLineEdge(
                    new PresetVertex(new Vector2(1, 1))
                ),
                new StraightLineEdge(
                    new PresetVertex(new Vector2(-1, 1))
                )
            ).next(
                new StraightLineEdge(
                    new PresetVertex(new Vector2(2, 2))
                )
            )
        ).isEqualTo(
            new CompoundEdge.Wrapped(
                new PresetVertex(Vector2.Zero),
                new StraightLineEdge(
                    new PresetVertex(new Vector2(-1, 1))
                )
            )
        );
    }

    /**
     * Checks that only one DirectedEdge per direction is added to
     * LocalRotations.
     */
    @Test
    public void onlyAddsOneEdgePerDirection() {
        assertThat(
            new PresetRotations(
                new PresetVertex(Vector2.Zero)
            ).with(
                new StraightLineEdge(
                    new PresetVertex(new Vector2(1, 1))
                ),
                new StraightLineEdge(
                    new PresetVertex(new Vector2(2, 2))
                )
            ).edges()
             .size()
        ).isEqualTo(1);
    }

    /** Checks that DirectedEdges can be removed by direction. */
    @Test
    public void removesEdgeByDirection() {
        assertThat(
            new PresetRotations(
                new PresetVertex(Vector2.Zero)
            ).with(
                new StraightLineEdge(
                    new PresetVertex(new Vector2(1, 1))
                )
            ).without(
                new StraightLineEdge(
                    new PresetVertex(new Vector2(2, 2))
                )
            ).edges()
             .size()
        ).isEqualTo(0);
    }
}
