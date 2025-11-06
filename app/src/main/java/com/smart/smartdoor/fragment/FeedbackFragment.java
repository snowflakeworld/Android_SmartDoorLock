package com.smart.smartdoor.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.smartdoor.R;
import com.smart.smartdoor.items.FeedbackItem;
import com.smart.smartdoor.service.PermanentService;
import com.smart.smartdoor.task.feedback.AsyncTaskFeedbackList;
import com.smart.smartdoor.task.feedback.AsyncTaskSendFeedback;
import com.smart.smartdoor.task.feedback.FeedbackLoadListener;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.User;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;


public class FeedbackFragment extends Fragment {

    View mRootView = null;
    RecyclerView mRecyclerMsg = null;
    FeedbackAdapter mAdapter = null;
    List<FeedbackItem> mFeedbackArr = new ArrayList<>();
    long mTotalCount = -1;
    int mTotalPage = -1;
    int mCurPage = 0;
    boolean bHasNextPage = false;
    private PtrClassicFrameLayout mPtrFrame;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_feedback, container, false);

        initComponents();
        loadData();

        Global.fixTheAllViews((ViewGroup) mRootView);

        new Handler().postDelayed(() -> {
            if (User.instance.password.isEmpty()) {
                User.instance.login(getActivity(), () -> PermanentService.startService(getActivity()));
            }
        }, 300);

        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;
    }

    private void initComponents() {
        ViewGroup.MarginLayoutParams mParam = (ViewGroup.MarginLayoutParams) mRootView.findViewById(R.id.topbar).getLayoutParams();
        mParam.topMargin = Global.getStatusBarHeight(getActivity());
        mRootView.findViewById(R.id.topbar).setLayoutParams(mParam);

        mRootView.findViewById(R.id.btn_back).setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        mRootView.findViewById(R.id.btn_send).setOnClickListener(v -> {
            sendFeedback();
        });

        mPtrFrame = mRootView.findViewById(R.id.ptr_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (!bHasNextPage) {
                    Global.showMyToast(getActivity(), R.string.warning_no_more_data, true);
                    mPtrFrame.refreshComplete();
                } else {
                    mCurPage++;
                    loadData();
                }
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mRecyclerMsg, header);
            }
        });
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        mPtrFrame.setPullToRefresh(false);
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.setShouldShowLastUpdate(false);


        mRecyclerMsg = mRootView.findViewById(R.id.recycler_list);
        mRecyclerMsg.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, true));
        mAdapter = new FeedbackAdapter();
        mRecyclerMsg.setAdapter(mAdapter);
    }

    private void sendFeedback() {
        String msg = ((EditText) mRootView.findViewById(R.id.edit_feedback)).getText().toString();
        if (msg.trim().isEmpty()) {
            Global.showMyToast(getActivity(), R.string.feedback_warning_send_data_empty, true);
            return;
        }

        AsyncTaskSendFeedback task = new AsyncTaskSendFeedback(getActivity(), User.instance.cId, msg, new Runnable() {
            @Override
            public void run() {
                ((EditText) mRootView.findViewById(R.id.edit_feedback)).setText("");

                mTotalCount = mTotalPage = -1;
                mCurPage = 0;
                bHasNextPage = false;

                mFeedbackArr.clear();

                loadData();
            }
        });
        task.execute();
    }

    private void loadData() {
        AsyncTaskFeedbackList task = new AsyncTaskFeedbackList(getActivity(), User.instance.cId, mCurPage, Constants.FEEDBACK_PAGE_SIZE, new FeedbackLoadListener() {
            @Override
            public void onResult(List<FeedbackItem> data, long totalCount, int totalPage, int curPage, boolean hasNextPage) {
                mTotalCount = totalCount;
                mTotalPage = totalPage;
                mCurPage = curPage;
                bHasNextPage = hasNextPage;

                mFeedbackArr.addAll(data);

                mAdapter.notifyDataSetChanged();

                if (data.size() > 0)
                    mRecyclerMsg.smoothScrollToPosition(mFeedbackArr.size() - data.size());

                mRootView.findViewById(R.id.empty).setVisibility(mTotalCount > 0 ? View.GONE : View.VISIBLE);

                if (mPtrFrame.isRefreshing())
                    mPtrFrame.refreshComplete();
            }
        });
        task.execute();
    }

    private class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ItemViewHolder> {

        @NonNull
        @Override
        public FeedbackAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = (viewType == 0 ? LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_out, parent, false)
                    : LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_in, parent, false));
            Global.fixTheAllViews((ViewGroup) view);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            FeedbackItem msgItem = mFeedbackArr.get(position);

            holder.textMsg.setMaxWidth(Global.getScreenWidth(getActivity()) * 2 / 3);

            holder.textMsg.setText(msgItem.getMsg());
            holder.textDatetime.setText(Global.getFormatDatetime(msgItem.getCreateAt()));
            holder.iconUser.setImageResource(msgItem.getIsUser() == 1 ? R.drawable.ic_user_flaticon : R.drawable.ic_building_flaticon);
        }

        @Override
        public int getItemCount() {
            return mFeedbackArr.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mFeedbackArr.get(position).getIsUser() == 1 ? 0 : 1;
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            public TextView textMsg;
            public TextView textDatetime;
            public ImageView iconUser;

            public ItemViewHolder(View itemView) {
                super(itemView);
                textMsg = itemView.findViewById(R.id.content);
                textDatetime = itemView.findViewById(R.id.datetime);
                iconUser = itemView.findViewById(R.id.icon);
            }
        }
    }
}