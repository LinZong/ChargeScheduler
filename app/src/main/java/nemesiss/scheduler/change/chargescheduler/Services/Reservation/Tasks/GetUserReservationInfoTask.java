package nemesiss.scheduler.change.chargescheduler.Services.Reservation.Tasks;

import android.util.Log;
import android.util.Pair;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Constants.RequestUrl;
import nemesiss.scheduler.change.chargescheduler.Models.Response.ReservationInfo;
import nemesiss.scheduler.change.chargescheduler.Services.Users.CommonServices;
import nemesiss.scheduler.change.chargescheduler.Utils.CustomPostExecuteAsyncTask;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;
import nemesiss.scheduler.change.chargescheduler.Utils.TaskPostExecuteWrapper;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetUserReservationInfoTask extends CustomPostExecuteAsyncTask<Long,Void, List<ReservationInfo>>
{
    private OkHttpClient client;

    public GetUserReservationInfoTask(TaskPostExecuteWrapper<List<ReservationInfo>> DoInPostExecute)
    {
        super(DoInPostExecute);
    }

    @Override
    protected List<ReservationInfo> doInBackground(Long... longs)
    {
        String token = ChargerApplication.getToken().getToken();
        try
        {
            List<Pair<String,String>> body = new ArrayList<>();
            body.add(new Pair<>("id",String.valueOf(ChargerApplication.getLoginedUser().getId())));
            if(longs!=null && longs.length>0)
            {
                if(longs[0]!=null){
                    //Should query reservation specified.
                    body.add(new Pair<>("resid",String.valueOf(longs[0])));
                }
                if(longs[1]!=null){
                    body.add(new Pair<>("newone","1"));
                }
            }
            Response resp = CommonServices.SendPostRequest(client, RequestUrl.getMyReservations(),
                    GlobalUtils.BearerAuthRequestHeaders(token),body);
            if(GlobalUtils.ConfirmResponseSuccessful(resp))
            {
                String json = resp.body().string();
                Gson gson = new Gson();
                List<ReservationInfo> infoList = gson.fromJson(json,new TypeToken<List<ReservationInfo>>(){}.getType());
                return infoList;
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        Log.e("ReservationServices","不能获取用户的预约历史信息.");
        return null;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        client = GlobalUtils.GetOkHttpClient().build();
    }

    @Override
    protected void onPostExecute(List<ReservationInfo> list)
    {
        super.onPostExecute(list);
    }
}