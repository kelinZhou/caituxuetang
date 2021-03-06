package com.yuanin.fuliclub.learnPart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.mvvm.base.AbsLifecycleActivity;
import com.yuanin.fuliclub.R;
import com.yuanin.fuliclub.adapter.ViewPagerFragmentAdapter;
import com.yuanin.fuliclub.base.ReturnResult;
import com.yuanin.fuliclub.base.ReturnResult2;
import com.yuanin.fuliclub.config.StaticMembers;
import com.yuanin.fuliclub.coursePart.bean.ClassInfoVo;
import com.yuanin.fuliclub.coursePart.bean.CourseDetailsVo;
import com.yuanin.fuliclub.coursePart.bean.CourseInfo;
import com.yuanin.fuliclub.coursePart.bean.CourseStartTimeListVo;
import com.yuanin.fuliclub.homePart.banner.BottomBackgroundVo;
import com.yuanin.fuliclub.loginRegister.LoginActivity;
import com.yuanin.fuliclub.util.DateUtil;
import com.yuanin.fuliclub.util.DensityUtil;
import com.yuanin.fuliclub.util.PopupWindowUtils;
import com.yuanin.fuliclub.util.RoundedCornersTransformation;
import com.yuanin.fuliclub.util.ToastUtils;
import com.yuanin.fuliclub.util.ViewPagerUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CourseDetailsLoginActivity extends AbsLifecycleActivity<CourseViewModel> {
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.classInfo)
    TextView classInfo;
    @BindView(R.id.ivItemCourseImage)
    ImageView ivItemCourseImage;
    @BindView(R.id.ivCourseBg)
    ImageView ivCourseBg;
    @BindView(R.id.tvCourseName)
    TextView tvCourseName;
    @BindView(R.id.tvItemCourseSlogan)
    TextView tvItemCourseSlogan;
    @BindView(R.id.rlCourseHeader)
    RelativeLayout rlCourseHeader;
    @BindView(R.id.magic_indicator1)
    MagicIndicator magicIndicator;
    @BindView(R.id.mViewPager)
    ViewPager mViewPager;
    @BindView(R.id.tvCoursePrice)
    TextView tvCoursePrice;
    @BindView(R.id.tvOriginalPrice)
    TextView tvOriginalPrice;
    @BindView(R.id.tvCourseTime)
    TextView tvCourseTime;
    @BindView(R.id.rl_price_info)
    RelativeLayout rl_price_info;
    @BindView(R.id.clMain)
    ConstraintLayout clMain;

   /* @BindView(R.id.titleBar)
    EasyTitleBar titleBar;
*/

    private List<Fragment> fragmentList;
    private static final String[] CHANNELS = new String[]{"课程目录", "课程笔记"};
    private List<String> mDataList = Arrays.asList(CHANNELS);

    private WeakReference<CourseDetailsLoginActivity> weakReference;
    private CourseDetailListFragment courseDetailListFragment;
    private String courseId;
    private CourseDetailsVo courseDetails;
    private Context mContext = CourseDetailsLoginActivity.this;
    private View popuClassInfo;
    private View popuClassWaiting;
    private ClassInfoVo classInfoVo;
    private CourseDetailNoteListFragment courseDetailNoteListFragment;

    @Override
    protected int getScreenMode() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_course_details_buyed;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //状态栏颜色设置为透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);

            //去除半透明状态栏(如果有)
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：让内容显示到状态栏
            //SYSTEM_UI_FLAG_LAYOUT_STABLE：状态栏文字显示白色
            //SYSTEM_UI_FLAG_LIGHT_STATUS_BAR：状态栏文字显示黑色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }


        weakReference = new WeakReference<>(this);

        loadManager.showSuccess();


        initViewPager();
        initMagicIndicator();

        courseId = getIntent().getStringExtra("courseId");
        if (!TextUtils.isEmpty(courseId)) {
            if (StaticMembers.IS_NEED_LOGIN) {
                mViewModel.getCourseDetails(courseId);
            } else {
                mViewModel.getCourseDetailsLogin(courseId);
            }

        }

        if (StaticMembers.IS_NEED_LOGIN) {
            mViewModel.getCoursrKonbbleList(courseId);
        } else {
            mViewModel.getCoursrKonbbleListLogin(courseId);
        }


        //手动控制
        //FloatWindow.get().hide();

        popuClassInfo = View.inflate(this, R.layout.popupwindow_class_info, null);
        popuClassWaiting = View.inflate(this, R.layout.popupwindow_class_waiting, null);
    }


    @Override
    protected void dataObserver() {
        super.dataObserver();

        registerSubscriber(CourseRepository.EVENT_KEY_COURSE_DETAILS, ReturnResult.class).observe(this, returnResult -> {
            if (returnResult != null) {
                if (returnResult.isSuccess()) {
                    courseDetails = (CourseDetailsVo) returnResult.getData();
                    //setCourseInfo(courseDetails);
                    courseDetailNoteListFragment.setDatas(courseId);
                    courseDetailListFragment.setDatas(courseId, true);

                } else {
                    ToastUtils.showToast(returnResult.getMessage());
                }
            }
        });

        registerSubscriber(CourseRepository.EVENT_KEY_COURSE_KNOBBLE_LIST_LOGIN, ReturnResult2.class).observe(this, returnResult -> {
            if (returnResult != null) {
                if (returnResult.isSuccess()) {
                    List<CourseKnobbleInfoVo> knobbleInfoVoList = (List<CourseKnobbleInfoVo>) returnResult.getData();
                    CourseInfo course = returnResult.getCourse();
                    setCourseInfo2(course);

                    mViewModel.getUserClassInfo(course.getPeriodsId());

                } else {
                    ToastUtils.showToast(returnResult.getMessage());
                }
            }
        });

        registerSubscriber(CourseRepository.EVENT_KEY_COURSE_CLASS_INFO, ReturnResult.class).observe(this, returnResult -> {
            if (returnResult != null) {
                if (returnResult.isSuccess()) {
                    classInfoVo = (ClassInfoVo) returnResult.getData();
                } else {
                    ToastUtils.showToast(returnResult.getMessage());
                }
            }
        });

        registerSubscriber(CourseRepository.EVENT_KEY_COURSE_DETAILS_LOGIN, ReturnResult.class).observe(this, returnResult -> {
            if (returnResult != null) {
                if (returnResult.isSuccess()) {
                    courseDetails = (CourseDetailsVo) returnResult.getData();
                    //setCourseInfo(courseDetails);
                    courseDetailNoteListFragment.setDatas(courseId);
                    courseDetailListFragment.setDatas(courseId , true);
                } else {
                    ToastUtils.showToast(returnResult.getMessage());
                }
            }
        });


        registerSubscriber(CourseRepository.EVENT_KEY_COURSE_START_TIME_LIST, ReturnResult.class).observe(this, returnResult -> {
            if (returnResult != null) {
                if (returnResult.isSuccess()) {
                    List<CourseStartTimeListVo> startTimeList = (List<CourseStartTimeListVo>) returnResult.getData();

                    if (startTimeList.size() > 0) {
                        SelectTimeDialogFragment.show(getSupportFragmentManager(), courseDetails.getCourseName(),startTimeList, new SelectTimeDialogFragment.OnSelectTimeListener() {
                            @Override
                            public void selectTime(CourseStartTimeListVo time) {
                                //TODO 跳转订单
                                Intent intent = new Intent(mContext, OrderPayActivity.class);
                                intent.putExtra("courseId", courseId);
                                intent.putExtra("periodsId", String.valueOf(time.getId()));
                                startActivity(intent);
                            }
                        });
                    } else {
                        ToastUtils.showToast("当前没有开课日期。");
                    }

                } else {
                    ToastUtils.showToast(returnResult.getMessage());
                }
            }
        });
    }

    private void setCourseInfo2(CourseInfo course) {

        tvCourseName.setText(course.getCourseName());
        tvItemCourseSlogan.setText(course.getCourseTitle());
// 圆角图片 new RoundedCornersTransformation 参数为 ：半径 , 外边距 , 圆角方向(ALL,BOTTOM,TOP,RIGHT,LEFT,BOTTOM_LEFT等等)
        //顶部左边圆角
        RoundedCornersTransformation transformation = new RoundedCornersTransformation
                (8, 0, RoundedCornersTransformation.CornerType.BOTTOM_LEFT);
        //顶部右边圆角
        RoundedCornersTransformation transformation1 = new RoundedCornersTransformation
                (8, 0, RoundedCornersTransformation.CornerType.BOTTOM_RIGHT);

        //组合各种Transformation,
        MultiTransformation<Bitmap> mation = new MultiTransformation<>
                //Glide设置圆角图片后设置ImageVIew的scanType="centerCrop"无效解决办法,将new CenterCrop()添加至此
                (new CenterCrop(), transformation, transformation1);

        Glide.with(mContext)
                .load(course.get_$Background68())
                .override(300,300)
                .placeholder(R.mipmap.course_bg)
                //切圆形
                .apply(RequestOptions.bitmapTransform(mation))
                .into(ivCourseBg);


        //设置图片圆角角度
        RoundedCorners roundedCorners2 = new RoundedCorners(DensityUtil.dip2px(mContext, 8));
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options2 = RequestOptions
                .bitmapTransform(roundedCorners2)
                .override(300, 300)
                .placeholder(R.mipmap.item_course);

        Glide.with(mContext).load(course.getSmallPicture())
                .apply(options2)
                .into(ivItemCourseImage);

        tvCourseTime.setText("训练营有效期：" + DateUtil.timeStamp2Date(String.valueOf(course.getStartDate()/1000),"yyyy-MM-dd") + "~" +
                DateUtil.timeStamp2Date(String.valueOf(course.getEndDate()/1000),"yyyy-MM-dd"));
        tvCourseTime.setVisibility(View.VISIBLE);


    }

    private void setCourseInfo(CourseDetailsVo courseDetails) {
        tvCourseName.setText(courseDetails.getCourseName());
        tvItemCourseSlogan.setText(courseDetails.getCourseApplyCrowd());

        // 圆角图片 new RoundedCornersTransformation 参数为 ：半径 , 外边距 , 圆角方向(ALL,BOTTOM,TOP,RIGHT,LEFT,BOTTOM_LEFT等等)
        //顶部左边圆角
        RoundedCornersTransformation transformation = new RoundedCornersTransformation
                (8, 0, RoundedCornersTransformation.CornerType.BOTTOM_LEFT);
        //顶部右边圆角
        RoundedCornersTransformation transformation1 = new RoundedCornersTransformation
                (8, 0, RoundedCornersTransformation.CornerType.BOTTOM_RIGHT);

        //组合各种Transformation,
        MultiTransformation<Bitmap> mation = new MultiTransformation<>
                //Glide设置圆角图片后设置ImageVIew的scanType="centerCrop"无效解决办法,将new CenterCrop()添加至此
                (new CenterCrop(), transformation, transformation1);

        Glide.with(mContext)
                .load(courseDetails.getBackground())
                .override(300,300)
                .placeholder(R.mipmap.course_bg)
                //切圆形
                .apply(RequestOptions.bitmapTransform(mation))
                .into(ivCourseBg);


        //设置图片圆角角度
        RoundedCorners roundedCorners2 = new RoundedCorners(DensityUtil.dip2px(mContext, 8));
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options2 = RequestOptions
                .bitmapTransform(roundedCorners2)
                .override(300, 300)
                .placeholder(R.mipmap.item_course);

        Glide.with(mContext).load(courseDetails.getSmallPicture())
                .apply(options2)
                .into(ivItemCourseImage);




        if (courseDetails.getIsBuy() == 0) {
            //未购买
            rl_price_info.setVisibility(View.VISIBLE);
            tvCourseTime.setVisibility(View.GONE);
            tvCoursePrice.setText(String.valueOf(courseDetails.getCostPrice()));
            tvOriginalPrice.setText("¥" + courseDetails.getRulingPrice());
            tvOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        } else if (courseDetails.getIsBuy() == 1) {
            //已购买
            rl_price_info.setVisibility(View.GONE);
            tvCourseTime.setVisibility(View.VISIBLE);
        }
    }

    private void initMagicIndicator() {
        magicIndicator.setBackgroundColor(Color.parseColor("#ffffff"));
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setNormalColor(context.getResources().getColor(R.color.color_text_gray));
                simplePagerTitleView.setSelectedColor(context.getResources().getColor(R.color.theme_color));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(1);
                indicator.setColors(context.getResources().getColor(R.color.theme_color));
                //indicator.setLineHeight(6);
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(2);
        // 包含3个fragment界面
        List<TabIndicatorEntity> list = ViewPagerUtils.getTabIndicator(2);
        // 3个fragment界面封装
        fragmentList = new ArrayList<Fragment>();
        courseDetailListFragment = new CourseDetailListFragment();
        fragmentList.add(courseDetailListFragment);
        courseDetailNoteListFragment = new CourseDetailNoteListFragment();
        fragmentList.add(courseDetailNoteListFragment);
        // 设置ViewPager适配器
        ViewPagerFragmentAdapter viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), list, fragmentList);
        mViewPager.setAdapter(viewPagerFragmentAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }



    @OnClick({R.id.ivBack, R.id.classInfo, R.id.tvBuyButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.classInfo:
                //Toast.makeText(CourseDetailsLoginActivity.this, "点击分享课程", Toast.LENGTH_SHORT).show();
                if (classInfoVo.getIn() == 2) {
                    PopupWindow classWaiting = PopupWindowUtils.createClassWaiting(popuClassWaiting, mContext);
                    classWaiting.showAsDropDown(clMain, Gravity.CENTER, 0, 0);

                } else if (classInfoVo.getIn() == 1) {
                    ClassInfoVo.Teacher teacher = classInfoVo.getTeacher();
                    PopupWindow ContactUsPop = PopupWindowUtils.createClassnfoPop(popuClassInfo, mContext,
                            teacher.getTitle(), teacher.getWx_number(),teacher.getWx_url());
                    ContactUsPop.showAtLocation(clMain, Gravity.CENTER, 0, 0);
                }

                break;
            case R.id.tvBuyButton:

                if (StaticMembers.IS_NEED_LOGIN) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                } else {

                    mViewModel.getCourseStartTime(courseId);
                }

                break;
        }
    }

}
