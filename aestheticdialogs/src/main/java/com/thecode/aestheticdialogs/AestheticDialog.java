package com.thecode.aestheticdialogs;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

@Keep
public class AestheticDialog {

    // === ENUMS ===
    public enum DialogAnimation {
        DEFAULT,
        SLIDE_UP, SLIDE_DOWN,
        SLIDE_LEFT, SLIDE_RIGHT,
        SWIPE_LEFT, SWIPE_RIGHT,
        IN_OUT, CARD, SHRINK, SPLIT, DIAGONAL, SPIN, WINDMILL, FADE, ZOOM
    }

    public enum DialogStyle {
        TOASTER, CONNECTIFY, FLAT, RAINBOW, FLASH
    }

    public enum DialogType {
        SUCCESS, ERROR, WARNING, INFO
    }

    public interface OnDialogClickListener {
        void onClick(Builder dialog);
    }

    public static class Builder {

        private final Activity activity;
        private final DialogStyle dialogStyle;
        private final DialogType dialogType;
        private final AlertDialog.Builder dialogBuilder;
        private AlertDialog alertDialog;
        private String title = "Title";
        private String message = "Message";

        private boolean isDarkMode = false;
        private boolean isCancelable = true;
        private int duration = 0;
        private int gravity = Gravity.NO_GRAVITY;
        private DialogAnimation animation = DialogAnimation.DEFAULT;
        private View layoutView;

        private OnDialogClickListener onClickListener = new OnDialogClickListener() {
            @Override
            public void onClick(Builder dialog) {
                dialog.dismiss();
            }
        };

