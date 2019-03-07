package nemesiss.scheduler.change.chargescheduler.Fragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nemesiss.scheduler.change.chargescheduler.R;

public class ProcessingFragment extends Fragment
{
    private View view;
    private Unbinder unbinder;
    @BindView(R.id.ProcessStatusImageView) ImageView imageView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.processing_reservation_fragment,container,false);
        unbinder = ButterKnife.bind(this,view);
        ((AnimationDrawable) imageView.getBackground()).start();
        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }
}
