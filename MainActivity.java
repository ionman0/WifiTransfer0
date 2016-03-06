package com.example.warrencheung.wifitransfer;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    protected static final int CHOOSE_FILE_RESULT_CODE = 20;

    Button getConnect;
    Button findImg;
    Button clientButton;
    Button ssh;
    ZoomableImageView Img;

    public static String TAG = "myTag";
    public static String address = "";
    public static String filePath;

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    public static final String IP_SERVER = "192.168.49.1";
    public static int PORT = 8888;
    private static boolean server_running = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findImg = (Button) findViewById(R.id.button2);
        getConnect = (Button) findViewById(R.id.button1);
        clientButton = (Button) findViewById(R.id.clientButton);
        ssh = (Button) findViewById(R.id.ssh);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            address = b.getString("address");
            //filePath = b.getString("filePath");
        }

        findImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //textInfo.setText("");
                try {
                    sendImage();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        getConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Connect.class));
            }
        });

        clientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
            }
        });

        ssh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Ssh.class));
            }
        });
    }

    private void sendImage() throws ExecutionException, InterruptedException {
        String filePath;

        FileRequest fileRequest = new FileRequest(MainActivity.this);
        filePath = fileRequest.getPicture(MainActivity.this);

        Img = (ZoomableImageView) findViewById(R.id.imageView);
        Img.setImageBitmap(BitmapFactory.decodeFile(filePath));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        String localIP = Utils.getLocalIPAddress();
        // Trick to find the ip in the file /proc/net/arp
        //device.deviceAddress = address;
        String client_mac_fixed = new String(address).replace("99", "19");
        String clientIP = Utils.getIPFromMac(client_mac_fixed);


        // User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.
        Uri uri = data.getData();
        TextView statusText = (TextView) findViewById(R.id.textView);
        statusText.setText("Sending: " + uri);
        Log.d(TAG, "Intent----------- " + uri);
        Intent serviceIntent = new Intent(this, FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());

        if (localIP.equals(IP_SERVER)) {
            serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, clientIP);
        } else {
            serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, IP_SERVER);
        }

        serviceIntent.putExtra(FileTransferService.EXTRAS_PORT, PORT);
        startService(serviceIntent);
    }
}
