package com.example.blogapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blogapp.AuthActivity;
import com.example.blogapp.Constant;
import com.example.blogapp.EditUserInfoActivity;
import com.example.blogapp.HomeActivity;
import com.example.blogapp.R;
import com.example.blogapp.adapters.AccountPostAdapter;
import com.example.blogapp.models.Post;
import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {
    private View view;
    private MaterialToolbar toolbar;
    private CircleImageView imgProfile;
    private TextView txtName, txtPostsCount;
    private Button btnEditAcc;
    private RecyclerView recyclerView;
    private ArrayList<Post> arrayList;
    private SharedPreferences preferences;
    private AccountPostAdapter adapter;
    private String imgUrl = "";

    public AccountFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_account, container, false);
        init();

        return view;
    }

    private void init() {
        preferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        toolbar = view.findViewById(R.id.toolbarAccount);
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        imgProfile = view.findViewById(R.id.imgAccProfile);
        txtName = view.findViewById(R.id.txtAccName);
        txtPostsCount = view.findViewById(R.id.txtAccPostCount);
        recyclerView = view.findViewById(R.id.recyclerAcc);
        btnEditAcc = view.findViewById(R.id.btnEditAcc);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        btnEditAcc.setOnClickListener(v-> {
            Intent i = new Intent(((HomeActivity)getContext()), EditUserInfoActivity.class);
            i.putExtra("imgUrl", imgUrl);
            startActivity(i);
        });
    }

    private void getData() {
        arrayList = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET, Constant.MY_POST, response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    JSONArray posts = object.getJSONArray("posts");

                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject p = posts.getJSONObject(i);

                        Post post = new Post();
                        post.setPhoto(Constant.URL + "storage/posts/" + p.getString("photo"));

                        arrayList.add(post);
                    }

                    JSONObject user = object.getJSONObject("user");
                    txtName.setText(user.getString("name") + " " + user.getString("lastname"));
                    txtPostsCount.setText(arrayList.size() + "");
                    Picasso.get().load(Constant.URL + "storage/profiles/" + user.getString("photo")).into(imgProfile);

                    adapter = new AccountPostAdapter(getContext(), arrayList);
                    recyclerView.setAdapter(adapter);

                    imgUrl = Constant.URL + "storage/profiles/" + user.getString("photo");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }) {
            // add token to header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_account, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemLogout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Do you want to logout ?");
            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    logout();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
         StringRequest request = new StringRequest(Request.Method.GET, Constant.LOGOUT, response -> {

             try {
                 JSONObject object = new JSONObject(response);

                 if (object.getBoolean("success")) {
                     SharedPreferences.Editor editor = preferences.edit();
                     editor.clear();
                     editor.apply();
                     startActivity(new Intent(((HomeActivity)getContext()), AuthActivity.class));
                     ((HomeActivity)getContext()).finish();
                 }

             } catch (JSONException e) {
                 e.printStackTrace();
             }

         }, error -> {
            error.printStackTrace();
         }) {
            // add token to header

             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 String token = preferences.getString("token", "");
                 HashMap<String, String> map = new HashMap<>();
                 map.put("Authorization", "Bearer " + token);
                 return map;
             }
         };

         RequestQueue queue = Volley.newRequestQueue(getContext());
         queue.add(request);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            getData();
        }

        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
}
