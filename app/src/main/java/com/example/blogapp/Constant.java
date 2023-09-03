package com.example.blogapp;

public class Constant {
    public static final String URL = "http://192.168.0.2/";     // Change this URL and must match URL if laravel started
    public static final String HOME = URL + "api";

    // Login, Register & Logout
    public static final String LOGIN = HOME + "/login";
    public static final String REGISTER = HOME + "/register";
    public static final String LOGOUT = HOME + "/logout";
    public static final String SAVE_USER_INFO = HOME + "/save_user";
    public static final String POSTS = HOME + "/posts";

    // Post CRUD
    public static final String MY_POST = POSTS + "/my_post";
    public static final String ADD_POST = POSTS + "/create";
    public static final String UPDATE_POST = POSTS + "/update";
    public static final String DELETE_POST = POSTS + "/delete";
    public static final String LIKE_POST = POSTS + "/like";

    // Comment CRD
    public static final String COMMENTS = POSTS + "/comments";
    public static final String CREATE_COMMENT = HOME + "/comments/create";
    public static final String DELETE_COMMENT = HOME + "/comments/delete";
}