        public Builder(Activity activity, DialogStyle dialogStyle, DialogType dialogType) {
            this.activity = activity;
            this.dialogStyle = dialogStyle;
            this.dialogType = dialogType;
            this.dialogBuilder = new AlertDialog.Builder(activity);
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setDarkMode(boolean isDarkMode) {
            this.isDarkMode = isDarkMode;
            return this;
        }

        public Builder setOnClickListener(OnDialogClickListener listener) {
            this.onClickListener = listener;
            return this;
        }

        public Builder setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        public Builder setDuration(int duration) {
            if (duration != 0) {
                this.duration = duration;
                new Handler(Looper.getMainLooper()).postDelayed(this::dismiss, duration);
            }
            return this;
        }

        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setAnimation(DialogAnimation animation) {
            this.animation = animation;
            return this;
        }

        public AestheticDialog dismiss() {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            return new AestheticDialog();
        }

        private void chooseAnimation() {
            if (alertDialog.getWindow() == null) return;
            WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();

            switch (animation) {
                case ZOOM:
                    params.windowAnimations = R.style.DialogAnimationZoom;
                    break;
                case FADE:
                    params.windowAnimations = R.style.DialogAnimationFade;
                    break;
                case CARD:
                    params.windowAnimations = R.style.DialogAnimationCard;
                    break;
                case SHRINK:
                    params.windowAnimations = R.style.DialogAnimationShrink;
                    break;
                case SWIPE_LEFT:
                    params.windowAnimations = R.style.DialogAnimationSwipeLeft;
                    break;
                case SWIPE_RIGHT:
                    params.windowAnimations = R.style.DialogAnimationSwipeRight;
                    break;
                case IN_OUT:
                    params.windowAnimations = R.style.DialogAnimationInOut;
                    break;
                case SPIN:
                    params.windowAnimations = R.style.DialogAnimationSpin;
                    break;
                case SPLIT:
                    params.windowAnimations = R.style.DialogAnimationSplit;
                    break;
                case DIAGONAL:
                    params.windowAnimations = R.style.DialogAnimationDiagonal;
                    break;
                case WINDMILL:
                    params.windowAnimations = R.style.DialogAnimationWindMill;
                    break;
                case SLIDE_UP:
                    params.windowAnimations = R.style.DialogAnimationSlideUp;
                    break;
                case SLIDE_DOWN:
                    params.windowAnimations = R.style.DialogAnimationSlideDown;
                    break;
                case SLIDE_LEFT:
                    params.windowAnimations = R.style.DialogAnimationSlideLeft;
                    break;
                case SLIDE_RIGHT:
                    params.windowAnimations = R.style.DialogAnimationSlideRight;
                    break;
                default:
                    params.windowAnimations = R.style.DialogAnimation;
                    break;
            }
            alertDialog.getWindow().setAttributes(params);
        }

        public AestheticDialog show() {
            switch (dialogStyle) {

                // --- TOASTER ---
                case TOASTER: {
                    layoutView = activity.getLayoutInflater().inflate(R.layout.dialog_toaster, null);
                    TextView textMessageToaster = layoutView.findViewById(R.id.text_message_toaster);
                    TextView textTitleToaster = layoutView.findViewById(R.id.text_title_toaster);
                    ImageView imageCloseToaster = layoutView.findViewById(R.id.image_close_toaster);
                    View verticalViewToaster = layoutView.findViewById(R.id.vertical_view_toaster);
                    ImageView dialogIconToaster = layoutView.findViewById(R.id.dialog_icon_toaster);
                    View dialogLayoutToaster = layoutView.findViewById(R.id.dialog_layout_toaster);

                    textMessageToaster.setText(message);
                    textTitleToaster.setText(title);
                    imageCloseToaster.setOnClickListener(v -> onClickListener.onClick(this));

                    dialogBuilder.setView(layoutView);
                    alertDialog = dialogBuilder.create();
                    chooseAnimation();
                    alertDialog.show();

                    if (alertDialog.getWindow() != null) {
                        alertDialog.getWindow().setGravity(Gravity.TOP);
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                                activity.getResources().getDimensionPixelSize(R.dimen.popup_height_toaster));
                    }

                    switch (dialogType) {
                        case ERROR:
                            textTitleToaster.setTextColor(ContextCompat.getColor(activity, R.color.dialog_error));
                            verticalViewToaster.setBackgroundColor(ContextCompat.getColor(activity, R.color.dialog_error));
                            dialogIconToaster.setImageResource(R.drawable.ic_error_red_24dp);
                            break;
                        case SUCCESS:
                            textTitleToaster.setTextColor(ContextCompat.getColor(activity, R.color.dialog_success));
                            verticalViewToaster.setBackgroundColor(ContextCompat.getColor(activity, R.color.dialog_success));
                            dialogIconToaster.setImageResource(R.drawable.ic_check_circle_green_24dp);
                            break;
                        case WARNING:
                            textTitleToaster.setTextColor(ContextCompat.getColor(activity, R.color.dialog_warning));
                            verticalViewToaster.setBackgroundColor(ContextCompat.getColor(activity, R.color.dialog_warning));
                            dialogIconToaster.setImageResource(R.drawable.ic_warning_orange_24dp);
                            break;
                        case INFO:
                            textTitleToaster.setTextColor(ContextCompat.getColor(activity, R.color.dialog_info));
                            verticalViewToaster.setBackgroundColor(ContextCompat.getColor(activity, R.color.dialog_info));
                            dialogIconToaster.setImageResource(R.drawable.ic_info_blue_24dp);
                            break;
                    }

                    if (isDarkMode) {
                        dialogLayoutToaster.setBackgroundColor(ContextCompat.getColor(activity, R.color.dark_background));
                        textMessageToaster.setTextColor(ContextCompat.getColor(activity, R.color.md_white_1000));
                    }
                    break;
                }

                // --- RAINBOW ---
                case RAINBOW: {
                    layoutView = activity.getLayoutInflater().inflate(R.layout.dialog_rainbow, null);
                    View dialogLayoutRainbow = layoutView.findViewById(R.id.dialog_layout_rainbow);
                    ImageView dialogIconRainbow = layoutView.findViewById(R.id.dialog_icon_rainbow);
                    TextView textTitleRainbow = layoutView.findViewById(R.id.text_title_rainbow);
                    TextView textMessageRainbow = layoutView.findViewById(R.id.text_message_rainbow);
                    ImageView imageCloseRainbow = layoutView.findViewById(R.id.image_close_rainbow);

                    switch (dialogType) {
                        case ERROR:
                            dialogLayoutRainbow.setBackgroundColor(ContextCompat.getColor(activity, R.color.dialog_error));
                            dialogIconRainbow.setImageResource(R.drawable.ic_error_red_24dp);
                            break;
                        case SUCCESS:
                            dialogLayoutRainbow.setBackgroundColor(ContextCompat.getColor(activity, R.color.dialog_success));
                            dialogIconRainbow.setImageResource(R.drawable.ic_check_circle_green_24dp);
                            break;
                        case WARNING:
                            dialogLayoutRainbow.setBackgroundColor(ContextCompat.getColor(activity, R.color.dialog_warning));
                            dialogIconRainbow.setImageResource(R.drawable.ic_warning_orange_24dp);
                            break;
                        case INFO:
                            dialogLayoutRainbow.setBackgroundColor(ContextCompat.getColor(activity, R.color.dialog_info));
                            dialogIconRainbow.setImageResource(R.drawable.ic_info_blue_24dp);
                            break;
                    }

                    textMessageRainbow.setText(message);
                    textTitleRainbow.setText(title);
                    imageCloseRainbow.setOnClickListener(v -> onClickListener.onClick(this));

                    dialogBuilder.setView(layoutView);
                    alertDialog = dialogBuilder.create();
                    ;
                    chooseAnimation();
                    alertDialog.show();

                    if (alertDialog.getWindow() != null) {
                        alertDialog.getWindow().setGravity(Gravity.TOP);
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                                activity.getResources().getDimensionPixelSize(R.dimen.popup_height_emoji_dialog));
                    }
                    break;
                }

                // --- CONNECTIFY ---
                case CONNECTIFY: {
                    ImageView imgClose;
                    TextView textTitle;
                    TextView textMessage;
                    LinearLayout layoutDialog;

                    if (dialogType == DialogType.SUCCESS) {
                        layoutView = activity.getLayoutInflater().inflate(R.layout.dialog_connectify_success, null);
                        layoutDialog = layoutView.findViewById(R.id.dialog_layout_connectify_success);
                        imgClose = layoutView.findViewById(R.id.image_close_connectify_success);
                        textTitle = layoutView.findViewById(R.id.text_title_connectify_success);
                        textMessage = layoutView.findViewById(R.id.text_message_connectify_success);
                    } else {
                        layoutView = activity.getLayoutInflater().inflate(R.layout.dialog_connectify_error, null);
                        layoutDialog = layoutView.findViewById(R.id.dialog_layout_connectify_error);
                        imgClose = layoutView.findViewById(R.id.image_close_connectify_error);
                        textTitle = layoutView.findViewById(R.id.text_title_connectify_error);
                        textMessage = layoutView.findViewById(R.id.text_message_connectify_error);
                    }

                    textTitle.setText(title);
                    textMessage.setText(message);
                    imgClose.setOnClickListener(v -> onClickListener.onClick(this));

                    if (isDarkMode) {
                        layoutDialog.setBackgroundColor(ContextCompat.getColor(activity, R.color.dark_background));
                        textMessage.setTextColor(ContextCompat.getColor(activity, R.color.md_white_1000));
                    }

                    dialogBuilder.setView(layoutView);
                    alertDialog = dialogBuilder.create();
                    chooseAnimation();
                    alertDialog.show();

                    if (alertDialog.getWindow() != null) {
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.getWindow().setGravity(Gravity.TOP);
                        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                                WindowManager.LayoutParams.WRAP_CONTENT);
                    }
                    break;
                }

