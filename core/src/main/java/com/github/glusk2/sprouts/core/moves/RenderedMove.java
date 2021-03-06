package com.github.glusk2.sprouts.core.moves;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.github.glusk2.sprouts.core.util.RenderBatch;

/** A RenderedMove renders the move by rendering all of its Submoves. */
public final class RenderedMove implements RenderBatch {
    /** The wrapped Move to draw. */
    private final Move move;
    /** The thickness of the line drawn. */
    private final float lineThickness;
    /**
     * The number of segments for the circles between adjacent line
     * segments.
     */
    private final int circleSegmentCount;

    /**
     * Constructs a new RenderedMove from {@code move} and the rendering
     * settings.
     *
     * @param move the wrapped Move to draw
     * @param lineThickness the thickness of the line drawn
     * @param circleSegmentCount the number of segments for the circles between
     *                           adjacent line segments
     */
    public RenderedMove(
        final Move move,
        final float lineThickness,
        final int circleSegmentCount
    ) {
        this.move = move;
        this.lineThickness = lineThickness;
        this.circleSegmentCount = circleSegmentCount;
    }

    @Override
    public void render(final ShapeRenderer renderer) {
        Color movePaint = Color.GRAY;
        if (move.isValid()) {
            if (move.isCompleted()) {
                movePaint = Color.BLUE;
            } else {
                movePaint = Color.GREEN;
            }
        }
        renderer.begin(ShapeType.Filled);
        Iterator<Submove> it = move.iterator();
        while (it.hasNext()) {
            Submove next = it.next();
            new RenderedSubmove(
                next,
                lineThickness,
                circleSegmentCount,
                true,
                movePaint
            ).render(renderer);
            it = next;
        }
        renderer.end();
    }
}
