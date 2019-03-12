package nemesiss.scheduler.change.chargescheduler.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import nemesiss.scheduler.change.chargescheduler.Fragments.MyReservation.MyReservationListFragment;

import java.io.Serializable;

public class MyReservationPagerAdapter extends FragmentPagerAdapter
{
    public enum ReservationList implements Serializable
    {
        ALL_RESERVATION(0),
        FINISHED_RESERVATION(1),
        UNFINISHED_RESERVATION(2);

        private int index;
        ReservationList(int idx)
        {
            index = idx;
        }
    }

    final int PAGE_COUNT = 3;
    private Context context;
    private String[] TabsTitle;
    public MyReservationPagerAdapter(FragmentManager fm, Context ctx)
    {
        super(fm);
        context = ctx;
        TabsTitle = new String[]{"全部","已完成","未完成"};
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position){
            case 0:
                return MyReservationListFragment.NewInstance(ReservationList.ALL_RESERVATION);
            case 1:
                return MyReservationListFragment.NewInstance(ReservationList.FINISHED_RESERVATION);
            case 2:
                return MyReservationListFragment.NewInstance(ReservationList.UNFINISHED_RESERVATION);
            default:
                return MyReservationListFragment.NewInstance(ReservationList.ALL_RESERVATION);
        }
    }

    @Override
    public int getCount()
    {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return TabsTitle[position];
    }

}
