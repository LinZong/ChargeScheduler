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
import nemesiss.scheduler.change.chargescheduler.Models.Response.ReservationInfo;
import nemesiss.scheduler.change.chargescheduler.R;

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

        @BindView(R.id.Item_ShowReservationDetail)
        Button ReservationDetail;

        public ItemViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
