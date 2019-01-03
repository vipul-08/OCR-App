package com.scanlibrary;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

/**
 * Created by jhansi on 29/03/15.
 */
public class ResultFragment extends Fragment {

    private static ProgressDialogFragment progressDialogFragment;
    private View view;
    private ImageView scannedImageView;
    private Button doneButton;
    private Bitmap original;
    private Bitmap transformed;
    private ImageButton rotateLeftBtn;
    private ImageButton rotateRightBtn;


    public ResultFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.result_layout, null);
        init();
        return view;
    }

    private void init() {
        scannedImageView = (ImageView) view.findViewById(R.id.scannedImage);
        Bitmap bitmap = getBitmap();
        setScannedImage(bitmap);
        doneButton = view.findViewById(R.id.doneButton);
        rotateLeftBtn = view.findViewById(R.id.rotateLeft);
        rotateRightBtn = view.findViewById(R.id.rotateRight);

        rotateLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap temp = original;
                Matrix matrix = new Matrix();
                matrix.postRotate(-90);
                Bitmap rotatedBitmap = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true);
                original = rotatedBitmap;
                setScannedImage(original);
            }
        });

        rotateRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap temp = original;
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotatedBitmap = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true);
                original = rotatedBitmap;
                setScannedImage(original);
            }
        });

        doneButton.setOnClickListener(new DoneButtonClickListener());
    }

    private Bitmap getBitmap() {
        Uri uri = getUri();
        try {
            original = Utils.getBitmap(getActivity(), uri);
            getActivity().getContentResolver().delete(uri, null, null);
            return original;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri getUri() {
        Uri uri = getArguments().getParcelable(ScanConstants.SCANNED_RESULT);
        return uri;
    }

    public void setScannedImage(Bitmap scannedImage) {
        scannedImageView.setImageBitmap(scannedImage);
    }

    protected synchronized void showProgressDialog(String message) {
        if (progressDialogFragment != null && progressDialogFragment.isVisible()) {
            // Before creating another loading dialog, close all opened loading dialogs (if any)
            progressDialogFragment.dismissAllowingStateLoss();
        }
        progressDialogFragment = null;
        progressDialogFragment = new ProgressDialogFragment(message);
        FragmentManager fm = getFragmentManager();
        progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    protected synchronized void dismissDialog() {
        progressDialogFragment.dismissAllowingStateLoss();
    }

    private class DoneButtonClickListener implements View.OnClickListener {

        public static final String URL = " https://63763b82.ngrok.io";

        @Override
        public void onClick(View v) {
            showProgressDialog(getResources().getString(R.string.loading));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap bitmap = original;

                        Log.d(TAG, "run: Bitmap: " + bitmap);
                        Uri uri = Utils.getUri(getActivity(), bitmap);
                        Log.d(TAG, "run: URI: " + uri);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 1920, 1080, true);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                        byte[] byteArray = stream.toByteArray();
                        bitmap.recycle();

                        uploadData(Base64.encodeToString(byteArray, Base64.DEFAULT), getType(), uri);
                        //uploadImage(byteArray, uri);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void uploadData(String base64Data, String type, final Uri uri) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("image", base64Data);
                jsonObject.accumulate("type", type);
                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl("http://b30cca56.ngrok.io")
                        .addConverterFactory(GsonConverterFactory.create());
                Retrofit retrofit = builder.build();

                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

                NewClient newClient = retrofit.create(NewClient.class);
                Call<Response> call = newClient.sendData(body);
                call.enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        try {
                            if (response.isSuccessful()) {
                                Toast.makeText(getActivity(), "Successfull", Toast.LENGTH_LONG).show();
                                Response returnObj = response.body();
                                Log.d("JsonReturn", returnObj.getStatus() + " " + returnObj.getFields().toString());
                                Intent data = new Intent();
                                data.putExtra("uri", uri);
                                data.putExtra("fields", returnObj.getFields().toString());
                                data.putExtra("type", getType());
                                data.putExtra("name",returnObj.getName());
                                getActivity().setResult(Activity.RESULT_OK, data);
                                dismissDialog();
                                getActivity().finish();
                            } else {
                                dismissDialog();
                                //Toast.makeText(getActivity(), "Not completely Successful", Toast.LENGTH_LONG).show();
                                Snackbar snackbar = Snackbar.make(getView(),"Failed!!! Due to high network traffic.",1000);
                                snackbar.show();
                                Log.d(TAG, "instance initializer: Not completely successful!!");
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: Try catch error:");
                            //Toast.makeText(getActivity(), "y catch error", Toast.LENGTH_LONG).show();
                            Snackbar snackbar = Snackbar.make(getView(),"Failed!!! Due to high network traffic.",1000);
                            snackbar.show();
                        }
                    }


                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        Log.d(TAG, "onFailure: Throwable: " + t);
                        Snackbar snackbar = Snackbar.make(getView(),"Failed!!! Your internet connection seems to be slow.",1000);
                        snackbar.show();
                        //Toast.makeText(getActivity(), "Failed!!! Your internet connection seems to be slow.", Toast.LENGTH_LONG).show();
                        dismissDialog();
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private String getType() {
            Log.d("Type", getArguments().getString("type"));
            return getArguments().getString("type");
        }

        private void uploadImage(byte[] imageBytes, final Uri uri) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), imageBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", "Image" /*+ timeStamp*/ + ".png", requestFile);
            Call<Response> call = retrofitInterface.uploadImage(body);
            call.enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                    try {

                        if (response.isSuccessful()) {

                            Response responseBody = response.body();
                            Log.d("Response", responseBody.getStatus() + " " + responseBody.getFields().toString());
                            Intent data = new Intent();
                            data.putExtra("uri", uri);
                            data.putExtra("fields", responseBody.getFields().toString());
                            data.putExtra("type", getType());
                            getActivity().setResult(Activity.RESULT_OK, data);
                            dismissDialog();
                            getActivity().finish();

                        } else {


                            ResponseBody errorBody = response.errorBody();
                            Gson gson = new Gson();
                            try {
                                dismissDialog();

                                Response errorResponse = gson.fromJson(errorBody.string(), Response.class);
                                Log.d("Error", errorResponse.getStatus() + "");

                            } catch (IOException e) {
                                dismissDialog();
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        dismissDialog();
                        Toast.makeText(getActivity().getBaseContext(), "Sorry, Please upload again", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onResponse: Try catch error : " + e);
                    }
                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
                    dismissDialog();
                    Toast.makeText(getActivity().getBaseContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}