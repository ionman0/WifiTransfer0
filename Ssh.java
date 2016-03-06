package com.example.warrencheung.wifitransfer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Ssh extends AppCompatActivity {

    Button connect;
    String name;
    String password;
    String host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssh);

        connect = (Button) findViewById(R.id.sshButton);

        final EditText edName = (EditText) findViewById(R.id.editText);
        final EditText edPassword = (EditText) findViewById(R.id.editText2);
        final EditText edHost = (EditText) findViewById(R.id.editText3);
        final TextView status = (TextView) findViewById(R.id.textView2);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edName.getText().toString();
                password = edPassword.getText().toString();
                host = edHost.getText().toString();
                ConnectSSH connectSSH = new ConnectSSH();
                connectSSH.getConnected(name, password, host, new CallBack(){
                    @Override
                    public void done(String returned){
                        if (returned == null) {
                        } else {
                            status.setText(returned);
                        }
                    }
                });

            }
        });
    }
}
