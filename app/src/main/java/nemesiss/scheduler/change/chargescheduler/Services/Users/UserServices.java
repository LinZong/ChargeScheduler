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
import java.security.KeyPair;
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
    private OkHttpClient client;

    public UserServices()
    {
        client = new OkHttpClient.Builder().connectTimeout(4500, TimeUnit.MILLISECONDS).build();
    }

    public LoginStatus Login(String PhoneNumber, String Password,boolean NeedEncryptPassword)
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
                    ChargerApplication.setLoginedUser(new User(PhoneNumber,null,Password));

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
        Log.e("USERLOGINERROR","无法得到登陆模型.检查网络连接.");
        return LoginStatus.LOGIN_UNKNOWN_ERROR;
    }

    public RegisterStatus Register(String PhoneNumber,String Password,int CarTypeId)
    {
        String encryptedPassword = HMacSha256.Encrypt(Password);
        CommonResponseModel model = SendRegisterRequest(PhoneNumber,encryptedPassword,CarTypeId);
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
        Log.e("USERREGISTERERROR","无法得到注册模型.检查网络连接.");
        return RegisterStatus.REGISTER_UNKNOWN_ERROR;
    }

    private CommonResponseModel SendRegisterRequest(String PhoneNumber,String Password,int CarTypeId)
    {

        List<Pair<String,String>> body = new ArrayList<>();
        body.add(new Pair<>("phone",PhoneNumber));
        body.add(new Pair<>("password",Password));
        body.add(new Pair<>("cartypeid",String.valueOf(CarTypeId)));

        try
        {
            Response resp = SendPostRequest(RequestUrl.getRegisterUrl(),null,body);
            if(resp!=null && resp.isSuccessful())
            {
                Gson gson = new Gson();
                return gson.fromJson(resp.body().string(),CommonResponseModel.class);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            Log.e("USERLOGINERROR", "User register failed. Result is : " + e.getMessage());
        }
        return null;
    }

    private TokenResponseModel SendLoginRequest(String PhoneNumber, String Password, String Signature)
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
            Response resp = SendPostRequest(RequestUrl.getLoginUrl(),null,body);
            if (resp != null && resp.isSuccessful())
            {
                String respJson = resp.body().string();
                Gson gson = new Gson();
                return gson.fromJson(respJson, TokenResponseModel.class);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            Log.e("USERLOGINERROR", "User login failed. Result is : " + e.getMessage());
        }
        return null;
    }

    private Response SendPostRequest(String url,List<Pair<String,String>> Header,List<Pair<String,String>> Body) throws IOException
    {
        MultipartBody.Builder requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if(Body!=null)
        {
            for (Pair<String, String> p : Body)
            {
                requestBody.addFormDataPart(p.first,p.second);
            }
        }

        Request.Builder reqBuilder = new Request.Builder().url(url).post(requestBody.build());
        if(Header!=null)
        {
            for (Pair<String, String> head : Header)
            {
                reqBuilder.addHeader(head.first,head.second);
            }
        }
        Request req = reqBuilder.build();
        return client.newCall(req).execute();
    }

    public static boolean CheckIfNeedToRefreshToken(Date tokenTaggedExpireDate)
    {
        Date Now = new Date();
        long tagged = tokenTaggedExpireDate.getTime();
        long now = Now.getTime();
        return  ((int)(tagged - now)/(1000*60)) < 5;
    }
}
