package com.example.healthcare.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthcare.Model.CommentModelClass;
import com.example.healthcare.Model.DpsOfUsers;
import com.example.healthcare.Model.Like;
import com.example.healthcare.Model.UserConstantModel;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    Context context;
    List<CommentModelClass> commentModelClasses;
    public static final String COMMENTED_LIKES = "CommentedLikes";

    public CommentAdapter(Context context, List<CommentModelClass> answeredModelClasses) {
        this.context = context;
        this.commentModelClasses = answeredModelClasses;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_recyclerview_layout, parent, false);
        return new CommentAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, final int position) {
        Glide.with(context).load("null").placeholder(R.mipmap.ic_launcher).into(holder.commentDp);
        holder.UserName.setText(commentModelClasses.get(position).getNameOfUser());


        GetLikesOfCurrentPostOfCurrentComment(holder.noOfLikes, commentModelClasses.get(position).getPushKey());
        holder.comment.setText(commentModelClasses.get(position).getComment());
        CheckIfUserAlreadyLikedComments(holder.commentLiked, commentModelClasses.get(position).getPushKey());
        GetDpsOfUser(holder.commentDp,commentModelClasses.get(position).getUserUid());
        holder.commentLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCommentedLikes(commentModelClasses.get(position).getPushKey());
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentModelClasses.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView commentDp;
        TextView UserName, noOfLikes, comment, commentLiked;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentDp = itemView.findViewById(R.id.commentDp);
            UserName = itemView.findViewById(R.id.commentName);
            noOfLikes = itemView.findViewById(R.id.noOfLikes);
            comment = itemView.findViewById(R.id.commentDescription);
            commentLiked = itemView.findViewById(R.id.commentLiked);
        }
    }



    //////////// This function will set the Dp of specific user ///////////////////////////////

    public void GetDpsOfUser(final CircleImageView circleImageView, String userId){
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("DpsOfUsers");
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot !=null){
                    DpsOfUsers dpsOfUsers=dataSnapshot.getValue(DpsOfUsers.class);
                    Glide.with(context).load(dpsOfUsers.getImageUri()).placeholder(R.mipmap.ic_launcher).into(circleImageView);
                }else {
                    Glide.with(context).load("null").placeholder(R.mipmap.ic_launcher).into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //////////// This function will set the Dp of specific user ///////////////////////////////




    //////////// This function will set the Like to specific Comment ////////////////////

    public void setCommentedLikes(String pushKeyOfComment) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(COMMENTED_LIKES);
        databaseReference.child(pushKeyOfComment).child(UserConstantModel.Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Like liked = dataSnapshot.getValue(Like.class);
                    if (liked != null) {
                        if (liked.isLiked()) {
                            dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Disliked", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("Liked", true);
                        dataSnapshot.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });


    }

    //////////// This function will set the Like to specific Comment ////////////////////


    //////////// This function will get the number of Likes of specific comment of specific Post ////////////////

    public void GetLikesOfCurrentPostOfCurrentComment(final TextView noOfLikes, String pushKeyOfComment) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(COMMENTED_LIKES);
        databaseReference.child(pushKeyOfComment).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noOfLikes.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //////////// This function will get the Likes of specific comment of specific Post ////////////////


    /////// This function will set if user liked then text will set to liked and color will be changed/////////////////


    public void CheckIfUserAlreadyLikedComments(final TextView Liked, String pushKey) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(COMMENTED_LIKES);
        databaseReference.child(pushKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(UserConstantModel.Uid)) {
                    Liked.setText("Liked");
                    Liked.setTextColor(context.getResources().getColor(R.color.LikedColor));
                } else {
                    Liked.setText("Like");
                    Liked.setTextColor(context.getResources().getColor(R.color.LikeColor));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
/////// This function will set if user liked then text will set to liked and color will be changed/////////////////
}