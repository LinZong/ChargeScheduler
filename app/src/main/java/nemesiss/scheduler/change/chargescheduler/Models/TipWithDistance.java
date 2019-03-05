package nemesiss.scheduler.change.chargescheduler.Models;

import com.amap.api.services.help.Tip;
import com.amap.api.services.route.DistanceItem;
import com.amap.api.services.route.DistanceResult;

public class TipWithDistance extends Tip
{
    private DistanceItem distanceResult;

    public static TipWithDistance TransfromTipToTipWithDistance(Tip tip,DistanceItem dr)
    {
        TipWithDistance twd = new TipWithDistance();
        twd.setAdcode(tip.getAdcode());
        twd.setAddress(tip.getAddress());
        twd.setDistrict(tip.getDistrict());
        twd.setName(tip.getName());
        twd.setID(tip.getPoiID());
        twd.setPostion(tip.getPoint());
        twd.setTypeCode(tip.getTypeCode());
        twd.setDistanceResult(dr);
        return twd;
    }

    public void setDistanceResult(DistanceItem distanceResult)
    {
        this.distanceResult = distanceResult;
    }

    public DistanceItem getDistanceResult()
    {
        return distanceResult;
    }
}

