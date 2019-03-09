package nemesiss.scheduler.change.chargescheduler;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Services.Users.UserServices;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
{
    @BindView(R.id.PhoneNumberTextInput)
    TextView phoneText;
    @BindView(R.id.Login_PasswordTextInput)
    TextView passwordText;
    @BindView(R.id.Login_Coordinator)
    CoordinatorLayout coordinatorLayout;
    private ProgressDialog LoginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //添加校验错误提示
        phoneText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
//        phoneText = findViewById(R.id.PhoneNumberTextInput);
//        passwordText = findViewById(R.id.Login_PasswordTextInput);
//        coordinatorLayout = findViewById(R.id.Login_Coordinator);
    }

    public void AttemptLogin(View view)
    {
        LoginProgress = GlobalUtils.ShowProgressDialog(MainActivity.this, false, "正在登陆", "请稍后...");
        LoginProgress.show();
        String un = phoneText.getText().toString();
        String pw = passwordText.getText().toString();
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

}
