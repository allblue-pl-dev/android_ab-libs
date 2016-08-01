package pl.allblue.notifications;

import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import pl.allblue.app.ABActivity;
import pl.allblue.widget.ABGifView;

public class Notifications
{

    private ABActivity activity = null;

    private boolean loading_InProgress = false;
    private boolean message_InProgress = false;

    private int message_ImageIds_Success = 0;
    private int message_ImageIds_Failure = 0;

    /* Widgets */
    private ViewGroup wLoading = null;
    private ABGifView wLoading_Gif = null;
    private TextView wLoading_Message = null;

    private ViewGroup wMessage = null;
    private ImageView wMessage_Image = null;
    private TextView wMessage_Message = null;

    public Notifications(ABActivity activity,
             int loading_view_id, int loading_gif_view_id, int loading_text_view_id,
             int message_view_id, int message_image_view_id, int message_text_view_id,
             int success_message_image_id, int failure_message_image_id)
    {
        this.activity = activity;

        ViewGroup view_group;

        /* Loading View */
        view_group = this.activity.abAddContentView(loading_view_id);

        this.wLoading = view_group;
        this.wLoading_Gif = (ABGifView) view_group.findViewById(
                loading_gif_view_id);
        this.wLoading_Message = (TextView) view_group.findViewById(
                loading_text_view_id);

        if (wLoading == null)
            throw new AssertionError("`loading_view_id` does not exist.");
        if (wLoading_Gif == null)
            throw new AssertionError("`loading_gif_view_id` does not exist.");
        if (wLoading_Message == null) {
            throw new AssertionError(
                    "`loading_message_text_view_id` does not exist.");
        }
        
        this.wLoading.setVisibility(View.GONE);

        /* Message View */
        view_group = this.activity.abAddContentView(message_view_id);

        this.wMessage = view_group;
        this.wMessage_Image = (ImageView)view_group.findViewById(
                message_image_view_id);
        this.wMessage_Message = (TextView)view_group.findViewById(
                message_text_view_id);

        this.message_ImageIds_Success = success_message_image_id;
        this.message_ImageIds_Failure = failure_message_image_id;

        this.wMessage.setVisibility(View.GONE);
    }

    public void startLoading(String message)
    {
        this.activity.abSoftKeyboard_Hide();
        this.activity.getActionBar().setDisplayHomeAsUpEnabled(
                this.activity.abHasParent());

        this.loading_InProgress = true;

        this.wLoading_Message.setText(message);
        this.wLoading_Gif.play();
        this.wLoading.setVisibility(View.VISIBLE);
    }

    public void finishLoading()
    {
        this.wLoading.setVisibility(View.GONE);
        this.wLoading_Gif.stop();
        this.wLoading_Message.setText("");

        this.loading_InProgress = false;

        if (!this.message_InProgress) {
            this.activity.getActionBar().setDisplayHomeAsUpEnabled(
                    this.activity.abHasParent());
            this.activity.setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    public void hideMessage()
    {
        this.wMessage.setVisibility(View.GONE);
        this.wMessage_Image.setImageResource(0);
        this.wMessage_Message.setText("");

        this.message_InProgress = false;

        if (!this.loading_InProgress) {
            this.activity.getActionBar().setDisplayHomeAsUpEnabled(
                    this.activity.abHasParent());
            this.activity.setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    public void showMessage(int image_id, String message,
            final Runnable message_close_listener)
    {
        this.activity.abSoftKeyboard_Hide();
        this.activity.getActionBar().setDisplayHomeAsUpEnabled(false);

        this.message_InProgress = true;

        this.wMessage.setVisibility(View.VISIBLE);
        this.wMessage_Image.setImageResource(image_id);
        this.wMessage_Message.setText(message);

        final Notifications self = this;
        this.wMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.hideMessage();

                if (message_close_listener != null)
                    message_close_listener.run();
            }
        });
    }

    public void showMessage_Failure(String message,
            Runnable after_messasge_listener)
    {
        this.showMessage(this.message_ImageIds_Failure, message,
                after_messasge_listener);
    }

    public void showMessage_Failure(String message)
    {
        this.showMessage_Failure(message, null);
    }

    public void showMessage_Success(String message,
            Runnable after_messasge_listener)
    {
        this.showMessage(this.message_ImageIds_Success, message,
                after_messasge_listener);
    }

    public void showMessage_Success(String message)
    {
        this.showMessage_Success(message, null);
    }

}
