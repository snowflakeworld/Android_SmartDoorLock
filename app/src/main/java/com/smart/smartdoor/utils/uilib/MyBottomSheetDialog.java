package com.smart.smartdoor.utils.uilib;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.smart.smartdoor.R;
import com.smart.smartdoor.utils.Global;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class MyBottomSheetDialog {
    private Context context;
    private List<String> strArr;
    private BottomSheetDialog dialog;
    private OnItemSelectListener listener;
    private ListView listView;

    public MyBottomSheetDialog(Context context, String title, List<String> strArr, OnItemSelectListener listener) {
        this.context = context;
        this.strArr = strArr;
        this.listener = listener;

        dialog = new BottomSheetDialog(context, R.style.MyDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null);
        dialog.setContentView(view);

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.WHITE);
        gradientDrawable.setCornerRadius(Global.dp(context, 24));

        RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(Color.WHITE), gradientDrawable, null);

        view.findViewById(R.id.sdialog_main).setBackground(rippleDrawable);

        ((TextView) dialog.findViewById(R.id.txt_title)).setText(title);
        listView = view.findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, strArr);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            listener.onItemSelected(position);
            dialog.dismiss();
        });
    }

    public void show() {
        dialog.show();
    }

    public interface OnItemSelectListener {
        void onItemSelected(int pos);
    }
}
