package edu.cornell.gdiac.rekindled.obstacle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.rekindled.GameCanvas;

public class BorderMarker {
    private ParticleEffect[] markers;
    private int counter;
    private float scale;
    private float timer;
    private static final float TIME = 2f;

    public BorderMarker(int size, float scale) {
        this.counter = 0;
        this.scale = scale;
        this.timer = 0f;
        markers = new ParticleEffect[size];

        ParticleEffect pe;
        for (int i = 0; i < size; i++) {
            pe = new ParticleEffect();
            pe.load(Gdx.files.internal("particles/light_marker4.party"), Gdx.files.internal(""));
            pe.scaleEffect(scale);
            pe.start();
            markers[i] = pe;
        }

//        pe = new ParticleEffect();
//        pe.load(Gdx.files.internal("particles/mouse.party"), Gdx.files.internal(""));
//        for (ParticleEmitter e : pe.getEmitters()) {
//            e.setPosition(Gdx.input.getX(), Gdx.input.getY());
//        }
//        pe.start();

    }


    public void dispose() {
        for (ParticleEffect pe : markers)
            pe.dispose();
    }

    public void clear() {
        this.counter = 0;
    }

    public void add(float x, float y) {
        markers[counter].setPosition(x, y);
        this.counter++;
    }

    public void update(float delta, Vector2 cameraPos) {
        this.timer+= delta;
        boolean reset = this.timer > TIME;
        if (reset)
            this.timer = 0;

        for (int i = 0; i < counter; i++) {
            float x = markers[i].getEmitters().first().getX();
            float y = markers[i].getEmitters().first().getY();
            markers[i].setPosition((1280 / 2f) - (cameraPos.x - x), (720 / 2f) - (cameraPos.y - y));
            markers[i].update(delta);

            if (reset)
                markers[i].reset(false);
        }
    }

    public void draw(GameCanvas canvas) {
        for (int i = 0; i < counter; i++) {
            canvas.drawParticle(markers[i]);
        }
    }

    private void initParticle(ParticleEffect pe) {
        pe.load(Gdx.files.internal("particles/light_marker3.party"), Gdx.files.internal(""));
        pe.scaleEffect(scale);
        pe.start();
    }
}
