package nemesiss.scheduler.change.chargescheduler.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import nemesiss.scheduler.change.chargescheduler.R;

public class ReserverTimeFrag extends Fragment implements ChainFragment
{
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.reserver_time_fragment,container,false);
        return view;
    }
    public void ToNextFragment(Fragment fragment)
    {

    }
}
