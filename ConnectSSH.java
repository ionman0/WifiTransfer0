package com.example.warrencheung.wifitransfer;

import android.os.AsyncTask;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

/**
 * Created by WarrenCheung on 1/3/2016.
 */
public class ConnectSSH {

    public void getConnected(String name, String password, String host, CallBack callBack){
        new SSHConnect(name, password, host, callBack).execute();
    }

    public class SSHConnect extends AsyncTask<Void, Void, String>{
        String name;
        String pw;
        String host;
        CallBack callBack;

        public SSHConnect(String name, String password, String host, CallBack callBack){
            this.name = name;
            this.pw = password;
            this.host = host;
            this.callBack = callBack;
        }

    protected String doInBackground(Void... params) {
        String returned = null;
        try {

                JSch jsch = new JSch();
                Session session = jsch.getSession(name, host, 22);
                session.setPassword(pw);

                // Avoid asking for key confirmation
                Properties prop = new Properties();
                prop.put("StrictHostKeyChecking", "no");
                session.setConfig(prop);

                session.connect();

                // SSH Channel
                ChannelExec channelssh = (ChannelExec) session.openChannel("exec");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                channelssh.setOutputStream(baos);

                // Execute command
                channelssh.setCommand("ls");
                channelssh.connect();
                channelssh.disconnect();

                returned = baos.toString();


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return returned;
    }
        @Override
        protected void onPostExecute(String returned){
            callBack.done(returned);
            super.onPostExecute(returned);
        }

}
}
