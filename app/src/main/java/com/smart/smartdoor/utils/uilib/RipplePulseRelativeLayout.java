package com.smart.smartdoor.utils.uilib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import androidx.core.content.ContextCompat;
import com.smart.smartdoor.R;

public class RipplePulseRelativeLayout extends RelativeLayout {

    private static final int STROKE = 1;
    private static final int FILL = 0;

    private Paint ripplePaint;
    private RectF rippleBounds;
    private AnimatorSet animatorSet;

    private float radius = 0F;
    private int _rippleColor;
    private int _pulseType = STROKE;
    private float _rippleStrokeWidth = 0F;
    private int _pulseDuration = 0;
    private int _startDelay = 0;
    private int _endDelay = 0;
    private float _rippleStartRadiusPercent = 0F;
    private float _rippleEndRadiusPercent = 150F;
    private int _pulseInterpolator = android.R.anim.decelerate_interpolator;
    private boolean _showPreview = false;

    public RipplePulseRelativeLayout(Context context) {
        this(context, null);
    }

    public RipplePulseRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RipplePulseRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        _rippleColor = ContextCompat.getColor(context, R.color.com_chattylabs_component_color_green);
        _pulseDuration = context.getResources().getInteger(android.R.integer.config_longAnimTime);
        _rippleStrokeWidth = context.getResources().getDimension(R.dimen.com_chattylabs_component_dimen_stroke);
        setWillNotDraw(false);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RipplePulseRelativeLayout, defStyle, 0);
            TypedValue typedValue = new TypedValue();

            _rippleColor = a.getColor(R.styleable.RipplePulseRelativeLayout_pulse_layout_RippleColor, _rippleColor);
            _rippleStrokeWidth = a.getValue(R.styleable.RipplePulseRelativeLayout_pulse_layout_RippleStrokeWidth, typedValue) ? typedValue.getFloat() : _rippleStrokeWidth;
            _pulseDuration = a.getValue(R.styleable.RipplePulseRelativeLayout_pulse_layout_PulseDuration, typedValue) ? typedValue.data : _pulseDuration;
            _startDelay = a.getValue(R.styleable.RipplePulseRelativeLayout_pulse_layout_StartDelay, typedValue) ? typedValue.data : _startDelay;
            _endDelay = a.getValue(R.styleable.RipplePulseRelativeLayout_pulse_layout_EndDelay, typedValue) ? typedValue.data : _endDelay;
            _pulseType = a.getValue(R.styleable.RipplePulseRelativeLayout_pulse_layout_PulseType, typedValue) ? typedValue.data : _pulseType;
            _rippleStartRadiusPercent = a.getValue(R.styleable.RipplePulseRelativeLayout_pulse_layout_RippleStartRadiusPercent, typedValue) ? typedValue.getFloat() : _rippleStartRadiusPercent;
            _rippleEndRadiusPercent = a.getValue(R.styleable.RipplePulseRelativeLayout_pulse_layout_RippleEndRadiusPercent, typedValue) ? typedValue.getFloat() : _rippleEndRadiusPercent;
            _pulseInterpolator = a.getValue(R.styleable.RipplePulseRelativeLayout_pulse_layout_PulseInterpolator, typedValue) ? typedValue.resourceId : _pulseInterpolator;

            if (isInEditMode()) {
                _showPreview = a.getBoolean(R.styleable.RipplePulseRelativeLayout_pulse_layout_ShowPreview, _showPreview);
            }

            a.recycle();
        }

        ripplePaint = new Paint();
        ripplePaint.setAntiAlias(true);
        rippleBounds = new RectF();
        animatorSet = new AnimatorSet();
        invalidatePaint();
    }

    public void startPulse() {
        if (!animatorSet.isRunning()) {
            startAnimator();
        }
    }

    public void stopPulse() {
        animatorSet.removeAllListeners();
        animatorSet.cancel();
        invalidate();
    }

    public boolean isAnimationRunning() {
        return animatorSet.isRunning();
    }

    private void startAnimator() {
        ValueAnimator scale = ValueAnimator.ofFloat(_rippleStartRadiusPercent, _rippleEndRadiusPercent);
        scale.addUpdateListener(animation -> {
            radius = (float) animation.getAnimatedValue();
            if (radius > 0) {
                invalidate(rippleBounds, radius);
                invalidate();
            }
        });

        ValueAnimator alpha = ValueAnimator.ofInt(255, 0);
        alpha.addUpdateListener(animation -> {
            int alphaValue = (int) animation.getAnimatedValue();
            ripplePaint.setAlpha(alphaValue);
        });
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ripplePaint.setAlpha(255);
            }
        });

        animatorSet.setDuration(_pulseDuration);
        animatorSet.setStartDelay(_startDelay);
        animatorSet.setInterpolator(AnimationUtils.loadInterpolator(getContext(), _pulseInterpolator));
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                postDelayed(() -> {
                    if (!animatorSet.isRunning()) {
                        invalidate(rippleBounds, _rippleStartRadiusPercent);
                        animatorSet.start();
                    }
                }, _endDelay);
            }
        });
        animatorSet.playTogether(scale, alpha);
        animatorSet.start();
    }

    private void invalidatePaint() {
        ripplePaint.setColor(_rippleColor);
        ripplePaint.setStrokeWidth(_pulseType == STROKE ? _rippleStrokeWidth : 0F);
        ripplePaint.setStyle(_pulseType == FILL ? Paint.Style.FILL : Paint.Style.STROKE);
    }

    private void invalidate(RectF bounds, float percent) {
        float halfWidth = (getWidth() / 2);
        float halfHeight = (getHeight() / 2);
        bounds.set(
                halfWidth - (halfWidth * (percent / 100.0f)),
                halfHeight - (halfHeight * (percent / 100.0f)),
                halfWidth + (halfWidth * (percent / 100.0f)),
                halfHeight + (halfHeight * (percent / 100.0f))
        );
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPulse();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (isInEditMode() && _showPreview) {
            invalidate(rippleBounds, _rippleStartRadiusPercent);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            parent.setClipChildren(false);
            if (!parent.isInLayout()) {
                parent.requestLayout();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (animatorSet.isRunning() || isInEditMode()) {
            drawPulse(canvas, rippleBounds, ripplePaint);
            if (isInEditMode() && _showPreview) {
                for (int idx = (int) _rippleStartRadiusPercent; idx <= _rippleEndRadiusPercent; idx += 50) {
                    Paint paint = new Paint(ripplePaint);
                    paint.setAlpha(_pulseType == FILL ? 50 : 100);
                    RectF bounds = new RectF(rippleBounds);
                    invalidate(bounds, idx);
                    drawPulse(canvas, bounds, paint);
                }
            }
        }
    }

    private void drawPulse(Canvas canvas, RectF bounds, Paint paint) {
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), bounds.width() / 2, paint);
    }
}