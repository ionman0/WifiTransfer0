package com.example.warrencheung.wifitransfer;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

/**
 * Created by WarrenCheung on 5/12/2015.
 */
public class FileRequest {
    ProgressDialog progressDialog;


    public static final String IP_SERVER = "192.168.49.1";
    public static int PORT = 8888;
    private static boolean server_running = false;

    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    //ProgressDialog progressDialog = null;
    public FileRequest(Context context) {
        progressDialog = new ProgressDialog(context);
    }

    public String getPicture(Context context) throws ExecutionException, InterruptedException {
        return new ServerAsyncTask(context).execute().get();
        // server_running = true;
    }

    public static class ServerAsyncTask extends AsyncTask<Void, Void, String> {

        private final Context context;
        //private final TextView statusText;

        /**
         * @param context

         */
        public ServerAsyncTask(Context context) {
            this.context = context;
            //this.statusText = (TextView) statusText;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(PORT);
                Log.d(MainActivity.TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.d(MainActivity.TAG, "Server: connection done");
                final File f = new File(Environment.getExternalStorageDirectory() + "/"
                        + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                        + ".jpg");

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();

                Log.d(MainActivity.TAG, "server: copying files " + f.toString());
                InputStream inputstream = client.getInputStream();
                copyFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
                server_running = false;
                return f.getAbsolutePath();
            } catch (IOException e) {
                Log.e(MainActivity.TAG, e.getMessage());
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                /* Request picture browser
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + result), "image*//*");
                context.startActivity(intent);*/

                /*Intent i = new Intent(context, FullscreenActivity.class);
                i.putExtra("filePath", "file://" + result);
                context.startActivity(i);*/
            }

        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(MainActivity.TAG, e.toString());
            return false;
        }
        return true;
    }
}

