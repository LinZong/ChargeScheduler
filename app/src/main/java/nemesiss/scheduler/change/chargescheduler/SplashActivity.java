package nemesiss.scheduler.change.chargescheduler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;
import nemesiss.scheduler.change.chargescheduler.Application.ChargeActivity;
import nemesiss.scheduler.change.chargescheduler.Services.Users.UserServices;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalPermissions;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;

import java.util.ArrayList;
import java.util.List;

import static nemesiss.scheduler.change.chargescheduler.Constants.GlobalVariables.LOGIN_PERSISTENCE;

public class SplashActivity extends ChargeActivity
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

    private void JumpToLoginActivity(){
        Intent it = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(it);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }

    private void JumpToSearchChargerActivity()
    {
        Intent it = new Intent(SplashActivity.this,SearchChargerActivity.class);
        startActivity(it);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }


    private void TryLoginPresetAccount()
    {
        //JumpToLoginActivity();
        //检测是否保存了账号，如果保存了就尝试登录，登陆成功直接进入主界面，否则没有保存账号或者账号已失效的，跳回登陆界面
        SharedPreferences sp = getSharedPreferences(LOGIN_PERSISTENCE, Context.MODE_PRIVATE);
        boolean HaveLoginIdentity = sp.getBoolean("HaveLoginIdentity",true);
        if(HaveLoginIdentity)
        {
            String pn = sp.getString("PhoneNumber","");
            String pw = sp.getString("Password","");
            if(GlobalUtils.ConfirmStringsAllNotEmpty(pn,pw))
            {
                Runnable LoginRunnable = () -> {
                    UserServices.LoginStatus status = UserServices.Login(pn,pw, true);
                    if(status.equals(UserServices.LoginStatus.LOGIN_SUCCESSFUL))
                    {
                        runOnUiThread(this::JumpToSearchChargerActivity);
                    }
                    else
                    {
                        runOnUiThread(this::JumpToLoginActivity);
                    }
                };
                new Thread(LoginRunnable).start();
            }
            else
            {
                JumpToLoginActivity();
            }
        }
        else
        {
            JumpToLoginActivity();
        }
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
                        JumpToLoginActivity();
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
            if(result) TryLoginPresetAccount();
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
    }
}
