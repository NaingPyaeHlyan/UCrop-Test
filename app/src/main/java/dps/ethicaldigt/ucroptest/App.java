package dps.ethicaldigt.ucroptest;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

@SuppressLint("Registered")
public class App  extends Application{
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}