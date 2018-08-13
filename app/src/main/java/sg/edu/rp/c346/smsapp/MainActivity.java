package sg.edu.rp.c346.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etTo;
    EditText etContent;
    Button btnSend;
    Button btnMsg;

    BroadcastReceiver br = new MessageReceived();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTo = findViewById(R.id.editTextTo);
        etContent = findViewById(R.id.editTextContent);
        btnSend = findViewById(R.id.buttonSend);
        btnMsg = findViewById(R.id.buttonMsg);

        checkPermission();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br,filter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = etTo.getText().toString();

                String[] split = num.split(",");
                for(int i = 0; i < split.length;i++){
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(split[i].trim(), null, etContent.getText().toString(), null, null);
                }
                etContent.setText(null);
                Toast toast = Toast.makeText(getBaseContext(),"Message sent",Toast.LENGTH_LONG);
                toast.show();
            }
        });

        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sendSMS();

               etContent.setText(null);
            }
        });
    }

    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }

    private void sendSMS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
        {
            Uri smsTo = Uri.parse("smsto:" + etTo.getText().toString());
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, smsTo);
            sendIntent.putExtra("sms_body", etContent.getText().toString());
            startActivity(sendIntent);

        }
        else // For early versions, do what worked for you before.
        {
            Uri smsUri = Uri.parse("smsto:" + etTo.getText().toString());
            Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
            intent.setType("vnd.android-dir/mms-sms");
            intent.putExtra("sms_body", etContent.getText().toString());

            startActivity(intent);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }
}
