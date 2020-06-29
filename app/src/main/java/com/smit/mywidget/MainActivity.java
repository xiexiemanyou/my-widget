package com.smit.mywidget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.smit.mywidget.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_FOR_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
        requestPermissions();

    }

    private void requestPermissions(){
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        boolean bRequest = false;

        for(String permission : permissions){
            if(ContextCompat.checkSelfPermission(getApplication(), permission) != PackageManager.PERMISSION_GRANTED){
                bRequest = true;
                break;
            }
        }
        if(bRequest){
            ActivityCompat.requestPermissions(this, permissions, CODE_FOR_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODE_FOR_PERMISSION){
            for(int result : grantResults){
            }
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                permissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.storage_permissions_remind, CODE_FOR_PERMISSION);
            }
        }
    }

    private void permissionRationale(final String permisssion, int remindId, final int code){
        //用户不同意，向用户展示该权限作用
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permisssion)) {
            new AlertDialog.Builder(this)
                    .setMessage(remindId)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog1, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{permisssion}, code);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        }
    }


}
