package click.jws.com.firstplugin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import click.jws.com.firstplugin.service.ClickService;
import click.jws.com.firstplugin.thread.PluginThread;

public class PluginActivity extends AppCompatActivity implements OnClickListener{
    private static final String TAG = "PluginActivity";

    //main layout
    private View mPluginLayout;
    private TextView mtextViewHello;
    private TextView mTextView1;
    private TextView mTextView2;
    private Button mButtonStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);

        initView();
        initClickListener();
    }

    private void initView() {
        mPluginLayout = getLayoutInflater().inflate(R.layout.activity_plugin, null);
        mtextViewHello = findViewById(R.id.textView_helloworld);
        mTextView1 = findViewById(R.id.textView_1);
        mTextView2 = findViewById(R.id.textView_2);
        mButtonStart = findViewById(R.id.button_start);
    }

    private void initClickListener() {
        mtextViewHello.setOnClickListener(this);
        mTextView1.setOnClickListener(this);
        mTextView2.setOnClickListener(this);
        mButtonStart.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Log.d(TAG, "" + view.getId());
        switch(view.getId()) {
            case R.id.textView_helloworld:
                Toast.makeText(this, "Hello World!!!", Toast.LENGTH_LONG).show();
                break;
            case R.id.textView_1:
                Toast.makeText(this, "textView_1", Toast.LENGTH_LONG).show();
                break;
            case R.id.textView_2:
                Toast.makeText(this, "textView_2", Toast.LENGTH_LONG).show();
                break;
            case R.id.button_start:
                if (ClickService.isRunning()) {
                    new PluginThread(this).start();
                } else {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                }
                break;
        }

    }
}
