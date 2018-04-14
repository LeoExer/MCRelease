package com.leo.mc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.leo.mc.utils.Channels;

public class MainActivity extends AppCompatActivity {

    private TextView tvChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvChannel = findViewById(R.id.tv_channel);
    }

    public void getChannelByMetaInf(View view) {
        showChannel(Channels.getChannelByMetaInf(this));
    }

    public void getChannelByFC(View view) {
        showChannel(Channels.getChannelByFC(this));
    }

    private void showChannel(String channel) {
        if (TextUtils.isEmpty(channel)) {
            channel = "null";
        }
        tvChannel.setText(channel);
    }
}
