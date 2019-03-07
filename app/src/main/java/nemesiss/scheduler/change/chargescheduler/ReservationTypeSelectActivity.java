package nemesiss.scheduler.change.chargescheduler;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.amap.api.services.help.Tip;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import nemesiss.scheduler.change.chargescheduler.Fragments.ChainFragment;
import nemesiss.scheduler.change.chargescheduler.Fragments.ReserverTimeFrag;
import nemesiss.scheduler.change.chargescheduler.Fragments.ReserverTypeFrag;
import nemesiss.scheduler.change.chargescheduler.Models.ChargeReservation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationTypeSelectActivity extends FragmentActivity implements ChainFragment
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
        WillGoToAddressTip = getIntent().getParcelableExtra("WillGoToAddressTip");

        //显示下方碎片
        ToNextFragment(new ReserverTypeFrag());
        InitialViewBinding();


        SetWillGoToAddressTipInfo(WillGoToAddressTip);


    }

    private void InitialViewBinding()
    {
        ReservationTypeTextView = findViewById(R.id.ReservationTypeTextView);
        ReservationTargetChargerName = findViewById(R.id.ReservationTargetChargerName);
        ReservationTargerChargerAddress = findViewById(R.id.ReservationTargerChargerAddress);
        ReservationTargetChargerStatus = findViewById(R.id.ReservationTargetChargerStatus);
    }

    public void SetHintTitle(String title)
    {
        if(ReservationTypeTextView != null)
        {
            ReservationTypeTextView.setText(title);
        }
    }

    private void SetWillGoToAddressTipInfo(Tip tip)
    {
        if(tip!=null)
        {
            String detailedAddress = tip.getDistrict() + tip.getAddress();
            String chargeStationName = tip.getName();

            ReservationTargetChargerName.setText(chargeStationName);
            ReservationTargerChargerAddress.setText(detailedAddress);
        }
    }


    //管理调用链条的逻辑
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

    public void SetReservationTime(Date time)
    {
        reservation.setReservationTime(time);
    }

    @Override
    public void ToNextFragment(Fragment next)
    {
        FragmentManager fm = getSupportFragmentManager();
        if(fm!=null)
        {
            SetHintTitle("选择预约的类型");
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.ReservationFragment,next);
            setCurrentFragmentChain((ChainFragment)next);
            ft.commit();
        }
    }
    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
        }
    }

    public void GoToProcessReservationActivity()
    {
        Intent it = new Intent(ReservationTypeSelectActivity.this,ProcessReservationActivity.class);
        it.putExtra("ChargeReservation",reservation);
        it.putExtra("WillGoToAddressTip",WillGoToAddressTip);
        startActivity(it);
        finish();
    }
}
