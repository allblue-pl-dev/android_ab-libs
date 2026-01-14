package pl.allblue.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.appcompat.widget.AppCompatButton;

import pl.allblue.R;

/**
 * Created by SfTd on 28/04/2015.
 */
public class ABButton extends AppCompatButton implements View.OnClickListener
{

    private int animation = -1;
    private OnClickListener listeners_OnClick = null;

    public ABButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        this.addAttributes(context, attrs);

        super.setOnClickListener(this);
    }

    @Override
    public void setOnClickListener(OnClickListener listener)
    {
        this.listeners_OnClick = listener;
    }

    @Override
    public void onClick(View view)
    {
        if (this.animation > 0)
            view.startAnimation(AnimationUtils.loadAnimation(this.getContext(), this.animation));
        if (this.listeners_OnClick != null)
            this.listeners_OnClick.onClick(this);
    }

    private void addAttributes(Context context, AttributeSet attrs)
    {
        TypedArray typed_array = context.obtainStyledAttributes(attrs, R.styleable.ABButton);

        final int indexes_length = typed_array.getIndexCount();
        for (int i = 0; i < indexes_length; ++i) {
            int attr = typed_array.getIndex(i);
            if (attr == R.styleable.ABButton_ab_animation)
                this.animation = typed_array.getResourceId(attr, 0);
        }

        typed_array.recycle();
    }

}
