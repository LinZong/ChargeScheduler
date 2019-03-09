package nemesiss.scheduler.change.chargescheduler.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable
{
    private String Telephone;
    private String Username;
    private String Password;

    public String getTelephone()
    {
        return Telephone;
    }
    public String getPassword()
    {
        return Password;
    }
    public String getUsername()
    {
        return Username;
    }
    public void setPassword(String password)
    {
        Password = password;
    }
    public void setTelephone(String telephone)
    {
        Telephone = telephone;
    }
    public void setUsername(String username)
    {
        Username = username;
    }

    protected User(Parcel in)
    {

    }

    public User()
    {

    }
    public User(String telephone,String username,String password)
    {
        setTelephone(telephone);
        setPassword(password);
        setUsername(username);
    }
    public static final Creator<User> CREATOR = new Creator<User>()
    {
        @Override
        public User createFromParcel(Parcel in)
        {
            String tele = in.readString();
            String userName = in.readString();
            String passwd = in.readString();
            return new User(tele,userName,passwd);
        }

        @Override
        public User[] newArray(int size)
        {
            return new User[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(getTelephone());
        parcel.writeString(getUsername());
        parcel.writeString(getPassword());
    }
}
