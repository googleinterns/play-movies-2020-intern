package com.google.moviestvsentiments.usecase.addAccount;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.moviestvsentiments.R;

/**
 * An Activity that displays an editable text field for the creation of new accounts.
 */
public class AddAccountActivity extends AppCompatActivity {

    public static final String EXTRA_ACCOUNT_NAME = "com.google.moviestvsentiments.ACCOUNT_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        EditText editAccountName = findViewById(R.id.editAccountName);
        Button addAccountButton = findViewById(R.id.addAccountButton);
        addAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(editAccountName.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String accountName = editAccountName.getText().toString();
                    replyIntent.putExtra(EXTRA_ACCOUNT_NAME, accountName);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}