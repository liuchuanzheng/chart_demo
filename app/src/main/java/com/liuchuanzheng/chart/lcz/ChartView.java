package com.liuchuanzheng.chart.lcz;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘传政
 * @date 2019-11-11 14:43
 * QQ:1052374416
 * 电话:18501231486
 * 作用:没有直接使用canvas配合矩阵的缩放平移变换.因为缩放时,网格线不想变粗.
 * 选择了实时计算点的坐标的方式.
 * 注意事项:
 */
public class ChartView extends View {
    private static final String TAG = "ChartView";

    private Context context;
    //背景方格,粗线画笔
    private Paint backGroundSquarePaint_big;
    //背景方格,细线画笔
    private Paint backGroundSquarePaint_small;
    //折线图的点画笔
    private Paint pointPaint;
    //导联左端的文字
    private Paint daolianTextPaint;
    //控件宽
    private int mWidth;
    //控件高
    private int mHeight;
    private int backGroundColor = Color.parseColor("#808080"); // view的背景颜色
    //最大允许缩放值
    float maxScaleSize = 10;
    //最小允许缩放值
    float minScaleSize = 0.8f;
    //缩放监听
    private ScaleGestureDetector mScaleGestureDetector;
    //平移监听
    private GestureDetector mGestureDetector;
    //整个view缩放平移的操作矩阵
    private Matrix mMatrix;
    Canvas canvas;
    //原始数据点
    ArrayList<DataBean> pointList = new ArrayList<>();
    //上一次变换之后的点坐标. 这里拿到的就是最后画到上边的点坐标
    ArrayList<DataBean> lastPointList = new ArrayList<>();
    private float preScale = 1.0f;//之前的伸缩值
    private float curScale = 1.0f;//当前的伸缩值
    private float lastScaleFactor = 1.0f;//相对于上一次的缩放因子


    //原始数据点导联左端的坐标
    ArrayList<DaolianTextBean> daolianTextpointList = new ArrayList<>();
    //上一次变换之后的点坐标. 这里拿到的就是最后画到上边的点坐标
    //导联左端的坐标
    ArrayList<DaolianTextBean> lastDaolianTextPointList = new ArrayList<>();
    //大方格边长
    float bigSpace = 100;
    //小方格边长
    float smallSpace = bigSpace/5;
    //方格起始位置
    float startX = 0;
    float startY = 0;
    float endX = 4000;
    float endY = 4000;
    //上次平移距离
    float lastTranslateX = 0.0f;

    float lastTranslateY = 0.0f;

    //缩放点坐标
    float scaleFousX = 0.0f;
    float scaleFousY = 0.0f;



    //尺子的最大刻度
    private int maxRuler = 51;
    //尺子的最小刻度
    private int minRuler = 0;
    private Paint mRulerLinePaint;
    private Paint mRulerTextPaint;
    private boolean isCanMove;

    //是否显示刻度尺
    boolean isShowRuler = false;
    //是否显示背景网格线
    boolean isShowBackGroundLine = true;

    //总平移距离
    int totalRulerTranslateX;
    //总平移距离
    int totalRulerTranlateY;

    int mRuler10Height = 40;
    int mRuler5Height = 25;
    int mRuler1Height = 15;
    float mRulerDx = bigSpace/5;

    //原始坐标的最大值最小值记录
    int minXPosition = 0;
    int minYPosition = 0;
    float minX = 0;
    float minY = 0;
    int maxXPosition = 0;
    int maxYPosition = 0;
    float maxX = 0;
    float maxY = 0;
    //原始的12导联数据
    ArrayList<DaolianBean> originTotalDaolianList = new ArrayList<>();
    //经过增益变化后或显示隐藏某个导联的数据
    ArrayList<DaolianBean> totalDaolianList = new ArrayList<>();
    int xZengyi = 1;
    int yZengyi = 1;

    //显示的导联
    ArrayList<Integer> daolianSelectedList = new ArrayList<>();
    String[] totalDaolianTexts = {"I","II","III","aVR","aVL","aVF","V1","V2","V3","V4","V5","V6"};
    ArrayList<String> selectDaolianTexts = new ArrayList<>();



