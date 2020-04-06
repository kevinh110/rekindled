package edu.cornell.gdiac.rekindled;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.physics.box2d.Filter;

public class AuraLight extends PointLight {
    /** The default distance for a point source light */
    public static float DEFAULT_DISTANCE = 5f;

    /** Copy of the collision filter.  Necessary because the original version is private */
    protected Filter collisions;

    public AuraLight (RayHandler handler) {
        super(handler, 512, null, DEFAULT_DISTANCE, 0, 0);
        this.setSoftnessLength(0f);
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
