package nemesiss.scheduler.change.chargescheduler;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Constants.RequestUrl;
import nemesiss.scheduler.change.chargescheduler.Services.Users.UserServices;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;

public class MainActivity extends AppCompatActivity
{
    @BindView(R.id.PhoneNumberTextInput)
    TextView phoneText;
    @BindView(R.id.Login_PasswordTextInput)
    TextView passwordText;
    @BindView(R.id.Login_Coordinator)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.ChangeUrlEntry) Toolbar tb;
    private ProgressDialog LoginProgress;

    public static final String LOGIN_PERSISTENCE = "LoginPersistence";


    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //添加校验错误提示
        phoneText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        SharedPreferences sp = getSharedPreferences(LOGIN_PERSISTENCE,Context.MODE_PRIVATE);
        String RememberedPhone = sp.getString("PhoneNumber","");
        phoneText.setText(RememberedPhone);
//        phoneText = findViewById(R.id.PhoneNumberTextInput);
//        passwordText = findViewById(R.id.Login_PasswordTextInput);
//        coordinatorLayout = findViewById(R.id.Login_Coordinator);

        tb.setOnClickListener(this::EnterChangeUrlListener);

    }

    private void EnterChangeUrlListener(View view)
    {
        if(index == 6)
        {
            RequestUrl.SwitchRequestUrlHelper(View.inflate(MainActivity.this,R.layout.switch_api_address, null));
        }
        index = (index + 1)%7;
        if(index > 3) {
            Toast.makeText(MainActivity.this,"还有"+(7-index)+"步进入开发人员工具",Toast.LENGTH_SHORT).show();
        }
    }

    public void AttemptLogin(View view)
    {
        LoginProgress = GlobalUtils.ShowProgressDialog(MainActivity.this, false, "正在登陆", "请稍后...");
        LoginProgress.show();
        String un = phoneText.getText().toString();
        String pw = passwordText.getText().toString();
        SaveLoginPhoneNumber(un);
        new ValidatePasswordTask().execute(un, pw);
    }

    public void AttemptJumpToRegister(View view)
    {
        startActivity(new Intent(MainActivity.this,RegisterActivity.class));
    }

    class ValidatePasswordTask extends AsyncTask<String, Integer, UserServices.LoginStatus>
    {
        private UserServices us;

        @Override
        protected UserServices.LoginStatus doInBackground(String... strings)
        {
            if(strings.length==2 && GlobalUtils.ConfirmStringsAllNotEmpty(strings))
            {
                String pn = strings[0];
                String pw = strings[1];
                return us.Login(pn, pw, true);
            }
            return UserServices.LoginStatus.LOGIN_PASSWORD_WRONG;
        }

        @Override
        protected void onPostExecute(UserServices.LoginStatus loginStatus)
        {
            LoginProgress.cancel();
            switch (loginStatus)
            {
                case LOGIN_SUCCESSFUL:
                {
                    startActivity(new Intent(MainActivity.this, SearchChargerActivity.class));
                    finish();
                    break;
                }
                case LOGIN_UNKNOWN_ERROR:
                {
                    Snackbar.make(passwordText,"登陆失败, 检查网络连接",Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case LOGIN_PASSWORD_WRONG:
                {
                    Snackbar.make(passwordText,"登陆失败, 用户名或密码错误",Snackbar.LENGTH_SHORT).show();
                    break;
                }
                default:break;
            }
        }

        @Override
        protected void onPreExecute()
        {
            us = ChargerApplication.getUserServices();
        }
    }



    private void SaveLoginPhoneNumber(String PhoneNumber)
    {
        SharedPreferences.Editor editor = getSharedPreferences(LOGIN_PERSISTENCE, Context.MODE_PRIVATE).edit();
        editor.putString("PhoneNumber",PhoneNumber);
        editor.commit();
    }

}
