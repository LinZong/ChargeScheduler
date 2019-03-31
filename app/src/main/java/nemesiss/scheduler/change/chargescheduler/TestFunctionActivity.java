package nemesiss.scheduler.change.chargescheduler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.jaeger.library.StatusBarUtil;

public class TestFunctionActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_function);
        StatusBarUtil.setTransparent(this);
    }
}
