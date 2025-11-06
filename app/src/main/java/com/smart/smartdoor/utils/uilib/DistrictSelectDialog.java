package com.smart.smartdoor.utils.uilib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.smartdoor.R;
import com.smart.smartdoor.dto.response.SearchDistrictResponse;
import com.smart.smartdoor.task.user.AsyncTaskSearchDistricts;
import com.smart.smartdoor.task.user.SearchDistrictListener;
import com.smart.smartdoor.items.DistrictItem;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.uilib.alerts.dialog.CustomSDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class DistrictSelectDialog {
    private Context context;
    private DistrictSelectListener listener;

    private int nowDistrictId = -1;
    private CustomSDialog addressDialog = null;
    private List<DistrictItem> listDistrict = new ArrayList<>();
    private boolean[] arrayCheck = null;
    private List<DistrictItem> listSelected = new ArrayList<>();

    public DistrictSelectDialog(Context context) {
        this.context = context;
    }

    public void setListener(DistrictSelectListener listener) {
        this.listener = listener;
    }

    public void show() {
        addressDialog = new CustomSDialog(context, R.layout.dialog_select_address);
        addressDialog.updateCorner(Global.getColor(context, R.color.white));
        View dialogView = addressDialog.getView();
        ((TextView) dialogView.findViewById(R.id.sdialog_title)).setText(R.string.register_select_address_title);
        ((TextView) dialogView.findViewById(R.id.txt_button)).setText(R.string.register_select_address_back);
        ((TextView) dialogView.findViewById(R.id.sdialog_negative)).setText(R.string.button_cancel);
        ((MaterialButton) dialogView.findViewById(R.id.sdialog_positive)).setText(R.string.button_ok);

        dialogView.findViewById(R.id.sdialog_negative).setOnClickListener((v) -> {
            addressDialog.dismiss();
        });
        dialogView.findViewById(R.id.sdialog_positive).setOnClickListener((v) -> {
            if (nowDistrictId == -1) {
                Global.showMyToast(context, R.string.register_select_address_warning, true);
            } else {
                listener.onSelectDistrict(nowDistrictId, getSelectedDistrictInfo());
                addressDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.btn_back).setOnClickListener((v) -> {
            popSelectedDistrict();
        });

        addressDialog.show();

        nowDistrictId = -1;
        listSelected.clear();

        searchDistrict();
    }

    private String getSelectedDistrictInfo() {
        String result = "";
        for (DistrictItem item : listSelected)
            result += " " + item.getName();
        return result;
    }

    private void popSelectedDistrict() {
        if (listSelected.size() == 0)
            return;
        if (listSelected.size() > 0)
            listSelected.remove(listSelected.size() - 1);
        if (listSelected.size() == 0) {
            nowDistrictId = -1;
            searchDistrict();
        } else {
            nowDistrictId = listSelected.get(listSelected.size() - 1).getId();
            searchDistrict();
        }
    }

    private void resetSelectDistrictInfo() {
        ((TextView) addressDialog.getView().findViewById(R.id.txt_description)).setText(
                listSelected.size() == 0 ?
                        "" :
                        getSelectedDistrictInfo()
        );
    }

    private void searchDistrict() {
        resetSelectDistrictInfo();

        addressDialog.getView().findViewById(R.id.progress).setVisibility(View.VISIBLE);
        AsyncTaskSearchDistricts task = new AsyncTaskSearchDistricts(context, nowDistrictId, false, new SearchDistrictListener() {
            @Override
            public void onSearched(SearchDistrictResponse response) {
                addressDialog.getView().findViewById(R.id.progress).setVisibility(View.GONE);
                resetAddressCandidateRecycler(response);
            }

            @Override
            public void onFailed() {
                addressDialog.getView().findViewById(R.id.progress).setVisibility(View.GONE);
            }
        });
        task.execute();
    }

    private void resetAddressCandidateRecycler(SearchDistrictResponse response) {
        if (addressDialog == null || !addressDialog.isShowing())
            return;

        listDistrict.clear();
        for (DistrictItem district : response.getChildDistricts())
            listDistrict.add(district);
        arrayCheck = new boolean[listDistrict.size()];
        for (int i = 0; i < listDistrict.size(); ++i)
            arrayCheck[i] = false;

        View dialogView = addressDialog.getView();
        RecyclerView recyclerDistrict = dialogView.findViewById(R.id.recycler_list);
        recyclerDistrict.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recyclerDistrict.setAdapter(new AddressCandidateAdapter());
    }

    public interface DistrictSelectListener {
        void onSelectDistrict(int districtId, String districtInfo);
    }

    private class AddressCandidateAdapter extends RecyclerView.Adapter<AddressCandidateAdapter.ItemViewHolder> {

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_candidate_district, parent, false);
            Global.fixTheAllViews((ViewGroup) view);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            DistrictItem districtItem = listDistrict.get(position);

            holder.textName.setText(districtItem.getName());
            holder.background.setBackgroundColor(Global.getColor(context, arrayCheck[position] ? R.color.grey85 : R.color.transparent));

            holder.itemView.setOnClickListener(v -> {
                nowDistrictId = districtItem.getId();

                for (int i = 0; i < getItemCount(); ++i)
                    arrayCheck[i] = false;

                if (districtItem.getHasChild() == 1) {
                    listSelected.add(districtItem);
                    searchDistrict();
                } else {
                    arrayCheck[position] = true;
                    if (listSelected.size() == 0)
                        listSelected.add(districtItem);
                    if (listSelected.get(listSelected.size() - 1).getParentId() == districtItem.getParentId())
                        listSelected.remove(listSelected.size() - 1);
                    listSelected.add(districtItem);
                    resetSelectDistrictInfo();

                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listDistrict.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            public TextView textName;
            public View background;

            public ItemViewHolder(View itemView) {
                super(itemView);
                textName = itemView.findViewById(R.id.text);
                background = itemView.findViewById(R.id.background);
            }
        }
    }
}
