package nemesiss.scheduler.change.chargescheduler.Services.Reservation;

import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Constants.RequestUrl;
import nemesiss.scheduler.change.chargescheduler.Models.Response.Stations;
import nemesiss.scheduler.change.chargescheduler.Services.Users.CommonServices;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StationServices
{

    private SparseArray<Stations> AllStationsList = null;
    private OkHttpClient client = null;
    public StationServices()
    {
        AllStationsList = new SparseArray<>();
        client = GlobalUtils.GetOkHttpClient().build();
    }

    public SparseArray<Stations> getAllStationsList()
    {
        return AllStationsList;
    }

    public void RefreshAllStationInfo()
    {
        new GetAllStationTask().execute();
    }

    public Stations SetStationInfo(Stations info) throws IllegalArgumentException
    {
        if(AllStationsList!=null)
        {
            if(info!=null)
            {
                Stations selected = AllStationsList.get(info.getId(),null);
                if(selected!=null)
                {
                    info.setDistanceBetweenMe(selected.getDistanceBetweenMe());
                    AllStationsList.put(selected.getId(),info);
                }
                else AllStationsList.append(info.getId(),info);
                return info;
            }
            else throw new IllegalArgumentException("传入待更新的充电站信息为空!");
        }
        throw new IllegalArgumentException("全部充电站列表不存在! 请尝试重新获取.");
    }

    public Stations GetStationInfo(int key)
    {
        return AllStationsList.get(key,null);
    }

    public List<Stations> ShowInputTips(String key)
    {
        StringBuilder keyWordBuffer = new StringBuilder();
        List<Stations> maybe = new ArrayList<>();
        if(AllStationsList!=null)
        {
            int len = AllStationsList.size();
            for (int i = 0; i < len; i++)
            {
                Stations s = AllStationsList.valueAt(i);
                keyWordBuffer.append(s.getName()).append(s.getCity()).append(s.getAddress());

                if(keyWordBuffer.indexOf(key) != -1)
                {
                    maybe.add(s);
                }
                keyWordBuffer.setLength(0);//clear the buffer.
            }
        }
        return maybe;
    }

    public Stations UpdateStationInfo(int id)
    {
        Response resp = null;
        try
        {
            resp = CommonServices.SendGetRequest(client, RequestUrl.getSelectedStation(id),null,null);
            if(GlobalUtils.ConfirmResponseSuccessful(resp))
            {
                String jsonResp = resp.body().string();
                List<Stations> info = new Gson().fromJson(jsonResp,new TypeToken<List<Stations>>(){}.getType());
                return SetStationInfo(info.get(0));
            }
        } catch (Exception e)
        {
            Log.e("StationServices", "无法更新指定充电站信息, 充电站ID : "+id);
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Stations> GetCanArriveIn15MinStations()
    {
        int len = AllStationsList.size();
        ArrayList<Stations> can = new ArrayList<>();
        for (int i = 0; i < len; i++)
        {
            Stations s = AllStationsList.valueAt(i);
            //15 mins, avg 45km/h => 11.25km
            if(s.getDistanceBetweenMe()!=-1 && s.getDistanceBetweenMe() <= 20000)
            {
                can.add(s);
            }
        }

        Collections.sort(can, (stations, t1) -> {
            int d1 = stations.getDistanceBetweenMe(),d2 = t1.getDistanceBetweenMe();
            if(d1 < d2) return -1;
            else if(d1 == d2) return 0;
            else return 1;
        });
        //传出的结果是经历过从小到大排序的，最后就把这些数据加入到排队队列中。传出进一步处理。
        return can;
    }

    class GetAllStationTask extends AsyncTask<Void,Void, SparseArray<Stations>>
    {
        @Override
        protected SparseArray<Stations> doInBackground(Void... voids)
        {
            try
            {
                Response resp = CommonServices.SendGetRequest(client,RequestUrl.getAllStations(),null,null);
                if(GlobalUtils.ConfirmResponseSuccessful(resp))
                {
                    String jsonResp = resp.body().string();
                    Gson gson = new Gson();
                    List<Stations> stations = gson.fromJson(jsonResp,new TypeToken<List<Stations>>(){}.getType());
                    SparseArray<Stations> result = new SparseArray<>();
                    if(stations!=null)
                    {
                        Log.d("StationServices","成功获取到全部充电站信息.");
                        for (Stations s : stations)
                        {
                            result.append(s.getId(),s);
                        }
                        return result;
                    }

                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            Log.d("StationServices","获取全部充电站信息失败, 搜索功能和自动寻找充电站功能将不可用。");
            return null;
        }

        @Override
        protected void onPostExecute(SparseArray<Stations> result)
        {
            super.onPostExecute(result);
            if(result!=null)
            {
                AllStationsList = result;
            }
            else
            {
                Toast.makeText(ChargerApplication.getContext(),"无法获得全部的充电站信息,搜索功能和预约功能将不可用。",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
    }
}
