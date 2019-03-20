package nemesiss.scheduler.change.chargescheduler.Models;

public class RequestReservationInfo
{
    private int[] WantStation;
    private int[] WantStationDistance;
    private int RemainBattery;
    private double Latitude;
    private double Longitude;
    private long UserId;
    private int ReservationType;
    private long StartTime;

    public long getUserId()
    {
        return UserId;
    }

    public double getLatitude()
    {
        return Latitude;
    }

    public double getLongitude()
    {
        return Longitude;
    }

    public int getReservationType()
    {
        return ReservationType;
    }

    public int getRemainBattery()
    {
        return RemainBattery;
    }

    public int[] getWantStation()
    {
        return WantStation;
    }

    public int[] getWantStationDistance()
    {
        return WantStationDistance;
    }

    public long getStartTime()
    {
        return StartTime;
    }

    public void setLongitude(double longitude)
    {
        Longitude = longitude;
    }

    public void setLatitude(double latitude)
    {
        Latitude = latitude;
    }

    public void setStartTime(long startTime)
    {
        StartTime = startTime;
    }

    public void setReservationType(int reservationType)
    {
        ReservationType = reservationType;
    }

    public void setRemainBattery(int remainBattery)
    {
        RemainBattery = remainBattery;
    }

    public void setUserId(long userId)
    {
        UserId = userId;
    }

    public void setWantStation(int[] wantStation)
    {
        WantStation = wantStation;
    }

    public void setWantStationDistance(int[] wantStationDistance)
    {
        WantStationDistance = wantStationDistance;
    }
}
