package pl.allblue.io;

import android.content.Context;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by SfTd on 27/06/2016.
 */
public class ABFile
{

    static public boolean Delete(Context context, String file_path)
            throws FileNotFoundException
    {
        File file = new File(context.getFilesDir(), file_path);
        if (!file.exists())
            throw new FileNotFoundException();

        return file.delete();
    }

    static public String GetContent(Context context, String file_path)
            throws IOException
    {
       /* Data */
        File file = new File(context.getFilesDir(), file_path);
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            String json_string = new String(data, "UTF-8");

            return json_string;
        }

        throw new FileNotFoundException();
    }

    static public boolean Exists(Context context, String file_path)
    {
        return (new File(context.getFilesDir(), file_path)).exists();
    }

    static public void PutContent(Context context, String file_path,
            String content) throws IOException
    {
        File file = new File(context.getFilesDir(), file_path);
        FileOutputStream fos = new FileOutputStream(file);
        byte[] data_bytes = content.getBytes();
        fos.write(data_bytes);
        fos.flush();
        fos.getFD().sync();
        fos.close();
    }

}
