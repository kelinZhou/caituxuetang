package com.yuanin.fuliclub.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.yuanin.fuliclub.R;

/**
 * 倒计时TextView
 * Created by weijing on 2017-08-21 14:43.
 */

@SuppressLint("AppCompatCustomView")
public class CountDownTextView extends TextView {

    /**
     * 提示文字
     */
    private String mHintText = "重新发送";

    /**
     * 倒计时时间
     */
    private long mCountDownMillis = 60_000;

    /**
     * 剩余倒计时时间
     */
    private long mLastMillis;
    /**
     * 间隔时间差(两次发送handler)
     */
    private long mIntervalMillis = 1_000;
    /**
     * 开始倒计时code
     */
    private final int MSG_WHAT_START = 10_010;
    /**
     * 可用状态下字体颜色Id
     */
    private int usableColorId = R.color.theme_color;
    /**
     * 不可用状态下字体颜色Id
     */
    private int unusableColorId = R.color.color_orang_f4;


    public CountDownTextView(Context context) {
        super(context);
    }

    public CountDownTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CountDownTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_WHAT_START:
//                    Log.e("l", mLastMillis + "");
                    if (mLastMillis > 0) {
                        setUsable(false);
                        mLastMillis -= mIntervalMillis;
                        mHandler.sendEmptyMessageDelayed(MSG_WHAT_START, mIntervalMillis);
                    } else {
                        setUsable(true);
                    }
                    break;
            }
        }
    };


    /**
     * 设置是否可用
     *
     * @param usable
     */
    public void setUsable(boolean usable) {


        if (usable) {
            //可用
            if (!isClickable()) {
                setClickable(usable);
                setTextColor(getResources().getColor(usableColorId));
                setBackground(getResources().getDrawable(R.drawable.shape_count_down_bg));
                setGravity(Gravity.CENTER);
                setText(mHintText);
            }
        } else {
            //不可用
            if (isClickable()) {
                setClickable(usable);
                setTextColor(getResources().getColor(unusableColorId));
                setGravity(Gravity.CENTER_VERTICAL| Gravity.RIGHT);
                setBackground(null);
            }
            setText( mLastMillis / 1000+"S");
        }

    }

    /**
     * 设置倒计时颜色
     *
     * @param usableColorId   可用状态下的颜色
     * @param unusableColorId 不可用状态下的颜色
     */
    public void setCountDownColor(@ColorRes int usableColorId, @ColorRes int unusableColorId) {
        this.usableColorId = usableColorId;
        this.unusableColorId = unusableColorId;
    }

    /**
     * 设置倒计时时间
     *
     * @param millis 毫秒值
     */
    public void setCountDownMillis(long millis) {
        mCountDownMillis = millis;
    }

    /**
     * 开始倒计时
     */
    public void start() {
        mLastMillis = mCountDownMillis;
        mHandler.sendEmptyMessage(MSG_WHAT_START);
    }

    /**
     * 重置倒计时
     */
    public void reset() {
        mLastMillis = 0;
        mHandler.sendEmptyMessage(MSG_WHAT_START);
    }

    @Override
    public void setOnClickListener(@Nullable final OnClickListener onClickListener) {
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeMessages(MSG_WHAT_START);
                start();
                onClickListener.onClick(v);
            }
        });

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(MSG_WHAT_START);
    }
}