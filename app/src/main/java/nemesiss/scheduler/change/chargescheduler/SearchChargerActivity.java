package nemesiss.scheduler.change.chargescheduler;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.DragEvent;
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
import com.amap.api.services.route.DistanceItem;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import nemesiss.scheduler.change.chargescheduler.Application.ChargeActivity;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Constants.GlobalVariables;
import nemesiss.scheduler.change.chargescheduler.Constants.RequestUrl;
import nemesiss.scheduler.change.chargescheduler.Models.City;
import nemesiss.scheduler.change.chargescheduler.Models.Response.Stations;
import nemesiss.scheduler.change.chargescheduler.Models.Response.TokenResponseInfo;
import nemesiss.scheduler.change.chargescheduler.Models.Response.UserInfoResponseModel;
import nemesiss.scheduler.change.chargescheduler.Models.User;
import nemesiss.scheduler.change.chargescheduler.Services.Reservation.StationServices;
import nemesiss.scheduler.change.chargescheduler.Services.Users.CommonServices;
import nemesiss.scheduler.change.chargescheduler.Services.Users.UserServices;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchChargerActivity extends ChargeActivity implements AMapLocationListener, AMap.OnMyLocationChangeListener
{
    //注入自身的一些服务
    private StationServices stationServices;

    //高德地图相关组件引用
    private UiSettings mUiSettings = null;
    private AMap aMap = null;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationClientOption = null;

    //Activity自身控件引用
    @BindView(R.id.map)
    MapView mMapView;
    @Nullable
    @BindView(R.id.FloatingChargeBtn)
    FloatingActionButton StartReservationBtn;
    @Nullable
    @BindView(R.id.AutoSearchChargerBtn)
    Button AutoSearchChargerBtn;
    @Nullable
    @BindView(R.id.Search_FakeSearchBar)
    SearchView SearchBar;
    @Nullable
    @BindView(R.id.Search_SearchBarParentCardView)
    CardView cardView;
    @Nullable
    @BindView(R.id.Search_SearchMapConstraintLayout)
    ConstraintLayout Search_SearchMapConstraintLayout;
    @Nullable
    @BindView(R.id.RelaxLayout)
    SlidingUpPanelLayout SlidingUpPanel;

    @BindView(R.id.nav_selection_view)
    NavigationView LeftSlideNavMenu;
    @BindView(R.id.Search_SearchMapDrawerLayout)
    DrawerLayout Search_SearchMapDrawerLayout;

    //状态量
    private boolean IsApplicationBoot = true;
    private Location MyLocation = null;
    private City MyLocationCity = new City();
    private Stations CurrentSearchStation = null;
    private HashMap<String, Marker> OnMapMarker = new HashMap<>();
    private Parcelable SlidingUpInitialStatus = null;
    private boolean DoubleBackExit = false;
    private Handler DoubleBackExitHandler = new Handler();

    //Drawer动画相关
    private Runnable ShouldHandleMenuClicked = null;
    private float CurrentSlideOffset = 0.0f;

    //可观察对象
    private BehaviorSubject<Location> MyLocationObservable = BehaviorSubject.create();
    private BehaviorSubject<Stations> CurrentSearchStationObservable = BehaviorSubject.create();

    //触发订阅
    private void setMyLocation(Location myLocation)
    {
        MyLocation = myLocation;
        MyLocationObservable.onNext(MyLocation);
    }

    private void setCurrentSearchStation(Stations newTip)
    {
        CurrentSearchStation = newTip;
        CurrentSearchStationObservable.onNext(newTip);
    }

    //一些get/set
    public BehaviorSubject<Location> getMyLocationObservable()
    {
        return MyLocationObservable;
    }

    public BehaviorSubject<Stations> getCurrentSearchStationObservable()
    {
        return CurrentSearchStationObservable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_charger);
        ButterKnife.bind(this);



        stationServices = ChargerApplication.getStationServices();
        LeftSlideNavMenu.setNavigationItemSelectedListener(this::OnNavigationItemSelected);

        ConstraintLayout layout = (ConstraintLayout)LeftSlideNavMenu.getHeaderView(0);
        layout.setOnClickListener(this::ShowUserInfo);

        SlidingUpPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener()
        {
            @Override
            public void onPanelSlide(View panel, float slideOffset)
            {

                panel.findViewById(R.id.SlideChargeStatus).setAlpha(1-(float)Math.min(1, slideOffset /0.4));
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState)
            {

            }
        });

        SlidingUpPanel.setAnchorPoint(0.40f);
        SlidingUpInitialStatus = SlidingUpPanel.onSaveInstanceState();
        SlidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        SlidingUpPanel.setFadeOnClickListener(v -> SlidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN));


        StartReservationBtn.setOnClickListener(view -> {
                Intent it = new Intent(SearchChargerActivity.this, ReservationTypeSelectActivity.class);
                it.putExtra("WillGoToStations",CurrentSearchStation);
                startActivity(it);
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

        Search_SearchMapDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener()
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {

                super.onDrawerSlide(drawerView, slideOffset);

                if(CurrentSlideOffset > slideOffset && slideOffset < 0.015f &&ShouldHandleMenuClicked!=null){
                    ShouldHandleMenuClicked.run();
                    ShouldHandleMenuClicked=null;
                    runOnUiThread(()->{
                        LeftSlideNavMenu.setCheckedItem(R.id.menu_none);
                    });
                }
                CurrentSlideOffset = slideOffset;
            }
        });
        AutoSearchChargerBtn.setOnClickListener(this::AttemptAutoSearchChargeStation);

        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) SearchBar.findViewById(magId);

        magImage.setOnClickListener(this::ExpandDrawerSlider);
        //设置订阅可观察对象的逻辑
        SubScribeMyLocationChanged();
        SubScribeCurrentSearchStationChanged();

        mMapView.onCreate(savedInstanceState);
        if (aMap == null)
        {
            InitialAMapComponents();
            SetLocationStyle();
            ConfigureLocationClient();
        }
        StatusBarUtil.setTransparent(this);
        new GetDetailedUserTask().execute();
    }

    private void ShowUserInfo(View view)
    {
        Intent it = new Intent(SearchChargerActivity.this,UserInfoActivity.class);
        startActivity(it);
    }

    private boolean OnNavigationItemSelected(MenuItem menuItem)
    {
        int clickId = menuItem.getItemId();
        switch (clickId)
        {
            case R.id.nav_my_reservation:
            {
                ShouldHandleMenuClicked = () -> startActivity(new Intent(SearchChargerActivity.this,MyReservationActivity.class));
                break;
            }
            case R.id.nav_settings:
            {
                ShouldHandleMenuClicked = () -> {};
                break;
            }
            default:
                ShouldHandleMenuClicked = () -> {};
                break;
        }
        Search_SearchMapDrawerLayout.closeDrawers();
        return true;
    }

    private void ExpandDrawerSlider(View view)
    {
        Search_SearchMapDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void AttemptAutoSearchChargeStation(View view)
    {

            Intent it = new Intent(SearchChargerActivity.this, ReservationTypeSelectActivity.class);
            it.putExtra("ShouldJudgeStations", true);
            startActivity(it);

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
                    runOnUiThread(() -> {
                        for (int i = 0; i < 8; i++)
                        {
                            SlidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        }
                        AutoSearchChargerBtn.setVisibility(View.GONE);
                    });
                    Stations stations = (Stations)data.getSerializableExtra(getResources().getString(R.string.SearchLocationBackAddressName));
                    //给足够时间给面板展开
                    //触发CurrentSearchTip订阅更新
                    setCurrentSearchStation(stations);
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
                }, throwable -> {
                    throwable.printStackTrace();
                    Toast.makeText(SearchChargerActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });

        getMyLocationObservable()
                .observeOn(Schedulers.newThread())
                .flatMap(new Func1<Location, Observable<DistanceResult>>()
                {
                    @Override
                    public Observable<DistanceResult> call(Location location)
                    {
                        DistanceSearch ds = new DistanceSearch(SearchChargerActivity.this);
                        DistanceSearch.DistanceQuery dq = new DistanceSearch.DistanceQuery();
                        List<LatLonPoint> originPoints = new ArrayList<>();

                        SparseArray<Stations> stationsList = stationServices.getAllStationsList();
                        int listLen = stationsList.size();
                        //拿到全部充电站的坐标地址
                        for (int i = 0; i < listLen; i++)
                        {
                            Stations s = stationsList.valueAt(i);
                            originPoints.add(new LatLonPoint(s.getLatitude(),s.getLongitude()));
                        }
                        //计算自己跟充电扎之间的距离

                        LatLonPoint myPoint = new LatLonPoint(location.getLatitude(),location.getLongitude());


                        dq.setOrigins(originPoints);
                        dq.setDestination(myPoint);
                        dq.setType(DistanceSearch.TYPE_DRIVING_DISTANCE);
                        try {
                            DistanceResult result = ds.calculateRouteDistance(dq);
                            return Observable.just(result);
                        } catch (AMapException e)
                        {
                            e.printStackTrace();
                            return Observable.error(new Exception("服务器返回了错误的结果" + e.getErrorMessage()));
                        }
                    }
                })
                .subscribe(new Action1<DistanceResult>()
                {
                    @Override
                    public void call(DistanceResult distanceResult)
                    {
                        List<DistanceItem> items = distanceResult.getDistanceResults();
                        SparseArray<Stations> stats = stationServices.getAllStationsList();
                        int len = items.size();
                        for (int i = 0; i < len; i++)
                        {
                            Stations s = stats.valueAt(i);
                            s.setDistanceBetweenMe((int)items.get(i).getDistance());
                        }
                        ChargerApplication.FeaturesSwitcher[0] = true;
                    }
                }, th ->{
                    runOnUiThread(()->{
                        ChargerApplication.FeaturesSwitcher[0] = false;
                        Toast.makeText(SearchChargerActivity.this,"当前位置得到了更新,但无法计算距离各个充电站的位置，预约功能将受到影响。"+th.getMessage(),Toast.LENGTH_SHORT).show();
                    });
                });


    }



    private void SubScribeCurrentSearchStationChanged()
    {
        getCurrentSearchStationObservable()
                .observeOn(Schedulers.newThread())
                .map(selectedStat -> {
                    return stationServices.UpdateStationInfo(selectedStat.getId());
                })
                .flatMap(
                        (Func1<Stations, Observable<DistanceResult>>) tip ->
                        {
                                LatLonPoint llp = new LatLonPoint(tip.getLatitude(),tip.getLongitude());
                                DistanceSearch ds = new DistanceSearch(SearchChargerActivity.this);
                                DistanceSearch.DistanceQuery distanceQuery = new DistanceSearch.DistanceQuery();
                                //设置自己的坐标
                                List<LatLonPoint> originPoints = new ArrayList<>();
                                originPoints.add(new LatLonPoint(MyLocation.getLatitude(), MyLocation.getLongitude()));
                                distanceQuery.setOrigins(originPoints);
                                distanceQuery.setDestination(llp);

                                distanceQuery.setType(DistanceSearch.TYPE_DRIVING_DISTANCE);
                                try
                                {
                                    DistanceResult result = ds.calculateRouteDistance(distanceQuery);
                                    return Observable.just(result);

                                } catch (AMapException e)
                                {
                                    //TODO 添加错误处理逻辑
                                    e.printStackTrace();
                                    return Observable.error(new Exception("服务器返回了错误的结果" + e.getErrorMessage()));
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                    return Observable.error(e);
                                }
                        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DistanceResult>()
                {
                    @Override
                    public void call(DistanceResult dist)
                    {

                        //绘制Marker
                        if (aMap != null && dist != null)
                        {
                            //设置当前搜索的条目
                            Stations currStation = CurrentSearchStation;

                            SearchBar.setQuery(currStation.getName(), false);

                            //先清除掉之前的标记
                            aMap.clear(true);
                            OnMapMarker.clear();

                            String detailedAddress = currStation.getCity() + currStation.getAddress();
                            LatLng lt = new LatLng(currStation.getLatitude(), currStation.getLongitude());
                            final Marker marker = aMap.addMarker(new MarkerOptions()
                                    .position(lt)
                                    .title(currStation.getName())
                                    .snippet(detailedAddress)
                                    .draggable(false));
                            //把摄像机移动到定位中心点
                            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lt, 17));
                            OnMapMarker.put(currStation.getName(), marker);

                            //获取位置信息
                            DistanceItem dis = dist.getDistanceResults().get(0);
                            TextView addr = SlidingUpPanel.findViewById(R.id.SlideAddress);
                            TextView locName = SlidingUpPanel.findViewById(R.id.SlideLocationName);
                            TextView disTextView = SlidingUpPanel.findViewById(R.id.SlideDistance);
                            TextView instanceCount = SlidingUpPanel.findViewById(R.id.SlideInstanceCount);
                            TextView delayCount = SlidingUpPanel.findViewById(R.id.SlideDelayCount);
                            if (dis != null)
                            {
                                String disResult = String.format("距离您 %.2f 公里", (double) dis.getDistance() / 1000);
                                disTextView.setText(disResult);
                            }
                            locName.setText(currStation.getName());
                            addr.setText(detailedAddress);
                            instanceCount.setText(String.valueOf(currStation.getAvailableInstance()));
                            delayCount.setText(String.valueOf(currStation.getAvailableDelay()));
                        }
                    }
                }, (throwable) -> {
                    throwable.printStackTrace();
                    Toast.makeText(SearchChargerActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
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


    class GetDetailedUserTask extends AsyncTask<Void, Void, Boolean>
    {

        private UserServices us;
        private OkHttpClient client;

        @Override
        protected Boolean doInBackground(Void... voids)
        {
            List<Pair<String, String>> qs = new ArrayList<>();
            qs.add(new Pair<>("UserId", String.valueOf(ChargerApplication.getLoginedUser().getId())));
            TokenResponseInfo tkInfo = ChargerApplication.getToken();
            try
            {
                if (CommonServices.CheckIfNeedToRefreshToken(tkInfo.getDateExpire()))
                {
                    if (!CommonServices.RefreshToken()) return false;
                }

                List<Pair<String, String>> header = new ArrayList<>();
                header.add(new Pair<>("Authorization", "Bearer " + ChargerApplication.getToken().getToken()));

                Response resp = CommonServices.SendGetRequest(client, RequestUrl.getUserInfoUrl(), qs, header);
                if (resp != null && resp.isSuccessful())
                {
                    String json = resp.body().string();
                    Gson gson = new Gson();
                    UserInfoResponseModel model = gson.fromJson(json, UserInfoResponseModel.class);
                    User user = model.getUserInformation();
                    if (user != null)
                    {
                        User loginedUser = ChargerApplication.getLoginedUser();
                        loginedUser.setCarTypeId(user.getCarTypeId());
                        loginedUser.setCredits(user.getCredits());
                        loginedUser.setNickname(user.getNickname());
                        loginedUser.setOnTimeRatio(user.getOnTimeRatio());
                        loginedUser.setNumberPlate(user.getNumberPlate());
                        return true;
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            Toast.makeText(SearchChargerActivity.this, "加载个人信息失败", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            us = ChargerApplication.getUserServices();
            client = GlobalUtils.GetOkHttpClient().build();
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
            if (result)
            {
                TextView nickName = LeftSlideNavMenu.findViewById(R.id.NavHeaderUserNickname);
                TextView pn = LeftSlideNavMenu.findViewById(R.id.NavHeaderPhoneNumber);

                User user = ChargerApplication.getLoginedUser();
                nickName.setText(user.getNickname());
                pn.setText(user.getPhone());
            }
            client = null;//toggle gc.
        }
    }

    @Override
    public void onBackPressed()
    {
        if(Search_SearchMapDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            Search_SearchMapDrawerLayout.closeDrawers();
        }
        else
        {
            if(!DoubleBackExit) {
                DoubleBackExit = true;
                Toast.makeText(SearchChargerActivity.this,"再按一次退出程序", Toast.LENGTH_SHORT).show();
                DoubleBackExitHandler.postDelayed(this::DoubleBackExitCallback,1000);
            }
            else
            {
                DoubleBackExitHandler.removeCallbacks(this::DoubleBackExitCallback);
                finish();
            }
        }
    }

    private void DoubleBackExitCallback()
    {
        DoubleBackExit = false;
    }
}
