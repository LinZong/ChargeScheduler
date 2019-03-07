package nemesiss.scheduler.change.chargescheduler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
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
    private TextView phoneText;
    private TextView passwordText;
    private CoordinatorLayout coordinatorLayout;
    private ProgressDialog LoginProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneText = findViewById(R.id.PhoneNumberTextInput);
        passwordText = findViewById(R.id.Login_PasswordTextInput);
        coordinatorLayout = findViewById(R.id.Login_Coordinator);
    }

    public void AttemptLogin(View view)
    {
//        LoginProgress =  GlobalUtils.ShowProgressDialog(MainActivity.this,false,"正在登陆", "请稍后...");
//        LoginProgress.show();
//        String un = phoneText.getText().toString();
//        String pw = passwordText.getText().toString();
//        new ValidatePasswordTask().execute(un,pw);

        startActivity(new Intent(MainActivity.this,SearchChargerActivity.class));
        finish();
    }

    class ValidatePasswordTask extends AsyncTask<String,Integer,Boolean>
    {
        private OkHttpClient client;

        @Override
        protected Boolean doInBackground(String... strings)
        {

            String username = strings[0];
            String password = strings[1];//Need to do some validate here;
            Request req = new Request.Builder().url("http://192.168.88.126/people.json").build();
            try
            {
                Response resp =  client.newCall(req).execute();
                if(resp!=null && resp.isSuccessful()){
                    String userList = resp.body().string();
                    JSONObject listObj = new JSONObject(userList);
                    JSONArray listArr = listObj.getJSONArray("UserList");
                    int len = listArr.length();
                    for (int i = 0; i < len; i++)
                    {
                        JSONObject each = listArr.getJSONObject(i);
                        String corrName = each.getString("UserName");
                        String corrPasswd = each.getString("Password");
                        if(username.equals(corrName) && password.equals(corrPasswd))
                        {
                            return true;
                        }
                    }
                    return false;
                }
            } catch (IOException | JSONException e)
            {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            LoginProgress.cancel();
            if(result){
                Snackbar.make(passwordText,"登陆成功", Snackbar.LENGTH_SHORT).show();
            }
            else Snackbar.make(passwordText,"登陆失败, 用户名或密码错误", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute()
        {
            if(client==null) client = new OkHttpClient();
        }
    }

}
