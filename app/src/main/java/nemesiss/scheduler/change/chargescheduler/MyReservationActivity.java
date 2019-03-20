package nemesiss.scheduler.change.chargescheduler;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import nemesiss.scheduler.change.chargescheduler.Adapters.MyReservationPagerAdapter;
import nemesiss.scheduler.change.chargescheduler.Application.ChargeActivity;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Models.Response.ReservationInfo;
import nemesiss.scheduler.change.chargescheduler.Services.Reservation.ReservationServices;
import rx.subjects.BehaviorSubject;

import java.util.List;

public class MyReservationActivity extends ChargeActivity
{
    private MyReservationPagerAdapter ReservationPagerAdapter;

    @BindView(R.id.ReservationTypeTab)   TabLayout ReservationTypeTab;

    @BindView(R.id.ReservationDetailedPager)   ViewPager ReservationDetailedPager;
    @BindView(R.id.MyReservationActivityToolbar) Toolbar tb;

    private ReservationServices reservationServices;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservation);
        ButterKnife.bind(this);

        reservationServices = ChargerApplication.getReservationServices();

        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        if(ab!=null)
        {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        ReservationPagerAdapter = new MyReservationPagerAdapter(getSupportFragmentManager(),MyReservationActivity.this);
        ReservationDetailedPager.setAdapter(ReservationPagerAdapter);
        ReservationTypeTab.setupWithViewPager(ReservationDetailedPager);
        ReservationTypeTab.setTabMode(TabLayout.MODE_FIXED);
        ReservationDetailedPager.setOffscreenPageLimit(MyReservationPagerAdapter.PAGE_COUNT);
    }

    public void NotifyRefreshReservationInfo()
    {
        reservationServices.RefreshUserReservations();
    }

    public BehaviorSubject<List<ReservationInfo>> GetReservationInfoObservable()
    {
        return reservationServices.GetUserReservationInfoObservable();
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Position",ReservationTypeTab.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ReservationDetailedPager.setCurrentItem(savedInstanceState.getInt("Position"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
            default:break;
        }
        return true;
    }
}
