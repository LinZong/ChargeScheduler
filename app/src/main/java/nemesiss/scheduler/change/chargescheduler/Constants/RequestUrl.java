package nemesiss.scheduler.change.chargescheduler.Constants;

public class RequestUrl
{

    public static final String BaseUrl = "http://192.168.88.126:8970/";
    //public static final String BaseUrl = "http://111.230.238.192/learn/";
    public static final String Login = "user/login";
    public static final String Register = "user/register";
    public static final String CarType = "CheckServer/GetCarList";
    public static final String UserInfo = "user/info";
    public static final String GetBusyTimePeriod = "CheckServer/GetBusyTimePeriod";


    public static String getLoginUrl()
    {

        return BaseUrl + Login;
    }

    public static String getRegisterUrl()
    {
        return BaseUrl + Register;
    }

    public static String getCarTypeUrl()
    {
        return BaseUrl + CarType;
    }

    public static String getUserInfoUrl()
    {
        return BaseUrl + UserInfo;
    }

    public static String getBusyTimePeriod()
    {
        return BaseUrl + GetBusyTimePeriod;
    }
}
