package pl.allblue.ab;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.allblue.app.ABApp;

public class TableIds
{

    static public final String Preferences_TableIds = ABApp.Path +
            "TableIds.TableIds";


    Context context = null;

    public TableIds(Context context)
    {
        this.context = context;
    }

    public Integer getNext(String table_alias) throws EmptyTableIdsException
    {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this.context);

        int next_id = 0;

        try {
            JSONObject json = new JSONObject(preferences.getString(
                    TableIds.Preferences_TableIds, "{\"tablesIds\":{}"));

            JSONObject tables_ids = json.getJSONObject("tablesIds");
            if (!tables_ids.has(table_alias))
                throw new AssertionError("Cannot get next table id. Table alias does not exist.");

            JSONArray ids = tables_ids.getJSONArray(table_alias);
            if (ids.length() == 0)
                throw new EmptyTableIdsException();

            next_id = ids.getInt(0);

            JSONArray new_ids = new JSONArray();
            for (int i = 1; i < ids.length(); i++)
                new_ids.put(ids.getInt(i));
            tables_ids.put(table_alias, new_ids);
            json.put("tablesIds", tables_ids);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(TableIds.Preferences_TableIds, json.toString());
            editor.commit();
        } catch (JSONException e) {
            Log.e("App", "Cannot get next table id.", e);
            throw new AssertionError("Cannot get next table id: " + e.getMessage());
        }

        return next_id;
    }

    public void set(JSONObject tables_ids)
    {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this.context);

        JSONObject json = new JSONObject();
        try {
            json.put("tablesIds", tables_ids);

            Log.d("Test", "Saved table ids: " + tables_ids.toString());
        } catch (JSONException e) {
            Log.e("TableIds", "Cannot parse table ids.", e);
            new AssertionError("Cannot parse table ids: " + e.getMessage());
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TableIds.Preferences_TableIds, json.toString());
        editor.commit();
    }


    public class EmptyTableIdsException extends Exception
    {

    }

}
