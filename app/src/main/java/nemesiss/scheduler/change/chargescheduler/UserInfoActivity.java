package nemesiss.scheduler.change.chargescheduler;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import nemesiss.scheduler.change.chargescheduler.Application.ChargeActivity;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Models.User;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;

public class UserInfoActivity extends ChargeActivity
{

    @BindView(R.id.UserInfoID)
    EditText UserInfoID;
    @BindView(R.id.UserInfoPhoneNumber)
    EditText UserInfoPhoneNumber;
    @BindView(R.id.UserInfoNickName)
    EditText UserInfoNickName;

    @BindView(R.id.UserInfoNumberPlate)
    TextView UserInfoNumberPlate;
    @BindView(R.id.UserInfoCredits)
    TextView UserInfoCredits;
    @BindView(R.id.UserInfoOnTimeRatio)
    TextView UserInfoOnTimeRatio;
    @BindView(R.id.UserInfoProgressBar)
    RelativeLayout ProgressBar;

    @BindView(R.id.UserInfoToolbar)
    Toolbar tb;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        GlobalUtils.ToolbarShowReturnButton(UserInfoActivity.this,tb);
        MapUserInfoToView();
    }

    private void MapUserInfoToView()
    {
        User user = ChargerApplication.getLoginedUser();
        UserInfoID.setText(String.valueOf(user.getId()));
        UserInfoPhoneNumber.setText(user.getPhone());
        UserInfoNickName.setText(user.getNickname());
        UserInfoNumberPlate.setText(user.getNumberPlate());
        UserInfoCredits.setText(String.valueOf(user.getCredits()));
        UserInfoOnTimeRatio.setText(String.valueOf(user.getOnTimeRatio()));
    }
    private User MapViewToUserInfo()
    {
        return new User();
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

    public void LogoutUserAccount(View view)
    {   ChargeActivity.FinishAllActivities();
        Intent it = new Intent("nemesiss.scheduler.change.chargescheduler.loginActivityAction");
        startActivity(it);
    }
}
