package com.yuanin.fuliclub.minePart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.adapter.adapter.DelegateAdapter;
import com.adapter.adapter.ItemData;
import com.adapter.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.mvvm.base.AbsLifecycleActivity;
import com.next.easytitlebar.view.EasyTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yuanin.fuliclub.R;
import com.yuanin.fuliclub.base.ReturnResult;
import com.yuanin.fuliclub.learnPart.CourseDetailsLoginActivity;
import com.yuanin.fuliclub.learnPart.OrderPayActivity;
import com.yuanin.fuliclub.minePart.bean.MyMessageListVo;
import com.yuanin.fuliclub.minePart.bean.MyMessageVo;
import com.yuanin.fuliclub.minePart.bean.PersonalInfoEntity;
import com.yuanin.fuliclub.util.AdapterPool;
import com.yuanin.fuliclub.util.RefreshHelper;
import com.yuanin.fuliclub.util.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyMessageActivity extends AbsLifecycleActivity<MyViewModel> implements RefreshHelper.OnHelperRefreshListener, RefreshHelper.OnHelperLoadMoreListener, OnItemClickListener {

    @BindView(R.id.titleBar)
    EasyTitleBar titleBar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.ll_empty_mesage)
    LinearLayout ll_empty_mesage;

    private WeakReference<MyMessageActivity> weakReference;
    private ItemData mItems;
    private RefreshHelper refreshHelper;
    protected DelegateAdapter adapter;
    private Context activity = MyMessageActivity.this;

    protected boolean isLoadMore = false;

    protected boolean isRefresh = false;
    private ArrayList<MyMessageVo> myMessageVos;



    @Override
    protected int getScreenMode() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_message;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);

        weakReference = new WeakReference<>(this);

//        loadManager.showSuccess();
        titleBar.setBackImageRes(R.drawable.black);
        titleBar.getBackLayout().setOnClickListener(v -> finish());

        mItems = new ItemData();
        refreshHelper = new RefreshHelper.Builder()
                .setRefreshLayout(refreshLayout)
                .setOnRefreshListener(this)
                .setOnLoadMoreListener(this)
                .build();
        adapter = createAdapter();
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(createLayoutManager());
        mRecyclerView.addOnScrollListener(onScrollListener);

        //模拟数据
        /*myMessageVos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            myMessageVos.add(new MyMessageVo());
        }
        setUiData(myMessageVos);*/

        mViewModel.getMessageList("1");

    }

    protected void setUiData(Collection<?> data) {
        if (!isLoadMore) {
            mItems.clear();
            isLoadMore = false;
            mItems.addAll(data);
            setData();
        } else {
            mItems.addAll(data);
            setMoreData();
        }
    }

    @Override
    protected void dataObserver() {
        registerSubscriber(MyRepository.EVENT_KEY_MESSAGE_LIST, ReturnResult.class).observe(this, returnResult -> {
            if (returnResult != null) {
                if (returnResult.isSuccess()) {
                    loadManager.showSuccess();
                    MyMessageListVo myMessageVoList = (MyMessageListVo) returnResult.getData();
                    List<MyMessageVo> data = myMessageVoList.getData();

                    setUiData(data);

                    //ToastUtils.showToast(returnResult.getMessage());
                } else {
                    ToastUtils.showToast(returnResult.getMessage());
                }

            }
        });
    }

    protected void setData() {

        if (mItems.size() > 0) {
            refreshLayout.setVisibility(View.VISIBLE);
            ll_empty_mesage.setVisibility(View.GONE);
        } else {
            refreshLayout.setVisibility(View.GONE);
            ll_empty_mesage.setVisibility(View.VISIBLE);
        }

        adapter.setDatas(mItems);
        adapter.notifyDataSetChanged();
        if (isRefresh) {
            refreshHelper.refreshComplete();
        }
    }

    protected void setMoreData() {
        adapter.notifyDataSetChanged();
        if (isLoadMore) {
            refreshHelper.loadMoreComplete();
        }
        isLoadMore = false;
    }

    private RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(activity);
    }

    private DelegateAdapter createAdapter() {
        return AdapterPool.newInstance().getMyMessageAdapter(activity).setOnItemClickListener(this).build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void onRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
        mViewModel.getMessageList("1");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mViewModel != null) {
            mViewModel.getMessageList("1");
        }
    }

    @Override
    public void onLoadMore(boolean isLoadMore, int pageIndex) {
        this.isLoadMore = isLoadMore;
        mViewModel.getMessageList(String.valueOf(pageIndex));
    }


    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (activity != null) {
                    Glide.with(activity).resumeRequests();
                }
            } else {
                if (activity != null) {
                    Glide.with(activity).pauseRequests();
                }
            }
        }
    };

    @Override
    public void onItemClick(View view, int position, Object object) {
        if (object != null) {
            if (object instanceof MyMessageVo) {

                mViewModel.upDateMessageStatus(String.valueOf(((MyMessageVo) object).getId()), "2");

                Intent intent = new Intent(this, CourseDetailsLoginActivity.class);
                intent.putExtra("courseId", String.valueOf(((MyMessageVo) object).getCourseId()));
                startActivity(intent);
            }
        }
    }
}
