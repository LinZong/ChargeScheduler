package nemesiss.scheduler.change.chargescheduler.Constants;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.R;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;

public class RequestUrl
{

    public static final String[] BaseUrl = new String[]{"http://192.168.88.126:8970/", "http://111.230.238.192/learn/", ""};
    public static final String Login = "user/login";
    public static final String Register = "user/register";
    public static final String CarType = "CheckServer/GetCarList";
    public static final String UserInfo = "user/info";
    public static final String GetBusyTimePeriod = "CheckServer/GetBusyTimePeriod";
    public static final String GetAllStations = "CheckServer/Stations/";
    public static final String CreateNewReservation = "reservation/create";
    public static final String SetReservationState = "reservation/update";
    public static final String GetMyReservations = "reservation/info/";

    private static int CurrentMode = 1;


    public static String getLoginUrl()
    {
        return getBaseUrl() + Login;
    }

    public static String getRegisterUrl()
    {
        return getBaseUrl() + Register;
    }

    public static String getCarTypeUrl()
    {
        return getBaseUrl() + CarType;
    }

    public static String getUserInfoUrl()
    {
        return getBaseUrl() + UserInfo;
    }

    public static String getBusyTimePeriod()
    {
        return getBaseUrl() + GetBusyTimePeriod;
    }

    public static String getAllStations()
    {
        return getBaseUrl() + GetAllStations;
    }

    public static String getSelectedStation(int id)
    {
        return getAllStations() + id;
    }

    public static String getCreateNewReservation()
    {
        return getBaseUrl() + CreateNewReservation;
    }

    public static String getSetReservationState()
    {
        return getBaseUrl() + SetReservationState;
    }

    public static String getMyReservations()
    {
        return getBaseUrl() + GetMyReservations;
    }


    public static String getBaseUrl()
    {
        switch (CurrentMode)
        {
            case 0:
                return BaseUrl[0];
            case 1:
                return BaseUrl[1];
            case 2:
                return BaseUrl[2];
            default:
                return BaseUrl[1];
        }
    }

    public static void SetCurrentUrlMode(int mode)
    {
        CurrentMode = mode;
    }

    public static int GetCurrentMode()
    {
        return CurrentMode;
    }


    public static void SwitchRequestUrlHelper(View dialogInnerView)
    {
        MaterialSpinner spinner = dialogInnerView.findViewById(R.id.ChangeUrlSpinner);
        TextView textView = dialogInnerView.findViewById(R.id.CustomRequestUrl);
        spinner.setItems(BaseUrl[0], BaseUrl[1], "自定义Base URL");
        spinner.setSelectedIndex(CurrentMode);
        if (CurrentMode == 2)
        {
            textView.setVisibility(View.VISIBLE);
            textView.setText(BaseUrl[2]);
        }
        spinner.setOnItemSelectedListener((view, position, id, item) -> {
            if (position == 2)
            {
                textView.setVisibility(View.VISIBLE);
                textView.setText(BaseUrl[2]);
            } else textView.setVisibility(View.GONE);
        });
        AlertDialog.Builder bd = GlobalUtils.ShowAlertDialog(dialogInnerView.getContext(), false, "修改请求API地址 (开发人员专用)", null);
        bd.setView(dialogInnerView);
        bd.setPositiveButton("OK", (d, i) ->
        {
            BaseUrl[2] = textView.getText().toString();
            SetCurrentUrlMode(spinner.getSelectedIndex());

            ChargerApplication.LoadWhenApplicationStart();//重新请求程序运行所必要的数据
        });
        bd.setNegativeButton("Cancel", (d, i) -> {
        });
        bd.show();
    }
}
