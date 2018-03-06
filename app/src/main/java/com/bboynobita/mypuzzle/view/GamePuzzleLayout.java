package com.bboynobita.mypuzzle.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.bboynobita.mypuzzle.R;
import com.bboynobita.mypuzzle.utils.ImagePiece;
import com.bboynobita.mypuzzle.utils.ImageSplitterUtil;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



/**
 * Created by bboynobita on 2018/1/5.
 */

public class GamePuzzleLayout extends RelativeLayout implements View.OnClickListener {
private int mColumn=3;
    private int mWidth;
    private int level=1;
    //容器的内边距
    private int mPadding;
    //每张小图之间的距离(纵 横)
    private int mMargin=3;

    private boolean isGameSuccess;
    private boolean isGameOver;
    private int mItemWidth;
    private Bitmap mBitmap;
    private ImageView[] mGamePuzzleItems;
    private List<ImagePiece> mItemsBitmaps;
    private boolean once;
    public interface GamePuzzleListener{
        void nextLevel(int nextLevel);
        void timechanged(int currentTime);
        void gameover();
    }
    public GamePuzzleListener mListener;

    /**
     * 设置接口回调
     * @param mListener
     */
    public void setOnGamePuzzleListener(GamePuzzleListener mListener){
        this.mListener=mListener;
    }

    private static final int TIME_CHANGED=0x110;
    private static final int NEXT_LEVEL=0x111;


    private Handler mHandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TIME_CHANGED:
                    if(isGameSuccess||isGameOver||isPause){
                    return;
                    }
                    if(mListener!=null){
                        mListener.timechanged(mTime);
                        if (mTime==0){
                            isGameOver=true;
                            mListener.gameover();
                            return;
                        }
                    }
                    mTime--;
                    mHandler.sendEmptyMessageDelayed(TIME_CHANGED,1000);
                    break;
                case NEXT_LEVEL:
                    level+=1;
                    if(mListener!=null){
                    mListener.nextLevel(level);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 设置是否开启时间
     */
    private boolean isTimeEnabled=false;
    private int mTime;

    public void setTimeEnabled(boolean timeEnabled) {
        isTimeEnabled = timeEnabled;
    }

    public GamePuzzleLayout(Context context) {
        this(context,null);
    }

    public GamePuzzleLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GamePuzzleLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init() {
        mMargin= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,3,getResources().getDisplayMetrics());
        mPadding=min(getPaddingLeft(),getPaddingRight(),getPaddingTop(),getPaddingBottom());
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth=Math.min(getMeasuredHeight(),getMeasuredWidth());
        if (!once){
            //进行切图，排序
            initBitmap();
            //设置ImageView（）
            initItem();

            //判断是否开启时间
            checkTimeEnable();
            once=true;
        }
        setMeasuredDimension(mWidth,mWidth);
    }

    private void checkTimeEnable() {
        if (isTimeEnabled){
            //根据当前等级设置时间
            countTimeBaseLevel();
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }
    //根据当前等级设置时间
    private void countTimeBaseLevel() {
        //Math.pow(a,3)即可，即等于求a的3次方
        mTime=(int)Math.pow(2,level)*60;
    }


    private int min(int ... params) {
        int min =params[0];
        for (int param:params){
        if (param<min)
            min=param;
        }
        return min;
    }


    private void initItem() {
        mItemWidth=(mWidth-mPadding*2-mMargin*(mColumn-1))/mColumn;
        mGamePuzzleItems =new ImageView[mColumn*mColumn];
        for (int i=0;i<mGamePuzzleItems.length;i++){
            ImageView item=new ImageView(getContext());
            item.setOnClickListener(this);
            item.setImageBitmap(mItemsBitmaps.get(i).getBitmap());
            mGamePuzzleItems[i]=item;
            item.setId(i+1);
            //在item的tag存储了index
            item.setTag(i+"_"+mItemsBitmaps.get(i).getIndex());
            RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(mItemWidth,mItemWidth);
            //设置Item横向间隙，通过rightMargin
            //不是最后一列
            if((i+1)%mColumn!=0){
                lp.rightMargin=mMargin;
            }
            //不是第一列
            if(i%mColumn!=0){
                lp.addRule(RelativeLayout.RIGHT_OF,mGamePuzzleItems[i-1].getId());

            }
            //设置item的纵向间隙，通过BottomMargin
            //不是第一行
            if((i+1)>mColumn){
                lp.topMargin=mMargin;
                lp.addRule(RelativeLayout.BELOW,mGamePuzzleItems[i-mColumn].getId());
            }
            addView(item,lp);
        }
    }
    private boolean isPause;
    public void pause(){
        isPause=true;
        mHandler.removeMessages(TIME_CHANGED);
    }
    public void resume(){
        if(isPause){
            isPause=false;
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }
    public void restart(){
        isGameOver=false;
        mColumn--;
        nextLevel();
    }
    public void nextLevel(){
        this.removeAllViews();
        mAnimLayout=null;
        mColumn++;
        isGameSuccess=false;
        checkTimeEnable();
        initBitmap();
        initItem();
    }
    private void initBitmap() {
        if(mBitmap==null){
            mBitmap= BitmapFactory.decodeResource(getResources(), R.mipmap.bboy);
        }
        mItemsBitmaps= ImageSplitterUtil.splitImage(mBitmap,mColumn);
        Collections.sort(mItemsBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece a, ImagePiece b) {
                return Math.random()>0.5?1:-1;
            }
        });
    }
    private ImageView mFirst;
    private ImageView mSecond;
    @Override
    public void onClick(View v) {
        if(isAniming)
            return;
        if(mFirst==v){
            mFirst.setColorFilter(null);
            mFirst=null;
            return;
        }
        if(mFirst==null){
            mFirst= (ImageView) v;
            mFirst.setColorFilter(Color.parseColor("#55FF0000"));
        }
        else{
            mSecond= (ImageView) v;
            exchangeView();
        }
    }

