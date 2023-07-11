package pl.allblue.notifications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Confirmation
{

    static public void ShowDialog(Context context, String message,
            String yes_text, String no_text, final OnConfirmationResultListener listener)
    {
        DialogInterface.OnClickListener click_listener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    listener.onResult(true);
                    break;

                default:
                    listener.onResult(false);
                    break;
            }
        };

        DialogInterface.OnCancelListener cancel_listener = (dialogInterface) -> {
                listener.onResult(false);
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
            .setMessage(message)
            .setOnCancelListener(cancel_listener)
            .setPositiveButton(yes_text, click_listener)
            .setNegativeButton(no_text, click_listener);

        builder.show();
    }


    public interface OnConfirmationResultListener
    {
        void onResult(boolean result);
    }

}
