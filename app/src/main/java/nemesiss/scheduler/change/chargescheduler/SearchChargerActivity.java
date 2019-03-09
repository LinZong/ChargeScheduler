package nemesiss.scheduler.change.chargescheduler;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.DistanceItem;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import nemesiss.scheduler.change.chargescheduler.Models.City;
import nemesiss.scheduler.change.chargescheduler.Models.TipWithDistance;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalVariables;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchChargerActivity extends AppCompatActivity implements AMapLocationListener, AMap.OnMyLocationChangeListener
{
    //高德地图相关组件引用
    private UiSettings mUiSettings = null;
    private AMap aMap = null;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationClientOption = null;

    //Activity自身控件引用
    @BindView(R.id.map) MapView mMapView;
    @Nullable
    @BindView(R.id.FloatingChargeBtn) FloatingActionButton StartReservationBtn;
    @Nullable
    @BindView(R.id.AutoSearchChargerBtn) Button AutoSearchChargerBtn;
    @Nullable
    @BindView(R.id.Search_FakeSearchBar) SearchView SearchBar;
    @Nullable
    @BindView(R.id.Search_SearchBarParentCardView) CardView cardView;
    @Nullable
    @BindView(R.id.Search_SearchMapConstraintLayout) ConstraintLayout Search_SearchMapConstraintLayout;
    @Nullable
    @BindView(R.id.RelaxLayout) SlidingUpPanelLayout SlidingUpPanel;

    @BindView(R.id.nav_selection_view) NavigationView LeftSlideNavMenu;
    @BindView(R.id.Search_SearchMapDrawerLayout) DrawerLayout Search_SearchMapDrawerLayout;


    //状态量
    private boolean IsApplicationBoot = true;
    private boolean IsFirstTimeShowSlideLayout = true;
    private Location MyLocation = null;
    private City MyLocationCity = new City();
    private Tip CurrentSearchTip = null;
    private HashMap<String, Marker> OnMapMarker = new HashMap<>();
    private Parcelable SlidingUpInitialStatus = null;

    //可观察对象
    private BehaviorSubject<Location> MyLocationObservable = BehaviorSubject.create();
    private BehaviorSubject<Tip> CurrentSearchTipObservable = BehaviorSubject.create();

    //触发订阅
    private void setMyLocation(Location myLocation)
    {
        MyLocation = myLocation;
        MyLocationObservable.onNext(MyLocation);
    }

    private void setCurrentSearchTip(Tip newTip)
    {
        CurrentSearchTip = newTip;
        CurrentSearchTipObservable.onNext(newTip);
    }

    //一些get/set
    public BehaviorSubject<Location> getMyLocationObservable()
    {
        return MyLocationObservable;
    }

    public BehaviorSubject<Tip> getCurrentSearchTipObservable()
    {
        return CurrentSearchTipObservable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_charger);
        ButterKnife.bind(this);
//        mMapView = (MapView) findViewById(R.id.map);
//        SearchBar = findViewById(R.id.Search_FakeSearchBar);
//        cardView = findViewById(R.id.Search_SearchBarParentCardView);
//        Search_SearchMapConstraintLayout = findViewById(R.id.Search_SearchMapConstraintLayout);
//        AutoSearchChargerBtn = findViewById(R.id.AutoSearchChargerBtn);
//        StartReservationBtn = findViewById(R.id.FloatingChargeBtn);

//        SlidingUpPanel = findViewById(R.id.RelaxLayout);

        LeftSlideNavMenu.setNavigationItemSelectedListener(this::OnNavigationItemSelected);
        SlidingUpPanel.setAnchorPoint(0.40f);
        SlidingUpInitialStatus = SlidingUpPanel.onSaveInstanceState();
        SlidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);


        StartReservationBtn.setOnClickListener(view -> {
            Intent it = new Intent(SearchChargerActivity.this,ReservationTypeSelectActivity.class);
            it.putExtra("WillGoToAddressTip",CurrentSearchTip);
            startActivity(it);
        });

        Log.d("THREADINFO", "上面的设置运行在" + Thread.currentThread().getId());
        SlidingUpPanel.setFadeOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                SlidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });




        Search_SearchMapConstraintLayout.requestFocus();
        Search_SearchMapConstraintLayout.findFocus();
        SearchBar.setOnQueryTextFocusChangeListener((view, b) -> {
            if (b)
            {
                Search_SearchMapConstraintLayout.requestFocus();
                Search_SearchMapConstraintLayout.findFocus();
                Intent it = new Intent(SearchChargerActivity.this, SearchActivity.class);
                it.putExtra(getResources().getString(R.string.MyLocationCityName), MyLocationCity.getCityName());
                it.putExtra(getResources().getString(R.string.MyLocation_Intent), MyLocation);
                it.putExtra("FakeSearchBarResult", SearchBar.getQuery().toString());

                startActivityForResult(it, GlobalVariables.GOTO_SEARCH_VIEW_INTENT_CODE);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        AutoSearchChargerBtn.setOnClickListener(this::AttemptAutoSearchChargeStation);

        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) SearchBar.findViewById(magId);
        
        magImage.setOnClickListener(this::ExpandDrawerSlider);
        //设置订阅可观察对象的逻辑
        SubScribeMyLocationChanged();
        SubScribeCurrentSearchTipChanged();

        mMapView.onCreate(savedInstanceState);
        if (aMap == null)
        {
            InitialAMapComponents();
            SetLocationStyle();
            ConfigureLocationClient();
        }
    }

    private boolean OnNavigationItemSelected(MenuItem menuItem)
    {
        int clickId = menuItem.getItemId();
        switch (clickId){
            case R.id.nav_my_reservation:{
                Log.v("NAVMENU","点击了我的预约");
                break;
            }
            case R.id.nav_settings:{
                Log.v("NAVMENU","点击了设置");
                break;
            }
            default:break;
        }
        LeftSlideNavMenu.setCheckedItem(R.id.menu_none);
        Search_SearchMapDrawerLayout.closeDrawers();
        return false;
    }

    private void ExpandDrawerSlider(View view)
    {
        Search_SearchMapDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void AttemptAutoSearchChargeStation(View view)
    {
        AlertDialog.Builder bd = GlobalUtils.ShowAlertDialog(SearchChargerActivity.this,true,
                "测试版提醒", "由于该版本为测试版, 因此并没有自动搜寻最佳充电站的逻辑. 点击确定后将跳转到预约界面.\n" +
                        "你也可以尝试着搜索一下充电站信息，体验指定充电站预约的过程.");
        bd.setPositiveButton("确定", (d, i) -> {
            Intent it = new Intent(SearchChargerActivity.this,ReservationTypeSelectActivity.class);
            it.putExtra("WillGoToAddressTip",CurrentSearchTip);
            startActivity(it);
        });
        bd.show();

    }

    private void InitialAMapComponents()
    {
        aMap = mMapView.getMap();
        mUiSettings = aMap.getUiSettings();
    }

    private void ConfigureLocationClient()
    {
        if (locationClient == null)
        {
            locationClient = new AMapLocationClient(SearchChargerActivity.this);
        }
        if (locationClientOption == null)
        {
            locationClientOption = new AMapLocationClientOption();
        }
        locationClient.setLocationListener(this);
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClientOption.setOnceLocation(true);
        locationClient.setLocationOption(locationClientOption);
        //locationClient.startLocation();
    }

    private void SetLocationStyle()
    {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(8000);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        aMap.setOnMyLocationChangeListener(this);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case GlobalVariables
                    .GOTO_SEARCH_VIEW_INTENT_CODE:
            {
                if (resultCode == RESULT_OK)
                {
                    runOnUiThread(()->{
                        for (int i = 0; i < 8; i++)
                        {
                            SlidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        }
                        AutoSearchChargerBtn.setVisibility(View.GONE);
                    });
                    Tip tip = data.getParcelableExtra(getResources().getString(R.string.SearchLocationBackAddressName));
                    //给足够时间给面板展开
                    //触发CurrentSearchTip订阅更新
                    setCurrentSearchTip(tip);
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation)
    {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0)
        {
            //begin set location
            Toast.makeText(SearchChargerActivity.this, "定位成功", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            //aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } else
        {
            Log.e("AmapError", "location Error, ErrCode:"
                    + aMapLocation.getErrorCode() + ", errInfo:"
                    + aMapLocation.getErrorInfo());
        }
    }

    public void GetMyLocation(View view)
    {
        if (MyLocation != null)
        {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MyLocation.getLatitude(), MyLocation.getLongitude()), 17));
            SlidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            AutoSearchChargerBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMyLocationChange(Location location)
    {
        if (aMap != null)
        {
            Log.d("SearchChargeActivity", "定位自身成功");
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            setMyLocation(location);
            if (IsApplicationBoot)
            {
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                IsApplicationBoot = false;
            }
        }
    }

    private void SubScribeMyLocationChanged()
    {
        getMyLocationObservable()
                .observeOn(Schedulers.newThread())
                .map(latLng -> new LatLonPoint(latLng.getLatitude(), latLng.getLongitude()))
                .map(latLonPoint -> {
                    RegeocodeQuery regeocodeQuery = new RegeocodeQuery(latLonPoint, 100, GeocodeSearch.AMAP);
                    try
                    {
                        GeocodeSearch geocoderSearch = new GeocodeSearch(SearchChargerActivity.this);
                        return geocoderSearch.getFromLocation(regeocodeQuery);
                    } catch (AMapException e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                })
                .subscribe(regeocodeAddress -> {
                    if (regeocodeAddress != null && MyLocationCity != null)
                    {

                        MyLocationCity.setCityCode(regeocodeAddress.getCityCode());
                        MyLocationCity.setCityName(regeocodeAddress.getCity());
                    }
                }, new Action1<Throwable>()
                {
                    @Override
                    public void call(Throwable throwable)
                    {
                        throwable.printStackTrace();
                        Toast.makeText(SearchChargerActivity.this,throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void SubScribeCurrentSearchTipChanged()
    {
        getCurrentSearchTipObservable()
                .observeOn(Schedulers.newThread())
                .flatMap(
                        (Func1<Tip, Observable<TipWithDistance>>) tip ->
                        {
                            LatLonPoint llp = tip.getPoint();//目的地

                            DistanceSearch ds = new DistanceSearch(SearchChargerActivity.this);

                            DistanceSearch.DistanceQuery distanceQuery = new DistanceSearch.DistanceQuery();
                            List<LatLonPoint> originPoints = new ArrayList<>();
                            originPoints.add(new LatLonPoint(MyLocation.getLatitude(), MyLocation.getLongitude()));
                            distanceQuery.setOrigins(originPoints);
                            distanceQuery.setDestination(llp);
                            distanceQuery.setType(DistanceSearch.TYPE_DRIVING_DISTANCE);
                            try
                            {
                                DistanceResult result = ds.calculateRouteDistance(distanceQuery);
                                TipWithDistance twd = TipWithDistance.TransfromTipToTipWithDistance(tip, result.getDistanceResults().get(0));
                                return Observable.just(twd);

                            } catch (AMapException e)
                            {
                                //TODO 添加错误处理逻辑
                                e.printStackTrace();
                                return Observable.error(new Exception("服务器返回了错误的结果" + e.getErrorMessage()));
                            }

                        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<TipWithDistance>()
                {
                    @Override
                    public void call(TipWithDistance tip)
                    {

                        //绘制Marker
                        if (aMap != null && tip != null)
                        {
                            //设置当前搜索的条目
                            SearchBar.setQuery(tip.getName(), false);

                            //先清除掉之前的标记
                            aMap.clear(true);
                            OnMapMarker.clear();

                            String detailedAddress = tip.getDistrict() + tip.getAddress();
                            LatLng lt = new LatLng(tip.getPoint().getLatitude(), tip.getPoint().getLongitude());
                            final Marker marker = aMap.addMarker(new MarkerOptions()
                                    .position(lt)
                                    .title(tip.getName())
                                    .snippet(detailedAddress)
                                    .draggable(false));
                            //把摄像机移动到定位中心点
                            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lt, 17));
                            OnMapMarker.put(tip.getName(), marker);

                            //获取位置信息
                            DistanceItem dis = tip.getDistanceResult();
                            TextView addr = SlidingUpPanel.findViewById(R.id.SlideAddress);
                            TextView locName = SlidingUpPanel.findViewById(R.id.SlideLocationName);
                            TextView disTextView = SlidingUpPanel.findViewById(R.id.SlideDistance);
                            if (dis != null)
                            {
                                String disResult = String.format("距离您 %.2f 公里", (double) dis.getDistance() / 1000);
                                disTextView.setText(disResult);
                            }
                            //展现SlideUpLayout.
                            locName.setText(tip.getName());
                            addr.setText(detailedAddress);
                        }
                    }
                }, (throwable) -> {
                    throwable.printStackTrace();
                    Toast.makeText(SearchChargerActivity.this,throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mMapView.onDestroy();
        if (locationClient != null) locationClient.stopLocation();
        aMap.setMyLocationEnabled(false);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mMapView.onResume();
        SetLocationStyle();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mMapView.onPause();
        aMap.setMyLocationEnabled(false);
        if (locationClient != null) locationClient.stopLocation();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
