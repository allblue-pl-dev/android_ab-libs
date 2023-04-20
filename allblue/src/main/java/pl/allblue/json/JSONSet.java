package pl.allblue.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JSONSet
{

    private List<JSONField> fields = new ArrayList<>();

    private List<JSONSet> sets = new ArrayList<>();

    public JSONSet()
    {

    }

    public JSONArray addToJSONArray(List<String> field_names, JSONArray json)
            throws JSONException
    {
        for (JSONField field : this.fields) {
            int index = field_names.indexOf(field.getName());
            if (index == -1)
                continue;

            field.write(json, index, false);
        }

        return json;
    }

    public void addSet(JSONSet set)
    {
        this.sets.add(set);
    }

    public JSONField getField(String field_name)
    {
        for (int i = 0; i < this.fields.size(); i++)
            if (this.fields.get(i).getName().equals(field_name))
                return this.fields.get(i);

        throw new AssertionError("Field `"  + field_name + "` does not exist.");
    }

    public List<JSONField> getFields()
    {
        return this.fields;
    }

    public JSONArray getJSONArray(List<String> field_names) throws JSONException
    {
        return this.addToJSONArray(field_names, new JSONArray());
    }

    public JSONObject getJSONObject(boolean updated_values) throws JSONException
    {
        JSONObject json = new JSONObject();
        this.setJSONObject(json, updated_values);

        return json;
    }

    public JSONObject getJSONObject() throws JSONException
    {
        return this.getJSONObject(false);
    }

    public boolean hasField(String field_name)
    {
        for (int i = 0; i < this.fields.size(); i++) {
            if (this.fields.get(i).getName().equals(field_name))
                return true;
        }

        return false;
    }

    public void read(List<String> field_names, JSONArray json_array)
            throws JSONException
    {
        for (JSONField field : this.fields) {
            int index = field_names.indexOf(field.getName());
            if (index == -1) {
//                if (BuildConfig.DEBUG) {
//                    throw new AssertionError("Field `" + field.getName() +
//                            "` does not exist.");
//                }

                Log.w("JSONSet", "Cannot find field `" + field.getName() +
                        "` in: " + Arrays.toString(field_names.toArray()));
                continue;
            }

            try {
                field.read(json_array, index);
            } catch (JSONException e) {
                Log.e("JSONSet", "Error while reading: " + json_array.toString());
                throw e;
            }
        }

        for (int i = 0; i < this.sets.size(); i++)
            this.sets.get(i).read(field_names, json_array);
    }

    public void read(JSONObject json_object)
            throws JSONException
    {
        for (JSONField field : this.fields) {
            if (json_object.has(field.getName()))
                field.read(json_object);
        }

        for (int i = 0; i < this.sets.size(); i++)
            this.sets.get(i).read(json_object);
    }

    public void setJSONObject(JSONObject json, boolean updated_values) throws JSONException
    {
        for (JSONField field : this.fields) {
            if (!field.isSet(updated_values))
                continue;

            field.write(json, updated_values);
        }

        for (int i = 0; i < this.sets.size(); i++)
            this.sets.get(i).setJSONObject(json, updated_values);
    }

    protected void initializeFields()
    {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!JSONField.class.isAssignableFrom(field.getType()))
                    continue;

                JSONField json_field = (JSONField)field.get(this);
                if (json_field == null)
                    continue;

                json_field.setJSONSet(this);
                this.fields.add(json_field);
            } catch (Exception e) {
                Log.d("ABActivity", "Field exception: " + e.getMessage());
            }
        }
    }

}
