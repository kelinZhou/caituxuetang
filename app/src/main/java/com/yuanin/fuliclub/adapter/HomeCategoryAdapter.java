package com.yuanin.fuliclub.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.yuanin.fuliclub.R;
import com.yuanin.fuliclub.base.BaseViewHolder;
import com.yuanin.fuliclub.homePart.banner.CatagoryInfoVo;

import java.util.List;

/**
 * @author：tqzhang  on 18/6/26 14:05
 */
public class HomeCategoryAdapter extends BaseRecyclerAdapter<CatagoryInfoVo> {


    public HomeCategoryAdapter(Context context, @Nullable List<CatagoryInfoVo> list, int itemLayoutId) {
        super(context, list, R.layout.item_classify);
    }

    @Override
    protected void convert(BaseViewHolder holder, final CatagoryInfoVo catagoryInfoVo, int position, List payloads) {
        ImageView categroyIcon = holder.getView(R.id.iv_classify);
        TextView categroyName = holder.getView(R.id.tv_classify);
        Glide.with(getContext())
                .load(catagoryInfoVo.resId)
                .into(categroyIcon);
        categroyName.setText(catagoryInfoVo.title);

    }
}
