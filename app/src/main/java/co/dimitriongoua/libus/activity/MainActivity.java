package co.dimitriongoua.libus.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.dimitriongoua.libus.R;
import co.dimitriongoua.libus.adapter.LibelleListAdapter;
import co.dimitriongoua.libus.listener.RecyclerTouchListener;
import co.dimitriongoua.libus.model.Libelle;
import co.dimitriongoua.libus.util.Master;
import co.dimitriongoua.libus.util.SessionManager;
import io.realm.Realm;

import static co.dimitriongoua.libus.config.Constants.USSD_NUMBER;

public class MainActivity extends AppCompatActivity{

    private SessionManager session;
    private Master master;

    private List<Libelle> libelleLeftList = new ArrayList<>();
    private List<Libelle> libelleRightList = new ArrayList<>();
    private LibelleListAdapter adapterLeft;
    private LibelleListAdapter adapterRight;

    private RecyclerView rv_left_col;
    private RecyclerView rv_right_col;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);
        master  = new Master(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    100);
        }
        rv_left_col  = findViewById(R.id.rv_left_col);
        rv_right_col = findViewById(R.id.rv_right_col);


        adapterRight = new LibelleListAdapter(libelleRightList);
        RecyclerView.LayoutManager rightLayoutManager = new LinearLayoutManager(this);
        rv_right_col.setLayoutManager(rightLayoutManager);
        rv_right_col.setItemAnimator(new DefaultItemAnimator());
        rv_right_col.setAdapter(adapterRight);
        rv_right_col.addOnItemTouchListener(new RecyclerTouchListener(this, rv_right_col, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Libelle libelle = libelleRightList.get(position);
                executeUSSD(libelle.getUssd());
            }

            @Override
            public void onLongClick(View view, final int position) {
                showContextMenu(libelleRightList.get(position));
            }
        }));

        adapterLeft = new LibelleListAdapter(libelleLeftList);
        RecyclerView.LayoutManager leftLayoutManager = new LinearLayoutManager(this);
        rv_left_col.setLayoutManager(leftLayoutManager);
        rv_left_col.setItemAnimator(new DefaultItemAnimator());
        rv_left_col.setAdapter(adapterLeft);
        rv_left_col.addOnItemTouchListener(new RecyclerTouchListener(this, rv_left_col, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Libelle libelle = libelleLeftList.get(position);
                executeUSSD(libelle.getUssd());
            }

            @Override
            public void onLongClick(View view, final int position) {
                showContextMenu(libelleLeftList.get(position));
            }
        }));

        refreshLibelles();
    }

    public void execute(View view) {
        String data;
        switch (view.getId()){
            case R.id.tv_compose:
                data = "4-1-E1A1532-1000";
                break;
            case R.id.tv_forfait:
                data = "3-3-1-2-1";
                break;
            case R.id.tv_solde:
                data = "6-1";
                break;
            case R.id.tv_credit:
                data = "1-1-100";
                break;
            case R.id.iv_settings:
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                return;
            case R.id.iv_add:
                showAddDialog();
                return;
            default:
                return;
        }
        if (data.trim().length() == 0) return;
        session.setUSSDData(data);
        session.setSleeping(false);
        String ussdCode = "*" + USSD_NUMBER + Uri.encode("#");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussdCode)));
    }


    public void showAddDialog() {

        View viewInflated = LayoutInflater.from(MainActivity.this).inflate(R.layout.form_add_command, null);
        final TextInputEditText tiet_libelle = viewInflated.findViewById(R.id.tiet_libelle);
        final TextInputEditText tiet_ussd    = viewInflated.findViewById(R.id.tiet_ussd);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Ajouter un libellé")
                .setView(viewInflated)
                .setCancelable(false)
                .setPositiveButton("Créer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (tiet_libelle.getText() == null || tiet_ussd.getText() == null) return;
                        String lib = tiet_libelle.getText().toString();
                        String ussd    = tiet_ussd.getText().toString();
                        if (TextUtils.isEmpty(lib) || TextUtils.isEmpty(ussd)) return;

                        // Enregistement du libellé
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        Libelle libelle = realm.createObject(Libelle.class);
                        libelle.setLibelle(lib);
                        libelle.setUssd(ussd);
                        libelle.setCreated(master.getCurrentDate());
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

    private void refreshLibelles(){
        libelleLeftList.clear();
        libelleRightList.clear();
        List<Libelle> libelles = master.getLibelles();
        for (int i = 0; i < libelles.size(); i++) {
            if (i%2 == 0) libelleLeftList.add(libelles.get(i));
            else libelleRightList.add(libelles.get(i));
        }
        adapterLeft.notifyDataSetChanged();
        adapterRight.notifyDataSetChanged();
    }

    private void executeUSSD(String ussd){
        String data = "";
        String number;
        ussd = ussd.replace("*", "-");
        String suite = ussd.substring(1);
        if (suite.contains("-")) {
            String[] commande = suite.split("-");
            data   = suite.substring(suite.indexOf("-")).replace("#", "");
            if (data.startsWith("-")) data = data.substring(1);
            number = commande[0];
        } else {
            number = suite.replace("#", "");
            number = number.replace("-", "");
        }
        if (data.trim().length() == 0) {
            String ussdCode = "*" + number + Uri.encode("#");
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussdCode)));
            return;
        }
        session.setUSSDData(data);
        session.setSleeping(false);
        String ussdCode = "*" + number + Uri.encode("#");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussdCode)));
    }

    public void showConfirmation(final Libelle libelle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setMessage("Voullez-vous retirer le libellé \"" + libelle.getLibelle() + "\" ?")
                .setPositiveButton("Oui, Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
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

    public void showContextMenu(final Libelle libelle) {
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
                Toast.makeText(MainActivity.this, "Modification...", Toast.LENGTH_SHORT).show();
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
}
