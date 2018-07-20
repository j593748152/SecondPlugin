package click.jws.com.firstplugin.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Iterator;
import java.util.List;

public class ClickService extends AccessibilityService {
    private static final String TAG = "ClickService";

    public static final String AUTO_CLICK_ACTION = "auto.click";
    public static final String EVENT_FLAG = "event_flag";
    public static final int EVENT_UNKNOWN = 1;
    public static final int EVENT_CLICK = 1;
    public static final int EVENT_KEY = 2;

    public static final String VIEW_ID = "view_id";
    public static final String VIEW_TEXT = "view_text";
    public static final String KEY_CODE = "key_code";

    private ClickReceiver receiver;
    private static ClickService service;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //接收事件,如触发了通知栏变化、界面变化等
        Log.i(TAG, "onAccessibilityEvent " + event.toString());
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.i(TAG, "onKeyEvent " + event.getKeyCode());
        //接收按键事件
        return super.onKeyEvent(event);
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "onInterrupt");
        //服务中断，如授权关闭或者将服务杀死
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "onServiceConnected");
        service = this;
        //连接服务后,一般是在授权成功后会接收到
        if (receiver == null) {
            receiver = new ClickReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(AUTO_CLICK_ACTION);
            registerReceiver(receiver, intentFilter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    //执行返回
    public void performBack() {
        Log.i(TAG, "performBack");
        this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    //perform click view
    private void performClick(String resourceIdorText) {

        Log.i(TAG, "performClick " + resourceIdorText);

        AccessibilityNodeInfo nodeInfo = this.getRootInActiveWindow();
        AccessibilityNodeInfo targetNode = null;
        targetNode = findNodeInfosById(nodeInfo, resourceIdorText);
        if (targetNode != null && targetNode.isClickable()) {
            targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            targetNode = findNodeInfosByText(nodeInfo, resourceIdorText);
            if (targetNode != null && targetNode.isClickable()) {
                targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    //perform click key
    private void performKeyCode(final int keyCode) {
        Log.i(TAG, "performKeyCode " + keyCode);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    //通过id查找
    public static AccessibilityNodeInfo findNodeInfosById(AccessibilityNodeInfo nodeInfo, String resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(resId);
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }

    //通过文本查找
    public static AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text) {
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 判断当前服务是否正在运行
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isRunning() {
        if (service == null) {
            return false;
        }
        AccessibilityManager accessibilityManager = (AccessibilityManager) service.getSystemService(Context.ACCESSIBILITY_SERVICE);
        AccessibilityServiceInfo info = service.getServiceInfo();
        if (info == null) {
            return false;
        }
        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        Iterator<AccessibilityServiceInfo> iterator = list.iterator();

        boolean isConnect = false;
        while (iterator.hasNext()) {
            AccessibilityServiceInfo i = iterator.next();
            if (i.getId().equals(info.getId())) {
                isConnect = true;
                break;
            }
        }
        if (!isConnect) {
            return false;
        }
        return true;
    }

    public class ClickReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int i = intent.getIntExtra(EVENT_FLAG, EVENT_UNKNOWN);
            Log.i(TAG, "event flag " + i);
            switch (i) {
                case EVENT_CLICK:
                    String resourceid = intent.getStringExtra(VIEW_ID);
                    performClick(resourceid);
                    break;
                case EVENT_KEY:
                    int keyCode = intent.getIntExtra(KEY_CODE, KeyEvent.KEYCODE_UNKNOWN);
                    performKeyCode(keyCode);
                    break;
            }
        }
    }
}
