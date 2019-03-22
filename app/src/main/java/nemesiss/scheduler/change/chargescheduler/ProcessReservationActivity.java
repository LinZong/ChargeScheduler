package nemesiss.scheduler.change.chargescheduler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.gson.Gson;
import nemesiss.scheduler.change.chargescheduler.Application.ChargeActivity;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Constants.RequestUrl;
import nemesiss.scheduler.change.chargescheduler.Fragments.ProcessingFailedFragment;
import nemesiss.scheduler.change.chargescheduler.Fragments.ProcessingFinishedFragment;
import nemesiss.scheduler.change.chargescheduler.Fragments.ProcessingFragment;
import nemesiss.scheduler.change.chargescheduler.Models.ChargeReservation;
import nemesiss.scheduler.change.chargescheduler.Models.RequestReservationInfo;
import nemesiss.scheduler.change.chargescheduler.Models.Response.CommonResponseModel;
import nemesiss.scheduler.change.chargescheduler.Models.Response.Stations;
import nemesiss.scheduler.change.chargescheduler.Services.Reservation.StationServices;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessReservationActivity extends ChargeActivity
{

    @BindView(R.id.JudgeStationHint)
    TextView JudgeStationHint;
    private ChargeReservation reservation;
    private ArrayList<Stations> WillGoToStations;
    private StationServices stationServices;


    private boolean ShouldJudge = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_reservation);
        ButterKnife.bind(this);

        stationServices = ChargerApplication.getStationServices();

        //get ChargeReservation Info and try to connect server with an async task.
        LoadProcessingStatusFragment(new ProcessingFragment());

        reservation = (ChargeReservation) getIntent().getSerializableExtra("ChargeReservation");
        WillGoToStations = (ArrayList<Stations>)getIntent().getSerializableExtra("WillGoToStations");

        ShouldJudge = getIntent().getBooleanExtra("ShouldJudgeStations",false);
        if(ShouldJudge){
            JudgeStationHint.setVisibility(View.VISIBLE);
        }
        new RequestReservationTask().execute();
    }

    private void LoadProcessingStatusFragment(Fragment fragment)
    {
        FragmentManager fm = getSupportFragmentManager();
        if(fm!=null)
        {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.ProcessingStatusLayout,fragment);
            ft.commit();
        }
    }

    class RequestReservationTask extends AsyncTask<Void,Void, CommonResponseModel>
    {

        private OkHttpClient client;
        @Override
        protected CommonResponseModel doInBackground(Void... cr)
        {
            ArrayList<Stations> willRequest = null;
            if(ShouldJudge)
            {
                willRequest = stationServices.GetCanArriveIn15MinStations();
            }
            else
                willRequest = WillGoToStations;
            if(willRequest==null||willRequest.size()==0)
                return null;

            RequestReservationInfo reqInfo = new RequestReservationInfo();

            switch (reservation.getChargeType())
            {
                case ChargeImmediate:
                    //有不同的构造方式
                    reqInfo.setUserId(ChargerApplication.getLoginedUser().getId());
                    reqInfo.setReservationType(0);
                    int[] wantStation = new int[willRequest.size()];
                    int[] wantStationDistance = new int[willRequest.size()];
                    for (int i = 0; i < willRequest.size(); i++)
                    {
                        wantStation[i] = willRequest.get(i).getId();
                        wantStationDistance[i] = willRequest.get(i).getDistanceBetweenMe();
                    }
                    reqInfo.setRemainBattery(reservation.getRemainBattery());
                    reqInfo.setWantStation(wantStation);
                    reqInfo.setWantStationDistance(wantStationDistance);
                    break;
                case ChargeAllNight:
                    reqInfo.setUserId(ChargerApplication.getLoginedUser().getId());
                    reqInfo.setReservationType(1);
                    int[] wantStation1 = new int[willRequest.size()];
                    for (int i = 0; i < willRequest.size(); i++)
                    {
                        wantStation1[i] = willRequest.get(i).getId();
                    }
                    reqInfo.setRemainBattery(reservation.getRemainBattery());
                    reqInfo.setWantStation(wantStation1);
                    reqInfo.setStartTime(GlobalUtils.Date2UnixStamp(reservation.getReservationTime()));
                    break;
            }

            Gson gson = new Gson();
            String json = gson.toJson(reqInfo);

            List<Pair<String,String>> headers = GlobalUtils.BearerAuthRequestHeaders(ChargerApplication.getToken().getToken());
            Request.Builder reqBd = new Request.Builder().url(RequestUrl.getCreateNewReservation()).post(RequestBody.create(MediaType.parse("application/json"),json));
            for (Pair<String,String> s :headers)
            {
                reqBd.addHeader(s.first,s.second);
            }

            Request req = reqBd.build();

            try
            {
                Response resp = client.newCall(req).execute();
                if(GlobalUtils.ConfirmResponseSuccessful(resp))
                {
                    String respJson = resp.body().string();
                    return gson.fromJson(respJson,CommonResponseModel.class);
                }
            } catch (IOException e)
            {
                e.printStackTrace();
                Log.e("ProcessReservation","不能成功发送预约请求");
            }
            return null;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            client = GlobalUtils.GetOkHttpClient().build();
        }

        @Override
        protected void onPostExecute(CommonResponseModel result)
        {
            super.onPostExecute(result);
            JudgeStationHint.setVisibility(View.GONE);
            if(result==null)
            {
                LoadProcessingStatusFragment(new ProcessingFailedFragment());
            }
            else
            {
                String tips = "";
                switch (result.getStatusCode()){

                    case 1300:
                        tips = "成功预约.";
                        LoadProcessingStatusFragment(new ProcessingFinishedFragment());
                        break;
                    case 1301:
                        tips = "用户不存在.";
                        LoadProcessingStatusFragment(new ProcessingFailedFragment());
                        break;
                    case 1302:
                    case 1303:
                        tips = "远程服务器出现错误.";
                        LoadProcessingStatusFragment(new ProcessingFailedFragment());
                        break;
                    case 1305:
                        tips = "没有可供延时预约使用的桩位";
                        LoadProcessingStatusFragment(new ProcessingFailedFragment());
                        break;
                    case 1306:
                        tips = "当前用户诚信分不足, 不能进行预约操作.";
                        LoadProcessingStatusFragment(new ProcessingFailedFragment());
                        break;
                }
                JudgeStationHint.setText(tips);
                JudgeStationHint.setVisibility(View.VISIBLE);
            }
        }
    }
}
