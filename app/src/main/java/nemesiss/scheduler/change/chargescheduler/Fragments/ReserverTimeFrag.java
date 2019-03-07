package nemesiss.scheduler.change.chargescheduler.Fragments;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import com.jaredrummler.materialspinner.MaterialSpinner;
import nemesiss.scheduler.change.chargescheduler.R;
import nemesiss.scheduler.change.chargescheduler.ReservationTypeSelectActivity;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReserverTimeFrag extends Fragment implements ChainFragment, FragmentBackHandler
{
    private ReservationTypeSelectActivity activity;
    private MaterialSpinner ReservationTimeSpanSpinner;
    private Button ConfirmTimeSelection;
    private View view;
    private List<Date> CanSelectTime = new ArrayList<>();

    private int LastSelectIndex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.reserver_time_fragment, container, false);

        ReservationTimeSpanSpinner = view.findViewById(R.id.ReservationTimeSpanSpinner);
        ReservationTimeSpanSpinner.setOnItemSelectedListener((view, position, id, item) -> LastSelectIndex = position);
        ConfirmTimeSelection = view.findViewById(R.id.ConfirmTimeSelection);
        ConfirmTimeSelection.setOnClickListener(this::OnConfirmClick);

        LastSelectIndex = 0;
        if(savedInstanceState!=null)
        {
            LastSelectIndex = savedInstanceState.getInt("LastSelectIndex",0);
        }

        ReservationTimeSpanSpinner.setSelectedIndex(LastSelectIndex);
        activity = (ReservationTypeSelectActivity) getActivity();
        GenerateCanReserveTime();
        List<String> res = FormatReservationTimeSpan(CanSelectTime);
        ReservationTimeSpanSpinner.setItems(res);
        return view;
    }


    private void GenerateCanReserveTime()
    {
        if (CanSelectTime != null)
        {
            if (!CanSelectTime.isEmpty()) CanSelectTime.clear();

            for (int i = 0; i < 24; i++)
            {
                CanSelectTime.add(getOldHour(i));
            }
        }
    }

    private List<String> FormatReservationTimeSpan(List<Date> BeginTimeSpan)
    {
        ArrayList<String> result = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        int len = BeginTimeSpan.size();
        SimpleDateFormat BeginFormatter = new SimpleDateFormat("yyyy-MM-dd H:00", Locale.CHINA);
        SimpleDateFormat EndFormatter = new SimpleDateFormat("MM-dd H:00", Locale.CHINA);
        for (int i = 0; i < len; i++)
        {
            cal.setTime(BeginTimeSpan.get(i));
            Date d1 = cal.getTime();
            String BeginStr = BeginFormatter.format(d1);
            cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 1);
            Date d2 = cal.getTime();
            String EndStr = EndFormatter.format(d2);
            result.add(BeginStr + " 至 " + EndStr);
        }
        return result;
    }

    public void ToNextFragment(Fragment fragment)
    {
        //通知Activity开始预约

    }

    @Override
    public boolean onBackPressed()
    {
        activity.SetHintTitle("选择预约的类型");
        return BackHandlerHelper.handleBackPress(this);
    }


    //Helper
    public static Date getOldHour(int distanceHour)
    {
        //"M-dd H:00"
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd H:00", Locale.CHINA);
        Date beginDate = new Date();

        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.HOUR, date.get(Calendar.HOUR) + distanceHour);
        Date endDate = null;
        try
        {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return endDate;
    }

    private void OnConfirmClick(View view)
    {
        Date date = CanSelectTime.get(ReservationTimeSpanSpinner.getSelectedIndex());
        activity.SetReservationTime(date);
        AlertDialog.Builder albd = GlobalUtils.ShowAlertDialog(view.getContext(),true, "确认您的预约", "是否确定预约?");
        albd.setPositiveButton("OK", (dialogInterface, i) -> {
            activity.GoToProcessReservationActivity();
        })
        .setNegativeButton("Cancel", (dialogInterface, i) -> {
        });
        albd.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        outState.putInt("LastSelectIndex",LastSelectIndex);
    }
}
