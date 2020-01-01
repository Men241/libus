package com.dimitriongoua.libus.app;

import android.app.Application;
import android.content.pm.PackageInfo;

import com.dimitriongoua.libus.migration.LibusMigration;
import com.dimitriongoua.libus.util.Master;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Libus extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        final RealmConfiguration config = new RealmConfiguration.Builder()
                .name("libus.realm")
                .schemaVersion(Master.getCurrentVersion(this))
                .migration(new LibusMigration())
                .build();

        Realm.setDefaultConfiguration(config);
        Realm.getInstance(config);
    }

    @Override
    public void onTerminate() {
        Realm.getDefaultInstance().close();
        super.onTerminate();
    }
}