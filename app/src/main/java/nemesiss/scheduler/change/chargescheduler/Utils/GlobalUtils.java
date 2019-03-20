package nemesiss.scheduler.change.chargescheduler.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GlobalUtils
{
    public static OkHttpClient.Builder clientInstance = null;

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

    public static void ShowNoNetworkError()
    {
        Toast.makeText(ChargerApplication.getContext(),"无法连接服务器,请检查网络连接",Toast.LENGTH_SHORT).show();
    }

    public static List<Pair<String,String>> BearerAuthRequestHeaders(String token)
    {
        List<Pair<String,String>> header = new ArrayList<>();
        header.add(new Pair<>("Authorization","Bearer "+token));
        return header;
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

    public static OkHttpClient.Builder GetOkHttpClient()
    {
        if(clientInstance == null)
        {
            clientInstance = new OkHttpClient.Builder().connectTimeout(4500, TimeUnit.MILLISECONDS);
        }
        return clientInstance;
    }

    public static double GetDistanceBetweenLatLng(double lat1, double lng1, double lat2,double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378.137;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        return s;
    }
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static Date UnixStamp2Date(long timeStamp)
    {
        String  formats = "yyyy-MM-dd HH:mm:ss";
        Long timestamp = timeStamp * 1000;
        return new Date(timestamp);
    }

    public static long Date2UnixStamp(Date date)
    {
        return date.getTime()/1000;
    }
}
