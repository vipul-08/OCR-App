package com.mobicule.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<NewModal> list = new ArrayList<>();
    CustomAdapter adapter;

    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Bundle bundle = getIntent().getExtras();

        String title = bundle.getString("title");


        Log.d("ListActivity", "onCreate: title:  " + title);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle(title);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab =findViewById(R.id.cameraFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, ScanActivity.class);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);
                startActivityForResult(intent, 99);
            }
        });

        recyclerView = findViewById(R.id.recyclerList);
        adapter = new CustomAdapter(ListActivity.this, list);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(ListActivity.this, LinearLayoutManager.VERTICAL, false);
        verticalLayoutManager.setReverseLayout(true);
        verticalLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(verticalLayoutManager);

        list.add(new NewModal("Pan card 1"));
        list.add(new NewModal("Pan card 2"));
        list.add(new NewModal("Pan card 3"));
        list.add(new NewModal("Pan card 4"));

        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 99) && (resultCode == Activity.RESULT_OK)) {
            String fields = data.getStringExtra("fields");
            startActivity(new Intent(ListActivity.this, EditFormActivity.class).putExtra("fields", fields));
        }
    }
}
