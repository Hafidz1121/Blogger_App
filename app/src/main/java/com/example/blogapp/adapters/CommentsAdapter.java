package com.example.blogapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogapp.R;
import com.example.blogapp.models.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsHolder> {
    private Context context;
    private ArrayList<Comment> list;

    public CommentsAdapter(Context context, ArrayList<Comment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);
        return new CommentsHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsHolder holder, int position) {
        Comment comment = list.get(position);
        Picasso.get().load(comment.getUser().getPhoto()).into(holder.imgProfile);
        holder.txtName.setText(comment.getUser().getUsername());
        holder.txtDate.setText(comment.getDate());
        holder.txtComment.setText(comment.getComment());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CommentsHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgProfile;
        private TextView txtName, txtDate, txtComment;
        private ImageButton btnOption;

        public CommentsHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgCommentProfile);
            txtName = itemView.findViewById(R.id.txtCommentName);
            txtDate = itemView.findViewById(R.id.txtCommentDate);
            txtComment = itemView.findViewById(R.id.textCommentText);
            btnOption = itemView.findViewById(R.id.btnCommentOption);
        }
    }
}
