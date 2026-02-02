package com.bbclaude.chat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends Activity {
    private ChatDbHelper db;
    private MessageAdapter adapter;
    private ListView listView;
    private EditText input;
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = new ChatDbHelper(this);
        listView = (ListView) findViewById(R.id.list_messages);
        input = (EditText) findViewById(R.id.input_message);
        send = (Button) findViewById(R.id.btn_send);

        adapter = new MessageAdapter(this, new ArrayList<Message>());
        listView.setAdapter(adapter);
        adapter.setAll(db.getAllMessages());
        scrollToBottom();

        send.setEnabled(false);
        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                send.setEnabled(s.toString().trim().length() > 0);
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        Button settings = (Button) findViewById(R.id.btn_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this, SettingsActivity.class));
            }
        });
    }

    private void sendMessage() {
        String text = input.getText().toString().trim();
        if (text.isEmpty()) {
            return;
        }

        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS, MODE_PRIVATE);
        String apiKey = prefs.getString(SettingsActivity.KEY_API, "");
        if (apiKey.isEmpty()) {
            Toast.makeText(this, R.string.no_key, Toast.LENGTH_SHORT).show();
            return;
        }

        input.setText("");
        long ts = System.currentTimeMillis();
        long id = db.insertMessage("user", text, ts);
        adapter.add(new Message(id, "user", text, ts));
        scrollToBottom();

        new SendTask(apiKey, text).execute();
    }

    private void scrollToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                if (adapter.getCount() > 0) {
                    listView.setSelection(adapter.getCount() - 1);
                }
            }
        });
    }

    private class SendTask extends AsyncTask<Void, Void, String> {
        private final String apiKey;
        private final String userText;
        private String error;

        SendTask(String apiKey, String userText) {
            this.apiKey = apiKey;
            this.userText = userText;
        }

        @Override
        protected void onPreExecute() {
            send.setEnabled(false);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                List<Message> recent = db.getRecentMessages(10);
                Collections.reverse(recent);

                JSONArray messages = new JSONArray();
                for (int i = 0; i < recent.size(); i++) {
                    Message msg = recent.get(i);
                    JSONObject m = new JSONObject();
                    m.put("role", msg.role);
                    m.put("content", msg.content);
                    messages.put(m);
                }

                JSONObject payload = new JSONObject();
                payload.put("model", "claude-3-haiku-20240307");
                payload.put("max_tokens", 256);
                payload.put("temperature", 0.7);
                payload.put("messages", messages);

                URL url = new URL("https://api.anthropic.com/v1/messages");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(30000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("x-api-key", apiKey);
                conn.setRequestProperty("anthropic-version", "2023-06-01");

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                writer.write(payload.toString());
                writer.flush();
                writer.close();

                int code = conn.getResponseCode();
                InputStream stream = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                if (code < 200 || code >= 300) {
                    error = "HTTP " + code + ": " + sb.toString();
                    return null;
                }

                JSONObject response = new JSONObject(sb.toString());
                JSONArray contentArr = response.getJSONArray("content");
                if (contentArr.length() > 0) {
                    JSONObject first = contentArr.getJSONObject(0);
                    return first.optString("text", "");
                }
                return "";
            } catch (Exception e) {
                error = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String assistantText) {
            send.setEnabled(true);

            if (assistantText == null) {
                Toast.makeText(ChatActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                return;
            }

            long ts = System.currentTimeMillis();
            long id = db.insertMessage("assistant", assistantText, ts);
            adapter.add(new Message(id, "assistant", assistantText, ts));
            scrollToBottom();
        }
    }
}
