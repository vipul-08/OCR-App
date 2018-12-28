package com.mobicule.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    ArrayList<ProofModal> list;
    GridView gridView;


    int[] images = {
            R.drawable.bag, R.drawable.pan, R.drawable.ada, R.drawable.chk, R.drawable.dri, R.drawable.vot
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("DocumentScanner");

        gridView = findViewById(R.id.gridview);
        list = new ArrayList<>();
        list.add(new ProofModal("PAN Card"));
        list.add(new ProofModal("Aadhar Card"));
        list.add(new ProofModal("Driving Licence"));
        list.add(new ProofModal("Voting Card"));
        list.add(new ProofModal("Passport"));
        list.add(new ProofModal("Bank Cheque"));


        HomeActivity.CustomAdapter customAdapter = new HomeActivity.CustomAdapter();
        gridView.setAdapter(customAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomeActivity.this, ListActivity.class);
                String title = list.get(position).getProofName();
                Log.d("HomeActivity", "onItemClick: " + title);
                //Create the bundle
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });



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
            View v1 = getLayoutInflater().inflate(R.layout.single_element, null);
            TextView name = v1.findViewById(R.id.name);
            ImageView image = v1.findViewById(R.id.image);

            name.setText(list.get(position).getProofName());
            image.setImageResource(images[position]);

            return v1;
        }
    }
}
