package com.mobicule.myapp;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class EditFormActivity extends AppCompatActivity {

    Button saveBtn;
    LinearLayout formData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_form);

        saveBtn = findViewById(R.id.saveBtn);
        formData = findViewById(R.id.formData);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formData.addView(generateLayout("title","value"));
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
        layout.addView(editText);
        return layout;
    }

}
