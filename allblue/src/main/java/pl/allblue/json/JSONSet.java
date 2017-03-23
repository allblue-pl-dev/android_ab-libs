package pl.allblue.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JSONSet
{

    private State state = State.None;
    private List<JSONField> fields = new ArrayList<>();

    public JSONSet()
    {

    }

    public void delete()
    {
        this.state = State.Deleted;
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
        JSONArray json = new JSONArray();

        for (JSONField field : this.fields) {
            int index = field_names.indexOf(field.getName());
            if (index == -1)
                continue;

            field.write(json, index, false);
        }

        return json;
    }

    public JSONObject getJSONObject(boolean updated_values) throws JSONException
    {
        JSONObject json = new JSONObject();

        for (JSONField field : this.fields) {
            if (!field.isSet(updated_values))
                continue;

            field.write(json, updated_values);
        }

        return json;
    }

    public JSONObject getJSONObject() throws JSONException
    {
        return this.getJSONObject(false);
    }

    public boolean isDeleted()
    {
        return this.state == State.Deleted;
    }

    public boolean isUpdated()
    {
        return this.state == State.Updated;
    }

    public boolean isNew()
    {
        return this.state == State.New;
    }

    public boolean isState(State state)
    {
        if (state == State.Deleted)
            return this.isDeleted();
        else if (state == state.Updated)
            return this.isUpdated();
        else if (state == State.New)
            return this.isNew();

        return false;
    }

    public void read(List<String> field_names, JSONArray json_array)
            throws JSONException
    {
        for (JSONField field : this.fields) {
            int index = field_names.indexOf(field.getName());
            if (index == -1) {
                throw new AssertionError("Field `" + field.getName() +
                        "` does not exist.");
//                Log.w("JSONSet", "Cannot find field `" + field.getName() +
//                        "` in: " + Arrays.toString(field_names.toArray()));
                // continue;
            }

            field.read(json_array, index);
        }
    }

    public void read(JSONObject json_object)
            throws JSONException
    {
        for (JSONField field : this.fields) {
            if (json_object.has(field.getName()))
                field.read(json_object);
        }
    }

    public State removeState_Deleted()
    {
        if (this.state != State.Deleted)
            return this.state;

        State state = State.None;

        for (int i = 0; i < this.fields.size(); i++) {
            JSONField field = this.fields.get(i);
            if (field.isSet(true)) {
                state = State.Updated;
                break;
            }
        }
        this.state = state;

        return this.state;
    }

    public State removeState_Updated()
    {
        if (this.state != State.Updated)
            return this.state;

        for (int i = 0; i < this.fields.size(); i++) {
            JSONField field = this.fields.get(i);
            field.unsetValue(true);
        }

        this.state = State.None;

        return this.state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public void setState_Updated()
    {
        this.state = State.Updated;
    }

    public void setState_New()
    {
        this.state = State.New;
    }

    public void update(JSONSet update_set)
    {
        List<JSONField> update_fields = update_set.getFields();
        for (int i = 0; i < update_fields.size(); i++) {
            JSONField update_field = update_fields.get(i);

            if (update_field.isSet()) {
                this.getField(update_field.getName()).setValue(
                        update_field.getValue(), true);
            }
        }
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


    public enum State
    {

        None,
        New,
        Updated,
        Deleted

    }

}
