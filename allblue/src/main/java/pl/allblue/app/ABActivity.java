package pl.allblue.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ABActivity extends Activity
{

    static public final String Extras_IsNew = ABApp.Path +
            "ABActivity.Extras_IsNew";
    static public final String Extras_HasParent = ABApp.Path +
            "ABActivity.Extras_HasParent";


    /* Parent */
    private boolean parent = false;

    /* Layout */
    private String layout_Name = null;
    private int layout_Id = 0;

    private ViewGroup content = null;

    /* Soft Keyboard */
    private boolean softKeyboard_Visible = false;

    /* Widgets */
    boolean widgets_InitializeOnCreate = false;

    public ABActivity(int activity_layout_id)
    {
        super();

        this.layout_Id = activity_layout_id;
    }

    public ViewGroup abAddContentView(int layout_id)
    {
        Context context = this.getApplicationContext();

        ViewGroup view_group = new FrameLayout(context);
        View.inflate(context, layout_id, view_group);
        ViewGroup.LayoutParams layout_params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        this.addContentView(view_group, layout_params);

        return view_group;
    }

    public String abGetLayoutName()
    {
        return this.layout_Name;
    }

    public boolean abHasParent()
    {
        return this.parent;
    }

    public void abSoftKeyboard_Hide()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.content.getWindowToken(), 0);
    }

    public void abStartActivity(Intent intent)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        this.finish();
    }

    public void abStartChildActivity(Intent intent,
            Integer for_result_request_code)
    {
        intent.putExtra(ABActivity.Extras_IsNew, true);
        intent.putExtra(ABActivity.Extras_HasParent, true);

        if (for_result_request_code == null)
            this.startActivity(intent);
        else
            this.startActivityForResult(intent, for_result_request_code);
    }

    public void abStartChildActivity(Intent intent)
    {
        this.abStartChildActivity(intent, null);
    }

    public void abStartChildActivity_ForResult(Intent intent,
            int for_result_request_code)
    {
        this.abStartChildActivity(intent, for_result_request_code);
    }

    protected boolean abDispatchTouchEvent(MotionEvent event)
    {
        return false;
    }

    protected void abExtras_Load(Bundle extras)
    {

    }

    protected void abExtras_Save(Bundle extras)
    {

    }

    protected void abOnCreate(Bundle saved_instance_state)
    {

    }

    protected void abWidgets_Initialize()
    {
        Class<?> ab_activity_class = this.getClass();
        while (true) {
            Field[] fields = null;
            try {
                fields = ab_activity_class.getDeclaredFields();
            } catch (Exception e) {
                break;
            }
            for (Field field : fields) {
                try {
                    if (!Modifier.isPublic(field.getModifiers()))
                        continue;

                    if (field.getName().length() < 2)
                        continue;

                    if (field.getName().charAt(0) != 'w')
                        continue;

                    if (!Character.isUpperCase(field.getName().charAt(1)))
                        continue;

                    if (!View.class.isAssignableFrom(field.getType()))
                        continue;

                    Log.d("ABActivity", "Getting: " + this.layout_Name + "_" +
                            field.getName().substring(1));

                    String view_name = this.layout_Name + "_" +
                            field.getName().substring(1);
                    int view_id = this.getResources().getIdentifier(view_name,
                            "id", this.getPackageName());

                    if (view_id == 0)
                        throw new AssertionError("Cannot find widget: " + view_name);

                    View view = this.findViewById(view_id);

                    Log.d("ABActivity", view.toString());
                    field.set(this, view);
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
            }

            ab_activity_class = ab_activity_class.getSuperclass();
            if (ab_activity_class == ABActivity.class ||
                    ab_activity_class == null)
                break;
        }
    }

    protected void abWidgets_SetInitializeOnCreate(
            boolean initialize_widgets_on_create)
    {
        this.widgets_InitializeOnCreate = initialize_widgets_on_create;
    }


    /* Activity Overrides */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            InputMethodManager imm = (InputMethodManager)this.getSystemService(
                    Activity.INPUT_METHOD_SERVICE);

            if (imm.hideSoftInputFromWindow(this.content.getWindowToken(), 0))
                return true;
        }

        if (this.abDispatchTouchEvent(event))
            return true;

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();

        if (item_id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle out_state)
    {
        out_state.putBoolean(ABActivity.Extras_IsNew, false);
        out_state.putBoolean(ABActivity.Extras_HasParent, this.parent);

        this.abExtras_Save(out_state);

        super.onSaveInstanceState(out_state);
    }

    @Override
    public void setContentView(int layout_id)
    {
        super.setContentView(layout_id);
        this.content = (ViewGroup)this.findViewById(android.R.id.content);

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onCreate(Bundle saved_instance_state)
    {
        super.onCreate(saved_instance_state);

        /* Layout */
        this.layout_Name = this.getApplicationContext().getResources()
                .getResourceEntryName(this.layout_Id);
        this.setContentView(this.layout_Id);

        /* Widgets */
        if (this.widgets_InitializeOnCreate)
            this.abWidgets_Initialize();

        /* OnCreate */
        this.abOnCreate(saved_instance_state);

        /* Extras */
        Bundle extras = saved_instance_state;

        if (extras == null)
            extras = this.getIntent().getExtras();
        if (extras == null)
            extras = new Bundle();

        /* AB Extras */
        this.parent = extras.getBoolean(ABActivity.Extras_HasParent, false);
        this.getActionBar().setDisplayHomeAsUpEnabled(this.parent);

        /* Other Extras */
        this.abExtras_Load(extras);
    }

}
