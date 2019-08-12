package pl.allblue.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import pl.allblue.api.Api;

public class HttpRequest
{

    static public final int TIMEOUT = 30000;


    static public void Post(final String uri, final Map<String, String> fields,
            final ResponseListener response_listener, final String id)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = HttpRequest.Post_Sync(uri, fields);
                response_listener.onResponseReceived(response, id);
            }
        });

        thread.start();
    }

    static public void Post(final String uri, final Map<String, String> fields,
           final ResponseListener response_listener)
    {
        HttpRequest.Post(uri, fields, response_listener, null);
    }

    static public void Post_Stream(final String uri, final Map<String, String> fields,
            final ResponseListener_Stream response_listener, final String id)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Response_Stream response = HttpRequest.Post_Stream_Sync(uri, fields);
                response_listener.onResponseReceived(response, id);
            }
        });

        thread.start();
    }

    static public Response Post_Sync(final String uri, final Map<String, String> fields)
    {
        HttpURLConnection url_connection = null;

        StringBuilder response = new StringBuilder();
        boolean success = false;
        String error_message = "";

        Reader in = null;

        try {
            URL url = new URL(uri);
            url_connection = (HttpURLConnection)url.openConnection();

            String fields_string = HttpRequest.GetFieldsString(fields);
            byte[] post_data = fields_string.getBytes("UTF-8");

            url_connection.setConnectTimeout(HttpRequest.TIMEOUT);
            url_connection.setReadTimeout(HttpRequest.TIMEOUT);
            url_connection.setDoOutput(true);
            url_connection.setInstanceFollowRedirects(false);
            url_connection.setRequestMethod("POST");
            url_connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            url_connection.setRequestProperty("charset", "utf-8");
            url_connection.setRequestProperty("Content-Length", Integer.toString(post_data.length));
            url_connection.setUseCaches(false);

            DataOutputStream dos = new DataOutputStream(url_connection.getOutputStream());
            dos.write(post_data);

            in = new BufferedReader(new InputStreamReader(url_connection.getInputStream(), "UTF-8"));
            for (int c = in.read(); c != -1; c = in.read())
                response.append((char)c);

            success = true;
        } catch (Exception e) {
            Log.e("HttpRequest", "Exception", e);

            try {
                if (url_connection != null) {
                    error_message = "JSON Exception: " + e.getMessage() +
                            ", response code: " + url_connection.getResponseCode();
                } else
                    error_message = e.getMessage();
            } catch (IOException io_e) {
                Log.e("HttpRequest", "IOException", io_e);
                error_message = "IO Exception: " + io_e.getMessage();
            }
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                // Do nothing.
            }

            if (url_connection != null)
                url_connection.disconnect();
        }

        return new Response(success, error_message, response.toString());
    }

    static public Response_Stream Post_Stream_Sync(final String uri, final Map<String,
            String> fields)
    {
        HttpURLConnection url_connection = null;

        boolean success = false;
        String error_message = "";
        InputStream stream = null;

        try {
            URL url = new URL(uri);
            url_connection = (HttpURLConnection)url.openConnection();

            String fields_string = HttpRequest.GetFieldsString(fields);
            byte[] post_data = fields_string.getBytes("UTF-8");

            url_connection.setConnectTimeout(HttpRequest.TIMEOUT);
            url_connection.setReadTimeout(HttpRequest.TIMEOUT);
            url_connection.setDoOutput(true);
            url_connection.setInstanceFollowRedirects(false);
            url_connection.setRequestMethod("POST");
            url_connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            url_connection.setRequestProperty("charset", "utf-8");
            url_connection.setRequestProperty("Content-Length", Integer.toString(post_data.length));
            url_connection.setUseCaches(false);

            DataOutputStream dos = new DataOutputStream(url_connection.getOutputStream());
            dos.write(post_data);

            success = true;
        } catch (Exception e) {
            Log.e("HttpRequest", "Exception", e);

            try {
                if (url_connection != null) {
                    error_message = "JSON Exception: " + e.getMessage() +
                            ", response code: " + url_connection.getResponseCode();
                } else
                    error_message = e.getMessage();
            } catch (IOException io_e) {
                Log.e("HttpRequest", "IOException", io_e);
                error_message = "IO Exception: " + io_e.getMessage();
            } finally {
                if (url_connection != null)
                    url_connection.disconnect();
            }
        } finally {
//            if (url_connection != null)
//                url_connection.disconnect();
        }

        return new Response_Stream(success, error_message, url_connection);
    }

    static private String GetFieldsString(Map<String, String> fields)
    {
        /* Fields String */
        String fields_string = "";
        Iterator it = fields.entrySet().iterator();
        while (it.hasNext()) {
            if (fields_string != "")
                fields_string += "&";

            Map.Entry<String, String> field = (Map.Entry<String, String>)it.next();

            if (field.getValue() == null)
                continue;

            try {
                fields_string += URLEncoder.encode(field.getKey(), "UTF-8") + "=" +
                        URLEncoder.encode(field.getValue(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                fields_string += field.getKey() + "=" + field.getValue();
            } catch (Exception e) {
                Log.d("HttpRequest", "GetFieldsString error", e);
            }

            it.remove();
        }

        return fields_string;
    }


    private HttpRequest()
    {

    }


    static public class Response extends Response_Base
    {

        private String data = null;

        public Response(boolean success, String error_message, String data)
        {
            super(success, error_message);

            this.data = data;
        }

        public String getData()
        {
            return this.data;
        }

    }


    static public class Response_Stream extends Response_Base
    {

        private HttpURLConnection urlConnection = null;

        public Response_Stream(boolean success, String error_message,
                HttpURLConnection urlConnection)
        {
            super(success, error_message);

            this.urlConnection = urlConnection;
        }

        public HttpURLConnection getURLConnection()
        {
            return this.urlConnection;
        }

    }


    static abstract public class Response_Base
    {

        private boolean success = false;
        private String errorMessage = "";

        public Response_Base(boolean success, String error_message)
        {
            this.success = success;
            this.errorMessage = error_message;
        }

        public boolean isSuccess()
        {
            return this.success;
        }

        public String getErrorMessage()
        {
            return this.errorMessage;
        }

    }


    public interface ResponseListener
    {
        void onResponseReceived(Response response, String id);
    }

    public interface ResponseListener_Stream
    {
        void onResponseReceived(Response_Stream response, String id);
    }

}
