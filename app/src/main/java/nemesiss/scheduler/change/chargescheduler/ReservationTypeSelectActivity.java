package nemesiss.scheduler.change.chargescheduler;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.amap.api.services.help.Tip;
import nemesiss.scheduler.change.chargescheduler.Fragments.ChainFragment;
import nemesiss.scheduler.change.chargescheduler.Fragments.ReserverTimeFrag;
import nemesiss.scheduler.change.chargescheduler.Fragments.ReserverTypeFrag;
import nemesiss.scheduler.change.chargescheduler.Models.ChargeReservation;

import java.util.ArrayList;
import java.util.List;

public class ReservationTypeSelectActivity extends AppCompatActivity implements ChainFragment
{



    public enum ChargeType {
        ChargeImmediate,
        ChargeAllNight
    }

    private TextView ReservationTypeTextView;
    private TextView ReservationTargetChargerName;
    private TextView ReservationTargerChargerAddress;
    private TextView ReservationTargetChargerStatus;
    private int CurrentFragmentNum = 0;
    private List<Fragment> ReplaceFragmentList = new ArrayList<>();
    private Tip WillGoToAddressTip;
    private ChainFragment current;

    private ChargeReservation reservation = new ChargeReservation();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_type_select);
        current = (ChainFragment) getSupportFragmentManager().findFragmentById(R.id.ReservationFragment);
        ToNextFragment(new ReserverTypeFrag());
        WillGoToAddressTip = getIntent().getParcelableExtra("WillGoToAddressTip");
    }

    public void setCurrentFragmentChain(ChainFragment currentFragmentId)
    {
        current = currentFragmentId;
    }

    public ChainFragment getCurrentFragmentChain()
    {
        return current;
    }
    public void SetReservationType(ChargeType type)
    {
        reservation.setChargeType(type);
    }
    public void SetReservationTime(String time)
    {
        reservation.setReservationTime(time);
    }
    @Override
    public void ToNextFragment(Fragment next)
    {
        FragmentManager fm = getSupportFragmentManager();
        if(fm!=null)
        {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.ReservationFragment,next);
            setCurrentFragmentChain((ChainFragment)next);
            ft.commit();
        }
    }
}
