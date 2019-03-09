package nemesiss.scheduler.change.chargescheduler;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jaredrummler.materialspinner.MaterialSpinner;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Models.Response.CarType;
import nemesiss.scheduler.change.chargescheduler.Services.Users.CarServices;
import nemesiss.scheduler.change.chargescheduler.Services.Users.UserServices;
import nemesiss.scheduler.change.chargescheduler.Utils.GlobalUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity
{

    @BindView(R.id.Register_Toolbar) Toolbar toolbar;
    @BindView(R.id.Register_PhoneTextInput)
    EditText PhoneNumberEditText;
    @BindView(R.id.Register_PasswordTextInput)
    EditText PasswordNumberEditText;
    @BindView(R.id.UserLawCheckBox)
    CheckBox checkUserLaw;
    @BindView(R.id.Register_CarTypeSelect)
    MaterialSpinner spinner;
    ProgressDialog RegisterProgress;



    private List<CarType> carTypeList = null;
    private List<String> carTypeNameList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        carTypeList = new ArrayList<>();
        carTypeNameList = new ArrayList<>();
        RegisterProgress = GlobalUtils.ShowProgressDialog(RegisterActivity.this,false,"正在注册", "请稍后...");

        new LoadCanSelectCarType().execute();

        //设置返回键显示逻辑
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
    private void ClearAllError()
    {
        PhoneNumberEditText.setError(null);
        PasswordNumberEditText.setError(null);
        checkUserLaw.setError(null);
    }
    public void AttemptRegister(View view)
    {
        //先清除掉所有设置的错误.
        ClearAllError();
        String pn = PhoneNumberEditText.getText().toString();
        String pw = PasswordNumberEditText.getText().toString();
        boolean IsChecked = checkUserLaw.isChecked();
        int seletedId = -1;
        //TODO 完成CarType的显示
        if(TextUtils.isEmpty(pn))
        {
            PhoneNumberEditText.setError("手机号码不能为空");
            return;
        }
        else if(TextUtils.isEmpty(pw))
        {
            PasswordNumberEditText.setError("密码不能为空");
            return;
        }
        else if(!ValidatePassword(pw))
        {
            PasswordNumberEditText.setError("密码至少需要六位");
            return;
        }
        if(carTypeList.isEmpty())
        {
            Snackbar.make(PasswordNumberEditText,"没有选择车型, 不允许注册",Snackbar.LENGTH_SHORT).show();
            return;
        }
        else if(!IsChecked)
        {
            checkUserLaw.setError("必须同意用户协议");
            Snackbar.make(PasswordNumberEditText,"必须同意用户协议",Snackbar.LENGTH_SHORT).show();
            return;
        }
        else {
            // TODO : 开始向服务器发送注册请求
            //可以parse选择的id
            seletedId = carTypeList.get(spinner.getSelectedIndex()).getId();
            RegisterProgress.show();
            new RegisterTask().execute(pn,pw,String.valueOf(seletedId));
        }
    }
    public boolean ValidatePassword(String passwd)
    {
        //保证密码至少六位.
        return Pattern.matches("^.{6,}$",passwd);
    }

    class RegisterTask extends AsyncTask<String,Void, UserServices.RegisterStatus>
    {

        private UserServices us;
        @Override
        protected UserServices.RegisterStatus doInBackground(String... strings)
        {
            String pn = strings[0];
            String pw = strings[1];
            int carType = Integer.parseInt(strings[2]);
            return us.Register(pn,pw,carType);
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            us = ChargerApplication.getUserServices();
        }

        @Override
        protected void onPostExecute(UserServices.RegisterStatus registerStatus)
        {
            super.onPostExecute(registerStatus);
            RegisterProgress.cancel();
            switch (registerStatus){
                case REGISTER_SUCCESS:{
                    AlertDialog.Builder bd = GlobalUtils.ShowAlertDialog(RegisterActivity.this,false, "注册成功", "按下确定返回登录界面.");
                    bd.setPositiveButton("确定", (d, i) -> finish());
                    bd.show();
                    break;
                }
                case REGISTER_DB_RWERROR:{
                    Snackbar.make(PasswordNumberEditText,"注册失败, 远端服务器出现错误.",Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case REGISTER_UNKNOWN_ERROR:{
                    Snackbar.make(PasswordNumberEditText,"注册失败, 远端服务器出现错误.",Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case REGISTER_PHONE_NUMBER_CONFLICT:{
                    Snackbar.make(PasswordNumberEditText,"注册失败, 手机号已经被使用.",Snackbar.LENGTH_SHORT).show();
                    break;
                }
                default:break;
            }
        }
    }

    class LoadCanSelectCarType extends AsyncTask<Void,Void, List<CarType>>
    {
        private CarServices cs;
        @Override
        protected List<CarType> doInBackground(Void... voids)
        {
            return cs.GetAllCarType();
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            cs = ChargerApplication.getCarServices();
        }

        @Override
        protected void onPostExecute(List<CarType> carTypes)
        {
            super.onPostExecute(carTypes);
            if(carTypes!=null)
            {
                //开始渲染到Spinner上
                carTypeList = carTypes;
                carTypeNameList.clear();
                int len = carTypeList.size();
                for (int i = 0; i < len; i++)
                {
                    carTypeNameList.add(carTypeList.get(i).getName());
                }
                spinner.setItems(carTypeNameList);
                spinner.setSelectedIndex(0);
            }
        }
    }

}
