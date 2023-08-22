package com.example.blogapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blogapp.fragments.HomeFragment;
import com.example.blogapp.models.Post;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {
    private int position = 0, id = 0;
    private EditText textDesc;
    private Button btnSave;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        init();
    }

    private void init() {
        sharedPreferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        textDesc = findViewById(R.id.txtDescEditPost);
        btnSave = findViewById(R.id.btnEdit);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        position = getIntent().getIntExtra("position", 0);
        id = getIntent().getIntExtra("postId", 0);
        textDesc.setText(getIntent().getStringExtra("text"));

        btnSave.setOnClickListener(v-> {
            if (!textDesc.getText().toString().isEmpty()) {
                savePost();
            }
        });
    }

    private void cancelEdit(View view) {
        super.onBackPressed();
    }

    private void savePost() {
        dialog.setMessage("Saving...");
        dialog.show();

        StringRequest request =new StringRequest(Request.Method.POST, Constant.UPDATE_POST, response -> {
            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    // update the post in recyclerView
                    Post post = HomeFragment.arrayList.get(position);
                    post.setDesc(textDesc.getText().toString());
                    HomeFragment.arrayList.set(position, post);
                    HomeFragment.recyclerView.getAdapter().notifyItemChanged(position);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        }) {
            // Add token to header

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", id + "");
                map.put("desc", textDesc.getText().toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditPostActivity.this);
        queue.add(request);
    }
}