package nemesiss.scheduler.change.chargescheduler.Application;

import android.app.Application;
import android.content.Context;
import nemesiss.scheduler.change.chargescheduler.Models.Response.TokenResponseInfo;
import nemesiss.scheduler.change.chargescheduler.Models.User;
import nemesiss.scheduler.change.chargescheduler.Services.Users.CarServices;
import nemesiss.scheduler.change.chargescheduler.Services.Users.UserServices;

public class ChargerApplication extends Application
{
    private static Context ctx;
    //定义需要注册的服务
    private static UserServices userServices;
    private static CarServices carServices;
    //定义需要全局被引用的变量
    private static User LoginedUser;
    private static TokenResponseInfo token;
    @Override
    public void onCreate()
    {
        super.onCreate();
        ctx = getApplicationContext();
        userServices = new UserServices();
        carServices = new CarServices();
    }

    public static Context getContext()
    {
        return ctx;
    }

    public static UserServices getUserServices()
    {
        return userServices;
    }

    public static CarServices getCarServices(){return  carServices;}

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
}
