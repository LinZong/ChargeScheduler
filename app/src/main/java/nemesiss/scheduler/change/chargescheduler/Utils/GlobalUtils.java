package nemesiss.scheduler.change.chargescheduler.Utils;

import android.Manifest;
import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
public class GlobalUtils
{
    public static ProgressDialog ShowProgressDialog(Context ctx,boolean Cancelable,String title,String content){
        ProgressDialog dialog = new ProgressDialog(ctx);
        dialog.setCancelable(Cancelable);
        dialog.setTitle(title);
        dialog.setMessage(content);
        return dialog;
    }

    public static AlertDialog.Builder ShowAlertDialog(Context ctx,boolean Cancelable,String title,String content)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        dialog.setCancelable(Cancelable);
        dialog.setTitle(title);
        dialog.setMessage(content);
        return dialog;
    }
}
