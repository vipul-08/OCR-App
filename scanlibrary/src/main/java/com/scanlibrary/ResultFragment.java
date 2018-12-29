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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

        public static final String URL = "http://066daf97.ngrok.io";

        @Override
        public void onClick(View v) {
            showProgressDialog(getResources().getString(R.string.loading));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent data = new Intent();
                        Bitmap bitmap = transformed;
                        if (bitmap == null) {
                            bitmap = original;
                        }
                        Log.d(TAG, "run: Bitmap: " + bitmap);
                        Uri uri = Utils.getUri(getActivity(), bitmap);

                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 1920, 1080, true);

                        //Bitmap bmp = intent.getExtras().get("data");
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                        byte[] byteArray = stream.toByteArray();
                        bitmap.recycle();

                        uploadImage(byteArray, uri);

                        /*data.putExtra(ScanConstants.SCANNED_RESULT, uri);
                        getActivity().setResult(Activity.RESULT_OK, data);
                        original.recycle();
                        System.gc();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissDialog();
                                getActivity().finish();
                            }
                        });*/

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private String getType() {
            Log.d("Type", getArguments().getString("type"));
            return getArguments().getString("type");
            //return typeDoc;
        }

        private void uploadImage(byte[] imageBytes, final Uri uri) {

            //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), imageBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", "Image" /*+ timeStamp*/ + ".png", requestFile);
            Call<Response> call = retrofitInterface.uploadImage(body);
            //mProgressBar.setVisibility(View.VISIBLE);
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
                            //original.recycle();
                            //System.gc();
                            //getActivity().runOnUiThread(new Runnable() {
                            //    @Override
                            //    public void run() {
                            dismissDialog();
                            getActivity().finish();
                            //    }
                            //});

                        } else {


                            ResponseBody errorBody = response.errorBody();
                            Gson gson = new Gson();
                            try {
                                dismissDialog();

                                Response errorResponse = gson.fromJson(errorBody.string(), Response.class);
                                Log.d("Error", errorResponse.getStatus() + "");
                                //Snackbar.make(findViewById(R.id.content), errorResponse.getMessage(),Snackbar.LENGTH_SHORT).show();

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