                // --- FLASH ---
                case FLASH: {
                    layoutView = activity.getLayoutInflater().inflate(R.layout.dialog_flash, null);
                    LinearLayout dialogFrameFlash = layoutView.findViewById(R.id.dialog_layout_flash);
                    ImageView imgIconFlash = layoutView.findViewById(R.id.img_icon_flash);
                    TextView dialogTitleFlash = layoutView.findViewById(R.id.dialog_title_flash);
                    TextView dialogMessageFlash = layoutView.findViewById(R.id.dialog_message_flash);
                    Button btnActionFlash = layoutView.findViewById(R.id.btn_action_flash);

                    if (dialogType == DialogType.SUCCESS) {
                        dialogFrameFlash.setBackgroundResource(R.drawable.rounded_green_gradient_bg);
                        imgIconFlash.setImageResource(R.drawable.circle_validation_success);
                    } else {
                        dialogFrameFlash.setBackgroundResource(R.drawable.rounded_red_gradient_bg);
                        imgIconFlash.setImageResource(R.drawable.circle_validation_error);
                    }

                    dialogMessageFlash.setText(message);
                    dialogTitleFlash.setText(title);
                    btnActionFlash.setOnClickListener(v -> onClickListener.onClick(this));

                    dialogBuilder.setView(layoutView);
                    alertDialog = dialogBuilder.create();
                    chooseAnimation();
                    alertDialog.show();

                    if (alertDialog.getWindow() != null) {
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.getWindow().setGravity(Gravity.CENTER);
                        alertDialog.getWindow().setLayout(
                                activity.getResources().getDimensionPixelSize(R.dimen.popup_width),
                                activity.getResources().getDimensionPixelSize(R.dimen.popup_height));
                    }
                    break;
                }

