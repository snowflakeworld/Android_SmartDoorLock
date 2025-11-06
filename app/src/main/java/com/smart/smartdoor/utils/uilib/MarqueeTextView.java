package com.smart.smartdoor.utils.uilib;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class MarqueeTextView extends androidx.appcompat.widget.AppCompatTextView {
    public MarqueeTextView(Context context) {
        super(context);

        setSingleLine(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setSelected(true);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setSingleLine(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setSelected(true);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSingleLine(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setSelected(true);
    }
}
