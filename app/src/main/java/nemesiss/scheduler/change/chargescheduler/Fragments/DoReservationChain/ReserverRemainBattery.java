package nemesiss.scheduler.change.chargescheduler.Fragments.DoReservationChain;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import nemesiss.scheduler.change.chargescheduler.Fragments.ChainFragment;
import nemesiss.scheduler.change.chargescheduler.R;
import nemesiss.scheduler.change.chargescheduler.ReservationTypeSelectActivity;

public class ReserverRemainBattery extends Fragment implements ChainFragment, FragmentBackHandler
{

    @BindView(R.id.RemainBatterySeekbar)
    SeekBar sb;
    @BindView(R.id.RemainBatteryNum)
    TextView sbNum;
    @BindView(R.id.ConfirmBatterySelection)
    Button btn;

    private ReservationTypeSelectActivity activity;
    private Unbinder unbinder;
    private View view;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        activity = (ReservationTypeSelectActivity) getActivity();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.reserver_battery_fragment,container,false);
        unbinder = ButterKnife.bind(this,view);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                sbNum.setText(String.valueOf(i));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
        btn.setOnClickListener(this::SetRemainBattery);
        return view;
    }

    private void SetRemainBattery(View view)
    {
        activity.SetRemainBattery(Integer.parseInt(sbNum.getText().toString()));
        switch (activity.GetReservationType())
        {

            case ChargeImmediate:
                activity.GoToProcessReservationActivity();
                break;
            case ChargeAllNight:
                ToNextFragment(new ReserverTimeFrag());
                break;
        }
    }

    @Override
    public boolean onBackPressed()
    {
        activity.SetHintTitle("选择预约的类型");
        return BackHandlerHelper.handleBackPress(this);
    }

    @Override
    public void ToNextFragment(Fragment next)
    {

        FragmentManager fm = activity.getSupportFragmentManager();
        if(fm!=null)
        {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in,R.anim.slide_out,R.anim.pop_slide_in,R.anim.pop_slide_out);
            ft.replace(R.id.ReservationFragment,next);
            activity.SetHintTitle("选择预约的时间");
            activity.setCurrentFragmentChain((ChainFragment)next);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }
}
