package pl.allblue.json.fields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.allblue.json.JSONField;

public class LongJSON extends JSONField<Long>
{

    private Long value = null;

    public LongJSON(String name)
    {
        super(name);
    }


    /* JSONField Overrides */
    @Override
    protected boolean compareValue(Long value)
    {
        Long this_Value = this.getValue();

        if (this_Value == null)
            return value == null;

        return this.getValue().compareTo(value) == 0;
    }

    @Override
    protected Long readValue(JSONArray json_array, int index) throws JSONException
    {
        return json_array.getLong(index);
    }

    @Override
    protected Long readValue(JSONObject json_object) throws JSONException
    {
        return json_object.getLong(this.getName());
    }

    @Override
    protected void writeValue(JSONArray json_array, int index, Long value)
            throws JSONException
    {
        json_array.put(index, (long)value);
    }

    @Override
    protected void writeValue(JSONObject json_object, Long value)
            throws JSONException
    {
        json_object.put(this.getName(), (long)value);
    }
    
}
