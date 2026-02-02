package com.bbclaude.chat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
    public static final String PREFS = "bbclaude_prefs";
    public static final String KEY_API = "api_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final EditText input = (EditText) findViewById(R.id.input_api_key);
        final TextView status = (TextView) findViewById(R.id.text_status);
        Button save = (Button) findViewById(R.id.btn_save);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String current = prefs.getString(KEY_API, "");
        if (!current.isEmpty()) {
            input.setText(current);
            status.setText("API key saved");
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = input.getText().toString().trim();
                if (key.isEmpty()) {
                    Toast.makeText(SettingsActivity.this, R.string.empty_key, Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                prefs.edit().putString(KEY_API, key).apply();
                status.setText("Saved");
                Toast.makeText(SettingsActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
