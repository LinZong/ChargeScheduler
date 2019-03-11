package nemesiss.scheduler.change.chargescheduler.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import okhttp3.Request;
import okhttp3.Response;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class GlobalUtils
{
    public static ProgressDialog ShowProgressDialog(Context ctx,boolean Cancelable,String title,String content){
        ProgressDialog dialog = new ProgressDialog(ctx);
        dialog.setCancelable(Cancelable);
        dialog.setTitle(title);
        dialog.setMessage(content);
        return dialog;
    }

    public static AlertDialog.Builder ShowAlertDialog(Context ctx,boolean Cancelable,String title,String content)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        dialog.setCancelable(Cancelable);
        dialog.setTitle(title);
        dialog.setMessage(content);
        return dialog;
    }

    public static Request.Builder BearerAuthRequest(String token)
    {
        return new Request.Builder().addHeader("Authorization","Bearer "+token);
    }

    public static SimpleDateFormat TokenDateFormatter()
    {
        return new SimpleDateFormat("yyyy/M/d HH:mm:ss", Locale.CHINA);
    }

    public static SimpleDateFormat BusyPeriodFormatter()
    {
        return new SimpleDateFormat("H:mm",Locale.CHINA);
    }

    public static boolean ConfirmStringsAllNotEmpty(String[] strs)
    {
        for (int i = 0; i < strs.length; i++)
        {
            if(TextUtils.isEmpty(strs[i])) return false;
        }
        return true;
    }

    public static boolean ConfirmResponseSuccessful(Response resp)
    {
        return resp!=null && resp.isSuccessful();
    }

}
