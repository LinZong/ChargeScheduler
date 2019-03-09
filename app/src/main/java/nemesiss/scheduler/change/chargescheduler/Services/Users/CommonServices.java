package nemesiss.scheduler.change.chargescheduler.Services.Users;

import android.util.Pair;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class CommonServices
{
    public static Response SendPostRequest(OkHttpClient client, String url, List<Pair<String,String>> Header, List<Pair<String,String>> Body) throws IOException
    {
        MultipartBody.Builder requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if(Body!=null)
        {
            for (Pair<String, String> p : Body)
            {
                requestBody.addFormDataPart(p.first,p.second);
            }
        }

        Request.Builder reqBuilder = new Request.Builder().url(url).post(requestBody.build());
        if(Header!=null)
        {
            for (Pair<String, String> head : Header)
            {
                reqBuilder.addHeader(head.first,head.second);
            }
        }
        Request req = reqBuilder.build();
        return client.newCall(req).execute();
    }
    public static Response SendGetRequest(OkHttpClient client,
                                          String url,
                                          List<Pair<String,String>> queryString,
                                          List<Pair<String,String>> header) throws IOException
    {
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        if(queryString!=null)
        {
            for (Pair<String, String> qs : queryString)
            {
                builder.addQueryParameter(qs.first,qs.second);
            }
        }
        HttpUrl newUrl = builder.build();
        Request.Builder reqBuilder = new Request.Builder().url(newUrl);
        if(header!=null)
        {
            for (Pair<String, String> head : header)
            {
                reqBuilder.addHeader(head.first,head.second);
            }
        }
        return client.newCall(reqBuilder.build()).execute();
    }
}
