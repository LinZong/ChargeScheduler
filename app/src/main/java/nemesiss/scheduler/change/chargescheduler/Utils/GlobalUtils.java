package nemesiss.scheduler.change.chargescheduler.Utils;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
public class GlobalUtils
{
    public static ProgressDialog ShowProgressDialog(Context ctx,boolean Cancalable,String title,String content){
        ProgressDialog dialog = new ProgressDialog(ctx);
        dialog.setCancelable(Cancalable);
        dialog.setTitle(title);
        dialog.setMessage(content);
        return dialog;
    }
}
