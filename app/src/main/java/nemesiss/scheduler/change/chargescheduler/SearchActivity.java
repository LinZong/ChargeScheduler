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
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.jakewharton.rxbinding.widget.RxSearchView;
import nemesiss.scheduler.change.chargescheduler.Adapters.SearchResultAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchActivity extends AppCompatActivity implements Inputtips.InputtipsListener
{
    private SearchView searchView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private SearchResultAdapter resultAdapter;
    private List<Tip> SearchResult = new ArrayList<>();
    private String MyCityFromMain = null;
    private Location MyLocationFronMainActivity = null;

    private void GetLocationFromMainActivity()
    {
        MyCityFromMain = getIntent().getStringExtra(getResources().getString(R.string.MyLocationCityName));
        MyLocationFronMainActivity = getIntent().getParcelableExtra(getResources().getString(R.string.MyLocation_Intent));
        String fakeSearchResult = getIntent().getStringExtra("FakeSearchBarResult");
        //将上次搜索结果带过来
        if(!TextUtils.isEmpty(fakeSearchResult) && searchView != null)
        {
            searchView.setQuery(fakeSearchResult,false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);



        swipeRefreshLayout = findViewById(R.id.SearchRefreshIcon);
        swipeRefreshLayout.setEnabled(false);
        searchView = findViewById(R.id.Search_SearchBar);
        recyclerView = findViewById(R.id.Search_SearchResultRecycleView);
        GetLocationFromMainActivity();
        linearLayoutManager = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        resultAdapter = new SearchResultAdapter(SearchResult, searchView, SearchActivity.this);
        recyclerView.setAdapter(resultAdapter);

        RxSearchView.queryTextChangeEvents(searchView)
                .observeOn(AndroidSchedulers.mainThread())
                .map(searchViewQueryTextEvent -> {
                    Log.d("THREADINFO","第一次map运行在"+Thread.currentThread().getId());
                    swipeRefreshLayout.setEnabled(true);
                    swipeRefreshLayout.setRefreshing(true);
                    return searchViewQueryTextEvent;
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                .map(

                        searchViewQueryTextEvent -> {
                            Log.d("THREADINFO","第2次map运行在"+Thread.currentThread().getId());
                            boolean IsSubmitted = searchViewQueryTextEvent.isSubmitted();
                            String QueryText = searchViewQueryTextEvent.queryText().toString();
                            InputtipsQuery inputtipsQuery = null;
                            if (!TextUtils.isEmpty(QueryText))
                            {
                                inputtipsQuery = new InputtipsQuery(QueryText, MyCityFromMain);
                                inputtipsQuery.setLocation(new LatLonPoint(
                                        MyLocationFronMainActivity.getLatitude(),
                                        MyLocationFronMainActivity.getLongitude()));
                            }
                            return inputtipsQuery;
                        })
                .map(
                        (Func1<InputtipsQuery, List<Tip>>) inputtipsQuery -> {
                            Log.d("THREADINFO","第3次map运行在"+Thread.currentThread().getId());
                            if (inputtipsQuery != null)
                            {
                                Inputtips it = new Inputtips(SearchActivity.this, inputtipsQuery);
                                try
                                {
                                    return it.requestInputtips();
                                } catch (AMapException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            return new ArrayList<>();
                        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tips -> {
                    Log.d("THREADINFO","最终subscribe运行在"+Thread.currentThread().getId());
                    SearchResult.clear();
                    SearchResult.addAll(tips);
                    resultAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setEnabled(false);
                });
    }

    @Override
    public void onGetInputtips(List<Tip> list, int i)
    {
        if (i == 1000)
        {
            Log.d("INPUTTIPS", "成功");
            for (Tip tip : list)
            {
                Log.d("INPUTTIPS", tip.getAdcode());
                Log.d("INPUTTIPS", tip.getAddress());
                Log.d("INPUTTIPS", tip.getDistrict());
                Log.d("INPUTTIPS", tip.getName());
                Log.d("INPUTTIPS", tip.getPoiID());
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
