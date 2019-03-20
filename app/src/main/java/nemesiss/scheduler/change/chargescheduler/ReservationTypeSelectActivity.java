package nemesiss.scheduler.change.chargescheduler;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import nemesiss.scheduler.change.chargescheduler.Fragments.ChainFragment;
import nemesiss.scheduler.change.chargescheduler.Fragments.DoReservationChain.ReserverTypeFrag;
import nemesiss.scheduler.change.chargescheduler.Models.ChargeReservation;
import nemesiss.scheduler.change.chargescheduler.Models.Response.Stations;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationTypeSelectActivity extends FragmentActivity implements ChainFragment
{

    public enum ChargeType {
        ChargeImmediate,
        ChargeAllNight
    }

    @BindView(R.id.ReservationTypeTextView) TextView ReservationTypeTextView;
    @BindView(R.id.ReservationTargetChargerName) TextView ReservationTargetChargerName;
    @BindView(R.id.ReservationTargerChargerAddress) TextView ReservationTargerChargerAddress;
    @BindView(R.id.InstanceCount) TextView instanceCount;
    @BindView(R.id.DelayCount) TextView delayCount;

    private int CurrentFragmentNum = 0;
    private List<Fragment> ReplaceFragmentList = new ArrayList<>();
    private Stations WillGoToStations;
    private ChainFragment current;
    private ChargeReservation reservation = new ChargeReservation();

    private boolean ShouldJudge = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_type_select);
        ButterKnife.bind(this);

        current = (ChainFragment) getSupportFragmentManager().findFragmentById(R.id.ReservationFragment);
        WillGoToStations = (Stations) getIntent().getSerializableExtra("WillGoToStations");


        ShouldJudge = getIntent().getBooleanExtra("ShouldJudgeStations",false);
        if(ShouldJudge){
            ReservationTargetChargerName.setText("将为您自动匹配最合适的充电站。");
            ReservationTargerChargerAddress.setVisibility(View.GONE);
        }

        //显示下方碎片
        ToNextFragment(new ReserverTypeFrag());
        SetWillGoToAddressTipInfo(WillGoToStations);
    }


    public void SetHintTitle(String title)
    {
        if(ReservationTypeTextView != null)
        {
            ReservationTypeTextView.setText(title);
        }
    }

    private void SetWillGoToAddressTipInfo(Stations stat)
    {
        if(stat !=null)
        {
            String detailedAddress = stat.getCity() + stat.getAddress();
            String chargeStationName = stat.getName();
            ReservationTargetChargerName.setText(chargeStationName);
            ReservationTargerChargerAddress.setText(detailedAddress);
            instanceCount.setText(String.valueOf(stat.getAvailableInstance()));
            delayCount.setText(String.valueOf(stat.getAvailableDelay()));
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

    public ReservationTypeSelectActivity.ChargeType GetReservationType()
    {
        return reservation.getChargeType();
    }

    public void SetReservationType(ChargeType type)
    {
        reservation.setChargeType(type);
    }

    public void SetReservationTime(Date time)
    {
        reservation.setReservationTime(time);
    }
    public void SetRemainBattery(int batt)
    {
        reservation.setRemainBattery(batt);
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
        ArrayList<Stations> stat = new ArrayList<>();
        stat.add(WillGoToStations);

        Intent it = new Intent(ReservationTypeSelectActivity.this,ProcessReservationActivity.class);
        it.putExtra("ChargeReservation",reservation);
        it.putExtra("WillGoToStations", stat);
        it.putExtra("ShouldJudgeStations",ShouldJudge);
        startActivity(it);
        finish();
    }
}
