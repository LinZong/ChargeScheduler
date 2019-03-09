package nemesiss.scheduler.change.chargescheduler.Services.Users;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nemesiss.scheduler.change.chargescheduler.Constants.RequestUrl;
import nemesiss.scheduler.change.chargescheduler.Models.Response.CarType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CarServices
{
    private OkHttpClient client;

    public CarServices()
    {
       client = new OkHttpClient.Builder().connectTimeout(4500, TimeUnit.MILLISECONDS).build();
    }

    public List<CarType> GetAllCarType()
    {
        String url = RequestUrl.getCarTypeUrl();
        try
        {
            Response resp = CommonServices.SendGetRequest(client,url,null,null);
            if(resp!= null && resp.isSuccessful())
            {
                String respJson = resp.body().string();
                Gson gson = new Gson();
                return gson.fromJson(respJson,new TypeToken<List<CarType>>(){}.getType());
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            Log.e("CARSERVICEERROR","请求所有车型号失败, 原因为"+e.getMessage());
        }
        return null;
    }
}
