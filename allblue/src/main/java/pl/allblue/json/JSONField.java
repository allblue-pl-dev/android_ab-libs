package pl.allblue.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

abstract public class JSONField<ValueClass>
{

    private String name = null;
    private JSONSet jsonSet = null;

    Object value = null;
    private boolean value_Set = false;

    Object updatedValue = null;
    private boolean updatedValue_Set = false;

    public JSONField(String name)
    {
        this.name = name;
    }

    public boolean isSet(boolean updated_value)
    {
        if (updated_value)
            return this.updatedValue_Set;

        return this.value_Set || this.updatedValue_Set;
    }

    public boolean isSet()
    {
        return this.isSet(false);
    }

    public String getName()
    {
        return this.name;
    }

    public ValueClass getValue(boolean updated_value)
    {
        if (this.updatedValue_Set || updated_value)
            return (ValueClass)this.updatedValue;

        return (ValueClass)this.value;
    }

    public ValueClass getValue()
    {
        return this.getValue(false);
    }

    public boolean isEqual(ValueClass value)
    {
        if (!this.isSet())
            return false;

        if (this.getValue() == null)
            return value == null;

        if (value == null)
            return false;

        return this.compareValue(value);
    }

    public void read(JSONArray json_array, int index) throws JSONException
    {
        if (json_array.isNull(index)) {
            this.setValue(null);
            return;
        }

        this.setValue(this.readValue(json_array, index));
    }

    public void read(JSONObject json_object) throws JSONException
    {
        if (json_object.isNull(this.getName())) {
            this.setValue(null);
            return;
        }

        this.setValue(this.readValue(json_object));
    }

    public void setJSONSet(JSONSet json_set)
    {
        this.jsonSet = json_set;
    }

    public void setValue(ValueClass value, boolean update)
    {
        if (this.jsonSet == null)
            throw new AssertionError("`JSONSet` not set.");

        /* No update or modifying new set. */
        if (!update || this.jsonSet.isNew()) {
            this.value = value;
            this.value_Set = true;

            return;
        }

        if (this.isEqual(value))
            return;

        /* Update */
        this.updatedValue = value;
        this.updatedValue_Set = true;

        this.jsonSet.setState_Updated();

        return;
    }

    public void setValue(ValueClass value)
    {
        this.setValue(value, false);
    }

    public void unsetValue(boolean updated)
    {
        if (!updated) {
            this.value = null;
            this.value_Set = false;
        } else {
            this.updatedValue = null;
            this.updatedValue_Set = false;
        }
    }

    public void write(JSONArray json_array, int index, boolean updated_value)
            throws JSONException
    {
        ValueClass value = this.getValue(updated_value);

        if (value == null) {
            json_array.put(index, JSONObject.NULL);
            return;
        }

        this.writeValue(json_array, index, value);
    }

    public void write(JSONObject json_object, boolean updated_value) throws JSONException
    {
        ValueClass value = this.getValue(updated_value);

        if (value == null) {
            json_object.put(this.getName(), JSONObject.NULL);
            return;
        }

        this.writeValue(json_object, value);
    }

    abstract protected boolean compareValue(ValueClass value);

    abstract protected ValueClass readValue(JSONArray json_array, int index)
            throws JSONException;
    abstract protected ValueClass readValue(JSONObject json_object)
            throws JSONException;

    abstract protected void writeValue(JSONArray json_array, int index,
            ValueClass value) throws JSONException;
    abstract protected void writeValue(JSONObject json_object,
            ValueClass value) throws JSONException;

}
