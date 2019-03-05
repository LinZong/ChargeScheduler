package nemesiss.scheduler.change.chargescheduler.Parcels;

import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.services.core.LatLonPoint;

public class TipParcelable implements Parcelable
{

    private double Latitude;
    private double Longitude;
    private String Name;
    private String AdCode;
    private String District;
    private String PoiId;
    private String TypeCode;

    public static final Creator<TipParcelable> CREATOR = new Creator<TipParcelable>()
    {
        @Override
        public TipParcelable createFromParcel(Parcel in)
        {
            TipParcelable tipP = new TipParcelable();
            tipP.Latitude = in.readDouble();
            tipP.Longitude = in.readDouble();
            tipP.Name = in.readString();
            tipP.AdCode = in.readString();
            tipP.District = in.readString();
            tipP.PoiId = in.readString();
            tipP.TypeCode = in.readString();
            return tipP;
        }
        @Override
        public TipParcelable[] newArray(int size)
        {
            return new TipParcelable[size];
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
        parcel.writeDouble(Latitude);
        parcel.writeDouble(Longitude);
        parcel.writeString(Name);
        parcel.writeString(AdCode);
        parcel.writeString(District);
        parcel.writeString(PoiId);
        parcel.writeString(TypeCode);
    }


    //some relax get function


    public double getLatitude()
    {
        return Latitude;
    }

    public double getLongitude()
    {
        return Longitude;
    }

    public LatLonPoint getLatLonPoint()
    {
        return new LatLonPoint(Latitude,Longitude);
    }
    public String getAdCode()
    {
        return AdCode;
    }

    public String getDistrict()
    {
        return District;
    }

    public String getName()
    {
        return Name;
    }

    public String getPoiId()
    {
        return PoiId;
    }

    public String getTypeCode()
    {
        return TypeCode;
    }
}
