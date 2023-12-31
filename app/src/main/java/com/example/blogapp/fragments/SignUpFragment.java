package com.example.blogapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blogapp.AuthActivity;
import com.example.blogapp.Constant;
import com.example.blogapp.HomeActivity;
import com.example.blogapp.R;
import com.example.blogapp.UserInfoActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {
    private View view;
    private TextInputLayout layoutEmail, layoutPassword, layoutConfirmPassword;
    private TextInputEditText txtEmail, txtPassword, txtConfirmPassword;
    private Button btnSignUp;
    private TextView txtSignIn;
    private ProgressDialog progressDialog;

    public SignUpFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sign_up, container, false);
        init();
        return view;
    }

    private void init() {
        layoutEmail = view.findViewById(R.id.layoutEmailSignUp);
        layoutPassword = view.findViewById(R.id.layoutPasswordSignUp);
        layoutConfirmPassword = view.findViewById(R.id.layoutConfirmPasswordSignUp);
        txtEmail  = view.findViewById(R.id.txtInputEmailSignUp);
        txtPassword = view.findViewById(R.id.txtInputPasswordSignUp);
        txtConfirmPassword = view.findViewById(R.id.txtInputConfirmPasswordSignUp);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        txtSignIn = view.findViewById(R.id.btnTxtSignIn);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        txtSignIn.setOnClickListener(v-> {
            // change fragments
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SignInFragment()).commit();
        });

        btnSignUp.setOnClickListener(v-> {
            if (validate()) {
                register();
            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!txtEmail.getText().toString().isEmpty()) {
                    layoutEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!txtPassword.getText().toString().isEmpty()) {
                    layoutPassword.setErrorEnabled(false);
                }

                if (txtPassword.getText().toString().length() > 7) {
                    layoutPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!txtConfirmPassword.getText().toString().isEmpty()) {
                    layoutConfirmPassword.setErrorEnabled(false);
                }

                if (txtConfirmPassword.getText().toString().length() > 7) {
                    layoutConfirmPassword.setErrorEnabled(false);
                }

                if (txtConfirmPassword.getText().toString().equals(txtPassword.getText().toString())) {
                    layoutConfirmPassword.setErrorEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private boolean validate() {
        if (txtEmail.getText().toString().isEmpty()) {
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Email is required");
            return false;
        }

        if (txtPassword.getText().toString().isEmpty()) {
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError("Password is required");
            return false;
        }

        if (txtConfirmPassword.getText().toString().isEmpty()) {
            layoutConfirmPassword.setErrorEnabled(true);
            layoutConfirmPassword.setError("Confirm Password is required");
            return false;
        }

        if (txtPassword.getText().toString().length() < 8) {
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError("Required at least 8 characters");
            return false;
        }

        if (!txtConfirmPassword.getText().toString().equals(txtPassword.getText().toString())) {
            layoutConfirmPassword.setErrorEnabled(true);
            layoutConfirmPassword.setError("Confirm password does not match");
            return false;
        }

        return true;
    }

    private void register() {
        progressDialog.setMessage("Registering");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.REGISTER, response -> {
            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    JSONObject user = object.getJSONObject("user");

                    // shared preference user
                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("token", object.getString("token"));
                    editor.putString("name", user.getString("name"));
                    editor.putInt("id", user.getInt("id"));
                    editor.putString("lastname", user.getString("lastname"));
                    editor.putString("photo", user.getString("photo"));
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    // if login success
                    startActivity(new Intent((AuthActivity)getContext(), UserInfoActivity.class));
                    ((AuthActivity) getContext()).finish();

                    Toast.makeText(getContext(), "Register Success", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            progressDialog.dismiss();
        }, error -> {
            error.printStackTrace();
            progressDialog.dismiss();
        }){
            // add parameter

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", txtEmail.getText().toString().trim());
                map.put("password", txtConfirmPassword.getText().toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}
