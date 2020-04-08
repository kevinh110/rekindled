package edu.cornell.gdiac.rekindled.light;

import box2dLight.ConeLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Filter;

public class SightConeLight extends ConeLight {
    private static final float DISTANCE = 7f;
    /**
     * Creates light shaped as a circle's sector with given radius, direction and arc angle
     *
     * @param rayHandler      not {@code null} instance of RayHandler
     * @param rays            number of rays - more rays make light to look more realistic
     *                        but will decrease performance, can't be less than MIN_RAYS
     * @param color           color, set to {@code null} to use the default color
     * @param distance        distance of cone light
     * @param x               axis position
     * @param y               axis position
     * @param directionDegree direction of cone light
     * @param coneDegree
     */
    public SightConeLight(RayHandler rayHandler) {
        super(rayHandler, 512, Color.RED, DISTANCE, 0, 0, 0f, 60f);

        Filter filter = new Filter();
        filter.maskBits = 0;
        this.setContactFilter(filter);
    }
}
