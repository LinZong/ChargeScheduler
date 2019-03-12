package nemesiss.scheduler.change.chargescheduler.Fragments.MyReservation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nemesiss.scheduler.change.chargescheduler.Adapters.MyReservationPagerAdapter;
import nemesiss.scheduler.change.chargescheduler.Adapters.ReservationItemAdapter;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Models.Response.ReservationInfo;
import nemesiss.scheduler.change.chargescheduler.R;

import java.util.ArrayList;
import java.util.List;

public class MyReservationListFragment extends Fragment
{
    public static final String ShowReservationType = "ShowReservationType";
    private MyReservationPagerAdapter.ReservationList CurrentFragmentType;
    private View view;
    private Unbinder unbinder;

    @BindView(R.id.MyReservation_RecycleView)
    RecyclerView MyReservation_RecycleView;

    private ReservationItemAdapter adapter;

    public static MyReservationListFragment NewInstance(MyReservationPagerAdapter.ReservationList listType)
    {
        Bundle args = new Bundle();
        args.putSerializable(ShowReservationType,listType);
        MyReservationListFragment pageFragment = new MyReservationListFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        CurrentFragmentType = (MyReservationPagerAdapter.ReservationList) getArguments().getSerializable(ShowReservationType);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.my_reservation_fragment,container,false);
        unbinder = ButterKnife.bind(this,view);


        //load fake data
        List<ReservationInfo> xx = new ArrayList<>();
        for (int i = 0; i < 5; i++)
        {
            xx.add(new ReservationInfo());
        }

        adapter = new ReservationItemAdapter(xx);
        LinearLayoutManager manager = new LinearLayoutManager(inflater.getContext());

        MyReservation_RecycleView.setLayoutManager(manager);
        MyReservation_RecycleView.setAdapter(adapter);





        return view;
    }



    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }
}
