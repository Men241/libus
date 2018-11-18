package co.dimitriongoua.libus.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import co.dimitriongoua.libus.R;
import co.dimitriongoua.libus.util.SessionManager;

import static co.dimitriongoua.libus.config.Constants.USSD_NUMBER;

public class MainActivity extends AppCompatActivity{

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    100);
        }
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
        final EditText et_libelle = viewInflated.findViewById(R.id.et_libelle);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Ajouter un libellé")
                .setView(viewInflated)
                .setCancelable(false)
                .setPositiveButton("Créer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, et_libelle.getText().toString(), Toast.LENGTH_SHORT).show();
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
}
