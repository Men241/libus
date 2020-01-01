package com.dimitriongoua.libus.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.crashlytics.android.Crashlytics;
import com.dimitriongoua.libus.model.LibusButton;

import io.realm.Realm;
import io.realm.Sort;

public class Master {


    public Master() {

    }

    public List<LibusButton> getLibelles(Context context) {

        Realm realm = Realm.getDefaultInstance();

        List<LibusButton> buttons = new ArrayList<>(realm.where(LibusButton.class).findAll().sort("created", Sort.DESCENDING));

        if (buttons.size() == 0) {
            SessionManager session = new SessionManager(context);
            if (session.isFirstLaunch()) {
                realm.beginTransaction();
                realm.copyToRealm(new LibusButton("Acheter 300 F de crédit de communication via Airtel Money", "*150*1*1*300#"));
                realm.copyToRealm(new LibusButton("Appeler ma personne", "066123456"));
                realm.copyToRealm(new LibusButton("Consuluter mon solde Libertis", "#111#"));
                realm.copyToRealm(new LibusButton("Acheter des d'unités EDAN", "*150*7*1*1*1*2#"));
                realm.copyToRealm(new LibusButton("Consulter mon solde Airtel", "*137#"));
                realm.copyToRealm(new LibusButton("Activer un forfait internet Airtel de 325 Mo à 1.000 F valable 3 jours", "*111*1*1*1*1#"));
                realm.commitTransaction();
                session.setFirstLaunch();
                return new ArrayList<>(realm.where(LibusButton.class).findAll().sort("created", Sort.DESCENDING));
            }
        }

        return buttons;
    }

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.FRENCH);
        Date date = new Date();
        return dateFormat.format(date);

    }

    public static int getCurrentVersion(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Crashlytics.logException(e);
        }
         return pInfo.versionCode;
    }
}
