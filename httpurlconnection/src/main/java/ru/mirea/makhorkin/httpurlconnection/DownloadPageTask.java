package ru.mirea.makhorkin.httpurlconnection;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadPageTask extends AsyncTask<String, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.binding.textView.setText("Загружаем...");
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            return downloadIpInfo(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject responseJson = new JSONObject(result);

            MainActivity.binding.textView.setText(result);
            if(MainActivity.status == 1){
                MainActivity.binding.textView.setText(responseJson.getString("ip"));
                MainActivity.binding.textViewCity.setText(responseJson.getString("city"));
                MainActivity.binding.textViewRegion.setText(responseJson.getString("region"));
                MainActivity.binding.textViewCountry.setText(responseJson.getString("country"));
                MainActivity.binding.textViewLocation.setText(responseJson.getString("loc"));

            }
            if(MainActivity.status == 2){
                MainActivity.binding.textView.setText(responseJson.getString("current_weather"));
            }

        } catch (JSONException e) {
            Log.d(getClass().getSimpleName(), "error");
            e.printStackTrace();
        }

    }

    private String downloadIpInfo(String address) throws IOException {
        InputStream inputStream = null;
        String data = "";
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int read = 0;
                while ((read = inputStream.read()) != -1) {
                    bos.write(read);
                }
                bos.close();
                data = bos.toString();

            } else {
                data = connection.getResponseMessage() + ". Error Code: " + responseCode;
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return data;
    }
}
