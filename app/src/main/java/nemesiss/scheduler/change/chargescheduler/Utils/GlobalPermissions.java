package nemesiss.scheduler.change.chargescheduler.Utils;

import android.*;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class GlobalPermissions
{
    public static final String[] NeedAllPermissions =
            new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE
            };

    private static boolean DontShowAgain = false;
    public static final int GRANT_ALL_PERMISSION_CODE = 3927;
    public static boolean RequestPermissions(Context ctx,Activity activity,String[] RequestPermissionList){
        List<String> NoGrantedPermissions = new ArrayList<>();
        for (int i = 0; i < NeedAllPermissions.length; i++)
        {
            if(ContextCompat.checkSelfPermission(ctx,NeedAllPermissions[i])!= PackageManager.PERMISSION_GRANTED){
                NoGrantedPermissions.add(NeedAllPermissions[i]);
            }
        }
        if(!NoGrantedPermissions.isEmpty()){
            ActivityCompat.requestPermissions(activity,NoGrantedPermissions.toArray(new String[0]), GRANT_ALL_PERMISSION_CODE);
            return false;
        }
        return true;
    }
    public static boolean RequestAllPermissions(Context ctx,Activity activity)
    {
        return RequestPermissions(ctx,activity,NeedAllPermissions);
    }

    public static void SetDontShowAgainFlag()
    {
        DontShowAgain = true;
    }
    public static boolean GetDontShowAgianFlag(){
        return DontShowAgain;
    }
}
