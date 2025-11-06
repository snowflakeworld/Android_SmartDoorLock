package com.smart.smartdoor.utils.uilib;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.smart.smartdoor.R;

public class CircularProgressBar extends View {

    private static final float DEFAULT_MAX_VALUE = 100f;
    private static final float DEFAULT_START_ANGLE = 270f;
    private static final long DEFAULT_ANIMATION_DURATION = 1500L;

    // Properties
    private ValueAnimator progressAnimator;
    private Handler indeterminateModeHandler;

    // View
    private RectF rectF = new RectF();
    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Attributes
    private float progress = 0f;
    private float progressMax = DEFAULT_MAX_VALUE;
    private float progressBarWidth;
    private float backgroundProgressBarWidth;
    private int progressBarColor = Color.BLACK;
    private Integer progressBarColorStart = null;
    private Integer progressBarColorEnd = null;
    private GradientDirection progressBarColorDirection = GradientDirection.LEFT_TO_RIGHT;
    private int backgroundProgressBarColor = Color.GRAY;
    private Integer backgroundProgressBarColorStart = null;
    private Integer backgroundProgressBarColorEnd = null;
    private GradientDirection backgroundProgressBarColorDirection = GradientDirection.LEFT_TO_RIGHT;
    private boolean roundBorder = false;
    private float startAngle = DEFAULT_START_ANGLE;
    private ProgressDirection progressDirection = ProgressDirection.TO_RIGHT;
    private boolean indeterminateMode = false;

    private Float progressIndeterminateMode = 0f;
    private ProgressDirection progressDirectionIndeterminateMode = ProgressDirection.TO_RIGHT;
    private Float startAngleIndeterminateMode = DEFAULT_START_ANGLE;

    private OnProgressChangeListener onProgressChangeListener;
    private OnIndeterminateModeChangeListener onIndeterminateModeChangeListener;

    private final Runnable indeterminateModeRunnable = new Runnable() {
        @Override
        public void run() {
            if (indeterminateMode) {
                postIndeterminateModeHandler();
                progressDirectionIndeterminateMode = reverse(progressDirectionIndeterminateMode);
                if (isToRight(progressDirectionIndeterminateMode)) {
                    setProgressWithAnimation(0f, 1500L, null, null);
                } else {
                    setProgressWithAnimation(progressMax, 1500L, null, null);
                }
            }
        }
    };

    public CircularProgressBar(Context context) {
        this(context, null);
    }

