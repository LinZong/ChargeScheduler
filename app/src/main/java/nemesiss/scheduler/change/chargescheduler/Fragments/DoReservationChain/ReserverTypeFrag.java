package nemesiss.scheduler.change.chargescheduler.Fragments.DoReservationChain;

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
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import nemesiss.scheduler.change.chargescheduler.Fragments.ChainFragment;
import nemesiss.scheduler.change.chargescheduler.R;
import nemesiss.scheduler.change.chargescheduler.ReservationTypeSelectActivity;
import nemesiss.scheduler.change.chargescheduler.Services.Users.CommonServices;

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
                    ToNextFragment(new ReserverRemainBattery());
                    break;
                case R.id.ChargeAllNightBtn:
                    activity.SetReservationType(ReservationTypeSelectActivity.ChargeType.ChargeAllNight);
                    ToNextFragment(new ReserverRemainBattery());
                    break;
                default:break;
            }
        }
    };
    private View view;
    private Unbinder unbinder;
    private ReservationTypeSelectActivity activity;
    private boolean IsBusyTime = false;
    @BindView(R.id.ChargeImmediateBtn) Button ChargeImmeBtn;
    @BindView(R.id.ChargeAllNightBtn) Button ChargeNightBtn;
    @BindView(R.id.IsBusyTimePeriodHint)
    TextView IsBusyTimePeriodHint;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.reserver_type_fragment,container,false);
        activity = (ReservationTypeSelectActivity) getActivity();
        //ButterKnife需要绑Fragment的时候要给当前view。
        unbinder = ButterKnife.bind(this,view);

        IsBusyTime = CommonServices.CheckIfBusyPeriodBasedOnPreset();
        if(IsBusyTime)
        {
            IsBusyTimePeriodHint.setVisibility(View.VISIBLE);
            ChargeNightBtn.setEnabled(false);
        }
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
            activity.SetHintTitle("设置剩余的电量");
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

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }
}
