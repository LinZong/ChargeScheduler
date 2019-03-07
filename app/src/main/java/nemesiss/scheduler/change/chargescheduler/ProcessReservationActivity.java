package nemesiss.scheduler.change.chargescheduler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.amap.api.services.help.Tip;
import nemesiss.scheduler.change.chargescheduler.Fragments.ProcessingFinishedFragment;
import nemesiss.scheduler.change.chargescheduler.Fragments.ProcessingFragment;
import nemesiss.scheduler.change.chargescheduler.Models.ChargeReservation;

public class ProcessReservationActivity extends AppCompatActivity
{

    private ChargeReservation reservation;
    private Tip WillGoToAddressTip;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_reservation);

        //get ChargeReservation Info and try to connect server with an async task.
        LoadProcessingStatusFragment(new ProcessingFragment());

        reservation = (ChargeReservation) getIntent().getSerializableExtra("ChargeReservation");
        WillGoToAddressTip = getIntent().getParcelableExtra("WillGoToAddressTip");

        //假装持续三秒之后回到主界面
        Handler handler = new Handler();
        handler.postDelayed(()-> LoadProcessingStatusFragment(new ProcessingFinishedFragment()), 3000);
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

    class RequestReservationTask extends AsyncTask<ChargeReservation,Void,Boolean>
    {

        @Override
        protected Boolean doInBackground(ChargeReservation... cr)
        {
            return null;
        }
    }
}
