package nemesiss.scheduler.change.chargescheduler.Models;

import nemesiss.scheduler.change.chargescheduler.ReservationTypeSelectActivity;

public class ChargeReservation
{
    private ReservationTypeSelectActivity.ChargeType chargeType;
    private String ReservationTime;

    public ReservationTypeSelectActivity.ChargeType getChargeType()
    {
        return chargeType;
    }

    public String getReservationTime()
    {
        return ReservationTime;
    }

    public void setChargeType(ReservationTypeSelectActivity.ChargeType chargeType)
    {
        this.chargeType = chargeType;
    }

    public void setReservationTime(String reservationTime)
    {
        ReservationTime = reservationTime;
    }
}
