package nemesiss.scheduler.change.chargescheduler.Application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import cn.jpush.android.api.JPushInterface;
import nemesiss.scheduler.change.chargescheduler.Models.Response.BusyTimePeriod;
import nemesiss.scheduler.change.chargescheduler.Models.Response.TokenResponseInfo;
import nemesiss.scheduler.change.chargescheduler.Models.User;
import nemesiss.scheduler.change.chargescheduler.R;
import nemesiss.scheduler.change.chargescheduler.Services.Reservation.ReservationServices;
import nemesiss.scheduler.change.chargescheduler.Services.Reservation.StationServices;
import nemesiss.scheduler.change.chargescheduler.Services.Users.CarServices;
import nemesiss.scheduler.change.chargescheduler.Services.Users.UserServices;

public class ChargerApplication extends Application
{
    private static Context ctx;
    //定义需要注册的服务
    private static UserServices userServices;
    private static CarServices carServices;
    private static StationServices stationServices;
    private static ReservationServices reservationServices;
    //定义需要全局被引用的变量
    private static User LoginedUser;
    private static TokenResponseInfo token;
    private static BusyTimePeriod busyTimePeriod;
    public static boolean[] FeaturesSwitcher = new boolean[10];

    @Override
    public void onCreate()
    {
        for (int i = 0; i < 10; i++)
        {
            FeaturesSwitcher[i] = true;
        }
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        //createNotificationChannel();
        ctx = getApplicationContext();
        userServices = new UserServices();
        carServices = new CarServices();
        stationServices = new StationServices();
        reservationServices = new ReservationServices();

    }



    public static Context getContext()
    {
        return ctx;
    }

    public static UserServices getUserServices()
    {
        return userServices;
    }

    public static CarServices getCarServices()
    {
        return carServices;
    }

    public static StationServices getStationServices()
    {
        return stationServices;
    }

    public static ReservationServices getReservationServices()
    {
        return reservationServices;
    }

    public static void setLoginedUser(User loginedUser)
    {
        //TODO : add some databased-persistence logic here.
        LoginedUser = loginedUser;
    }

    public static User getLoginedUser()
    {
        return LoginedUser;
    }

    public static void setToken(TokenResponseInfo tk)
    {
        token = tk;
    }

    public static TokenResponseInfo getToken()
    {
        return token;
    }

    public static BusyTimePeriod getBusyTimePeriod()
    {
        return busyTimePeriod;
    }

    public static void setBusyTimePeriod(BusyTimePeriod busyTimePeriod)
    {
        ChargerApplication.busyTimePeriod = busyTimePeriod;
    }


}
