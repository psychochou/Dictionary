package euphoria.psycho.dictionary;

import android.app.Notification;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;

public class ClipboardService extends Service {
    private static final int ID_FOREGROUND = 0x1;

    private ClipboardManager mClipboardManager;
    private final ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {

            if (mClipboardManager == null) {
                mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            }
            ClipData clipData = mClipboardManager.getPrimaryClip();
            if (clipData.getItemCount() < 1) return;
            String word = clipData.getItemAt(0).getText().toString();

            new QueryTask(getApplicationContext()).execute(word);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        startForeground(ID_FOREGROUND, new Notification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        listenClipboard();


        return super.onStartCommand(intent, flags, startId);
    }

    private void listenClipboard() {

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        clipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }
}
