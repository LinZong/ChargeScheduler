package nemesiss.scheduler.change.chargescheduler.Application;

import android.app.Application;
import android.content.Context;

public class ChargerApplication extends Application
{
    private static Context ctx;

    @Override
    public void onCreate()
    {
        super.onCreate();
        ctx = getApplicationContext();
    }

    public static Context getContext()
    {
        return ctx;
    }
}
