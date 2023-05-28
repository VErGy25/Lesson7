package ru.mirea.makhorkin.httpurlconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import ru.mirea.makhorkin.httpurlconnection.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    static ActivityMainBinding binding;
    public static String[] weather_params = {"temperature_2m_max_member01", "temperature_2m_max_member02"};
    public static int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 1;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = null;
                if (connectivityManager != null) {
                    networkInfo = connectivityManager.getActiveNetworkInfo();
                }
                if (networkInfo != null && networkInfo.isConnected()) {
                    DownloadPageTask downloadPageTask = new DownloadPageTask();
                    downloadPageTask.execute("https://ipinfo.io/json"); // запуск нового потока

                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Нет интернета", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        binding.buttonWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 2;
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = null;
                if (connectivityManager != null) {
                    networkInfo = connectivityManager.getActiveNetworkInfo();
                }

                if (networkInfo != null && networkInfo.isConnected()) {
                    DownloadPageTask downloadPageTask = new DownloadPageTask();
                    String url =  "https://api.open-meteo.com/v1/forecast?latitude=55.88&longitude=37.55&current_weather=true";
                    downloadPageTask.execute(url); // запуск нового потока

                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Нет интернета", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}