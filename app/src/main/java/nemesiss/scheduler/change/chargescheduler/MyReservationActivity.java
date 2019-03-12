package nemesiss.scheduler.change.chargescheduler;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import nemesiss.scheduler.change.chargescheduler.Adapters.MyReservationPagerAdapter;

public class MyReservationActivity extends AppCompatActivity
{
    private MyReservationPagerAdapter ReservationPagerAdapter;

    @BindView(R.id.ReservationTypeTab)   TabLayout ReservationTypeTab;

    @BindView(R.id.ReservationDetailedPager)   ViewPager ReservationDetailedPager;
    @BindView(R.id.MyReservationActivityToolbar)
    Toolbar tb;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservation);
        ButterKnife.bind(this);

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
