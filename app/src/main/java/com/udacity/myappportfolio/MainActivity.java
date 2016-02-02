package com.udacity.myappportfolio;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by Amardeep on 27/1/16.
 * Class to show the home page
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, myportfolio.com.udacity.myappportfolio.R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(myportfolio.com.udacity.myappportfolio.R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
    }

    private void initView() {
        findViewById(myportfolio.com.udacity.myappportfolio.R.id.sopify_streamer).setOnClickListener(this);
        findViewById(myportfolio.com.udacity.myappportfolio.R.id.super_duo).setOnClickListener(this);
        findViewById(myportfolio.com.udacity.myappportfolio.R.id.build_it_bigger).setOnClickListener(this);
        findViewById(myportfolio.com.udacity.myappportfolio.R.id.xyz_reader).setOnClickListener(this);
        findViewById(myportfolio.com.udacity.myappportfolio.R.id.capstone).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(myportfolio.com.udacity.myappportfolio.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == myportfolio.com.udacity.myappportfolio.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int toastMessageStringId;
        switch (v.getId()) {
            case myportfolio.com.udacity.myappportfolio.R.id.sopify_streamer:
                toastMessageStringId = myportfolio.com.udacity.myappportfolio.R.string.sopify_streamer_message;
                break;

            case myportfolio.com.udacity.myappportfolio.R.id.super_duo:
                toastMessageStringId = myportfolio.com.udacity.myappportfolio.R.string.super_duo_message;
                break;

            case myportfolio.com.udacity.myappportfolio.R.id.build_it_bigger:
                toastMessageStringId = myportfolio.com.udacity.myappportfolio.R.string.build_it_bigger_message;
                break;

            case myportfolio.com.udacity.myappportfolio.R.id.xyz_reader:
                toastMessageStringId = myportfolio.com.udacity.myappportfolio.R.string.xyz_reader_message;
                break;

            case myportfolio.com.udacity.myappportfolio.R.id.capstone:
                toastMessageStringId = myportfolio.com.udacity.myappportfolio.R.string.capstone_message;
                break;

            default:
                toastMessageStringId = myportfolio.com.udacity.myappportfolio.R.string.default_message;
                break;
        }
        showToast(toastMessageStringId);
    }

    private void showToast(int toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }
}
