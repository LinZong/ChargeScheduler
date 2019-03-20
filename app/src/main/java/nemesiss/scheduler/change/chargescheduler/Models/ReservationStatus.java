package nemesiss.scheduler.change.chargescheduler.Models;

public enum  ReservationStatus
{
    NOT_ARRIVED("尚未到达",0),
    NORMAL("完成充电",1),
    BELATED("迟到",2),
    CANCELBYUSER("被用户取消",3),
    CANCELBYSYSTEM("被系统取消",4);

    private String TIPS;
    private int idx;
    ReservationStatus(String tips,int index)
    {
        TIPS = tips;
    }

    public int getIdx()
    {
        return idx;
    }
}
