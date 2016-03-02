package com.udacity.myappportfolio.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.udacity.myappportfolio.R;
import com.udacity.myappportfolio.databinding.ActivityMainBinding;

/**
 * Created by Amardeep on 27/1/16.
 * Class to show the home page
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);
        initView(binding);
    }

    private void initView(ActivityMainBinding binding) {
        binding.sopifyStreamer.setOnClickListener(this);
        binding.superDuo.setOnClickListener(this);
        binding.buildItBigger.setOnClickListener(this);
        binding.xyzReader.setOnClickListener(this);
        binding.capstone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int toastMessageStringId;
        switch (v.getId()) {
            case R.id.sopifyStreamer:
                toastMessageStringId = R.string.sopify_streamer_message;
                startActivity(new Intent(this, MovieActivity.class));
                break;

            case R.id.superDuo:
                toastMessageStringId = R.string.super_duo_message;
                break;

            case R.id.build_it_bigger:
                toastMessageStringId = R.string.build_it_bigger_message;
                break;

            case R.id.xyzReader:
                toastMessageStringId = R.string.xyz_reader_message;
                break;

            case R.id.capstone:
                toastMessageStringId = R.string.capstone_message;
                break;

            default:
                toastMessageStringId = R.string.default_message;
                break;
        }
        showToast(toastMessageStringId);
    }

    private void showToast(int toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }
}
