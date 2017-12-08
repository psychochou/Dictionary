package euphoria.psycho.dictionary;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {

    private static final int REQUEST_PERMISSION = 0X1;
    private static final int MENE_REQUEST_FLOATING_PERMISSION = 0X1;

    private ListView mListView;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.setContext(this);
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
            }, REQUEST_PERMISSION);
        } else {
            initialize();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        //    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initialize();
    }

    private void initialize() {


        setContentView(R.layout.activity_main);
        mEditText = findViewById(R.id.et);
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    new QueryTask(MainActivity.this).execute(mEditText.getText().toString().trim().toLowerCase());
                }
                return false;
            }
        });
        Intent clipboardService = new Intent(this, ClipboardService.class);
        startService(clipboardService);
    }

    private void requestAlertPermission() {
        if (Utils.isMiui()) {
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENE_REQUEST_FLOATING_PERMISSION, 0, "开启悬浮窗口权限");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENE_REQUEST_FLOATING_PERMISSION:
                requestAlertPermission();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
