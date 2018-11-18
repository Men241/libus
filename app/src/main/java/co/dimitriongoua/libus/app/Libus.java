package co.dimitriongoua.libus.app;

import android.app.Application;

import io.realm.Realm;

public class Libus extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }
}
