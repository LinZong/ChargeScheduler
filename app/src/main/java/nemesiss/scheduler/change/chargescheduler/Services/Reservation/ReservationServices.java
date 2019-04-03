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
import nemesiss.scheduler.change.chargescheduler.Services.Reservation.Tasks.GetUserReservationInfoTask;
import nemesiss.scheduler.change.chargescheduler.Services.Users.CommonServices;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;
import nemesiss.scheduler.change.chargescheduler.Utils.TaskPostExecuteWrapper;
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
        new GetUserReservationInfoTask(TaskRet -> {
            GetUserReservationInfoObservable().onNext(TaskRet);
        }).execute();
    }

    public void SendReservationInfo()
    {
        //RequestReservationInfo info = new RequestReservationInfo();

    }
}