    public ChartView(Context context) {
        super(context);
        init(context,null,0);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        mMatrix = new Matrix();
        initData();
        initPaint();
        initGestureDetector();
    }

    /**
     * 模拟数据
     */
    private void initData() {
       /* for (int a = 0; a < 4; a++) {
            DaolianBean daolianBean = new DaolianBean();
            for (float i = 200; i < 400; i+=1) {
                float random = (float) ((Math.random() * 11)-5);
                daolianBean.getPointList().add(new DataBean(i,400+random+a*100));
            }
            originTotalDaolianList.add(daolianBean);
        }*/


        for (int a = 0; a < 12; a++) {
            DaolianBean daolianBean = new DaolianBean();
            int b = 1;
            for (float i = 200; i < 1400; i+=10) {
                float random =0;
                if (b == 1){
                    random = (float) ((Math.random() * 61));

                }else{
                    random = -(float) ((Math.random() * 61));
                }
                daolianBean.getPointList().add(new DataBean(i,random));
                b*= -1;
            }
            originTotalDaolianList.add(daolianBean);
        }


        //取反坐标轴
        jiaozhengZuobiaoxi();

        //默认选中所有12个导联
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            list.add(i);
        }
        daolianSelectedList.clear();
        daolianSelectedList.addAll(list);

