package nemesiss.scheduler.change.chargescheduler.Models.Response;

import android.util.Log;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TokenResponseInfo
{
    private String token;
    private String expire;

    public String getExpire()
    {
        return expire;
    }

    public Date getDateExpire()
    {
        if(expire!=null)
        {
            SimpleDateFormat fmt = GlobalUtils.TokenDateFormatter();
            Date d = null;
            try
            {
                d = fmt.parse(expire);
            } catch (ParseException e)
            {
                e.printStackTrace();
                Log.e("STRINGPARSETODAY", "过期时间解析到日期失败");
            }
            return d;
        }
        return null;
    }

    public String getToken()
    {
        return token;
    }

    public void setExpire(String expire)
    {
        this.expire = expire;
    }

    public void setToken(String token)
    {
        this.token = token;
    }
}