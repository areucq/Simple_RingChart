package com.simplechartdemo.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {
    int i;
    RingChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chart = (RingChart)findViewById(R.id.ringChart);
        chart.addItem("Linux", 1.56f, getResources().getColor(android.R.color.holo_red_light));
        chart.addItem("Win",22.37f, getResources().getColor(android.R.color.holo_green_light));
        chart.addItem("OsX", 4.98f, getResources().getColor(android.R.color.holo_blue_light));

//        findViewById(R.id.cleaBt).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (i == 0)
//                    chart.clearData();
//
//                if (i == 1) {
//                    chart.addItem("Linux", 1.56f, getResources().getColor(android.R.color.holo_red_light));
//                    chart.addItem("Windows", 22.37f, getResources().getColor(android.R.color.holo_green_light));
//                    chart.addItem("OsX", 4.98f, getResources().getColor(android.R.color.holo_blue_light));
//                }
//
//                i++;
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
