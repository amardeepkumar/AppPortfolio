package myportfolio.udacity.com.myappportfolio;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
    }

    private void initView() {
        findViewById(R.id.sopify_streamer).setOnClickListener(this);
        findViewById(R.id.super_duo).setOnClickListener(this);
        findViewById(R.id.build_it_bigger).setOnClickListener(this);
        findViewById(R.id.xyz_reader).setOnClickListener(this);
        findViewById(R.id.capstone).setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        int toastMessageStringId;
        switch (v.getId()) {
            case R.id.sopify_streamer:
                toastMessageStringId = R.string.sopify_streamer_message;
                break;

            case R.id.super_duo:
                toastMessageStringId = R.string.super_duo_message;
                break;

            case R.id.build_it_bigger:
                toastMessageStringId = R.string.build_it_bigger_message;
                break;

            case R.id.xyz_reader:
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
