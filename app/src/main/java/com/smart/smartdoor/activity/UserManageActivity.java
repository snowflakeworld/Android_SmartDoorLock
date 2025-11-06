package com.smart.smartdoor.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.smartdoor.R;
import com.smart.smartdoor.items.UserItem;
import com.smart.smartdoor.task.manage.AsyncTaskUserManageList;
import com.smart.smartdoor.task.manage.AsyncTaskUserManageProcess;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class UserManageActivity extends BaseActivity {

    private List<UserItem> userItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Global.setStatusBarColorResId(this, R.color.white, true);
        Global.setNavigationBarColorResId(this, R.color.white, true);

        setContentView(R.layout.activity_usermanage);

        initComponents();
        loadData();

        Global.fixTheAllViews((ViewGroup) getWindow().getDecorView());
    }

    private void initComponents() {
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            onBackPressed();
        });

        recyclerView = findViewById(R.id.recycler_list);
        userAdapter = new UserAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(userAdapter);
    }

    private void loadData() {
        AsyncTaskUserManageList task = new AsyncTaskUserManageList(this, User.instance.cId, User.instance.password, userItems, new Runnable() {
            @Override
            public void run() {
                showData();
            }
        });
        task.execute();
    }

    private void showData() {
        if (userItems.size() == 0) {
            findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.empty).setVisibility(View.GONE);
        }

        userAdapter.notifyDataSetChanged();
    }

    private void process(long id, int state) {
        Global.showAlertDialog(UserManageActivity.this, Global.getString(UserManageActivity.this, R.string.usermanage_confirm_title), Global.getString(UserManageActivity.this, R.string.usermanage_confirm_content),
                Global.getString(UserManageActivity.this, R.string.button_ok), Global.getString(UserManageActivity.this, R.string.button_cancel), () -> {
                    AsyncTaskUserManageProcess task = new AsyncTaskUserManageProcess(UserManageActivity.this, User.instance.cId, User.instance.password, id, state, () -> {
                        loadData();
                    });
                    task.execute();
                }, null);
    }

    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.ItemViewHolder> {

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            Global.fixTheAllViews((ViewGroup) view);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            UserItem item = userItems.get(position);

            if (item.getRoleType().equals(Constants.AUTH_ADMIN)) {
                holder.cardRole.setCardBackgroundColor(Global.getColor(UserManageActivity.this, R.color.home_card_color_1));
                holder.txtRole.setText(Global.getString(UserManageActivity.this, R.string.register_auth_type_admin));
            } else {
                holder.cardRole.setCardBackgroundColor(Global.getColor(UserManageActivity.this, R.color.home_card_color_3));
                holder.txtRole.setText(Global.getString(UserManageActivity.this, R.string.register_auth_type_user));
            }
            holder.txtUser.setText(item.getName() + "(" + item.getCid() + ")");
            holder.txtDatetime.setText(Global.getFormatDatetime(item.getUpdateAt()));

            switch (item.getState()) {
                case Constants.STATE_DISABLE:
                    holder.cardState.setStrokeColor(Global.getColor(UserManageActivity.this, R.color.grey50));
                    holder.txtState.setText(R.string.state_disabled);
                    holder.txtState.setTextColor(Global.getColor(UserManageActivity.this, R.color.grey50));
                    holder.btnReject.setVisibility(View.GONE);
                    holder.btnAccept.setVisibility(View.VISIBLE);
                    holder.divider.setVisibility(View.GONE);
                    holder.btnAccept.setText(R.string.button_enable);
                    break;
                case Constants.STATE_REQUEST:
                    holder.cardState.setStrokeColor(Global.getColor(UserManageActivity.this, R.color.red400));
                    holder.txtState.setText(R.string.state_requested);
                    holder.txtState.setTextColor(Global.getColor(UserManageActivity.this, R.color.red400));
                    holder.btnReject.setVisibility(View.VISIBLE);
                    holder.btnAccept.setVisibility(View.VISIBLE);
                    holder.divider.setVisibility(View.VISIBLE);
                    holder.btnReject.setText(R.string.button_reject);
                    holder.btnAccept.setText(R.string.button_accept);
                    break;
                case Constants.STATE_NORMAL:
                    holder.cardState.setStrokeColor(Global.getColor(UserManageActivity.this, R.color.blue400));
                    holder.txtState.setText(R.string.state_accepted);
                    holder.txtState.setTextColor(Global.getColor(UserManageActivity.this, R.color.blue400));
                    holder.btnReject.setVisibility(View.VISIBLE);
                    holder.btnAccept.setVisibility(View.GONE);
                    holder.divider.setVisibility(View.GONE);
                    holder.btnReject.setText(R.string.button_disable);
                    break;
            }

            holder.btnReject.setOnClickListener(v -> process(item.getId(), item.getState() == Constants.STATE_REQUEST ? Constants.STATE_DELETE : Constants.STATE_DISABLE));
            holder.btnAccept.setOnClickListener(v -> process(item.getId(), Constants.STATE_NORMAL));
        }

        @Override
        public int getItemCount() {
            return userItems.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            public MaterialCardView cardRole;
            public MaterialCardView cardState;
            public TextView txtRole;
            public TextView txtState;
            public TextView txtUser;
            public TextView txtDatetime;
            public MaterialButton btnAccept;
            public MaterialButton btnReject;
            public View divider;

            public ItemViewHolder(View itemView) {
                super(itemView);
                cardRole = itemView.findViewById(R.id.card_role);
                txtRole = itemView.findViewById(R.id.txt_role);
                cardState = itemView.findViewById(R.id.card_state);
                txtState = itemView.findViewById(R.id.txt_state);
                txtUser = itemView.findViewById(R.id.txt_user);
                txtDatetime = itemView.findViewById(R.id.txt_datetime);
                btnAccept = itemView.findViewById(R.id.btn_accept);
                btnReject = itemView.findViewById(R.id.btn_reject);
                divider = itemView.findViewById(R.id.divider);
            }
        }
    }
}
