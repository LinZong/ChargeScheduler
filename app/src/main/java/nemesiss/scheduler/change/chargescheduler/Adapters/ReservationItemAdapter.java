package nemesiss.scheduler.change.chargescheduler.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Models.Response.ReservationInfo;
import nemesiss.scheduler.change.chargescheduler.R;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;

import java.util.List;

public class ReservationItemAdapter extends RecyclerView.Adapter<ReservationItemAdapter.ItemViewHolder>
{

    private List<ReservationInfo> reservationInfoList;
    public ReservationItemAdapter(List<ReservationInfo> reservList)
    {
        reservationInfoList = reservList;
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_reservation_item_layout,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position)
    {
        ReservationInfo res = reservationInfoList.get(position);

        Integer statId = res.getUsedStationId();
        if(statId!=null)
        {
            holder.StationName.setText(ChargerApplication.getStationServices().GetStationInfo(statId).getName());
        }
        else {
            holder.StationName.setText("此预约未指派充电站");
        }
        switch (res.getIsFinished()){
            case 0:
                holder.ReservationStatus.setText("尚未到达");
                break;
            case 1:
                holder.ReservationStatus.setText("完成充电");
                break;
            case 2:
                holder.ReservationStatus.setText("迟到");
                break;
            case 3:
                holder.ReservationStatus.setText("被用户取消");
                break;
            case 4:
                holder.ReservationStatus.setText("被系统取消");
                break;
        }
        switch (res.getReservationType())
        {
            case 0:
            case 2:
                holder.ReservationType.setText("即时预约");
                break;
            case 1:
                holder.ReservationType.setText("延时预约");
                break;
        }
        holder.ReservationTime.setText(GlobalUtils.TokenDateFormatter().format(GlobalUtils.UnixStamp2Date(res.getRaiseReservationTime())));
    }

    @Override
    public int getItemCount()
    {
        return reservationInfoList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.Item_StationImage)
        ImageView StationImage;

        @BindView(R.id.Item_StationName)
        TextView StationName;

        @BindView(R.id.Item_ReservationTime)
        TextView ReservationTime;

        @BindView(R.id.Item_ReservationStatus)
        TextView ReservationStatus;

        @BindView(R.id.Item_ReservationType)
        TextView ReservationType;

        @BindView(R.id.Item_ShowReservationDetail)
        Button ReservationDetail;

        public ItemViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