        calculateData();
    }
    //因为Android 坐标系y轴与心电图的坐标系相反,这里进行一次取反.
    //防止把图画倒.要保证只进行一次取反.
    private void jiaozhengZuobiaoxi(){
        for (DaolianBean daolianBean : originTotalDaolianList) {
            for (DataBean dataBean : daolianBean.getPointList()) {
                dataBean.y *= -1;
            }
        }
    }
    //计算数据,进行处理
    //主要是根据选择的显示导联,去除无用导联,并进行增益,最终合并入一个统一的大list,便于绘制和判断边界点.
    //核心思想:虽然绘制了多个导联,实际上我只是在绘制一个list中的所有点.
    private void calculateData() {
        totalDaolianList.clear();
        pointList.clear();
        selectDaolianTexts.clear();
        List<DaolianBean> tempList = depCopy(originTotalDaolianList);
        float addY = 0;
        for (int i = 0; i < tempList.size(); i++) {
            if (daolianSelectedList.contains(i)){
                selectDaolianTexts.add(totalDaolianTexts[i]);
                float maxY = tempList.get(i).getPointList().get(0).y;
                float minY = tempList.get(i).getPointList().get(0).y;

                for (int i1 = 0; i1 < tempList.get(i).getPointList().size(); i1++) {
                    DataBean dataBean = tempList.get(i).getPointList().get(i1);
                    if (dataBean.y> maxY) {
                        maxY = dataBean.y;
                        tempList.get(i).maxYPosition = i1;
                    }

                    if (dataBean.y< minY) {
                        minY = dataBean.y;
                        tempList.get(i).minYPosition = i1;
                    }
                    //加上之前的导联距离后,在加上自身的基线高度.
                    dataBean.y += (addY +Math.abs(minY));
                }
                //导联间隔
                float distanceDaolian = maxY-minY;
                totalDaolianList.add(tempList.get(i));
                //todo 每个导联增加y的距离,防止重叠
                //要根据不同导联的高度进行差异增加,这里先临时增加100
                addY += distanceDaolian;
            }
        }
//        totalDaolianList.addAll(depCopy(originTotalDaolianList));
        for (DaolianBean daolianBean : totalDaolianList) {
            for (DataBean dataBean : daolianBean.getPointList()) {
                //x增益算法
                dataBean.x = dataBean.x* xZengyi -200* (xZengyi -1);
                //y增益算法
                dataBean.y = dataBean.y *yZengyi;
                pointList.add(dataBean);
            }
        }
        //计算出所有点的最大,最小边界,便于边界拖动处理.
        minXPosition = 0;
        minYPosition = 0;
        if (pointList.size() >0){
            minX = pointList.get(0).x;
            minY = pointList.get(0).y;
        }

        maxXPosition = 0;
        maxYPosition = 0;
        if (pointList.size() >0){
            maxX = pointList.get(0).x;
            maxY = pointList.get(0).y;
        }

        for (int i = 0; i < pointList.size(); i++) {
            if ( pointList.get(i).x<minX){
                minX = pointList.get(i).x;
                minXPosition = i;
            }
            if ( pointList.get(i).y<minY){
                minY = pointList.get(i).y;
                minYPosition = i;
            }
            if ( pointList.get(i).x>maxX){
                maxX = pointList.get(i).x;
                maxXPosition = i;
            }
            if ( pointList.get(i).y>maxY){
                maxY = pointList.get(i).y;
                maxYPosition = i;
            }

        }

        lastPointList.clear();
        lastPointList.addAll(depCopy(pointList));
    }

    /***
     * 方法一对集合进行深拷贝 注意需要对泛型类进行序列化(实现Serializable)
     *
     * @param srcList
     * @param <T>
     * @return
     */
    public static <T> List<T> depCopy(List<T> srcList) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(srcList);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteIn);
            List<T> destList = (List<T>) inStream.readObject();
            return destList;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initPaint() {
        //大网格线画笔
        backGroundSquarePaint_big = new Paint(Paint.ANTI_ALIAS_FLAG);
        backGroundSquarePaint_big.setColor(Color.parseColor("#FF3300"));
        backGroundSquarePaint_big.setStrokeWidth(2f);
        //抗锯齿
        backGroundSquarePaint_big.setAntiAlias(true);
        backGroundSquarePaint_big.setStyle(Paint.Style.FILL);

        //小网格线画笔
        backGroundSquarePaint_small = new Paint(Paint.ANTI_ALIAS_FLAG);
        backGroundSquarePaint_small.setColor(Color.parseColor("#FF3300"));
        backGroundSquarePaint_small.setStrokeWidth(1f);
        //抗锯齿
        backGroundSquarePaint_small.setAntiAlias(true);
        backGroundSquarePaint_small.setStyle(Paint.Style.FILL);

        //折线图点的画笔
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.parseColor("#4DB38A"));
        pointPaint.setStrokeWidth(3f);
        //抗锯齿
        pointPaint.setAntiAlias(true);
        //不填充
        pointPaint.setStyle(Paint.Style.STROKE);

        //导联左端文字画笔
        daolianTextPaint = new Paint();
        daolianTextPaint.setColor(Color.parseColor("#FFFFFF"));
        daolianTextPaint.setAntiAlias(true);
        daolianTextPaint.setStyle(Paint.Style.FILL);
        daolianTextPaint.setStrokeWidth(1);
        daolianTextPaint.setTextSize(sp2px(11));

        initRulerPaint();

    }

    private void initRulerPaint() {
        mRulerLinePaint = new Paint();
        mRulerLinePaint.setColor(Color.parseColor("#FF0000"));
        mRulerLinePaint.setAntiAlias(true);//抗锯齿
        mRulerLinePaint.setStyle(Paint.Style.STROKE);
        mRulerLinePaint.setStrokeWidth(4);
        mRulerTextPaint = new Paint();
        mRulerTextPaint.setColor(Color.parseColor("#FF0000"));
        mRulerTextPaint.setAntiAlias(true);
        mRulerTextPaint.setStyle(Paint.Style.FILL);
        mRulerTextPaint.setStrokeWidth(1);
        mRulerTextPaint.setTextSize(40);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        canvas.concat(mMatrix);
        //画方格
        if (isShowBackGroundLine) {
            drawBackgroundSquareLine(canvas);
        }

        //画心电图点
        drawPoints(canvas);
        //画导联左边的文字
        drawDaoLianText(canvas);
        //画标尺
        if(isShowRuler){
            drawRuler();
        }

    }

    private void drawPoints(Canvas canvas) {
        if (pointList.size() <=0){
            return;
        }
        //不管三七二十一,先根据平移距离和缩放倍数计算出每个point对应的变化后坐标
        //这里一定要考虑缩放的手指位置,让用户看起来是基于手指中心处缩放.
        for (int i = 0; i < lastPointList.size(); i++) {
            DataBean lastDataBean = lastPointList.get(i);
            //先算手指中心点到上次坐标点的距离. 相当于坐标原点平移,到手指中心.乘放大倍数后得出放大后的坐标.
            //但是此坐标是相当于坐标原点平移后的,要还原平移距离.
            lastDataBean.x = (lastDataBean.x-scaleFousX)*lastScaleFactor+scaleFousX+lastTranslateX;
            lastDataBean.y = (lastDataBean.y-scaleFousY)*lastScaleFactor+scaleFousY+lastTranslateY;
        }
        checkAndChangePoints();

        Path path = new Path();
        //先移动到第一个点的位置
        path.moveTo(((lastPointList.get(0).x)),((lastPointList.get(0).y)));
        //绘制折线
        ArrayList<Integer> selectedDaolianStartPositionList = getSelectedDaolianStartPositionList();
        for (int i = 0; i < lastPointList.size(); i++) {
            if (selectedDaolianStartPositionList.contains(i)) {
                //每个导联的起始位置不连线
                path.moveTo(lastPointList.get(i).x,lastPointList.get(i).y);
            }else{
                path.lineTo(lastPointList.get(i).x,lastPointList.get(i).y);
            }


        }
        canvas.drawPath(path, pointPaint);

        scaleFousX = 0;
        scaleFousY = 0;
        lastTranslateX = 0;
        lastTranslateY = 0;
        lastScaleFactor = 1;



    }
    //得到目前所选导联,合成一个大list的每个起始位置.
    private ArrayList<Integer> getSelectedDaolianStartPositionList(){
        ArrayList<Integer> selectedDaolianStartPositionList = new ArrayList<>();
        int daolianStartPosition = 0;
        for (int i = 0; i < totalDaolianList.size(); i++) {
            daolianStartPosition += totalDaolianList.get(i).getPointList().size();
            selectedDaolianStartPositionList.add(daolianStartPosition);
        }
        return selectedDaolianStartPositionList;
    };

    /**
     * 检查点,并校正点
     * 看是不是托出了边界.如果拖出了边界,那就把点强制贴和边界
     * 这里的边界不是屏幕,也不是原始坐标相对于坐标原点距离,而是根据放大倍数计算出来的
     */
    private void checkAndChangePoints() {
        if (pointList.size() <=0){
            return;
        }
        //判断是否越界,超出后要回到起点

        //先进行右下角校验,再进行左上角校验,这样保证左上角优先
        float pre0DistanceX =(pointList.get(0).x - 0)*preScale;
        float pre0DistanceY =(pointList.get(0).y - 0)*preScale;


        //右下角不能在往里拖了
        if ((lastPointList.get(maxXPosition).x -getWidth()<=0
                || lastPointList.get(maxYPosition).y -getHeight()<=0)) {
            //如果最后一个点超出屏幕
            //说明超出边界.超出后要回到起点
            float tempx = 0;
            float tempy = 0;
            if (lastPointList.get(maxXPosition).x -getWidth()<=0
                    && lastPointList.get(maxXPosition).x <=pointList.get(maxXPosition).x) {
                //如果最后一个点超出屏幕,并且比原来靠左
                //说明超出边界.超出后要回到最大左移距离
                //越界距离
                if (pointList.get(maxXPosition).x-getWidth()>0){
                    tempx =getWidth()-lastPointList.get(maxXPosition).x;
                }else{
                    tempx =pointList.get(maxXPosition).x-lastPointList.get(maxXPosition).x;
                }

            }
            if (lastPointList.get(maxYPosition).y -getHeight()<=0
                    && lastPointList.get(maxYPosition).y <=pointList.get(maxYPosition).y) {
                //说明超出边界.超出后要回到最大左移距离
                //越界距离

                if (pointList.get(maxYPosition).y-getHeight()>0){
                    //大于长度
                    tempy =getHeight()-lastPointList.get(maxYPosition).y;
                }else{
                    tempy =pointList.get(maxYPosition).y-lastPointList.get(maxYPosition).y;
                }
            }
            if (tempx != 0 || tempy!=0){
                for (int i = 0; i < lastPointList.size(); i++) {
                    //只要有一边越界,就变换原始坐标值
                    DataBean lastDataBean = lastPointList.get(i);
                    lastDataBean.x+= tempx;
                    lastDataBean.y+= tempy;
                }
            }
        }
        //左上角不能在往里拖了
        if (lastPointList.get(0).x>=pre0DistanceX || lastPointList.get(0).y>=pre0DistanceY) {
            //说明超出边界.超出后要回到起点
            float tempx = 0;
            float tempy = 0;
            if (lastPointList.get(0).x>=pre0DistanceX ) {
                //说明超出边界.超出后要回到起点
                //越界距离
                tempx =lastPointList.get(0).x-pre0DistanceX;
            }
            if (lastPointList.get(0).y>=pre0DistanceY ) {
                //说明超出边界.超出后要回到起点
                //越界距离
                tempy =lastPointList.get(0).y-pre0DistanceY;
            }
            if (tempx != 0 || tempy!=0){
                for (int i = 0; i < lastPointList.size(); i++) {
                    //只要有一边越界,就变换原始坐标值
                    DataBean lastDataBean = lastPointList.get(i);
                    lastDataBean.x-= tempx;
                    lastDataBean.y-= tempy;
                }
            }
        }


    }
    //画每个导联左端的数字
    private void drawDaoLianText(Canvas canvas){
        /*float minY = lastPointList.get(0).y;
        float maxY = lastPointList.get(0).y;
        for (int i = 0; i < 200; i++) {
            if (lastPointList.get(i).y>maxY) {
                maxY = lastPointList.get(i).y;
            }
            if (lastPointList.get(i).y<minY) {
                minY = lastPointList.get(i).y;
            }
        }
        canvas.drawText("I", 50, (maxY+minY)/2, daolianTextPaint);*/
        int maxYPosition = 0;
        int minYPosition = 0;
        int daolianStartPosition = 0;

        for (int i = 0; i < totalDaolianList.size(); i++) {

            maxYPosition = daolianStartPosition +totalDaolianList.get(i).maxYPosition;
            minYPosition = daolianStartPosition +totalDaolianList.get(i).minYPosition;
            canvas.drawText(selectDaolianTexts.get(i), 50, (lastPointList.get(maxYPosition).y+lastPointList.get(minYPosition).y)/2, daolianTextPaint);
            daolianStartPosition += totalDaolianList.get(i).getPointList().size();

        }
    }

    private void drawBackgroundSquareLine(Canvas canvas) {

        int bigLineCount_heng = 30;
        int smallLineCount_heng = bigLineCount_heng*5;
        int bigLineCount_shu = 30;
        int smallLineCount_shu = bigLineCount_shu*5;

        for (int i = 0; i < bigLineCount_heng; i++) {
            //画粗横线
            canvas.drawLine(0*preScale, i*(bigSpace*preScale),  mWidth, i*(bigSpace*preScale), backGroundSquarePaint_big);

        }
        for (int i = 0; i < smallLineCount_heng; i++) {
            //画细横线
            if (i%5!= 0){
                canvas.drawLine(0*preScale, i*(smallSpace*preScale),  mWidth, i*(smallSpace*preScale), backGroundSquarePaint_small);
            }

        }
        for (int i = 0; i < bigLineCount_shu; i++) {
            //画粗竖线
            canvas.drawLine(i*(bigSpace*preScale), 0*preScale,  i*(bigSpace*preScale), mHeight,backGroundSquarePaint_big);
        }
        for (int i = 0; i < smallLineCount_shu; i++) {
            //画细竖线
            if (i%5!= 0){
                canvas.drawLine(i*(smallSpace*preScale), 0*preScale,  i*(smallSpace*preScale), mHeight,backGroundSquarePaint_small);
            }

        }

    }
    private void drawRuler(){
        checkRuler();
        //尺子也要随着放大而放大
        //画横向尺子
        canvas.save();
        //因为手指拖动了
        canvas.translate(totalRulerTranslateX, totalRulerTranlateY);
        canvas.translate(200,1000);
        canvas.drawLine(0, 0, maxRuler*mRulerDx*preScale, 0, mRulerLinePaint);
        for (float i = minRuler; i < maxRuler; i++) {
            if (i % 10 == 0) {
                if(i != 0){
                    canvas.drawLine(0, 0, 0, mRuler10Height*preScale, mRulerLinePaint);
                }
                String text = i / 100 + "";
                Rect rect = new Rect();
                float txtWidth = mRulerTextPaint.measureText(text);
                mRulerTextPaint.getTextBounds(text, 0, text.length(), rect);
                if(i != 0){
                    canvas.drawText(text, 0 - txtWidth / 2, mRuler10Height*preScale + rect.height() + 10, mRulerTextPaint);

                }else{
                    canvas.drawText("ms", 0 - txtWidth / 2, mRuler10Height*preScale + rect.height() + 10, mRulerTextPaint);
                    //画0
                    canvas.drawText("0", 0 - txtWidth / 2-mRuler5Height, (mRuler10Height+rect.height() + 10)/2, mRulerTextPaint);
                }

            } else if (i % 5 == 0) {
                canvas.drawLine(0, 0, 0, mRuler5Height*preScale, mRulerLinePaint);
            } else {
                canvas.drawLine(0, 0, 0, mRuler1Height*preScale, mRulerLinePaint);
            }
            canvas.translate(mRulerDx*preScale, 0);
        }
        canvas.restore();

        //画竖向尺子
        canvas.save();
        canvas.translate(totalRulerTranslateX, totalRulerTranlateY);
        canvas.rotate(90);
        canvas.translate(1000,-200);
        canvas.drawLine(0, 0, -(maxRuler*mRulerDx*preScale), 0, mRulerLinePaint);
        for (float i = minRuler; i < maxRuler; i++) {
            if (i % 10 == 0) {
                if(i != 0){
                    canvas.drawLine(0, 0, 0, mRuler10Height*preScale, mRulerLinePaint);
                }

                String text = i / 100 + "";
                Rect rect = new Rect();
                float txtWidth = mRulerTextPaint.measureText(text);
                mRulerTextPaint.getTextBounds(text, 0, text.length(), rect);
                //为了把文字转向
                canvas.save();
                canvas.rotate(-90,0, 0 );
                if(i != 0){
                    canvas.drawText(text, 0 - txtWidth -mRuler10Height*preScale-10,  rect.height()/2, mRulerTextPaint);
                }else{
                    canvas.drawText("µV", 0 - txtWidth -mRuler10Height*preScale-10,  rect.height()/2, mRulerTextPaint);
                }
                canvas.restore();
            } else if (i % 5 == 0) {
                canvas.drawLine(0, 0, 0, mRuler5Height*preScale, mRulerLinePaint);
            } else {
                canvas.drawLine(0, 0, 0, mRuler1Height*preScale, mRulerLinePaint);
            }
            canvas.translate(-mRulerDx*preScale, 0);
        }
        canvas.restore();
    }

    /**
     * 检查尺子,并校正尺子位置
     * 只判断尺子原点是否出了屏幕就行
     */
    private void checkRuler() {
        //留一个缝隙
        if (totalRulerTranslateX <-(200-10)){
            totalRulerTranslateX = -(200-10);
        }else if(totalRulerTranslateX >=getWidth()-(200+10)){
            totalRulerTranslateX = getWidth()-(200+10);
        }
        if (totalRulerTranlateY <-(1000-10)){
            totalRulerTranlateY = -(1000-10);
        }else if(totalRulerTranlateY >=getHeight()-(1000+10)){
            totalRulerTranlateY = getHeight()-(1000+10);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            mWidth = getWidth();
            mHeight = getHeight();
            setBackgroundColor(backGroundColor);
        }
        super.onLayout(changed, left, top, right, bottom);
    }
    private void initGestureDetector() {
        //缩放监听
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleMySize(detector);
                return true;

            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        });
        //拖动监听
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                //按下
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                //手指轻碰屏幕的一瞬间,尚未松开或拖动  由1个ACTION_DOWN触发与onDown区别：强调没有松开或者拖动的状态
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //单击行为
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //拖动行为
                scrollMySelf(e1,e2,distanceX,distanceY);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                //长按

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //快速滑动行为
                return false;
            }
        });

    }

    private void scrollMySelf(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (isShowRuler){
            totalRulerTranslateX -= distanceX;
            totalRulerTranlateY -= distanceY;
        }else{
            lastTranslateX= -distanceX;
            lastTranslateY = -distanceY;
        }

        invalidate();

    }

    private void scaleMySize(ScaleGestureDetector detector) {
//        Log.i(TAG, "focusX = " + detector.getFocusX());       // 缩放中心，x坐标
//        Log.i(TAG, "focusY = " + detector.getFocusY());       // 缩放中心y坐标
//        Log.i(TAG, "scale = " + detector.getScaleFactor());   // 缩放因子
        float scaleFactor = detector.getScaleFactor();

        curScale=scaleFactor*preScale;//当前的伸缩值*之前的伸缩值 保持连续性

        if(curScale<=maxScaleSize && curScale>=minScaleSize){
            //符合缩放倍数

            preScale=curScale;
            lastScaleFactor = scaleFactor;
            scaleFousX = detector.getFocusX();
            scaleFousY = detector.getFocusY();
            invalidate();

            return;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //设置监听关联
        mScaleGestureDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);

        return true;
    }


    public int dip2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    /**
     * sp转换px
     */
    public int sp2px(int spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    //显示或隐藏标尺
    public void showOrHideRuler(boolean isShow){
        isShowRuler = isShow;
        invalidate();
    }

    //是否显示背景网格线
    public void showOrHideBackGroundLine(boolean isShow){
        isShowBackGroundLine = isShow;
        invalidate();
    }

    //增益倍数
    public void xZengyi(int  a){
        if (a == xZengyi){
            //如果本次增益和上次增益相同,不起作用,防止回到初始状态
            return;
        }
        restore();
        xZengyi = a;
        calculateData();
        invalidate();
    }

    //增益倍数
    public void yZengyi(int  a){
        if (a == yZengyi){
            //如果本次增益和上次增益相同,不起作用,防止回到初始状态
            return;
        }
        restore();
        yZengyi = a;
        calculateData();
        invalidate();
    }

    //显示导联
    public void daolianSelect(ArrayList<Integer> list){
        if (list == null){
            return;
        }
        if (isSameList(list,daolianSelectedList)){
            //如果两个集合一样
            return;
        }
        restore();
        daolianSelectedList.clear();
        daolianSelectedList.addAll(list);
        calculateData();
        invalidate();

    }
    public boolean isSameList(ArrayList<Integer> firstList,ArrayList<Integer> secondList){
        if (firstList == null || secondList == null){
           return false;
        }
        if (firstList.size() == secondList.size()){
            for (Integer integer : firstList) {
                if (!secondList.contains(integer)) {
                    return false;
                }
            }
            return true;
        }else{
            return false;
        }

    }

    //还原状态
    public void restore(){
        curScale = 1.0f;
        preScale = 1.0f;
        scaleFousX = 0.0f;
        scaleFousY = 0.0f;
        lastTranslateX = 0;
        lastTranslateY = 0;
        totalRulerTranslateX = 0;
        totalRulerTranlateY = 0;
        isCanMove = false;
        isShowBackGroundLine = true;
        isShowRuler = false;
        xZengyi = 1;
        yZengyi = 1;
        lastPointList.clear();
        lastPointList.addAll(depCopy(pointList));
        //默认选中所有12个导联
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            list.add(i);
        }
        daolianSelectedList.clear();
        daolianSelectedList.addAll(list);
        calculateData();
        invalidate();
    }
}
