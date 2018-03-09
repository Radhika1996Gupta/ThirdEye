package com.example.radhikagupta.thirdeye;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private Button reg;
    private TextView tvLogin;
    private EditText etEmail, etPass,et1,et2,et3;
    private DbHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DbHelper(this);
        reg = (Button)findViewById(R.id.btnReg);
        tvLogin = (TextView)findViewById(R.id.tvLogin);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPass = (EditText)findViewById(R.id.etPass);
        et1=(EditText)findViewById(R.id.etnum1);
        et2=(EditText)findViewById(R.id.etnum2);
        et3=(EditText)findViewById(R.id.etnum3);
        reg.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnReg:
                register();
                break;
            case R.id.tvLogin:
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
                break;
            default:

        }
    }

    private void register(){
        User user=new User();
        user.email = etEmail.getText().toString();
        user.password = etPass.getText().toString();
        user.phone1=et1.getText().toString();
        user.phone2=et2.getText().toString();
        user.phone3=et3.getText().toString();
        if(user.email.isEmpty() && user.password.isEmpty() && user.phone1.isEmpty()&&user.phone2.isEmpty()&&user.phone3.isEmpty()){
            displayToast("Mandatory to fill all fields");
        }else if (!isValidEmail(user.email)) {
            displayToast("Invalid Email");
        }
        else if(!isValidPhone(user.phone1) && !isValidPhone(user.phone2) && !isValidPhone(user.phone3)) {
            displayToast("Invalid Phone Number");
        }
        else if (user.phone1.equals(user.phone2) || user.phone1.equals(user.phone3)||user.phone3.equals(user.phone2)){
            displayToast("Two or more Contact numbers can't be same");
        }
        else{
            db.addUser(user);
            displayToast("User registered");
            finish();
        }
    }

    private void displayToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public boolean isValidEmail(String target) {
        return (Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public boolean isValidPhone(String target) {
        if (target.length() >= 10 && target.length() <= 12) {
            return  Patterns.PHONE.matcher(target).matches();
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.finishAffinity(RegisterActivity.this);


                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}