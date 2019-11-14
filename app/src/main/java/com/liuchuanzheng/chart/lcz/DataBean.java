package com.liuchuanzheng.chart.lcz;

import java.io.Serializable;

/**
 * @author 刘传政
 * @date 2019-11-11 14:41
 * QQ:1052374416
 * 电话:18501231486
 * 作用:
 * 注意事项:
 */
public class DataBean implements Serializable {
    public float x;
    public float y;

    public DataBean(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
