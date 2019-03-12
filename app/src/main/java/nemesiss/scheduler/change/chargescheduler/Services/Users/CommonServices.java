package nemesiss.scheduler.change.chargescheduler.Services.Users;

import android.util.Log;
import android.util.Pair;
import com.google.gson.Gson;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Constants.RequestUrl;
import nemesiss.scheduler.change.chargescheduler.Models.Response.BusyTimePeriod;
import nemesiss.scheduler.change.chargescheduler.Models.User;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;
import okhttp3.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CommonServices
{
    public static Response SendPostRequest(OkHttpClient client, String url, List<Pair<String, String>> Header, List<Pair<String, String>> Body) throws IOException
    {
        MultipartBody.Builder requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (Body != null)
        {
            for (Pair<String, String> p : Body)
            {
                requestBody.addFormDataPart(p.first, p.second);
            }
        }

        Request.Builder reqBuilder = new Request.Builder().url(url).post(requestBody.build());
        if (Header != null)
        {
            for (Pair<String, String> head : Header)
            {
                reqBuilder.addHeader(head.first, head.second);
            }
        }
        Request req = reqBuilder.build();
        return client.newCall(req).execute();
    }

    public static Response SendGetRequest(OkHttpClient client,
                                          String url,
                                          List<Pair<String, String>> queryString,
                                          List<Pair<String, String>> header) throws IOException
    {
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        if (queryString != null)
        {
            for (Pair<String, String> qs : queryString)
            {
                builder.addQueryParameter(qs.first, qs.second);
            }
        }
        HttpUrl newUrl = builder.build();
        Request.Builder reqBuilder = new Request.Builder().url(newUrl);
        if (header != null)
        {
            for (Pair<String, String> head : header)
            {
                reqBuilder.addHeader(head.first, head.second);
            }
        }
        return client.newCall(reqBuilder.build()).execute();
    }


    public static boolean CheckIfNeedToRefreshToken(Date tokenTaggedExpireDate)
    {
        Date Now = new Date();
        long tagged = tokenTaggedExpireDate.getTime();
        long now = Now.getTime();
        return ((int) (tagged - now) / (1000 * 60)) < 5;
    }

    public static boolean RefreshToken()
    {
        User us = ChargerApplication.getLoginedUser();
        if (us == null) return false;
        UserServices.LoginStatus status = UserServices.Login(us.getPhone(), us.getPassword(), false);
        return status == UserServices.LoginStatus.LOGIN_SUCCESSFUL;
    }

    public static boolean CheckIfBusyPeriod(Date BusyBegin, Date BusyEnd)
    {

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        Calendar nowCalender = Calendar.getInstance();

        c1.setTime(BusyBegin);
        c2.setTime(BusyEnd);
        nowCalender.setTime(new Date());

        int c1Hour = c1.get(Calendar.HOUR_OF_DAY);
        int c2Hour = c2.get(Calendar.HOUR_OF_DAY);
        int nowHour = nowCalender.get(Calendar.HOUR_OF_DAY);
        return c1Hour <= nowHour && nowHour <= c2Hour;
    }
    public static boolean CheckIfBusyPeriodBasedOnPreset()
    {
        try
        {
            Date bg = ChargerApplication.getBusyTimePeriod().getBeginAsDate();
            Date ed = ChargerApplication.getBusyTimePeriod().getEndAsDate();
            return CheckIfBusyPeriod(bg,ed);
        } catch (ParseException e)
        {
            e.printStackTrace();
            Log.e("CommonServices","根据Application中的繁忙时间段检测是否为高峰期失败.");
        }
        return false;
    }
    public static BusyTimePeriod GetBusyTimePeriod(OkHttpClient client)
    {
        Response resp = null;
        BusyTimePeriod busy = null;
        try
        {
            resp = SendGetRequest(client, RequestUrl.getBusyTimePeriod(),null, null);
            if (GlobalUtils.ConfirmResponseSuccessful(resp))
            {
                String jsonResp = null;
                try
                {
                    jsonResp = resp.body().string();
                    busy = new Gson().fromJson(jsonResp,BusyTimePeriod.class);
                    ChargerApplication.setBusyTimePeriod(busy);
                    Log.d("CommonServices","成功设置高峰时间段.");
                    Log.d("CommonServices","当前是否为高峰时间段?"+
                            CommonServices.CheckIfBusyPeriod(
                            ChargerApplication
                            .getBusyTimePeriod()
                            .getBeginAsDate(),
                            ChargerApplication
                            .getBusyTimePeriod()
                            .getEndAsDate()));
                    return busy;
                } catch (IOException e)
                {
                    e.printStackTrace();
                    Log.e("CommonServices", "获取当前繁忙时间失败. 读取请求IO失败。重设为默认时间."+e.getMessage());
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Log.e("CommonServices", "获取当前繁忙时间失败。请求不成功。重设为默认时间.");
            }

        } catch (IOException e)
        {
            Log.e("CommonServices", "获取当前繁忙时间失败。无法连接远程服务器。重设为默认时间."+e.getMessage());
            e.printStackTrace();
        }
        busy = new BusyTimePeriod();
        busy.setBegin("9:00");
        busy.setEnd("18:00");
        ChargerApplication.setBusyTimePeriod(busy);
        return busy;
    }
}
