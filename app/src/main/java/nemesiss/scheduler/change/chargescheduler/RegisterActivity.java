package nemesiss.scheduler.change.chargescheduler;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity
{

    @BindView(R.id.Register_Toolbar) Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab!=null)
        {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
            default:break;
        }
        return true;
    }

    public void AttemptRegister(View view)
    {
    }
}
