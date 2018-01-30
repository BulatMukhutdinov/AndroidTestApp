package mera.com.testapp;

import android.app.Application;

public class TestApplication extends Application {

    public static TestApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}