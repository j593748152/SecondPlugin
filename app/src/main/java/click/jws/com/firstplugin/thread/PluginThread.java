package click.jws.com.firstplugin.thread;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import click.jws.com.firstplugin.service.ClickService;

public class PluginThread extends Thread {
    private static final String TAG = "PluginThread";

    private static final String AUTO_CLICK_ACTION = "auto.click";
    private Context mContext = null;

    public PluginThread(Context context) {
        super();
        mContext = context;
    }

    @Override
    public void run() {
        Intent intent = new Intent(AUTO_CLICK_ACTION);
        intent.putExtra(ClickService.EVENT_FLAG, ClickService.EVENT_KEY);
        intent.putExtra(ClickService.KEY_CODE, KeyEvent.KEYCODE_BACK);
        mContext.sendBroadcast(intent);

        try {
            this.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        intent = new Intent(AUTO_CLICK_ACTION);
        intent.putExtra(ClickService.EVENT_FLAG, ClickService.EVENT_CLICK);
        intent.putExtra(ClickService.VIEW_ID, "click.jws.com.firstplugin:id/textView_helloworld");
        mContext.sendBroadcast(intent);

    }

    private void performClick(String resourceId) {

    }


}
