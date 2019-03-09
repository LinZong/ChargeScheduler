package nemesiss.scheduler.change.chargescheduler.Utils;

import android.util.Base64;
import android.util.Log;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMacSha256
{
    public static String Encrypt(String message)
    {
        try {
            String secret = "THINKNOTE_PASSWORD_ENCODER";
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] rawHmac = sha256_HMAC.doFinal(message.getBytes());
            String result = Base64.encodeToString(rawHmac, Base64.NO_WRAP);
            return result;

        }
        catch (Exception e){
            Log.e("HMACSHA256ERROR","加密失败,错误原因为 : "+e.getMessage());
        }
        return null;
    }

}
