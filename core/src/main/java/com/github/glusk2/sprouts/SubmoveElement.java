package com.github.glusk2.sprouts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.github.glusk2.sprouts.comb.CachedCompoundEdge;
import com.github.glusk2.sprouts.comb.CompoundEdge;
import com.github.glusk2.sprouts.comb.DirectedEdge;
import com.github.glusk2.sprouts.comb.FaceIntersectionSearch;
import com.github.glusk2.sprouts.comb.Graph;
import com.github.glusk2.sprouts.comb.NearestSproutSearch;
import com.github.glusk2.sprouts.comb.PolylineEdge;
import com.github.glusk2.sprouts.comb.PolylineIntersectionSearch;
import com.github.glusk2.sprouts.comb.PresetVertex;
import com.github.glusk2.sprouts.comb.StraightLineEdge;
import com.github.glusk2.sprouts.comb.SubmoveTransformation;
import com.github.glusk2.sprouts.comb.TransformedGraph;
import com.github.glusk2.sprouts.comb.Vertex;
import com.github.glusk2.sprouts.comb.VoidVertex;
import com.github.glusk2.sprouts.geom.Polyline;
import com.github.glusk2.sprouts.geom.PolylinePiece;
import com.github.glusk2.sprouts.geom.TrimmedPolyline;

/**
 * A SubmoveElement is a Submove in a sequence of Submoves that comprise a Move.
 * <p>
 * The first element of any such sequence is always the {@link SubmoveHead}.
 */
public final class SubmoveElement implements Submove {
    /** The Graph Vertex in which {@code this} Submove begins. */
    private final Vertex origin;
    /** The polyline approximation of the move stroke. */
    private final Polyline stroke;
    /** The game state before {@code this} Submove. */
    private final Graph currentState;
    /**
     * The Vertex glue radius, used to auto-complete {@code this} Submove
     * when near a sprout.
     */
    private final float vertexGlueRadius;

    /**
     * Creates a new Submove.
     *
     * @param origin the Graph Vertex in which {@code this} Submove begins
     * @param stroke the polyline approximation of the move stroke
     * @param currentState the game state before {@code this} Submove
     * @param vertexGlueRadius the Vertex glue radius, used to auto-complete
     *                         {@code this} Submove when near a sprout
     */
    public SubmoveElement(
        final Vertex origin,
        final Polyline stroke,
        final Graph currentState,
        final float vertexGlueRadius
    ) {
        this.origin = origin;
        this.stroke = stroke;
        this.currentState = currentState;
        this.vertexGlueRadius = vertexGlueRadius;
    }

    @Override
    public Vertex origin() {
        return origin;
    }

    @Override
    public DirectedEdge direction() {
        List<Vector2> strokePoints = stroke.points();
        if (strokePoints.isEmpty()) {
            throw
                new IllegalStateException(
                    "At least 1 sample point is needed to establish a "
                     + "direction!"
                );
        }
        Set<CompoundEdge> moveFace =
            currentState.edgeFace(
                new CachedCompoundEdge(
                    origin,
                    new StraightLineEdge(
                        new PresetVertex(
                            strokePoints.get(0),
                            (String) null
                        )
                    )
                )
            );
        for (int i = 0; i < strokePoints.size(); i++) {
            Vector2 p1 = strokePoints.get(i);
            if (i > 0) {
                Vector2 p0 = strokePoints.get(i - 1);
                // Check if crosses itself
                Vertex crossPoint =
                    new PolylineIntersectionSearch(
                        p0,
                        p1,
                        new Polyline.WrappedList(strokePoints.subList(0, i)),
                        Color.BLACK
                    ).result();
                if (crossPoint.color().equals(Color.BLACK)) {
                    List<Vector2> returnPoints =
                        new ArrayList<Vector2>(strokePoints.subList(0, i));
                    returnPoints.add(crossPoint.position());
                    return
                        new PolylineEdge(
                            origin().color(),
                            Color.GRAY,
                            returnPoints
                        );
                }
                // Check if crosses the face
                crossPoint =
                    new FaceIntersectionSearch(moveFace, p0, p1).result();
                if (!crossPoint.equals(new VoidVertex(null))) {
                    List<Vector2> returnPoints =
                        new ArrayList<Vector2>(strokePoints.subList(0, i));
                    returnPoints.add(crossPoint.position());
                    Color toColor = crossPoint.color();
                    if (toColor.equals(Color.BLACK)) {
                        toColor = Color.GRAY;
                    }
                    return
                        new PolylineEdge(
                            origin().color(),
                            toColor,
                            returnPoints
                        );
                }
            }

            // Check if close to a sprout and finnish
            Vertex v = new NearestSproutSearch(currentState, p1).result();
            if (v.position().dst(p1) < vertexGlueRadius) {
                List<Vector2> returnPoints =
                    new ArrayList<Vector2>(strokePoints.subList(0, i));
                returnPoints.add(v.position());
                return
                    new PolylineEdge(
                        origin().color(),
                        v.color(),
                        returnPoints
                    );
            }
        }
        return
            new PolylineEdge(
                origin().color(),
                Color.CLEAR,
                strokePoints
            );
    }

    @Override
    public boolean isCompleted() {
        Color tipColor = Color.CLEAR;
        if (isReadyToRender()) {
            tipColor = direction().to().color();
        }
        return tipColor.equals(Color.BLACK) || tipColor.equals(Color.RED);
    }

    @Override
    public boolean isReadyToRender() {
        return !stroke.points().isEmpty();
    }

    @Override
    public boolean isValid() {
        if (!isReadyToRender()) {
            return false;
        }

        Vertex from = origin();
        Vertex to = direction().to();

        boolean intermediate = true;
        if (from.color().equals(Color.BLACK)) {
            intermediate &= currentState.isAliveSprout(from);
        }
        if (to.color().equals(Color.BLACK)) {
            intermediate &= currentState.isAliveSprout(to);
        }
        return intermediate && !to.color().equals(Color.GRAY);
    }

    @Override
    public boolean hasNext() {
        return isCompleted() && !direction().to().color().equals(Color.BLACK);
    }

    @Override
    public Submove next() {
        if (!hasNext()) {
            throw new IllegalStateException("This is the tail Submove.");
        }
        float minDistance = 0;
        Vertex tip = direction().to();
        if (!tip.color().equals(Color.RED)) {
            minDistance = vertexGlueRadius;
        }
        return
            new SubmoveElement(
                tip,
                new TrimmedPolyline(
                    new PolylinePiece(
                        stroke,
                        tip.position()
                    ),
                    minDistance
                ),
                new TransformedGraph(
                    new SubmoveTransformation(
                        new CachedCompoundEdge(this),
                        currentState
                    )
                ),
                vertexGlueRadius
            );
    }
}