package com.example.cssetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.cssetest.Retrofit.IMyService;
import com.example.cssetest.Retrofit.RetrofitClient;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView txt_create_account;
    MaterialEditText edt__login_email,edt_login_password;
    Button btn_login;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);

        edt__login_email = (MaterialEditText)findViewById(R.id.edit_email);
        edt_login_password = (MaterialEditText) findViewById(R.id.edit_password);
        btn_login = (Button) findViewById(R.id.btn_login);



        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               loginUser(edt__login_email.getText().toString(),
                        edt_login_password.getText().toString());
            }


        });


        txt_create_account =(TextView)findViewById(R.id.txt_create_account);
        txt_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View register_layout = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.register_layout,null);
                new MaterialStyledDialog.Builder(MainActivity.this)
                        .setIcon(R.drawable.ic_email)
                        .setTitle("Register")
                        .setDescription("Please fill all fields")
                        .setCustomView(register_layout)
                        .setNegativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveText("Confirm")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                MaterialEditText edt_register_email = (MaterialEditText)register_layout.findViewById(R.id.edit_email);
                                MaterialEditText edt_register_name = (MaterialEditText)register_layout.findViewById(R.id.edit_name);
                                MaterialEditText edt_register_password = (MaterialEditText)register_layout.findViewById(R.id.edit_password );

                                if(TextUtils.isEmpty(edt_register_email.getText().toString())){
                                    Toast.makeText(MainActivity.this,"Email Can not be null", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(TextUtils.isEmpty(edt_register_name.getText().toString())){
                                    Toast.makeText(MainActivity.this,"Name Can not be null", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(TextUtils.isEmpty(edt_register_password.getText().toString())){
                                    Toast.makeText(MainActivity.this,"Password Can not be null", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                registerUser(edt_register_name.getText().toString(),edt_register_email.getText().toString(),edt_register_password.getText().toString());


                            }
                        }).show();

            }
        });

    }










    private void registerUser(String name, String email, String password) {
        compositeDisposable.add(iMyService.registerUser(name,email,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(MainActivity.this," "+response, Toast.LENGTH_SHORT).show();

                    }
                }));

    }

    private void loginUser(String email, String password) {
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Email Can not be null", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Password Can not be null", Toast.LENGTH_SHORT).show();
            return;
        }


        compositeDisposable.add(iMyService.loginUser(email,password)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String response) throws Exception {
                Toast.makeText(MainActivity.this," "+response, Toast.LENGTH_SHORT).show();

            }
        }));
    }
}
