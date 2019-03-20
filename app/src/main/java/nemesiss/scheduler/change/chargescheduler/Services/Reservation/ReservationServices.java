package nemesiss.scheduler.change.chargescheduler.Services.Reservation;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Constants.RequestUrl;
import nemesiss.scheduler.change.chargescheduler.Models.RequestReservationInfo;
import nemesiss.scheduler.change.chargescheduler.Models.Response.ReservationInfo;
import nemesiss.scheduler.change.chargescheduler.Services.Users.CommonServices;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import rx.subjects.BehaviorSubject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReservationServices
{

    private BehaviorSubject<List<ReservationInfo>> UserReservationInfoObservable = BehaviorSubject.create();

    public ReservationServices()
    {

    }

    public BehaviorSubject<List<ReservationInfo>> GetUserReservationInfoObservable()
    {
        return UserReservationInfoObservable;
    }

    public void RefreshUserReservations()
    {
        new GetUserReservationInfoTask().execute();
    }



    public void SendReservationInfo()
    {
        RequestReservationInfo info = new RequestReservationInfo();

    }




    class GetUserReservationInfoTask extends AsyncTask<Void,Void,List<ReservationInfo>>
    {
        private OkHttpClient client;
        @Override
        protected List<ReservationInfo> doInBackground(Void... voids)
        {
            String token = ChargerApplication.getToken().getToken();
            try
            {
                List<Pair<String,String>> body = new ArrayList<>();
                body.add(new Pair<>("id",String.valueOf(ChargerApplication.getLoginedUser().getId())));

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
            if(list!=null)
            {
                GetUserReservationInfoObservable().onNext(list);
            }
            else
            {
                Toast.makeText(ChargerApplication.getContext(),"无法获得用户的预约信息, 请检查网络连接。",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
