package nemesiss.scheduler.change.chargescheduler.Fragments.MyReservation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nemesiss.scheduler.change.chargescheduler.Adapters.MyReservationPagerAdapter;
import nemesiss.scheduler.change.chargescheduler.Adapters.ReservationItemAdapter;
import nemesiss.scheduler.change.chargescheduler.Models.Response.ReservationInfo;
import nemesiss.scheduler.change.chargescheduler.MyReservationActivity;
import nemesiss.scheduler.change.chargescheduler.R;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

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
    @BindView(R.id.RefreshReservationList)
    SwipeRefreshLayout swipeRefreshLayout;


    private MyReservationActivity ParentActivity;
    private ReservationItemAdapter adapter;
    private LinearLayoutManager manager;
    private List<ReservationInfo> reservationInfoList;
    private Subscription sub;

    public static MyReservationListFragment NewInstance(MyReservationPagerAdapter.ReservationList listType)
    {
        Bundle args = new Bundle();
        args.putSerializable(ShowReservationType, listType);
        MyReservationListFragment pageFragment = new MyReservationListFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ParentActivity = (MyReservationActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.my_reservation_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        CurrentFragmentType = (MyReservationPagerAdapter.ReservationList) getArguments().getSerializable(ShowReservationType);
        reservationInfoList = new ArrayList<>();

        swipeRefreshLayout.setOnRefreshListener(() -> ParentActivity.NotifyRefreshReservationInfo());

        sub = ParentActivity.GetReservationInfoObservable()
                .doOnSubscribe(() -> ParentActivity.NotifyRefreshReservationInfo())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    if (adapter == null)
                    {
                        adapter = new ReservationItemAdapter(reservationInfoList);
                        manager = new LinearLayoutManager(inflater.getContext());
                        MyReservation_RecycleView.setLayoutManager(manager);
                        MyReservation_RecycleView.setAdapter(adapter);
                    }
                    switch (CurrentFragmentType)
                    {

                        case ALL_RESERVATION:
                            reservationInfoList.clear();
                            reservationInfoList.addAll(list);
                            break;
                        case FINISHED_RESERVATION:
                            reservationInfoList.clear();
                            for (ReservationInfo r : list)
                            {
                                int finish = r.getIsFinished();
                                if (finish == 1 || finish == 2)
                                {
                                    reservationInfoList.add(r);
                                }
                            }
                            break;
                        case UNFINISHED_RESERVATION:
                            reservationInfoList.clear();
                            for (ReservationInfo r : list)
                            {
                                int finish = r.getIsFinished();
                                if (finish == 3 || finish == 4 || finish == 0)
                                {
                                    reservationInfoList.add(r);
                                }
                            }
                            break;
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();

                });

        swipeRefreshLayout.setRefreshing(true);
        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
        if(!sub.isUnsubscribed()) sub.unsubscribe();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

    }
}
