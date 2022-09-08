package pl.allblue.api;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import pl.allblue.network.HttpRequest;


public class Api
{

    static public void JSON(final String uri, JSONObject fields,
            final ResponseListener response_listener, String id)
    {
        Map<String, String> post_fields = new HashMap<>();
        post_fields.put("json", fields.toString());

        HttpRequest.Post(uri, post_fields, new HttpRequest.ResponseListener() {
            @Override
            public void onResponseReceived(HttpRequest.Response response,
                    String id) {
                response_listener.onResponseReceived(Api.ParseHttpResponse(
                        uri, response), id);
            }
        }, id);
    }

    static public void JSON(final String uri, JSONObject fields,
            final ResponseListener response_listener)
    {
        Api.JSON(uri, fields, response_listener, null);
    }

    static public void JSON_Stream(final String uri, JSONObject fields,
            final ResponseListener_Stream response_listener, String id)
    {
        Map<String, String> post_fields = new HashMap<>();
        post_fields.put("json", fields.toString());

        HttpRequest.Post_Stream(uri, post_fields, new HttpRequest.ResponseListener_Stream() {
            @Override
            public void onResponseReceived(HttpRequest.Response_Stream response, String id)
            {
                response_listener.onResponseReceived(Api.ParseHttpResponse_Stream(
                        uri, response), id);
            }
        }, id);
    }

    static public void JSON_Stream(final String uri, JSONObject fields,
            final ResponseListener_Stream response_listener)
    {
        Api.JSON_Stream(uri, fields, response_listener, null);
    }

    static public Result ParseHttpResponse(String uri,
            HttpRequest.Response http_response)
    {
        if (!http_response.isSuccess()) {
            Log.d("Api", "Http EnableResult Error (" + uri + "): " +
                    http_response.getErrorMessage());

            return new Result(2,
                "Http EnableResult Error: " + http_response.getErrorMessage(),
                new JSONObject());
        }

        int result = 2;
        String message = null;
        JSONObject json = null;

        try {
            json = new JSONObject(http_response.getData());

            Log.d("Api", "Response from `" + uri + "`: " + json.toString());

            result = json.getInt("result");
            message = json.getString("message");

            if (json.has("log")) {
                Log.w("Api", "Log: " + json.getJSONArray("log").toString());
            }
        } catch (Exception e) {
            json = new JSONObject();
            result = 2;
            message = "Cannot parse json data: " + http_response.getData();
        }

        Result api_result = new Result(result, message, json);
        if (!api_result.isSuccess())
            Log.d("Api", "Failure/Error on `" + uri + "`: " + message);

        return api_result;
    }

    static public Result_Stream ParseHttpResponse_Stream(String uri,
            HttpRequest.Response_Stream http_response)
    {
        if (!http_response.isSuccess()) {
            Log.d("Api", "Http EnableResult Error (" + uri + "): " +
                    http_response.getErrorMessage());

            return new Result_Stream(2,
                    "Http EnableResult Error: " + http_response.getErrorMessage(),
                    null, null);
        }

        int result = 2;
        String message = null;
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(
                    http_response.getURLConnection().getInputStream(),
                    "UTF-8"));

//            StringBuilder response = new StringBuilder();
//            for (int c = in.read(); c != -1; c = in.read())
//                response.append((char)c);
//            Log.d("Stream Test", "Response: " + response.toString());

            String line = br.readLine();
            if (!line.equals("ABApi")) {
                result = 2;
                StringBuilder message_Builder = new StringBuilder();
                message_Builder.append(line);
                for (int c = br.read(); c != -1; c = br.read())
                    message_Builder.append((char)c);
                message = "Wrong response format: " + message_Builder.toString();
            } else {
                result = Integer.parseInt(br.readLine());

                int messageSize = Integer.parseInt(br.readLine());
                char[] messageChars = new char[messageSize];
                br.read(messageChars, 0, messageSize);
                message = new String(messageChars);
                br.readLine();
            }

//            while(true) {
//                String chunkSize_Str = in.readLine();
//                if (chunkSize_Str == null)
//                    break;
//
//                int chunkSize = Integer.parseInt(chunkSize_Str);
//                char[] chunkChars = new char[chunkSize];
//                in.read(chunkChars, 0, chunkSize);
//                in.readLine();
//            }
        } catch (Exception e) {
            Log.e("Api", "Stream Error", e);

            result = 2;
            message = "Stream Error: " + e.getMessage();

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e1) {
                    // Do nothing.
                }
            }
            br = null;
        }

        Result_Stream api_result = new Result_Stream(result, message,
                br, http_response.getURLConnection());

        if (!api_result.isSuccess())
            Log.d("Api", "Failure/Error on `" + uri + "`: " + message);

        return api_result;
    }


    static public Stream_Chunk Stream_ReadChunk(BufferedReader br)
    {
        String chunkName = null;
        char[] chunkChars = null;

        String line = "";

        try {
            chunkName = br.readLine();
            if (chunkName == null)
                return null;

            line = br.readLine();
            int chunkSize = Integer.parseInt(line);
            chunkChars = new char[chunkSize];

            br.read(chunkChars, 0, chunkSize);
            br.readLine();
        } catch (IOException e) {
            Log.e("Api", e.toString());
            return null;
        } catch (NumberFormatException e) {
            StringBuilder response = new StringBuilder();
            response.append(line);
            try {
                for (int c = br.read(); c != -1; c = br.read())
                    response.append((char)c);
            } catch (IOException ee) {
                // Do nothing.
            }
            Log.e("Api", "Wrong chunk format: " + response.toString());
            return null;
        }

        return new Stream_Chunk(chunkName, chunkChars == null ?
                null : new String(chunkChars));
    }

    static private String Stream_ReadString(BufferedReader in, int size)
    {
        StringBuilder str = new StringBuilder();

        try {
            for (int i = 0; i < size; i++)
                str.append((char)in.read());
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        return str.toString();
    }


    static public class Result extends Result_Base
    {

        private JSONObject json;

        public Result(int result, String message, JSONObject json)
        {
            super(result, message);

            this.json = json;
        }

        public JSONObject getData()
        {

            return this.json;
        }

    }


    static public class Result_Stream extends Result_Base
    {

        private BufferedReader bufferedReader = null;
        private HttpURLConnection urlConnection = null;

        public Result_Stream(int result, String message,
                BufferedReader bufferedReader,
                HttpURLConnection urlConnection)
        {
            super(result, message);

            this.bufferedReader = bufferedReader;
            this.urlConnection = urlConnection;
        }

        public BufferedReader getBufferedReader()
        {
            return this.bufferedReader;
        }

        public HttpURLConnection getURLConnection()
        {
            return this.urlConnection;
        }

        public Stream_Chunk readChunk()
        {
            return Api.Stream_ReadChunk(this.bufferedReader);
        }

    }


    static abstract public class Result_Base
    {
        private int result;
        private String message;

        public Result_Base(int result, String message)
        {
            this.result = result;
            this.message = message;
        }

        public boolean isError()
        {
            return !this.isSuccess() && !this.isFailure();
        }

        public boolean isFailure()
        {
            return this.result == 1;
        }

        public boolean isSuccess()
        {
            return this.result == 0;
        }

        public String getMessage()
        {
            return this.message;
        }

    }


    public interface ResponseListener
    {
        void onResponseReceived(Result result, String id);
    }


    public interface ResponseListener_Stream
    {
        void onResponseReceived(Result_Stream result, String id);
    }


    static public class Stream_Chunk
    {

        public String name = null;
        public String data = null;

        Stream_Chunk(String name, String data)
        {
            this.name = name;
            this.data = data;
        }

    }

}
