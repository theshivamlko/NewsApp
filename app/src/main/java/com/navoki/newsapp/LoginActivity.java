package com.navoki.newsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.username)
    EditText edtusername;

    @BindView(R.id.pass)
    EditText edtpass;

    @BindView(R.id.login)
    Button btnlogin;

    String strUser, strPass;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strPass = edtpass.getText().toString();
                strUser = edtusername.getText().toString();

                if (!TextUtils.isEmpty(strUser) && !TextUtils.isEmpty(strPass)) {
                    Task task = new Task();
                    task.execute();
                } else
                    Toast.makeText(LoginActivity.this, "Enter valid details"
                            , Toast.LENGTH_SHORT).show();
            }
        });
    }


    class Task extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String res = sendLoginData();

            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (s != null) {

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getBoolean("status")) {

                        JSONObject data = jsonObject.getJSONObject("data");
                        String name = data.getString("name");
                        Toast.makeText(LoginActivity.this, "Welcome " + name
                                , Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, NewActivity.class);
                        startActivity(intent);
                        finish();

                    } else
                        Toast.makeText(LoginActivity.this, jsonObject.getString("msg")
                                , Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private String sendLoginData() {

        try {
            URL url = new URL("https://apteral-bay.000webhostapp.com/attendanceapp" +
                    "/loginnews.php?email=" + strUser + "&pass=" + strPass);
            Log.e("URl", url.toString());

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Scanner scanner = new Scanner(httpURLConnection.getInputStream());
                scanner.useDelimiter("\\A");

                String s = scanner.next();
                Log.e("Rspsone", s + "");

                return s;

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}
