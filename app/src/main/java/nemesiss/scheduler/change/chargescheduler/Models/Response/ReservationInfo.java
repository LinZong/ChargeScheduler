package nemesiss.scheduler.change.chargescheduler.Models.Response;

import java.io.Serializable;

public class ReservationInfo extends CommonResponseModel implements Serializable
{
    private long Id;
    private long UserId;
    private int ReservationType;
    private Long StartTime;
    private Long EndTime;
    private int IsFinished;
    private long RaiseReservationTime;
    private Integer UsedStationId;
    private Long ArrivedTime;
    private int IsAssigned;

    public long getId()
    {
        return Id;
    }

    @Override
    public int getStatusCode()
    {
        return super.getStatusCode();
    }

    @Override
    public String getStatusMessage()
    {
        return super.getStatusMessage();
    }

    public int getIsFinished()
    {
        return IsFinished;
    }

    public int getReservationType()
    {
        return ReservationType;
    }

    public Integer getUsedStationId()
    {
        return UsedStationId;
    }

    public Long getArrivedTime()
    {
        return ArrivedTime;
    }

    public Long getEndTime()
    {
        return EndTime;
    }

    public long getRaiseReservationTime()
    {
        return RaiseReservationTime;
    }

    public Long getStartTime()
    {
        return StartTime;
    }

    public int getIsAssigned()
    {
        return IsAssigned;
    }

    public long getUserId()
    {
        return UserId;
    }
}
