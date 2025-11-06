package com.smart.smartdoor.activity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.smartdoor.R;
import com.smart.smartdoor.items.HistoryItem;
import com.smart.smartdoor.task.history.AsyncTaskGetHistory;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.User;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity {

    private List<HistoryItem> historyItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Global.setStatusBarColorResId(this, R.color.card_background_color, true);
        Global.setNavigationBarColorResId(this, R.color.white, true);

        setContentView(R.layout.activity_history);

        initComponents();
        loadData();

        Global.fixTheAllViews((ViewGroup) getWindow().getDecorView());
    }

    private void initComponents() {
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            onBackPressed();
        });
        findViewById(R.id.btn_from).setOnClickListener(v -> {
            Global.showDatePickerDialog(HistoryActivity.this, findViewById(R.id.btn_from));
        });
        findViewById(R.id.btn_to).setOnClickListener(v -> {
            Global.showDatePickerDialog(HistoryActivity.this, findViewById(R.id.btn_to));
        });
        findViewById(R.id.btn_refresh).setOnClickListener(v -> {
            loadData();
        });

        ((MaterialButton) findViewById(R.id.btn_from)).setText(Global.getFormatDate(Global.getCurrentDate()));
        ((MaterialButton) findViewById(R.id.btn_to)).setText(Global.getFormatDate(Global.getCurrentDate()));

        recyclerView = findViewById(R.id.recycler_list);
        historyAdapter = new HistoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(historyAdapter);
    }

    private void loadData() {
        String[] splits = ((MaterialButton) findViewById(R.id.btn_from)).getText().toString().split("\\.");
        int year = Integer.parseInt(splits[0]);
        int month = Integer.parseInt(splits[1]);
        int day = Integer.parseInt(splits[2]);
        String fromDate = String.format("%d-%02d-%02d", year, month, day);

        splits = ((MaterialButton) findViewById(R.id.btn_to)).getText().toString().split("\\.");
        year = Integer.parseInt(splits[0]);
        month = Integer.parseInt(splits[1]);
        day = Integer.parseInt(splits[2]);
        String toDate = String.format("%d-%02d-%02d", year, month, day);

        AsyncTaskGetHistory task = new AsyncTaskGetHistory(this, User.instance.cId, fromDate, toDate, historyItems, new Runnable() {
            @Override
            public void run() {
                showData();
            }
        });
        task.execute();
    }

    private void showData() {
        if (historyItems.size() == 0) {
            findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.empty).setVisibility(View.GONE);
        }

        historyAdapter.notifyDataSetChanged();
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ItemViewHolder> {

        @NonNull
        @Override
        public HistoryAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
            Global.fixTheAllViews((ViewGroup) view);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryAdapter.ItemViewHolder holder, int position) {
            HistoryItem item = historyItems.get(position);

            String actionDisplay = "";
            switch (item.getAction()) {
                case Constants.ACTION_REQUEST:
                    holder.img1.setColorFilter(Global.getColor(HistoryActivity.this, R.color.red400), PorterDuff.Mode.SRC_IN);
                    actionDisplay = Constants.ACTION_DISPLAY_REQUEST;
                    break;
                case Constants.ACTION_OPEN:
                    holder.img1.setColorFilter(Global.getColor(HistoryActivity.this, R.color.blue400), PorterDuff.Mode.SRC_IN);
                    actionDisplay = Constants.ACTION_DISPLAY_OPEN;
                    break;
            }
            if (item.getName() != null && !item.getName().isEmpty())
                actionDisplay += " (" + item.getName() + ")";

            switch (item.getMode()) {
                case Constants.OPEN_TYPE_FACE:
                    holder.img2.setImageResource(R.drawable.ic_face_viewfinder_flaticon_outline);
                    break;
                case Constants.OPEN_TYPE_FINGER:
                    holder.img2.setImageResource(R.drawable.ic_fingerprint_flaticon);
                    break;
                case Constants.OPEN_TYPE_KEY:
                    holder.img2.setImageResource(R.drawable.ic_key_flaticon_outline);
                    break;
                case Constants.OPEN_TYPE_PALM:
                    holder.img2.setImageResource(R.drawable.ic_hand_dots_flaticon_outline);
                    break;
                case Constants.OPEN_TYPE_PHONE:
                    holder.img2.setImageResource(R.drawable.ic_mobile_notch_flaticon_outline);
                    break;
                case Constants.OPEN_TYPE_PIN:
                    holder.img2.setImageResource(R.drawable.ic_password_alt_flaticon);
                    break;
            }

            holder.txtType.setText(actionDisplay);
            holder.txtDatetime.setText(Global.getFormatDatetime(item.getCreateAt()));
        }

        @Override
        public int getItemCount() {
            return historyItems.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            public ImageView img1;
            public ImageView img2;
            public TextView txtType;
            public TextView txtDatetime;

            public ItemViewHolder(View itemView) {
                super(itemView);
                img1 = itemView.findViewById(R.id.icon1);
                img2 = itemView.findViewById(R.id.icon2);
                txtType = itemView.findViewById(R.id.txt_type);
                txtDatetime = itemView.findViewById(R.id.txt_datetime);
            }
        }
    }
}
