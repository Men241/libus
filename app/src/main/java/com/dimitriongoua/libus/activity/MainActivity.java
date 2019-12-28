package com.dimitriongoua.libus.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.dimitriongoua.libus.R;
import com.dimitriongoua.libus.adapter.LibelleListAdapter;
import com.dimitriongoua.libus.listener.RecyclerTouchListener;
import com.dimitriongoua.libus.model.Libelle;
import com.dimitriongoua.libus.util.Master;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private static final int CALL_PERMISSION_CODE = 100;
    private Master master;

    @SuppressWarnings("CanBeFinal")
    private List<Libelle> libellesList = new ArrayList<>();
    private LibelleListAdapter adapter;

    private int current_libelle = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        master = new Master();

        RecyclerView rv_libelles = findViewById(R.id.rv_libelles);

        adapter = new LibelleListAdapter(libellesList);
        rv_libelles.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rv_libelles.setItemAnimator(new DefaultItemAnimator());
        rv_libelles.setAdapter(adapter);
        rv_libelles.addOnItemTouchListener(new RecyclerTouchListener(this, rv_libelles, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (isSmsPermissionGranted()) {
                    Libelle libelle = libellesList.get(position);
                    executeUSSD(libelle);
                } else {
                    current_libelle = position;
                    requestSendSmsPermission();
                }
            }

            @Override
            public void onLongClick(View view, final int position) {
                showContextMenu(libellesList.get(position));
            }
        }));

        refreshLibelles();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (current_libelle != -1) {
                    Libelle libelle = libellesList.get(current_libelle);
                    executeUSSD(libelle);
                    current_libelle = -1;
                }
            }
        }
    }

    public void execute(View view) {
        if (view.getId() == R.id.iv_add) {
            showSaveDialog();
        }
    }

    private void showSaveDialog() {
        showSaveDialog(null);
    }

    private void showSaveDialog(final Libelle libToSave) {

        View viewInflated = LayoutInflater.from(MainActivity.this).inflate(R.layout.form_add_command, null);
        final TextInputEditText tiet_libelle = viewInflated.findViewById(R.id.tiet_libelle);
        final TextInputEditText tiet_ussd = viewInflated.findViewById(R.id.tiet_ussd);

        String title = "Ajouter un libellé";
        String actionText = "Créer";
        if (libToSave != null) {
            title = "Modifier le libellé";
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
                        Libelle libelle;
                        if (libToSave == null) {
                            libelle = realm.createObject(Libelle.class);
                            libelle.setCreated(master.getCurrentDate());
                        } else libelle = libToSave;
                        libelle.setLibelle(lib);
                        libelle.setUssd(ussd);
                        realm.commitTransaction();

                        refreshLibelles();
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void refreshLibelles() {
        libellesList.clear();
        libellesList.addAll(master.getLibelles());
        adapter.notifyDataSetChanged();

    }

    private void executeUSSD(Libelle libelle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            return;

        String ussd = libelle.getUssd();

        if (ussd.contains("*")) {
            if (ussd.endsWith("*")) {
                ussd = ussd.substring(0, ussd.length() - 1);
            }
            if (!ussd.startsWith("*")) {
                ussd = "*" + ussd;
            }
            if (!ussd.endsWith("#")) ussd += "#";
        }

        libelle.newActivation();
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode(ussd))));
    }

    private void showConfirmation(final Libelle libelle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setMessage("Voullez-vous retirer le libellé \"" + libelle.getLibelle() + "\" ?")
                .setPositiveButton("Oui, Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {
                                libelle.deleteFromRealm();
                                refreshLibelles();
                            }
                        });
                    }
                })
                .setNegativeButton("Non", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showContextMenu(final Libelle libelle) {
        View viewInflated = LayoutInflater.from(MainActivity.this).inflate(R.layout.context_menu, null);
        final TextView tv_edit = viewInflated.findViewById(R.id.tv_edit);
        final TextView tv_delete = viewInflated.findViewById(R.id.tv_delete);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(viewInflated);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                showSaveDialog(libelle);
            }
        });
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                showConfirmation(libelle);
            }
        });

    }

    private boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSendSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CODE);
    }
}
