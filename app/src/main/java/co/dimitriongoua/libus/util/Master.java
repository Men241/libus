package co.dimitriongoua.libus.util;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.dimitriongoua.libus.model.Libelle;
import io.realm.Realm;
import io.realm.Sort;

public class Master {

    private static final String TAG = Master.class.getSimpleName();

    private Context context;

    public Master(Context context) {
        this.context = context;
    }

    public List<Libelle> getLibelles() {

        Realm realm = Realm.getDefaultInstance();

        return new ArrayList<>(realm.where(Libelle.class).findAll().sort("created", Sort.DESCENDING));
    }

    public String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.FRENCH);
        Date date = new Date();
        return dateFormat.format(date);

    }
}
