package com.mereexams.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Bilal on 09-Jun-17.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkAndGo();
    }

    void checkAndGo() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                updateUI(connectedToInternet());
            }
        });
        try {
            thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
            //thread.sleep(1000);
            thread.start();
        } catch (Exception e) {
            Log.e("Thread error", e.toString());
            finish();
        }
    }

    void updateUI(final boolean connected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (connected) {
                    Log.e("Connected", "true");
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("Connected", "false");
                    showNoConnectionDialog();
                }
            }
        });
    }

    void showNoConnectionDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("No Internet Connection");
        alertDialog.setMessage("Please check your internet connection and try again");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

    boolean connectedToNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d("Network", "Connected");
            return true;
        } else {
            Log.d("Network", "Not Connected");
            return false;
        }
    }

    boolean connectedToInternet() {
        if (connectedToNetwork()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("https://www.google.co.in/").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(3000); //choose your own timeframe
                urlc.setReadTimeout(4000); //choose your own timeframe
                urlc.connect();
                Log.d("Internet", "Connected");
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.d("Internet", "Not Connected");
                return false;  //connectivity exists, but no internet.
            }
        } else {
            Log.d("Internet", "Not Connected");
            return false;
        }
    }
}
