package nemesiss.scheduler.change.chargescheduler.Application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import nemesiss.scheduler.change.chargescheduler.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ChargeActivity extends AppCompatActivity
{
    private static List<Activity> AllActivities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AllActivities.add(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        AllActivities.remove(this);
    }

    public static void FinishAllActivities()
    {
        for(Activity ac : AllActivities)
        {
            if(!ac.isFinishing())
            {
                ac.finish();
            }
        }
    }
}
