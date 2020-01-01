package com.dimitriongoua.libus.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.dimitriongoua.libus.adapter.LibusButtonListAdapter;
import com.dimitriongoua.libus.listener.RecyclerTouchListener;
import com.dimitriongoua.libus.model.LibusButton;
import com.dimitriongoua.libus.util.Master;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static com.dimitriongoua.libus.R.id;
import static com.dimitriongoua.libus.R.layout;
import static com.dimitriongoua.libus.R.string;
import static com.dimitriongoua.libus.config.Endpoints.APP_STORE_URL;
import static com.dimitriongoua.libus.config.Endpoints.CHECK_UPDATES_URL;

public class MainActivity extends AppCompatActivity {

    private static final int CALL_PERMISSION_CODE = 100;
    private Master master;

    @SuppressWarnings("CanBeFinal")
    private List<LibusButton> libusButtonList = new ArrayList<>();
    private LibusButtonListAdapter adapter;

    private int current_button = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        master = new Master();

        RecyclerView rv_libusButtons = findViewById(id.rv_libusButtons);

        adapter = new LibusButtonListAdapter(libusButtonList);
        rv_libusButtons.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rv_libusButtons.setItemAnimator(new DefaultItemAnimator());
        rv_libusButtons.setAdapter(adapter);
        rv_libusButtons.addOnItemTouchListener(new RecyclerTouchListener(this, rv_libusButtons, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                showConfirmationExecute(position);

            }

            @Override
            public void onLongClick(View view, final int position) {
                showContextMenu(libusButtonList.get(position));
            }
        }));

        refreshButtons();
        checkUpdate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (current_button != -1) {
                    LibusButton libusButton = libusButtonList.get(current_button);
                    executeUSSD(libusButton);
                    current_button = -1;
                }
            }
        }
    }

    public void execute(View view) {
        if (view.getId() == id.iv_add) {
            showSaveDialog();
        }
    }

    private void showSaveDialog() {
        showSaveDialog(null);
    }

    private void showSaveDialog(final LibusButton libToSave) {

        View viewInflated = LayoutInflater.from(MainActivity.this).inflate(layout.form_add_button, null);
        final TextInputEditText tiet_libelle = viewInflated.findViewById(id.tiet_libelle);
        final TextInputEditText tiet_ussd = viewInflated.findViewById(id.tiet_ussd);

        String title = "Ajouter un bouton";
        String actionText = "Créer";
        if (libToSave != null) {
            title = "Modifier le bouton";
            actionText = "Modifier";
            tiet_libelle.setText(libToSave.getLibelle());
            tiet_ussd.setText(libToSave.getUssd());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title)
                .setView(viewInflated)
                .setCancelable(false)
                .setPositiveButton(actionText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (tiet_libelle.getText() == null || tiet_ussd.getText() == null) return;
                        String lib = tiet_libelle.getText().toString();
                        String ussd = tiet_ussd.getText().toString();
                        if (TextUtils.isEmpty(lib) || TextUtils.isEmpty(ussd)) return;

                        // Enregistement du libellé
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        LibusButton libusButton;
                        if (libToSave == null) {
                            libusButton = realm.createObject(LibusButton.class);
                            libusButton.setCreated(master.getCurrentDate());
                        } else libusButton = libToSave;
                        libusButton.setLibelle(lib);
                        libusButton.setUssd(ussd);
                        realm.commitTransaction();

                        refreshButtons();
                    }
                })
                .setNegativeButton(getString(string.annuler), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void refreshButtons() {
        libusButtonList.clear();
        libusButtonList.addAll(master.getLibelles());
        adapter.notifyDataSetChanged();

    }

    private void executeUSSD(LibusButton libusButton) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            return;

        String ussd = libusButton.getUssd();

        if (ussd.contains("*")) {
            if (ussd.endsWith("*")) {
                ussd = ussd.substring(0, ussd.length() - 1);
            }
            if (!ussd.startsWith("*")) {
                ussd = "*" + ussd;
            }
            if (!ussd.endsWith("#")) ussd += "#";
        }

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        libusButton.newActivation();
        realm.commitTransaction();

        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode(ussd))));
    }

    private void showConfirmationRemove(final LibusButton libusButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setMessage("Voullez-vous retirer le libellé \"" + libusButton.getLibelle() + "\" ?")
                .setPositiveButton("Oui, Supprimer", (dialogInterface, i) -> {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(@NonNull Realm realm) {
                            libusButton.deleteFromRealm();
                            refreshButtons();
                        }
                    });
                })
                .setNegativeButton("Non", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showConfirmationExecute(int position) {
        LibusButton libusButton = libusButtonList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setTitle("Confirmation")
                .setMessage("Voullez-vous exécuter ce bouton ? \n\"" + libusButton.getLibelle() + "\"\n" + libusButton.getUssd())
                .setPositiveButton("Oui, Exécuter", (dialogInterface, i) -> {
                    if (isSmsPermissionGranted()) {
                        executeUSSD(libusButton);
                    } else {
                        current_button = position;
                        requestSendSmsPermission();
                    }
                })
                .setNegativeButton("Non", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setMessage("Une mise à jour de l'application est disponible.")
                .setPositiveButton("Mettre à jour", (dialogInterface, i) -> {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(realm1 -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(APP_STORE_URL))));
                })
                .setNegativeButton("Ignorer", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showContextMenu(final LibusButton libusButton) {
        View viewInflated = LayoutInflater.from(MainActivity.this).inflate(layout.context_menu, null);
        final TextView tv_edit = viewInflated.findViewById(id.tv_edit);
        final TextView tv_delete = viewInflated.findViewById(id.tv_delete);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(viewInflated);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                showSaveDialog(libusButton);
            }
        });
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                showConfirmationRemove(libusButton);
            }
        });

    }

    private boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSendSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CODE);
    }

    private void checkUpdate() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, CHECK_UPDATES_URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            long version = response.getLong("version");
                            int versionCode = Master.getCurrentVersion(MainActivity.this);
                            if (version > versionCode) showUpdateDialog();
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                        }
                    }
                }, error -> Crashlytics.logException(error));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }
}
