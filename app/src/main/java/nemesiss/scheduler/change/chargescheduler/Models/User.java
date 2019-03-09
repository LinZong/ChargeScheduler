package nemesiss.scheduler.change.chargescheduler.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class User
{
    private String Phone;
    private long Id;
    private String Nickname;
    private int CarTypeId;
    private int Credits;
    private String Password;

    public User(){}
    public User(String phone,long id,String password)
    {
        setPhone(phone);
        setId(id);
        setPassword(password);
    }
    public String getPassword()
    {
        return Password;
    }

    public int getCarTypeId()
    {
        return CarTypeId;
    }

    public int getCredits()
    {
        return Credits;
    }

    public long getId()
    {
        return Id;
    }

    public String getNickname()
    {
        return Nickname;
    }

    public String getPhone()
    {
        return Phone;
    }

    public void setId(long id)
    {
        Id = id;
    }

    public void setCarTypeId(int carTypeId)
    {
        CarTypeId = carTypeId;
    }

    public void setCredits(int credits)
    {
        Credits = credits;
    }

    public void setNickname(String nickname)
    {
        Nickname = nickname;
    }

    public void setPhone(String phone)
    {
        Phone = phone;
    }

    public void setPassword(String password)
    {
        Password = password;
    }
}
