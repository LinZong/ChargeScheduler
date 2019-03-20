package nemesiss.scheduler.change.chargescheduler;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SearchView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.jakewharton.rxbinding.widget.RxSearchView;
import nemesiss.scheduler.change.chargescheduler.Adapters.SearchResultAdapter;
import nemesiss.scheduler.change.chargescheduler.Application.ChargeActivity;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Models.Response.Stations;
import nemesiss.scheduler.change.chargescheduler.Services.Reservation.StationServices;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchActivity extends ChargeActivity
{
    @BindView(R.id.Search_SearchBar)
    SearchView searchView;
    @BindView(R.id.Search_SearchResultRecycleView)
    RecyclerView recyclerView;
    @BindView(R.id.SearchRefreshIcon)
    SwipeRefreshLayout swipeRefreshLayout;

    private LinearLayoutManager linearLayoutManager;
    private SearchResultAdapter resultAdapter;
    private List<Stations> SearchResult = new ArrayList<>();
    private String MyCityFromMain = null;
    private Location MyLocationFromMainActivity = null;
    private StationServices stationServices = null;


    private void GetLocationFromMainActivity()
    {
        MyCityFromMain = getIntent().getStringExtra(getResources().getString(R.string.MyLocationCityName));
        MyLocationFromMainActivity = getIntent().getParcelableExtra(getResources().getString(R.string.MyLocation_Intent));
        String fakeSearchResult = getIntent().getStringExtra("FakeSearchBarResult");
        //将上次搜索结果带过来
        if (!TextUtils.isEmpty(fakeSearchResult) && searchView != null)
        {
            searchView.setQuery(fakeSearchResult, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        stationServices = ChargerApplication.getStationServices();

        swipeRefreshLayout.setEnabled(false);
        GetLocationFromMainActivity();
        linearLayoutManager = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        resultAdapter = new SearchResultAdapter(SearchResult, searchView, SearchActivity.this);
        recyclerView.setAdapter(resultAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }
        });

        RxSearchView.queryTextChangeEvents(searchView)
                .observeOn(AndroidSchedulers.mainThread())
                .map(searchViewQueryTextEvent -> {
                    swipeRefreshLayout.setEnabled(true);
                    swipeRefreshLayout.setRefreshing(true);
                    return searchViewQueryTextEvent;
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                .map(
                        searchViewQueryTextEvent -> {
                            boolean IsSubmitted = searchViewQueryTextEvent.isSubmitted();
                            String QueryText = searchViewQueryTextEvent.queryText().toString();
                            List<Stations> result = stationServices.ShowInputTips(QueryText);
                            return (result);
                        }
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tips -> {
                    SearchResult.clear();
                    SearchResult.addAll(tips);
                    resultAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setEnabled(false);
                });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
