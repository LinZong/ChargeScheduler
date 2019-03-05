package nemesiss.scheduler.change.chargescheduler.Models;

public class City
{
    public String CityName;
    public String CityCode;

    public String getCityCode()
    {
        return CityCode;
    }

    public String getCityName()
    {
        return CityName;
    }

    public void setCityCode(String cityCode)
    {
        CityCode = cityCode;
    }

    public void setCityName(String cityName)
    {
        CityName = cityName;
    }
}
