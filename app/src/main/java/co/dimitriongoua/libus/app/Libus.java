package co.dimitriongoua.libus.app;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Libus extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        //RealmConfiguration config = new RealmConfiguration.Builder().name("libus.db").build();
        //Realm.setDefaultConfiguration(config);
    }
}
