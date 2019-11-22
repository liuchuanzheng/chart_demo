package com.liuchuanzheng.chart.lcz;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author 刘传政
 * @date 2019-11-21 14:53
 * QQ:1052374416
 * 电话:18501231486
 * 作用:
 * 注意事项:
 */
public class DaolianBean implements Serializable {
    private boolean isShow = true;
    private ArrayList<DataBean> pointList = new ArrayList<>();
    private String name = "";
    public int minYPosition = 0;
    public int maxYPosition = 0;
    //最大的y跨度
    private int distanceY= 0;

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public ArrayList<DataBean> getPointList() {
        return pointList;
    }

    public void setPointList(ArrayList<DataBean> pointList) {
        this.pointList = pointList;
    }
}
