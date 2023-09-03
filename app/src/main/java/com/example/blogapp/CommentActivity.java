package com.example.blogapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blogapp.adapters.CommentsAdapter;
import com.example.blogapp.fragments.HomeFragment;
import com.example.blogapp.models.Comment;
import com.example.blogapp.models.Post;
import com.example.blogapp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Comment> list;
    private CommentsAdapter commentsAdapter;
    private EditText txtAddComment;
    private int postId = 0;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    public static int postPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        init();
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        postPosition = getIntent().getIntExtra("postPosition", -1);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.recyclerComments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        txtAddComment = findViewById(R.id.txtAddComment);

        postId = getIntent().getIntExtra("postId", 0);

        getComments();
    }

    private void getComments() {
        list = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.COMMENTS, response -> {
            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    JSONArray comments = new JSONArray(object.getString("comments"));

                    for (int i = 0; i < comments.length(); i++) {
                        JSONObject comment = comments.getJSONObject(i);
                        JSONObject user = comment.getJSONObject("user");

                        User mUser = new User();
                        mUser.setId(user.getInt("id"));
                        mUser.setPhoto(Constant.URL + "storage/profiles/" +user.getString("photo"));
                        mUser.setUsername(user.getString("name") + " " + user.getString("lastname"));

                        Comment mComment = new Comment();
                        mComment.setId(comment.getInt("id"));
                        mComment.setUser(mUser);
                        mComment.setDate(comment.getString("created_at"));
                        mComment.setComment(comment.getString("comment"));
                        list.add(mComment);
                    }
                }

                commentsAdapter = new CommentsAdapter(this, list);
                recyclerView.setAdapter(commentsAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        }) {
            // add token to header

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
                map.put("id", postId + "");
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(CommentActivity.this);
        queue.add(request);
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

    public void addComment(View view) {
        String commentText = txtAddComment.getText().toString();

        progressDialog.setMessage("Adding Comment...");
        progressDialog.show();
        if (commentText.length() > 0) {
            StringRequest request = new StringRequest(Request.Method.POST, Constant.CREATE_COMMENT, res-> {

                try {
                    JSONObject object = new JSONObject(res);

                    if (object.getBoolean("success")) {
                        JSONObject comment = object.getJSONObject("comment");
                        JSONObject user = object.getJSONObject("user");

                        Comment c = new Comment();
                        User u = new User();

                        u.setId(user.getInt("id"));
                        u.setUsername(user.getString("name") + " " + user.getString("lastname"));
                        u.setPhoto(Constant.URL + "storage/profiles/" + user.getString("photo"));

                        c.setUser(u);
                        c.setId(comment.getInt("id"));
                        c.setDate(comment.getString("created_at"));
                        c.setComment(comment.getString("comment"));

                        Post post = HomeFragment.arrayList.get(postPosition);
                        post.setComments(post.getComments() + 1);
                        HomeFragment.arrayList.set(postPosition, post);
                        HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();

                        list.add(c);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        txtAddComment.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();

            }, error -> {
                error.printStackTrace();
                progressDialog.dismiss();
            }) {
                // add token to header

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
                    map.put("id", postId + "");
                    map.put("comment", commentText);
                    return map;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(CommentActivity.this);
            queue.add(request);
        }
    }
}