package nemesiss.scheduler.change.chargescheduler.Models.Response;

import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BusyTimePeriod
{
    private String Begin;
    private String End;

    public String getBegin()
    {
        return Begin;
    }

    public String getEnd()
    {
        return End;
    }

    public void setBegin(String begin)
    {
        Begin = begin;
    }

    public void setEnd(String end)
    {
        End = end;
    }

    public Date getBeginAsDate() throws ParseException
    {
        SimpleDateFormat fmt = GlobalUtils.BusyPeriodFormatter();
        return fmt.parse(Begin);
    }

    public Date getEndAsDate() throws ParseException
    {
        SimpleDateFormat fmt = GlobalUtils.BusyPeriodFormatter();
        return fmt.parse(End);
    }
}
