package com.eaglesakura.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.eaglesakura.android.oari.ActivityResult;
import com.eaglesakura.android.oari.OnActivityResult;
import com.eaglesakura.sample.oari.R;


public class MainActivity extends AppCompatActivity {
    static final int REQUEST_REQUEST_PICK1 = 0x0001;
    static final int REQUEST_REQUEST_PICK2 = 0x0002;
    static final int REQUEST_OTHER = 0x0003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.pick_image1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_REQUEST_PICK1);
            }
        });

        findViewById(R.id.pick_image2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_REQUEST_PICK1);
            }
        });

        findViewById(R.id.other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_OTHER);
            }
        });
    }

    @OnActivityResult(REQUEST_REQUEST_PICK1)
    void resultPickImage1(int resultCode, Intent data) {
        Toast.makeText(this,
                String.format("resultPickImage1 code(%d)", resultCode),
                Toast.LENGTH_SHORT
        ).show();
    }

    @OnActivityResult(REQUEST_REQUEST_PICK2)
    void resultPickImage2(int resultCode, Intent data) {
        Toast.makeText(this,
                String.format("resultPickImage2 code(%d)", resultCode),
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!ActivityResult.invoke(this, requestCode, resultCode, data)) {
            // handle failed
            Toast.makeText(this,
                    String.format("failed request(%d) result(%d)", requestCode, resultCode),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