    private void exchangeView() {
        String firstTag= (String) mFirst.getTag();
        String secondTag= (String) mSecond.getTag();
        mFirst.setColorFilter(null);
        setUpAnimLayout();
        ImageView first=new ImageView(getContext());
        ImageView second=new ImageView(getContext());

        first.setImageBitmap(getBitmapByTag(firstTag));
        LayoutParams lp=new LayoutParams(mItemWidth,mItemWidth);
        lp.leftMargin=mFirst.getLeft()-mPadding;
        lp.topMargin=mFirst.getTop()-mPadding;
        first.setLayoutParams(lp);
        mAnimLayout.addView(first);

        second.setImageBitmap(getBitmapByTag(secondTag));
        LayoutParams lp2=new LayoutParams(mItemWidth,mItemWidth);
        lp2.leftMargin=mSecond.getLeft()-mPadding;
        lp2.topMargin=mSecond.getTop()-mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);

        //设置动画
        TranslateAnimation Firstanim=new TranslateAnimation(0,mSecond.getLeft()-mFirst.getLeft(),0,mSecond.getTop()-mFirst.getTop());
        TranslateAnimation Secondanim=new TranslateAnimation(0,-mSecond.getLeft()+mFirst.getLeft(),0,-mSecond.getTop()+mFirst.getTop());
        Firstanim.setDuration(300);
        Firstanim.setFillAfter(true);
        first.startAnimation(Firstanim);
        Secondanim.setDuration(300);
        Secondanim.setFillAfter(true);
        second.startAnimation(Secondanim);
        Firstanim.setAnimationListener(new Animation.AnimationListener() {
    @Override
    public void onAnimationStart(Animation animation) {
        mFirst.setVisibility(View.INVISIBLE);
        mSecond.setVisibility(View.INVISIBLE);
        isAniming=true;

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        String firstTag= (String) mFirst.getTag();
        String secondTag= (String) mSecond.getTag();
        mSecond.setImageBitmap(getBitmapByTag(firstTag));
        mFirst.setImageBitmap(getBitmapByTag(secondTag));
        mFirst.setVisibility(VISIBLE);
        mSecond.setVisibility(VISIBLE);
        mFirst.setTag(secondTag);
        mSecond.setTag(firstTag);
        mFirst=mSecond=null;
        mAnimLayout.removeAllViews();
        checkSuccess();
        isAniming=false;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
        });


    }

    /**
     * 判断游戏是否成功
     */
    private void checkSuccess() {
        boolean isSuccess=true;

        for(int i=0;i<mGamePuzzleItems.length;i++){
                ImageView image=mGamePuzzleItems[i];
                if(getImageIndexByTag((String)image.getTag())!=i){
                    isSuccess=false;
                 break;
                }
        }
        if (isSuccess){
            isGameSuccess=true;
            mHandler.removeMessages(TIME_CHANGED);
        //    Toast.makeText(getContext(),"游戏通关",Toast.LENGTH_LONG).show();
            mHandler.sendEmptyMessage(NEXT_LEVEL);
        }
    }

    private boolean isAniming;
    private RelativeLayout mAnimLayout;
    private void setUpAnimLayout() {
        if(mAnimLayout==null){
            mAnimLayout=new RelativeLayout(getContext());
            addView(mAnimLayout);
        }
    }
    public Bitmap getBitmapByTag(String tag) {
        String Params[]= tag.split("_");
        Bitmap bitmap = mItemsBitmaps.get(Integer.parseInt(Params[0])).getBitmap();
        return bitmap;
    }
    public int getImageIndexByTag(String tag){
String [] split=tag.split("_");
        return Integer.parseInt(split[1]);
    }
}
