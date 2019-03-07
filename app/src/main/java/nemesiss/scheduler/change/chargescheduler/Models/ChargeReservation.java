package nemesiss.scheduler.change.chargescheduler.Models;
import nemesiss.scheduler.change.chargescheduler.ReservationTypeSelectActivity;
import java.io.Serializable;
import java.util.Date;

public class ChargeReservation implements Serializable
{
    private ReservationTypeSelectActivity.ChargeType chargeType;
    private Date ReservationTime;

    public ReservationTypeSelectActivity.ChargeType getChargeType()
    {
        return chargeType;
    }

    public Date getReservationTime()
    {
        return ReservationTime;
    }

    public void setChargeType(ReservationTypeSelectActivity.ChargeType chargeType)
    {
        this.chargeType = chargeType;
    }

    public void setReservationTime(Date reservationTime)
    {
        ReservationTime = reservationTime;
    }
}
