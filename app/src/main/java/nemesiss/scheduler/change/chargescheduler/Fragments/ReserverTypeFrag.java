package nemesiss.scheduler.change.chargescheduler.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import nemesiss.scheduler.change.chargescheduler.R;
import nemesiss.scheduler.change.chargescheduler.ReservationTypeSelectActivity;

public class ReserverTypeFrag extends Fragment implements ChainFragment, FragmentBackHandler
{
    private View.OnClickListener SelectTypeListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int btnId = view.getId();
            switch (btnId){
                case R.id.ChargeImmediateBtn:
                    activity.SetReservationType(ReservationTypeSelectActivity.ChargeType.ChargeImmediate);
                    ToNextFragment(new ReserverTimeFrag());
                    break;
                case R.id.ChargeAllNightBtn:
                    activity.SetReservationType(ReservationTypeSelectActivity.ChargeType.ChargeAllNight);
                    ToNextFragment(new ReserverTimeFrag());
                    break;
                default:break;
            }
        }
    };
    private View view;
    private ReservationTypeSelectActivity activity;
    private Button ChargeImmeBtn;
    private Button ChargeNightBtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.reserver_type_fragment,container,false);
        activity = (ReservationTypeSelectActivity) getActivity();
        ChargeImmeBtn = view.findViewById(R.id.ChargeImmediateBtn);
        ChargeNightBtn = view.findViewById(R.id.ChargeAllNightBtn);
        ChargeImmeBtn.setOnClickListener(SelectTypeListener);
        ChargeNightBtn.setOnClickListener(SelectTypeListener);
        return view;
    }

    @Override
    public void ToNextFragment(Fragment fragment)
    {

        FragmentManager fm = activity.getSupportFragmentManager();
        if(fm!=null)
        {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in,R.anim.slide_out,R.anim.pop_slide_in,R.anim.pop_slide_out);
            ft.replace(R.id.ReservationFragment,fragment);
            activity.SetHintTitle("选择预约的时间");
            activity.setCurrentFragmentChain((ChainFragment)fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public boolean onBackPressed()
    {
        return false;
    }
}
