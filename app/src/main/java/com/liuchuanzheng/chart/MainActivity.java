package com.liuchuanzheng.chart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.liuchuanzheng.chart.lcz.ChartView;

public class MainActivity extends AppCompatActivity {
    boolean isshow;
    Button btn_isShowRuler;
    ChartView cv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btn_isShowRuler = findViewById(R.id.btn_isShowRuler);
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
         cv = findViewById(R.id.cv);

    }
}
