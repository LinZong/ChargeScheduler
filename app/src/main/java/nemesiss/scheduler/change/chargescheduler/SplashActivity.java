package nemesiss.scheduler.change.chargescheduler;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalPermissions;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new PrepareApplicationTask().execute();
    }

    private void JumpToMainActivity(){
        Intent it = new Intent(SplashActivity.this,SearchChargerActivity.class);
        startActivity(it);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        List<String> StillNeedPermission = new ArrayList<>();
        switch (requestCode){
            case GlobalPermissions.GRANT_ALL_PERMISSION_CODE:{
                if(grantResults.length > 0){
                    for (int i = 0; i < permissions.length; i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            StillNeedPermission.add(permissions[i]);
                            boolean checked = ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this,permissions[i]);
                            if(checked) GlobalPermissions.SetDontShowAgainFlag();
                        }
                    }

                    if(StillNeedPermission.size() <= 0) {
                        JumpToMainActivity();
                        return;
                    }
                    else {
                        if(GlobalPermissions.GetDontShowAgianFlag()){
                            AlertDialog.Builder builder =  ShowPermissionAlert(R.string.PermissionNoGrantedAlertTitle,
                                                                               R.string.PermissionNoGrantedAlertTitle,false);
                            builder.setPositiveButton("OK", (dialogInterface, i) -> finish());
                            builder.show();
                        }
                        else {
                           AlertDialog.Builder builder = ShowPermissionAlert(R.string.PermissionNoGrantedAlertTitle,
                                                                             R.string.PermissionNoGrantedTryAgainTitle,false);
                            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                                GlobalPermissions.RequestPermissions(SplashActivity.this,SplashActivity.this,StillNeedPermission.toArray(new String[0]));
                            });
                            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {

                                AlertDialog.Builder exitDialog =  ShowPermissionAlert(R.string.PermissionNoGrantedAlertTitle,
                                                                                      R.string.PermissionNoGrantedAlertTitle,false);
                                exitDialog.setCancelable(false);
                                exitDialog.setPositiveButton("OK", (d, ig) -> finish());
                                exitDialog.show();

                            });
                            builder.show();
                        }
                    }
                }
            }
        }
    }

    private AlertDialog.Builder ShowPermissionAlert(int TitleId,int MessageId,boolean Cancelable)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
        alertDialog.setTitle(R.string.PermissionNoGrantedAlertTitle);
        alertDialog.setMessage(R.string.PermissionDontShowAgainAlertMessage);
        alertDialog.setCancelable(Cancelable);
        return alertDialog;
    }

    class PrepareApplicationTask extends AsyncTask<Void,Void,Boolean>
    {

        @Override
        protected Boolean doInBackground(Void ...voids)
        {
            return GlobalPermissions.RequestAllPermissions(SplashActivity.this,SplashActivity.this);
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            if(result) JumpToMainActivity();
        }
    }
}
