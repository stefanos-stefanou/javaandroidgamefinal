package uk.ac.reading.sis05kol.engine.game.core.object.drawables.staticdrawables;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.Arrays;
import java.util.List;

import uk.ac.reading.sis05kol.engine.game.core.map.Position;
import uk.ac.reading.sis05kol.engine.game.core.object.Drawable;
import uk.ac.reading.sis05kol.engine.game.core.object.animator.DrawableAnimator;
import uk.ac.reading.sis05kol.engine.menuactivity.animations.elements.Element;

public class FireTower extends Drawable {
    private static final int IDLEINDEX=0;
    private static final int ATTACKINDEX=1;
    public FireTower(Context context, Position absolutePosition) {
        super(Arrays.asList(
                new DrawableAnimator(Element.FIREIDLE,context,0.16f),
                new DrawableAnimator(Element.FIREATTACK,context,0.16f)
        ), absolutePosition);
    }

    @Override
    public Bitmap getBitmap() {
        return animators.get(ATTACKINDEX).getBitmap();
    }
}