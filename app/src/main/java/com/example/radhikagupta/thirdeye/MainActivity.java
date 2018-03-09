package com.example.radhikagupta.thirdeye;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private Button btnLogout, btnalert;
    private TextView tv,tv1,tv2,tv3;
    EditText et;
    private Session session;
    LocationManager locationManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        session = new Session(this);
        if (!session.loggedin()) {
            logout();
        }
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnalert = (Button) findViewById(R.id.button);
        et = (EditText) findViewById(R.id.editText);
        tv=(TextView)findViewById(R.id.textView);
        tv1=(TextView)findViewById(R.id.textView2);
        tv2=(TextView)findViewById(R.id.textView3);
        tv3=(TextView)findViewById(R.id.textView5);
        et.setText("");
        String filename = "details.txt";
        if(savedInstanceState==null) {
            try {
                String email = extras.getString("email");
                String phone1 = extras.getString("phone1");
                String phone2 = extras.getString("phone2");
                String phone3 = extras.getString("phone3");


                //File file = new File(this.getFilesDir(), filename);
                OutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                String rawDetails = email + "\n" + phone1 + "\n" + phone2 + "\n" + phone3 + "\n";
                outputStream.write(rawDetails.getBytes());
                outputStream.close();

                tv.setText(email);
                tv1.setText(phone1);
                tv2.setText(phone2);
                tv3.setText(phone3);
            } catch (Exception e) {
                //extra string not available.Getting form file.
                try {
                    FileInputStream fileInputStream = openFileInput(filename);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
                    String email = br.readLine();
                    String phone1 = br.readLine();
                    String phone2 = br.readLine();
                    String phone3 = br.readLine();
                    tv.setText(email);
                    tv1.setText(phone1);
                    tv2.setText(phone2);
                    tv3.setText(phone3);
//                    Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
                } catch (IOException ex) {
                    //do something.
                }
            }
        }else
        {
            String savedemail=savedInstanceState.getString("mail");
            tv.setText(savedemail);
            String savedcon1=savedInstanceState.getString("p1");
            tv1.setText(savedcon1);
            String savedcon2=savedInstanceState.getString("p2");
            tv2.setText(savedcon2);
            String savedcon3=savedInstanceState.getString("p3");
            tv3.setText(savedcon3);
        }
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        btnalert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("mail",tv.getText().toString());
        savedInstanceState.putString("p1",tv1.getText().toString());
        savedInstanceState.putString("p2",tv2.getText().toString());
        savedInstanceState.putString("p3",tv3.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    private void logout(){
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
    }
    private void alert() {
        getLocation();
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                sendsms();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //
            }
        });

    }
    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            et.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude() + "\n"+addresses.get(0).getAddressLine(0));
            et.append("I AM AT THE ABOVE LOCATION,PLEASE HELP ! I NEED YOUR IMMEDIATE HELP");
        }catch(Exception e)
        {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
    public void sendsms()
    {

        String no1 =tv1.getText().toString();
        String no2=tv2.getText().toString();
        String no3=tv3.getText().toString();
        String msg = et.getText().toString();
        SmsManager s = SmsManager.getDefault();
        s.sendTextMessage(no1, null, msg, null, null);
        s.sendTextMessage(no2,null,msg,null,null);
        s.sendTextMessage(no3,null,msg,null,null);
        finish();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.finishAffinity(MainActivity.this);


                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}