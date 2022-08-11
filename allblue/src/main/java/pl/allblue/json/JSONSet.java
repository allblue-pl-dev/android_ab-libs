package pl.allblue.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.allblue.BuildConfig;

public class JSONSet
{

    private State state = State.None;
    private List<JSONField> fields = new ArrayList<>();

    private List<JSONSet> sets = new ArrayList<>();

    public JSONSet()
    {

    }

    public void addSet(JSONSet set)
    {
        this.sets.add(set);
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

    public boolean isDeleted()
    {
        return this.isState(State.Deleted);
    }

    public boolean isUpdated()
    {
        return this.isState(State.Updated);
    }

    public boolean isNew()
    {
        return this.isState(State.New);
    }

    public boolean isState(State state)
    {
        for (int i = 0; i < this.sets.size(); i++) {
            if (this.sets.get(i).isState(state))
                return true;
        }

        if (state == State.Deleted)
            return this.state == State.Deleted;
        else if (state == state.Updated)
            return this.state == State.Updated;
        else if (state == State.New)
            return this.state == State.New;

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

    public State removeState_Deleted()
    {
        for (int i = 0; i < this.sets.size(); i++)
            this.sets.get(i).removeState_Deleted();

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
        for (int i = 0; i < this.sets.size(); i++)
            this.sets.get(i).removeState_Updated();

        if (this.state != State.Updated)
            return this.state;

        for (int i = 0; i < this.fields.size(); i++) {
            JSONField field = this.fields.get(i);
            field.unsetValue(true);
        }

        this.state = State.None;

        return this.state;
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

    public void setState(State state)
    {
        for (int i = 0; i < this.sets.size(); i++)
            this.sets.get(i).setState(state);

        this.state = state;
    }

    public void setState_Updated()
    {
        this.setState(State.Updated);
    }

    public void setState_New()
    {
        this.setState(State.New);
    }

    public void update(JSONSet update_set)
    {
        List<JSONField> update_fields = update_set.getFields();
        for (int i = 0; i < update_fields.size(); i++) {
            JSONField update_field = update_fields.get(i);

            if (update_field.isSet()) {
                String update_field_name = update_field.getName();
                JSONField field = null;

                if (this.hasField(update_field_name))
                    field = this.getField(update_field_name);
                else {
                    for (int j = 0; j < this.sets.size(); j++) {
                        if (this.sets.get(j).hasField(update_field_name)) {
                            field = this.sets.get(j).getField(update_field_name);
                            break;
                        }
                    }
                }

                if (field == null) {
                    throw new AssertionError("Field `" + update_field.getName() +
                            "` does not exists in set of subsets.");
                }

                field.setValue(update_field.getValue(), true);
            }
        }

        for (int i = 0; i < update_set.sets.size(); i++)
            this.update(update_set.sets.get(i));
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
