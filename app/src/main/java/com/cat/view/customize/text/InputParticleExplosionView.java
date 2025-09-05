package com.cat.view.customize.text;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cat.view.customize.particle.ParticleExplosion;

import java.util.LinkedList;

public class InputParticleExplosionView extends View {

    private EditText editText;

    private final LinkedList<ParticleExplosion> particles = new LinkedList<>();

    private final TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            float[] coordinate = getCursorCoordinate();
            launch(coordinate[0], coordinate[1], i1 == 0 ? -1 : 1);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

    };

    public InputParticleExplosionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InputParticleExplosionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InputParticleExplosionView(Context context) {
        super(context);
    }

    public void bind(EditText editText) {
        //  this.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        this.editText = editText;
        this.editText.addTextChangedListener(textWatcher);
    }

    public void unbind() {
        editText.removeTextChangedListener(textWatcher);
        editText = null;
    }

    private void launch(float x, float y, int direction) {
        ParticleExplosion.Location location = new ParticleExplosion.Location(x, y);
        final ParticleExplosion explosion = new ParticleExplosion(location, direction);
        explosion.addAnimationEndListener(
            new ParticleExplosion.AnimationEndListener() {

                @Override
                public void onAnimationEnd() {
                    particles.remove(explosion);
                }

            }
        );
        particles.add(explosion);
        explosion.fire();
        invalidate();
    }

    /**
     * @return the coordinate of cursor. x=float[0]; y=float[1];
     */
    private float[] getCursorCoordinate() {
        int offsetX = 0;
        int offsetY = 0;
        float x = editText.getX();
        float y = editText.getY();
        Layout layout = editText.getLayout();
        int selectionStart = editText.getSelectionStart();
        if (layout != null && selectionStart != -1) {
            int line = layout.getLineForOffset(selectionStart);
            offsetX = (int) layout.getPrimaryHorizontal(selectionStart);
            offsetY = layout.getLineBottom(line);
            int[] location = new int[2];
            editText.getLocationOnScreen(location);
            x = location[0] + offsetX;
            // y = location[1] + offsetY;
        }
        // 当 EditText 的父 view 与 InputParticleExplosionView 的坐标（左上角的坐标值）不一致时进行修正
        int[] position = new int[2];
        if (editText.getParent() != null) {
            ViewGroup parent = (ViewGroup) editText.getParent();
            parent.getLocationInWindow(position);
        }
        int[] location = new int[2];
        getLocationInWindow(location);
        x = x + position[0] - location[0];
        y = y + position[1] - location[1];

        return new float[] { x, y };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).draw(canvas);
        }
        if (particles.size() > 0) invalidate();
    }

}