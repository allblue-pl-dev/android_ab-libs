package pl.allblue.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class JSONSetList<SetClass extends JSONSet> extends ArrayList<SetClass>
{

    public JSONSetList()
    {
        super();
    }

    public void addAll_JSONArrays(Class<? extends JSONSet> set_class,
            List<String> field_names, JSONArray json_array, int maxLength)
            throws IllegalAccessException, InstantiationException, JSONException
    {
        int setLength = json_array.length();
        if (maxLength > -1)
            setLength = Math.min(setLength, maxLength);
        for (int i = 0; i < setLength; i++) {
            SetClass json_set = (SetClass)set_class.newInstance();
            json_set.read(field_names, json_array.getJSONArray(i));

            this.add(json_set);
        }
    }

    public void addAll_JSONArrays(Class<? extends JSONSet> set_class,
            List<String> field_names, JSONArray json_array)
            throws IllegalAccessException, InstantiationException, JSONException
    {
        this.addAll_JSONArrays(set_class, field_names, json_array, -1);
    }

    public void addAll_JSONObjects(int index, Class<? extends JSONSet> set_class,
            JSONArray json_array) throws
            IllegalAccessException, InstantiationException, JSONException
    {
        int json_array_length = json_array.length();
        for (int i = 0; i < json_array_length; i++) {
            SetClass json_set = (SetClass)set_class.newInstance();
            json_set.read(json_array.getJSONObject(i));

            this.add(index + i, json_set);
        }
    }

    public JSONArray getAll_JSONObjects(String[] id_field_names) throws JSONException
    {
        JSONArray json_array = new JSONArray();
        for (int i = 0; i < this.size(); i++) {
            JSONObject set_json = this.get(i).getJSONObject(true);
            for (int j = 0; j < id_field_names.length; j++)
                this.get(i).getField(id_field_names[j]).write(set_json, false);

            //            Log.d("JSONSetList", "Id: " + this.get(i).getField(id_field_name).getValue().toString());
            //            Log.d("JSONSetList", "Setting: " + i + "# " + set_json.toString());
            json_array.put(set_json);
        }

        return json_array;
    }

    public JSONSetList<SetClass> getByField(String field_name, Object value)
    {
        JSONSetList<SetClass> set_list = new JSONSetList();

        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).getField(field_name).isEqual(value))
                set_list.add(this.get(i));
        }

        return set_list;
    }

    public SetClass getByField_First(String field_name, Object value)
    {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).getField(field_name).isEqual(value))
                return this.get(i);
        }

        return null;
    }

    public SetClass getFirst()
    {
        if (this.size() == 0)
            return null;

        return this.get(0);
    }

    public JSONSetList<SetClass> getOrderedBy(final OrderBy<SetClass> order_by)
    {
        JSONSetList<SetClass> set_list = new JSONSetList();
        set_list.addAll(this);

        Collections.sort(set_list, new Comparator<SetClass>() {
            @Override
            public int compare(SetClass setClass, SetClass t1)
            {
                return order_by.compares(setClass, t1);
            }
        });

        return set_list;
    }

    public JSONSetList<SetClass> getWhere(Where<SetClass> where)
    {
        JSONSetList<SetClass> set_list = new JSONSetList();

        for (int i = 0; i < this.size(); i++) {
            if (where.matches(this.get(i)))
                set_list.add(this.get(i));
        }

        return set_list;
    }

    public SetClass getWhere_First(Where<SetClass> where)
    {
        JSONSetList<SetClass> set_list = new JSONSetList();

        for (int i = 0; i < this.size(); i++) {
            if (where.matches(this.get(i)))
                return this.get(i);
        }

        return null;
    }

    public void removeNew_ByField(String field_name)
    {
        Iterator<SetClass> iter = this.iterator();
        while (iter.hasNext()) {
            SetClass fields = iter.next();
            for (int i = 0; i < this.size(); i++) {
                JSONSet fields_set_a = fields;
                JSONSet fields_set_b = this.get(i);

                JSONField field_a = fields_set_a.getField(field_name);
                JSONField field_b = fields_set_b.getField(field_name);
                if (field_a == null || field_b == null)
                    continue;

                if (field_a.compareValue(field_b.getValue())) {
                    iter.remove();
                    break;
                }
            }
        }
    }

//    public void updateBy(String compare_field_name, JSONSet update_set)
//    {
//        for (int i = 0; i < this.size(); i++) {
//            JSONSet set = this.get(i);
//            JSONField field = set.getField(compare_field_name);
//            JSONField update_field = update_set.getField(compare_field_name);
//
//            if (field.isEqual(update_field.getValue()))
//                set.update(update_set);
//        }
//    }


    public interface OrderBy<SetClass extends JSONSet>
    {
        int compares(SetClass set_a, SetClass set_b);
    }

    public interface Where<SetClass extends JSONSet>
    {
        boolean matches(SetClass set);
    }

}
