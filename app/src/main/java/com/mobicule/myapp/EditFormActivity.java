package com.mobicule.myapp;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditFormActivity extends AppCompatActivity {

    Button saveBtn;
    LinearLayout formData;
    int count = 0 ;
    ArrayList<EditText> editTexts;
    ArrayList<String> keys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_form);
        editTexts = new ArrayList<>();
        keys = new ArrayList<>();

        saveBtn = findViewById(R.id.saveBtn);
        formData = findViewById(R.id.formData);



        try {
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("fields"));
            for(int i = 0; i<jsonObject.names().length(); i++){
                Log.d("Fields", "key = " + jsonObject.names().getString(i) + " value = " + jsonObject.get(jsonObject.names().getString(i)));
                formData.addView(generateLayout(jsonObject.names().getString(i),jsonObject.get(jsonObject.names().getString(i)).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                for(int i = 0 ; i < editTexts.size() ; i++) {
                    EditText fld = editTexts.get(i);
                    try {
                        jsonObject.accumulate(keys.get(i),fld.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("Final Json: ",jsonObject.toString()+"");
                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl("http://33fb6f11.ngrok.io")
                        .addConverterFactory(GsonConverterFactory.create());
                Retrofit retrofit = builder.build();
                ProofClient proofClient = retrofit.create(ProofClient.class);
                Call<JSONObject> call = proofClient.insertData(jsonObject);
                call.enqueue(new Callback<JSONObject>() {
                    @Override
                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                        Toast.makeText(EditFormActivity.this,"All Cool",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        Toast.makeText(EditFormActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public TextInputLayout generateLayout(String title, String value) {
        TextInputLayout layout = new TextInputLayout(EditFormActivity.this);
        EditText editText = new EditText(EditFormActivity.this);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        editText.setHint(title);
        editText.setText(value);
        editText.setLayoutParams(lparams);
        editTexts.add(editText);
        keys.add(title);
        layout.addView(editText);
        return layout;
    }

}