    public CircularProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray attributes = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, 0, 0);

        progress = attributes.getFloat(R.styleable.CircularProgressBar_cpb_progress, progress);
        progressMax = attributes.getFloat(R.styleable.CircularProgressBar_cpb_progress_max, progressMax);

        progressBarWidth = pxToDp(attributes.getDimension(R.styleable.CircularProgressBar_cpb_progressbar_width,
                getResources().getDimension(R.dimen.default_stroke_width)));
        backgroundProgressBarWidth = pxToDp(attributes.getDimension(
                R.styleable.CircularProgressBar_cpb_background_progressbar_width,
                getResources().getDimension(R.dimen.default_background_stroke_width)));

        progressBarColor = attributes.getColor(R.styleable.CircularProgressBar_cpb_progressbar_color, progressBarColor);
        int start = attributes.getColor(R.styleable.CircularProgressBar_cpb_progressbar_color_start, 0);
        if (start != 0) progressBarColorStart = start;
        int end = attributes.getColor(R.styleable.CircularProgressBar_cpb_progressbar_color_end, 0);
        if (end != 0) progressBarColorEnd = end;
        progressBarColorDirection = toGradientDirection(
                attributes.getInteger(R.styleable.CircularProgressBar_cpb_progressbar_color_direction,
                        progressBarColorDirection.getValue()));

        backgroundProgressBarColor = attributes.getColor(
                R.styleable.CircularProgressBar_cpb_background_progressbar_color, backgroundProgressBarColor);
        int bgStart = attributes.getColor(R.styleable.CircularProgressBar_cpb_background_progressbar_color_start, 0);
        if (bgStart != 0) backgroundProgressBarColorStart = bgStart;
        int bgEnd = attributes.getColor(R.styleable.CircularProgressBar_cpb_background_progressbar_color_end, 0);
        if (bgEnd != 0) backgroundProgressBarColorEnd = bgEnd;
        backgroundProgressBarColorDirection = toGradientDirection(
                attributes.getInteger(R.styleable.CircularProgressBar_cpb_background_progressbar_color_direction,
                        backgroundProgressBarColorDirection.getValue()));

        progressDirection = toProgressDirection(
                attributes.getInteger(R.styleable.CircularProgressBar_cpb_progress_direction,
                        progressDirection.getValue()));

        roundBorder = attributes.getBoolean(R.styleable.CircularProgressBar_cpb_round_border, roundBorder);
        foregroundPaint.setStrokeCap(roundBorder ? Paint.Cap.ROUND : Paint.Cap.BUTT);

        startAngle = attributes.getFloat(R.styleable.CircularProgressBar_cpb_start_angle, 0f);

        indeterminateMode = attributes.getBoolean(R.styleable.CircularProgressBar_cpb_indeterminate_mode, indeterminateMode);

        attributes.recycle();

        progressBarWidth = dpToPx(progressBarWidth);
        backgroundProgressBarWidth = dpToPx(backgroundProgressBarWidth);
        foregroundPaint.setStrokeWidth(progressBarWidth);
        backgroundPaint.setStrokeWidth(backgroundProgressBarWidth);

        manageColor();
        manageBackgroundProgressBarColor();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (progressAnimator != null) progressAnimator.cancel();
        if (indeterminateModeHandler != null)
            indeterminateModeHandler.removeCallbacks(indeterminateModeRunnable);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        manageColor();
        manageBackgroundProgressBarColor();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawOval(rectF, backgroundPaint);

        float realProgress = ((indeterminateMode ? progressIndeterminateMode : progress) *
        DEFAULT_MAX_VALUE / progressMax);

        boolean isToRightFromIndeterminateMode = indeterminateMode && isToRight(progressDirectionIndeterminateMode);
        boolean isToRightFromNormalMode = !indeterminateMode && isToRight(progressDirection);
        float angle = (isToRightFromIndeterminateMode || isToRightFromNormalMode ? 360 : -360)
        * realProgress / 100f;

        canvas.drawArc(rectF,
                indeterminateMode ? startAngleIndeterminateMode : startAngle,
        angle, false, foregroundPaint);
    }

    @Override
    public void setBackgroundColor(int color) {
        backgroundProgressBarColor = color;
        manageBackgroundProgressBarColor();
        invalidate();
    }

    private void manageColor() {
        foregroundPaint.setShader(createLinearGradient(
                progressBarColorStart != null ? progressBarColorStart : progressBarColor,
                progressBarColorEnd != null ? progressBarColorEnd : progressBarColor,
        progressBarColorDirection));
    }

    private void manageBackgroundProgressBarColor() {
        backgroundPaint.setShader(createLinearGradient(
                backgroundProgressBarColorStart != null ? backgroundProgressBarColorStart : backgroundProgressBarColor,
                backgroundProgressBarColorEnd != null ? backgroundProgressBarColorEnd : backgroundProgressBarColor,
        backgroundProgressBarColorDirection));
    }

    private LinearGradient createLinearGradient(int startColor, int endColor, GradientDirection direction) {
        float x0 = 0f, y0 = 0f, x1 = 0f, y1 = 0f;
        switch (direction) {
            case LEFT_TO_RIGHT:
            x1 = getWidth();
            break;
            case RIGHT_TO_LEFT:
            x0 = getWidth();
            break;
            case TOP_TO_BOTTOM:
            y1 = getHeight();
            break;
            case BOTTOM_TO_END:
            y0 = getHeight();
            break;
        }
        return new LinearGradient(x0, y0, x1, y1, startColor, endColor, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        float highStroke = Math.max(progressBarWidth, backgroundProgressBarWidth);
        rectF.set(highStroke / 2f, highStroke / 2f, min - highStroke / 2f, min - highStroke / 2f);
    }

    private void postIndeterminateModeHandler() {
        if (indeterminateModeHandler != null)
            indeterminateModeHandler.postDelayed(indeterminateModeRunnable, DEFAULT_ANIMATION_DURATION);
    }

    public void setProgressWithAnimation(float progress, Long duration,
            TimeInterpolator interpolator, Long startDelay) {
        if (progressAnimator != null) progressAnimator.cancel();

        float from = indeterminateMode ? progressIndeterminateMode : this.progress;
        progressAnimator = ValueAnimator.ofFloat(from, progress);
        if (duration != null) progressAnimator.setDuration(duration);
        if (interpolator != null) progressAnimator.setInterpolator(interpolator);
        if (startDelay != null) progressAnimator.setStartDelay(startDelay);

        progressAnimator.addUpdateListener(animation -> {
        Float value = (Float) animation.getAnimatedValue();
        if (value != null) {
            if (indeterminateMode) progressIndeterminateMode = value;
            else this.progress = value;

            if (indeterminateMode) {
                float updateAngle = value * 360f / 100f;
                startAngleIndeterminateMode = DEFAULT_START_ANGLE +
                        (isToRight(progressDirectionIndeterminateMode) ? updateAngle : -updateAngle);
            }
            invalidate();
        }
    });
        progressAnimator.start();
    }

    // === Utilities ===
    private float dpToPx(float dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    private float pxToDp(float px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    private ProgressDirection toProgressDirection(int value) {
        switch (value) {
            case 1:
            return ProgressDirection.TO_RIGHT;
            case 2:
            return ProgressDirection.TO_LEFT;
            default:
            throw new IllegalArgumentException("Unsupported ProgressDirection value: " + value);
        }
    }

    private GradientDirection toGradientDirection(int value) {
        switch (value) {
            case 1:
            return GradientDirection.LEFT_TO_RIGHT;
            case 2:
            return GradientDirection.RIGHT_TO_LEFT;
            case 3:
            return GradientDirection.TOP_TO_BOTTOM;
            case 4:
            return GradientDirection.BOTTOM_TO_END;
            default:
            throw new IllegalArgumentException("Unsupported GradientDirection value: " + value);
        }
    }

    private ProgressDirection reverse(ProgressDirection direction) {
        return direction == ProgressDirection.TO_RIGHT ?
        ProgressDirection.TO_LEFT : ProgressDirection.TO_RIGHT;
    }

    private boolean isToRight(ProgressDirection direction) {
        return direction == ProgressDirection.TO_RIGHT;
    }

    // === Listener Interfaces ===
    public interface OnProgressChangeListener {
        void onProgressChanged(float progress);
    }

    public interface OnIndeterminateModeChangeListener {
        void onIndeterminateModeChanged(boolean mode);
    }

    // === Enums ===
    public enum ProgressDirection {
        TO_RIGHT(1),
        TO_LEFT(2);
        private final int value;

        ProgressDirection(int v) {
            this.value = v;
        }

        public int getValue() {
            return value;
        }
    }

    public enum GradientDirection {
        LEFT_TO_RIGHT(1),
        RIGHT_TO_LEFT(2),
        TOP_TO_BOTTOM(3),
        BOTTOM_TO_END(4);

        private final int value;

        GradientDirection(int v) {
            this.value = v;
        }

        public int getValue() {
            return value;
        }
    }
}
