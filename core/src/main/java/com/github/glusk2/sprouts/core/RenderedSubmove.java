package com.github.glusk2.sprouts.core;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.github.glusk2.sprouts.core.util.RenderBatch;

/** Represents a Submove drawn on the screen. */
public final class RenderedSubmove implements RenderBatch {
    /** The wrapped Submove to draw. */
    private final Submove submove;
    /** The thickness of the line drawn. */
    private final float lineThickness;
    /**
     * The number of segments for the circles between adjacent line
     * segments.
     */
    private final int circleSegmentCount;
    /**
     * A flag that specifies whether this Submove is being rendered in a
     * parent batch as part of a Move.
     */
    private final boolean isNestedBatch;

    /**
     * Constructs a new RenderedSubmove from the Submove and the rendering
     * settings.
     *
     * @param submove the wrapped Submove to draw
     * @param lineThickness the thickness of the line drawn
     * @param circleSegmentCount the number of segments for the circles between
     *                           adjacent line segments
     * @param isNestedBatch a flag that specifies whether this Submove is being
     *                      rendered in parent batch as part of a Move
     */
    public RenderedSubmove(
        final Submove submove,
        final float lineThickness,
        final int circleSegmentCount,
        final boolean isNestedBatch
    ) {
        this.submove = submove;
        this.lineThickness = lineThickness;
        this.circleSegmentCount = circleSegmentCount;
        this.isNestedBatch = isNestedBatch;
    }

    @Override
    public void render(final ShapeRenderer renderer) {
        if (!submove.isReadyToRender()) {
            return;
        }

        if (!isNestedBatch) {
            renderer.begin(ShapeType.Filled);
        }

        if (!submove.isValid()) {
            renderer.setColor(Color.GRAY);
        } else if (submove.isCompleted()) {
            renderer.setColor(Color.BLUE);
        } else {
            renderer.setColor(Color.GREEN);
        }
        List<Vector2> points = submove.direction().polyline().points();
        for (int i = 0; i < points.size(); i++) {
            Vector2 p1 = null;
            if (i == 0) {
                p1 = submove.origin().position();
            } else {
                p1 = points.get(i - 1);
            }
            Vector2 p2 = points.get(i);
            renderer.rectLine(p1, p2, lineThickness);
            renderer.circle(
                p2.x,
                p2.y,
                lineThickness / 2,
                circleSegmentCount
            );
        }

        if (!isNestedBatch) {
            renderer.end();
        }
    }
}
