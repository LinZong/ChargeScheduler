package nemesiss.scheduler.change.chargescheduler.Models.Response;

import java.io.Serializable;

public class Stations implements Serializable
{
    private int Id;
    private String Name;
    private String City;
    private String Address;
    private int AvailableInstance;
    private int AvailableDelay;
    private double Latitude;
    private double Longitude;
    private int AllInstance;
    private int AllDelay;
    private int DistanceBetweenMe = -1;

    public int getDistanceBetweenMe()
    {
        return DistanceBetweenMe;
    }

    public void setDistanceBetweenMe(int distanceBetweenMe)
    {
        DistanceBetweenMe = distanceBetweenMe;
    }

    public int getId()
    {
        return Id;
    }

    public String getName()
    {
        return Name;
    }

    public double getLongitude()
    {
        return Longitude;
    }

    public double getLatitude()
    {
        return Latitude;
    }

    public int getAllDelay()
    {
        return AllDelay;
    }

    public int getAllInstance()
    {
        return AllInstance;
    }

    public int getAvailableDelay()
    {
        return AvailableDelay;
    }

    public int getAvailableInstance()
    {
        return AvailableInstance;
    }

    public String getAddress()
    {
        return Address;
    }

    public String getCity()
    {
        return City;
    }

    public void setAvailableDelay(int availableDelay)
    {
        AvailableDelay = availableDelay;
    }

    public void setAddress(String address)
    {
        Address = address;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public void setId(int id)
    {
        Id = id;
    }

    public void setAllDelay(int allDelay)
    {
        AllDelay = allDelay;
    }

    public void setAllInstance(int allInstance)
    {
        AllInstance = allInstance;
    }

    public void setAvailableInstance(int availableInstance)
    {
        AvailableInstance = availableInstance;
    }

    public void setCity(String city)
    {
        City = city;
    }

    public void setLatitude(double latitude)
    {
        Latitude = latitude;
    }

    public void setLongitude(double longitude)
    {
        Longitude = longitude;
    }
}