                // --- FLAT ---
                case FLAT: {
                    layoutView = activity.getLayoutInflater().inflate(R.layout.dialog_flat, null);
                    TextView dialogTitleFlat = layoutView.findViewById(R.id.dialog_title_flat);
                    TextView dialogMessageFlat = layoutView.findViewById(R.id.dialog_message_flat);
                    Button btnActionFlat = layoutView.findViewById(R.id.btn_action_flat);
                    ImageView dialogIconFlat = layoutView.findViewById(R.id.dialog_icon_flat);
                    FrameLayout dialogFrameFlat = layoutView.findViewById(R.id.dialog_frame_flat);
                    LinearLayout dialogLayoutFlat = layoutView.findViewById(R.id.dialog_layout_flat);

                    dialogMessageFlat.setText(message);
                    dialogTitleFlat.setText(title);
                    btnActionFlat.setOnClickListener(v -> onClickListener.onClick(this));

                    switch (dialogType) {
                        case ERROR:
                            dialogIconFlat.setImageResource(R.drawable.ic_error_red_24dp);
                            btnActionFlat.setBackgroundResource(R.drawable.btn_red_selector);
                            dialogFrameFlat.setBackgroundResource(R.drawable.rounded_rect_red);
                            break;
                        case SUCCESS:
                            dialogIconFlat.setImageResource(R.drawable.ic_check_circle_green_24dp);
                            btnActionFlat.setBackgroundResource(R.drawable.btn_green_selector);
                            dialogFrameFlat.setBackgroundResource(R.drawable.rounded_rect_green);
                            break;
                        case WARNING:
                            dialogIconFlat.setImageResource(R.drawable.ic_warning_orange_24dp);
                            btnActionFlat.setBackgroundResource(R.drawable.btn_yellow_selector);
                            dialogFrameFlat.setBackgroundResource(R.drawable.rounded_rect_yellow);
                            break;
                        case INFO:
                            dialogIconFlat.setImageResource(R.drawable.ic_info_blue_24dp);
                            btnActionFlat.setBackgroundResource(R.drawable.btn_blue_selector);
                            dialogFrameFlat.setBackgroundResource(R.drawable.rounded_rect_blue);
                            break;
                    }

                    if (isDarkMode) {
                        dialogLayoutFlat.setBackgroundResource(R.drawable.rounded_dark_bg);
                        dialogTitleFlat.setTextColor(ContextCompat.getColor(activity, R.color.md_white_1000));
                        dialogMessageFlat.setTextColor(ContextCompat.getColor(activity, R.color.md_white_1000));
                    }

                    dialogBuilder.setView(layoutView);
                    alertDialog = dialogBuilder.create();
                    chooseAnimation();
                    alertDialog.show();

                    if (alertDialog.getWindow() != null) {
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.getWindow().setGravity(Gravity.CENTER);
                        alertDialog.getWindow().setLayout(
                                activity.getResources().getDimensionPixelSize(R.dimen.popup_width),
                                activity.getResources().getDimensionPixelSize(R.dimen.popup_height));
                    }
                    break;
                }
            }

            alertDialog.setCancelable(isCancelable);
            if (gravity != Gravity.NO_GRAVITY && alertDialog.getWindow() != null) {
                alertDialog.getWindow().setGravity(gravity);
            }

            return new AestheticDialog();
        }
    }
}

