package pl.allblue.ab;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import pl.allblue.app.ABApp;
import pl.allblue.io.ABFile;
import pl.allblue.json.JSONHelper;

public class TableIds
{

    static public final String FileNames_TableIds = ABApp.Path +
            "table-ids.json";
    /* Old */
    static public final String Preferences_TableIds = ABApp.Path +
            "TableIds.TableIds";
    /* / Old */

    Context context = null;

    public TableIds(Context context)
    {
        this.context = context;
    }

    public Integer getNext(String table_alias) throws CannotSaveTableIds,
            EmptyTableIdsException
    {
        try {
            SharedPreferences preferences = null;
            JSONObject json = null;

            if (ABFile.Exists(this.context, TableIds.FileNames_TableIds))
                json = JSONHelper.GetJSONFromFile(this.context, TableIds.FileNames_TableIds);
            else {
                preferences = PreferenceManager
                        .getDefaultSharedPreferences(this.context);

                json = new JSONObject(preferences.getString(
                        TableIds.Preferences_TableIds, "{\"tablesIds\":{}"));
            }

            int next_id = 0;

            JSONObject tables_ids = json.getJSONObject("tablesIds");
            if (!tables_ids.has(table_alias))
                throw new AssertionError("Cannot get next table id. Table alias does not exist.");

            JSONArray ids = tables_ids.getJSONArray(table_alias);
            if (ids.length() == 0)
                throw new EmptyTableIdsException();

            Log.d("Test", "Table Ids: " + ids.toString());

            next_id = ids.getInt(0);

            JSONArray new_ids = new JSONArray();
            for (int i = 1; i < ids.length(); i++)
                new_ids.put(ids.getInt(i));
            tables_ids.put(table_alias, new_ids);
            json.put("tablesIds", tables_ids);

            Log.d("Test", "Table Ids After: " + ids.toString());

            try {
                ABFile.PutContent(this.context, TableIds.FileNames_TableIds,
                        json.toString());
            } catch (IOException e) {
                throw new CannotSaveTableIds();
            }

            if (preferences != null) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(TableIds.Preferences_TableIds);
                if (!editor.commit())
                    throw new CannotSaveTableIds();
            }

            return next_id;
        } catch (IOException e) {
            Log.e("App", "Cannot get next table id from file.", e);
            throw new AssertionError("Cannot get next table id from file: " +
                    e.getMessage());
        } catch (JSONException e) {
            Log.e("App", "Cannot get next table id from json.", e);
            throw new AssertionError("Cannot get next table id from json: " +
                    e.getMessage());
        }
    }

    public void set(JSONObject tables_ids) throws CannotSaveTableIds
    {
        JSONObject json = new JSONObject();
        try {
            json.put("tablesIds", tables_ids);
        } catch (JSONException e) {
            Log.e("TableIds", "Cannot parse table ids.", e);
            throw new AssertionError("Cannot parse table ids: " + e.getMessage());
        }

        try {
            ABFile.PutContent(this.context, TableIds.FileNames_TableIds,
                    json.toString());
        } catch (IOException e) {
            throw new CannotSaveTableIds();
        }
    }


    public class CannotSaveTableIds extends Exception
    {

    }

    public class EmptyTableIdsException extends Exception
    {

    }

}
