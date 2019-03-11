package nemesiss.scheduler.change.chargescheduler.Services.Users;

import android.util.Log;
import android.util.Pair;
import com.google.gson.Gson;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.Constants.RequestUrl;
import nemesiss.scheduler.change.chargescheduler.Models.Response.CommonResponseModel;
import nemesiss.scheduler.change.chargescheduler.Models.Response.TokenResponseModel;
import nemesiss.scheduler.change.chargescheduler.Models.User;
import nemesiss.scheduler.change.chargescheduler.Utils.HMacSha256;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserServices
{

    public enum LoginStatus
    {
        LOGIN_SUCCESSFUL,
        LOGIN_PASSWORD_WRONG,
        LOGIN_UNKNOWN_ERROR
    }

    public enum RegisterStatus
    {
        REGISTER_SUCCESS,
        REGISTER_PHONE_NUMBER_CONFLICT,
        REGISTER_UNKNOWN_ERROR,
        REGISTER_DB_RWERROR
    }

    // 这些服务写成同步的，到时候可以根据业务逻辑在上层做异步调用也无妨。
    private static OkHttpClient client;

    public UserServices()
    {
        client = new OkHttpClient.Builder().connectTimeout(4500, TimeUnit.MILLISECONDS).build();
    }

    public static LoginStatus Login(String PhoneNumber, String Password,boolean NeedEncryptPassword)
    {

        // Try to encrypt password;

        String encryptedPassword = NeedEncryptPassword ? HMacSha256.Encrypt(Password) : Password;
        // Generate Signature

        StringBuilder sb = new StringBuilder(PhoneNumber);
        sb.insert(3, encryptedPassword);
        String insertedString = sb.toString();
        String signature = HMacSha256.Encrypt(insertedString);

        TokenResponseModel model = SendLoginRequest(PhoneNumber, encryptedPassword, signature);
        if (model != null)
        {
            switch (model.getStatusCode())
            {
                case 1100:
                {
                    //TODO : Persistence Token and Expire Time to somewhere, and tell UI finished.
                    //设置已登录的用户信息到全局Application中
                    ChargerApplication.setToken(model.getTokenResponse());
                    if(ChargerApplication.getLoginedUser()==null)
                        ChargerApplication.setLoginedUser(new User(PhoneNumber,model.getUserID(),encryptedPassword));
                    return LoginStatus.LOGIN_SUCCESSFUL;
                }
                case 1101:
                {
                    // TODO : Tell UI Username or password wrong.
                    return LoginStatus.LOGIN_PASSWORD_WRONG;
                }
                default:break;
            }
        }
        Log.e("UserServices","无法得到登陆模型.检查网络连接.");
        return LoginStatus.LOGIN_UNKNOWN_ERROR;
    }

    public static RegisterStatus Register(String PhoneNumber,String Password,int CarTypeId,String NumberPlate)
    {
        String encryptedPassword = HMacSha256.Encrypt(Password);
        CommonResponseModel model = SendRegisterRequest(PhoneNumber,encryptedPassword,CarTypeId,NumberPlate);
        if(model!=null)
        {
            switch (model.getStatusCode()){
                case 1000:{
                    return RegisterStatus.REGISTER_SUCCESS;
                }
                case 1001:{

                    return RegisterStatus.REGISTER_PHONE_NUMBER_CONFLICT;
                }
                case 1002:{

                    return RegisterStatus.REGISTER_DB_RWERROR;
                }
                default:break;
            }
        }
        Log.e("UserServices","无法得到注册模型.检查网络连接.");
        return RegisterStatus.REGISTER_UNKNOWN_ERROR;
    }

    private static CommonResponseModel SendRegisterRequest(String PhoneNumber,String Password,int CarTypeId,String NumberPlate)
    {

        List<Pair<String,String>> body = new ArrayList<>();
        body.add(new Pair<>("phone",PhoneNumber));
        body.add(new Pair<>("password",Password));
        body.add(new Pair<>("cartypeid",String.valueOf(CarTypeId)));
        body.add(new Pair<>("numberplate",NumberPlate));
        try
        {
            Response resp = CommonServices.SendPostRequest(client,RequestUrl.getRegisterUrl(),null,body);
            if(resp!=null && resp.isSuccessful())
            {
                Gson gson = new Gson();
                return gson.fromJson(resp.body().string(),CommonResponseModel.class);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            Log.e("UserServices", "User register failed. Result is : " + e.getMessage());
        }
        return null;
    }

    private static TokenResponseModel SendLoginRequest(String PhoneNumber, String Password, String Signature)
    {

//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("phone", PhoneNumber)
//                .addFormDataPart("password", Password)
//                .addFormDataPart("signature", Signature)
//                .build();
//
//        Request req = new Request.Builder().url(RequestUrl.getLoginUrl()).post(requestBody).build();
        List<Pair<String,String>> body = new ArrayList<>();
        body.add(new Pair<>("phone",PhoneNumber));
        body.add(new Pair<>("password",Password));
        body.add(new Pair<>("signature",Signature));

        try
        {
            Response resp = CommonServices.SendPostRequest(client,RequestUrl.getLoginUrl(),null,body);
            if (resp != null && resp.isSuccessful())
            {
                String respJson = resp.body().string();
                Gson gson = new Gson();
                return gson.fromJson(respJson, TokenResponseModel.class);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            Log.e("UserServices", "User login failed. Result is : " + e.getMessage());
        }
        return null;
    }

}
