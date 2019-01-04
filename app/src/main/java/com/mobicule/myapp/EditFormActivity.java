package com.mobicule.myapp;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.scanlibrary.ProgressDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditFormActivity extends AppCompatActivity {

    Button saveBtn;
    LinearLayout formData;
    ArrayList<EditText> editTexts;
    ArrayList<String> keys;
    Uri uri;
    String type;
    Bitmap bmp;
    String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_form);

        final Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("DocumentScanner");

        imageName = getIntent().getStringExtra("name");

        editTexts = new ArrayList<>();
        keys = new ArrayList<>();

        saveBtn = findViewById(R.id.saveBtn);
        formData = findViewById(R.id.formData);

        type = getIntent().getStringExtra("type");
        uri = getIntent().getExtras().getParcelable("uri");

        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("fields"));
            for(int i = 0; i<jsonObject.names().length(); i++){
                Log.d("Fields", "key = " + jsonObject.names().getString(i) + " value = " + jsonObject.get(jsonObject.names().getString(i)));
                formData.addView(generateLayout(String.valueOf(i),jsonObject.get(jsonObject.names().getString(i)).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog dialog = new ProgressDialog(EditFormActivity.this);
                dialog.setTitle("Uploading");
                dialog.setMessage("Please wait while we upload your data to database...");
                dialog.show();
                JSONObject jsonObject = new JSONObject();
                JSONObject finalJson = new JSONObject();
                try {
                    for(int i = 0 ; i < editTexts.size() ; i++) {
                        EditText fld = editTexts.get(i);
                        jsonObject.accumulate(String.valueOf(i),fld.getText().toString());
                    }
                    jsonObject.accumulate("name",imageName);
                    finalJson.accumulate("fields",jsonObject);
                    finalJson.accumulate("type",type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String pathD = Environment.getExternalStorageDirectory() + "/" + "DocumentScanner" + "/" + type +"/";
                File imageDir = new File(pathD, "imageDir");
                File dataDir = new File(pathD, "dataDir");

                if (!imageDir.exists() && !dataDir.exists()) {
                    if (!imageDir.mkdirs() && !dataDir.mkdirs()) {
                        Log.d("DocumentScanner", "failed to create directory");
                    }
                }

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                File imgFile = new File(imageDir, "Image" + "_" + timeStamp + ".png");
                File dataFile = new File(dataDir,"Data" + "_" + timeStamp + ".json");
                try {
                    imgFile.createNewFile();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    byte[] bmpData = bos.toByteArray();
                    FileOutputStream fos = new FileOutputStream(imgFile);
                    fos.write(bmpData);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("Final Json: ",jsonObject.toString()+"");
                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl("http://48fccd41.ngrok.io")
                        .addConverterFactory(GsonConverterFactory.create());
                Retrofit retrofit = builder.build();
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), finalJson.toString());

                ProofClient proofClient = retrofit.create(ProofClient.class);
                Call<DatabaseResponse> call = proofClient.insertData(body);
                call.enqueue(new Callback<DatabaseResponse>() {
                    @Override
                    public void onResponse(Call<DatabaseResponse> call, Response<DatabaseResponse> response) {
                        if(response.body().isStatus()) {
                            Toast.makeText(EditFormActivity.this,"Data inserted successfully",Toast.LENGTH_LONG).show();
                            dialog.dismiss();
//                            Snackbar snackbar = Snackbar.make(myToolbar.getRootView(),"Data inserted successfully",1000);
//                            snackbar.show();
                            startActivity(new Intent(EditFormActivity.this,HomeActivity.class));
                        }
                        else {

                            Toast.makeText(EditFormActivity.this,"Failure Due To : "+ response.body().getReason(),Toast.LENGTH_LONG).show();
                            dialog.dismiss();
//                            Snackbar snackbar = Snackbar.make(myToolbar.getRootView(),"Failure Due To : "+ response.body().getReason(),1000);
//                            snackbar.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DatabaseResponse> call, Throwable t) {
                        dialog.dismiss();
//                        Snackbar snackbar = Snackbar.make(myToolbar.getRootView(),"Something went wrong",1000);
//                        snackbar.show();
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
