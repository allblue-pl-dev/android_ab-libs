package pl.allblue.json.fields;

import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.allblue.json.JSONField;

public class IntJSON extends JSONField<Integer>
{

    private Integer value = null;

    public IntJSON(String name)
    {
        super(name);
    }


    /* JSONField Overrides */
    @Override
    protected boolean compareValue(Integer value)
    {
        Integer this_Value = this.getValue();

        if (this_Value == null)
            return value == null;

        return this.getValue().compareTo(value) == 0;
    }

    @Override
    protected Integer readValue(JSONArray json_array, int index) throws JSONException
    {
        return json_array.getInt(index);
    }

    @Override
    protected Integer readValue(JSONObject json_object) throws JSONException
    {
        return json_object.getInt(this.getName());
    }

    @Override
    protected void writeValue(JSONArray json_array, int index, Integer value)
            throws JSONException
    {
        json_array.put(index, (int)value);
    }

    @Override
    protected void writeValue(JSONObject json_object, Integer value) throws JSONException
    {
        json_object.put(this.getName(), (int)value);
    }
    
}
