package com.mobicule.myapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class LauncherActivity extends AppCompatActivity {

    ArrayList<ProofModal> list;
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        gridView = findViewById(R.id.gridview);
        list = new ArrayList<>();
        list.add(new ProofModal("PAN Card","https://upload.wikimedia.org/wikipedia/commons/3/31/A_sample_of_Permanent_Account_Number_%28PAN%29_Card.jpg"));
        list.add(new ProofModal("Aadhar Card","https://assets1.cleartax-cdn.com/s/img/2018/04/05172018/Aadhaar-card-sample-300x212.png"));
        list.add(new ProofModal("PAN Card","https://upload.wikimedia.org/wikipedia/commons/3/31/A_sample_of_Permanent_Account_Number_%28PAN%29_Card.jpg"));
        list.add(new ProofModal("Aadhar Card","https://assets1.cleartax-cdn.com/s/img/2018/04/05172018/Aadhaar-card-sample-300x212.png"));
        list.add(new ProofModal("PAN Card","https://upload.wikimedia.org/wikipedia/commons/3/31/A_sample_of_Permanent_Account_Number_%28PAN%29_Card.jpg"));
        list.add(new ProofModal("Aadhar Card","https://assets1.cleartax-cdn.com/s/img/2018/04/05172018/Aadhaar-card-sample-300x212.png"));
        list.add(new ProofModal("PAN Card","https://upload.wikimedia.org/wikipedia/commons/3/31/A_sample_of_Permanent_Account_Number_%28PAN%29_Card.jpg"));

        CustomAdapter customAdapter = new CustomAdapter();
        gridView.setAdapter(customAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(LauncherActivity.this, ScanActivity.class);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);
                startActivityForResult(intent,99);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 99) && (resultCode == Activity.RESULT_OK) ) {
            String fields = data.getStringExtra("fields");
            startActivity(new Intent(LauncherActivity.this,EditFormActivity.class).putExtra("fields",fields));
        }
    }

    private class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v1 = getLayoutInflater().inflate(R.layout.single_element,null);
            TextView name = v1.findViewById(R.id.name);
            ImageView image = v1.findViewById(R.id.image);

            name.setText(list.get(position).getProofName());
            image.setImageResource(R.drawable.ic_android_black_24dp);
            //Picasso.get().load(Uri.parse(list.get(position).getImageSrc())).into(image);

            return v1;
        }
    }

}