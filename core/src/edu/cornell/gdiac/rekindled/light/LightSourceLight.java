package edu.cornell.gdiac.rekindled.light;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Filter;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import edu.cornell.gdiac.rekindled.Constants;

public class LightSourceLight extends PointLight {
    /** The default distance for a point source light */
    public static float DEFAULT_DISTANCE = 8f;
    public static float BRIGHTNESS = 1f;

    /** Copy of the collision filter.  Necessary because the original version is private */
    protected Filter collisions;

    /**
     * Creates light shaped as a circle with default radius, color and position.
     *
     * The default radius is DEFAULT_DISTANCE, while the default color is DEFAULT_COLOR
     * in LightSource.  The default position is the origin.
     *
     * RayHandler is NOT allowed to be null.  This is the source of many design problems.
     *
     * The number of rays determines how realistic the light looks.  More rays will
     * decrease performance.  The number of rays cannot be less than MIN_RAYS.
     *
     * The soft shadow length is set to distance * 0.1f.  This is why it ignores thin
     * walls, and is not particularly useful.
     *
     * @param rayHandler	a non-null instance of RayHandler
     * @param rays			the number of rays
     */
    public LightSourceLight(RayHandler rayHandler) {
        super(rayHandler, 512, null, DEFAULT_DISTANCE, 0, 0);
        System.out.println(this.softShadowLength);
        this.setSoftnessLength(0f);
        this.setColor(BRIGHTNESS, BRIGHTNESS, BRIGHTNESS, BRIGHTNESS);

        Filter filter = new Filter();
        filter.maskBits = Constants.BIT_WALL;
        this.setContactFilter(filter);
    }




    /**
     * Returns the direction of this light in degrees
     *
     * The angle is measured from the right horizontal, as normal.  If the light
     * does not have a direction, this value is 0.
     *
     * @return the direction of this light in degrees
     */
    public float getDirection() {
        return direction;
    }


    public Filter getContactFilter() {
        return collisions;
    }

    @Override
    /**
     * Sets the current contact filter for this light
     *
     * The contact filter defines which obstacles block the light, and which are see
     * through.  As a general rule, sensor objects should not block light beams.
     *
     * @param filter the current contact filter for this light
     */
    public void setContactFilter(Filter filter) {
        collisions = filter;
        super.setContactFilter(filter);
    }

    /**
     * Creates a new contact filter for this light with given parameters
     *
     * The contact filter defines which obstacles block the light, and which are see
     * through.  As a general rule, sensor objects should not block light beams.
     * See Filter for a complete description of these parameters.
     *
     * @param categoryBits	the category of this light (to allow objects to exclude this light)
     * @param groupIndex    the group index of the light, for coarse-grain filtering
     * @param maskBits      the mask of this light (to allow the light to exclude objects)
     */
    public void setContactFilter(short categoryBits, short groupIndex, short maskBits) {
        collisions = new Filter();
        collisions.categoryBits = categoryBits;
        collisions.groupIndex = groupIndex;
        collisions.maskBits = maskBits;
        super.setContactFilter(collisions);
    }
}
