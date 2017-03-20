package pl.allblue.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by SfTd on 21/07/2015.
 */

public class ABStaticListView<CLASS> extends ListView
{

    private int itemsCount_Old = 0;

    public ABStaticListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    /* ListView Overrides */
    @Override
    protected void onDraw(Canvas canvas)
    {
        if (this.getCount() != this.itemsCount_Old)
        {
            int height = getChildAt(0).getHeight() + 1;

            this.itemsCount_Old = this.getCount();

            ViewGroup.LayoutParams layout_params = this.getLayoutParams();
            layout_params.height = this.getCount() * height;
            this.setLayoutParams(layout_params);
        }

        super.onDraw(canvas);
    }
    /* / ListView Overrides */

}
