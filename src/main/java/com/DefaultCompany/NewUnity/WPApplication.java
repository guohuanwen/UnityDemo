package com.DefaultCompany.NewUnity;

import android.app.Application;

public class WPApplication extends Application {

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static Application getApplication() {
        return application;
    }
}
