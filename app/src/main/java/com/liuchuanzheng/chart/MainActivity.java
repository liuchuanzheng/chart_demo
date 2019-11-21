package com.liuchuanzheng.chart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.liuchuanzheng.chart.lcz.ChartView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    boolean isshow;
    boolean isShowBackGroundLine = true;
    Button btn_isShowRuler;
    Button btn_isShowBackGroundLine;
    Button btn_zengyi;
    Button btn_restore;
    Button btn_selectDaoLian;
    ChartView cv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btn_isShowRuler = findViewById(R.id.btn_isShowRuler);
        btn_isShowBackGroundLine = findViewById(R.id.btn_isShowBackGroundLine);
        btn_zengyi = findViewById(R.id.btn_zengyi);
        btn_restore = findViewById(R.id.btn_restore);
        btn_selectDaoLian = findViewById(R.id.btn_selectDaoLian);
        btn_isShowRuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isshow = !isshow;
                if (isshow) {
                    btn_isShowRuler.setText("隐藏尺子");
                }else{
                    btn_isShowRuler.setText("显示尺子");
                }
                cv.showOrHideRuler(isshow);
            }
        });
        btn_isShowBackGroundLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowBackGroundLine = !isShowBackGroundLine;
                if (isShowBackGroundLine) {
                    btn_isShowBackGroundLine.setText("隐藏网格线");
                }else{
                    btn_isShowBackGroundLine.setText("显示网格线");
                }
                cv.showOrHideBackGroundLine(isShowBackGroundLine);
            }
        });
        btn_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv.restore();
            }
        });
        btn_zengyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv.zengyi(3);
            }
        });

        btn_selectDaoLian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //默认选中所有12个导联
                ArrayList<Integer> list = new ArrayList<>();
                list.add(0);
                list.add(1);
                list.add(3);
                cv.daolianSelect(list);
            }
        });
         cv = findViewById(R.id.cv);

    }
}
