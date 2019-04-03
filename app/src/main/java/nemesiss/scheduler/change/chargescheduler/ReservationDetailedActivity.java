package nemesiss.scheduler.change.chargescheduler;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.jaeger.library.StatusBarUtil;
import nemesiss.scheduler.change.chargescheduler.Application.ChargeActivity;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Models.Response.ReservationInfo;
import nemesiss.scheduler.change.chargescheduler.Models.Response.Stations;
import nemesiss.scheduler.change.chargescheduler.Services.Reservation.StationServices;
import nemesiss.scheduler.change.chargescheduler.Services.Reservation.Tasks.GetUserReservationInfoTask;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;

import java.util.List;

public class ReservationDetailedActivity extends ChargeActivity
{
    private static String FULL_ACTIVITY_CLASSNAME = "nemesiss.scheduler.change.chargescheduler.ReservationDetailedActivity";

    @BindView(R.id.ReservationDetailToolbar)
    CollapsingToolbarLayout toolbarLayout;

    @BindView(R.id.ReservationDetailChargerPhoto)
    ImageView StationImage;
    @BindView(R.id.SchedulerStatus)
    TextView SchedulerStatus;
    @BindView(R.id.ChargeHolderNum)
    TextView ChargeHolderNum;
    @BindView(R.id.ReservationCreateTime)
    TextView ReservationCreateTime;
    @BindView(R.id.ReservationArriveTime)
    TextView ReservationArriveTime;
    @BindView(R.id.ReservationStartChargeTime)
    TextView ReservationStartChargeTime;
    @BindView(R.id.ReservationFinishChargeTime)
    TextView ReservationFinishChargeTime;
    @BindView(R.id.ReservationIDNum)
    TextView ReservationIDNum;


    private StationServices stationServices = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detailed);
        stationServices = ChargerApplication.getStationServices();
        ButterKnife.bind(this);
        ReservationInfo infoFromList = (ReservationInfo)getIntent().getSerializableExtra("ReservationInfo");
        if(infoFromList!=null){
            ReceiveNewReservationDetail(infoFromList);
        }


        StatusBarUtil.setTransparent(this);
        Glide.with(ReservationDetailedActivity.this).load(R.drawable.scene).into(StationImage);
    }
    public static boolean IfReservationDetailIsTopActivity(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        String cmpNameTemp = null;
        if(runningTaskInfos != null){
            cmpNameTemp = runningTaskInfos.get(0).topActivity.getClassName();
        }
        if(TextUtils.isEmpty(cmpNameTemp)) return false;
        return FULL_ACTIVITY_CLASSNAME.equals(cmpNameTemp);
    }

    public void ReceiveNewReservationDetail()
    {

        new GetUserReservationInfoTask(TaskRet -> {
            if(TaskRet.size()==1)
            {
                ReservationInfo res = TaskRet.get(0);
                MapReservationInfoToView(res);
            }
        }).execute(null,1L);
    }

    public void ReceiveNewReservationDetail(ReservationInfo res)
    {
        MapReservationInfoToView(res);
    }

    private void MapReservationInfoToView(ReservationInfo res)
    {
        TryProvideStationName(res.getUsedStationId());
        ReservationCreateTime.setText(TryProvideFmtTimeString(res.getRaiseReservationTime()));
        ReservationStartChargeTime.setText(TryProvideFmtTimeString(res.getStartTime()));
        ReservationFinishChargeTime.setText(TryProvideFmtTimeString(res.getEndTime()));
        ReservationArriveTime.setText(TryProvideFmtTimeString(res.getArrivedTime()));
        ReservationIDNum.setText(String.valueOf(res.getId()));
        JudgeSchedulerStatus(res.getIsAssigned(),res.getIsFinished());
    }

    private String TryProvideFmtTimeString(Long time)
    {
        if(time!=null&&time!=0) return GlobalUtils.UnixStampToFmtString(time);
        return "暂无时间信息";
    }
    private void TryProvideStationName(Integer stationId)
    {
        if(stationId!=null)
        {
            Stations station = stationServices.GetStationInfo(stationId);
            toolbarLayout.setTitle(station.getName());
        }
    }
    private void JudgeSchedulerStatus(int IsAssigned,int IsFinished)
    {
        if(IsAssigned==0)
        {
            SchedulerStatus.setText("正在为您分配充电桩...");
            ChargeHolderNum.setText(null);
            return ;
        }
        else if(IsAssigned==1)
        {
            switch (IsFinished){
                case 0:{
                    SchedulerStatus.setText("已分配充电桩:");
                    ChargeHolderNum.setText("39号充电桩");
                    break;
                }
                case 1:{
                    SchedulerStatus.setText("充电已完成:");
                    ChargeHolderNum.setText("39号充电桩");
                    break;
                }
                case 2:{
                    SchedulerStatus.setText("充电已完成(迟到):");
                    ChargeHolderNum.setText("39号充电桩");
                    break;
                }
                case 3:{
                    SchedulerStatus.setText("此预约已被用户取消:");
                    ChargeHolderNum.setText("暂无充电桩信息");
                    break;
                }
                case 4:{
                    SchedulerStatus.setText("此预约已被系统取消:");
                    ChargeHolderNum.setText("暂无充电桩信息");
                    break;
                }
            }
        }
    }
}
