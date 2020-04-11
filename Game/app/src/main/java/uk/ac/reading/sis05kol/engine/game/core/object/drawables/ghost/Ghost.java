package uk.ac.reading.sis05kol.engine.game.core.object.drawables.ghost;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.arch.core.util.Function;
import uk.ac.reading.sis05kol.engine.game.core.interfaces.MapAwareAction;
import uk.ac.reading.sis05kol.engine.game.core.info.LevelInfo;
import uk.ac.reading.sis05kol.engine.game.core.map.Map;
import uk.ac.reading.sis05kol.engine.game.core.map.Position;
import uk.ac.reading.sis05kol.engine.game.core.map.path.Path;
import uk.ac.reading.sis05kol.engine.game.core.object.Drawable;
import uk.ac.reading.sis05kol.engine.game.core.object.animator.DrawableAnimator;
import uk.ac.reading.sis05kol.engine.game.core.object.drawables.portals.Portal;
import uk.ac.reading.sis05kol.engine.game.core.object.drawables.portals.RedPortal;
import uk.ac.reading.sis05kol.engine.game.core.utils.CoordinateSystemUtils;

public class Ghost extends Drawable{
    private static final int DOWNINDEX=0;
    private static final int UPINDEX=1;
    private static final int RIGHTINDEX=2;
    private static final int LEFTINDEX=3;
    private int currentIndex=DOWNINDEX;
    private static String loggerTag="GHOST";
    private Random random=new Random();
    private int acceptablePixelDeviation=5;
    private int speed=3;
    private int plusSpeedOffset=1;

    private Path.Node currentNode;
    private Path.Node nextNode;
    private ArrayList<Path.Node> visitedNodes=new ArrayList<>();

    private boolean toDestruct=false;
    public Ghost(Context context, LevelInfo levelInfo, Position absolutePosition, List<DrawableAnimator> animators) {
        super(animators,absolutePosition);
    }
    @Override
    public Bitmap getBitmap() {
        return animators.get(currentIndex).getBitmap();
    }

    @Override
    public MapAwareAction getNextMapAwareAction(Path path, Map map, Context context) {

        if(toDestruct){

            return MapAwareAction.buildDeleteMeAction(null,null, getAbsolutePosition(),this);
        }
        plusSpeedOffset=random.nextInt(2);

        Position tilePosition = CoordinateSystemUtils.getInstance().fromAbsoluteToTilePosition(getAbsolutePosition());
        Path.Node currNode=path.getNodeByPosition(tilePosition);
        if(currentNode==null){
            currentNode=currNode;
            nextNode=currentNode.getRandomNext();
        }
        if(nextNode==null){
            return MapAwareAction.buildIdleAction(null,null);
        }
        currentIndex=currentNode.getAnimatorIndex();
        Path.Node next=nextNode;
        Log.i(loggerTag,"i am in node "+String.valueOf(currentNode)+" and next node is"+next);
        Position tilePositionNext=next.getPosition();
        Position absolutePositionNext=CoordinateSystemUtils.getInstance().fromTileToAbsolutePositionWithRedundancy(tilePositionNext);
        int xDeviation=Math.abs(absolutePositionNext.getX()-getAbsolutePosition().getX());
        int yDeviation=Math.abs(absolutePositionNext.getY()-getAbsolutePosition().getY());

        Position newAbsolutePosition;

        if(xDeviation<=acceptablePixelDeviation){
            if(yDeviation>acceptablePixelDeviation) {
                newAbsolutePosition= getAbsolutePosition()
                        .setY(getAbsolutePosition().getY()+(absolutePositionNext.getY()>getAbsolutePosition().getY()?speed:-speed));
            }else {
                newAbsolutePosition= getAbsolutePosition().
                        setX(getAbsolutePosition().getX()+ (absolutePositionNext.getX() > getAbsolutePosition().getX() ? speed: -speed))
                        .setY(getAbsolutePosition().getY()+(absolutePositionNext.getY()>getAbsolutePosition().getY()?speed:-speed));
            }
        }else if(yDeviation<=acceptablePixelDeviation){
            if(xDeviation>acceptablePixelDeviation) {
                newAbsolutePosition= getAbsolutePosition().
                        setX(getAbsolutePosition().getX()+ (absolutePositionNext.getX() > getAbsolutePosition().getX() ? speed : -speed));
            }
            else {
                newAbsolutePosition= getAbsolutePosition().
                        setX(getAbsolutePosition().getX()+ (absolutePositionNext.getX() > getAbsolutePosition().getX() ? speed : -speed))
                        .setY(getAbsolutePosition().getY()+(absolutePositionNext.getY()>getAbsolutePosition().getY()?speed:-speed));
            }
        }
        else {
            newAbsolutePosition= getAbsolutePosition().
                    setX(getAbsolutePosition().getX()+ (absolutePositionNext.getX() >= getAbsolutePosition().getX() ? speed : -speed))
                    .setY(getAbsolutePosition().getY()+(absolutePositionNext.getY()>=getAbsolutePosition().getY()?speed:-speed));
        }
        if(xDeviation<=acceptablePixelDeviation && yDeviation<=acceptablePixelDeviation){
            currentNode=nextNode;
            if(currentNode==null){
                return MapAwareAction.buildIdleAction(null,null);
            }
            nextNode=currentNode.getRandomNext();
        }


        return MapAwareAction.buildMoveAction(null,null,getAbsolutePosition(),newAbsolutePosition,this);

    }

    public void setToDestruct(boolean toDestruct) {
        this.toDestruct = toDestruct;
    }

    @Override
    public Function<Drawable, Void> getOnCollisionHandler() {


        return new Function<Drawable, Void>() {
            Ghost drawable;

            @Override
            public Void apply(Drawable input) {
                Log.d(loggerTag,"CALLBACK OK");
                drawable.setToDestruct(true);
                Log.d(loggerTag,String.valueOf(input)+" AAAG");
                if(input instanceof Portal){
                    Log.d(loggerTag,"COLLISION WITH PORTAL");
                }
                else {
                    Log.d(loggerTag,"COLLISION WITH OTHER OBJ");
                }
                return null;

            }
            public Function<Drawable, Void>init(Ghost drawable){
                this.drawable=drawable;
                return this;
            }
        }.init(this);
    }
}
