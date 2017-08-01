package project1.freeunisecret;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static project1.freeunisecret.MainActivity.POST_CHILD;

/**
 * Created by Tornike on 30-Jul-17.
 */

public class Comments extends AppCompatActivity {

    @BindView(R.id.comments_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.write_comment) EditText et;
    @BindView(R.id.post_text_in_comments) TextView text;
    @BindView(R.id.post_time_in_comments) TextView time;
    @BindView(R.id.post_image_in_comments) ImageView img;
    @BindView(R.id.edit_button) ImageView editButton;
    @BindView(R.id.progressBar_in_comments)
    ProgressBar progressBar;
    @BindView(R.id.add_comment)
    Button addCommentButton;
    private DatabaseReference firebaseDatabaseReference;
    private String postId;
    String postText;
    String imageUrl;
    private  String postTime;
    private String nodeKey ;
    private LinearLayoutManager linearLayoutManager;
    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.bind(this);
        linearLayoutManager = new LinearLayoutManager(this);
        postId = getIntent().getStringExtra("postId");
        postText = getIntent().getStringExtra("postText");
        imageUrl = getIntent().getStringExtra("imageUrl");
        postTime = getIntent().getStringExtra("postTime");
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        realm = Realm.getDefaultInstance();
        recyclerView.setLayoutManager(linearLayoutManager);
        if(postText != null)
            text.setText(postText);
        time.setText(postTime);
        if(!imageUrl.isEmpty()){
            img.setVisibility(ImageView.VISIBLE);
            Picasso.with(this).load(imageUrl).into(img, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(ProgressBar.GONE);
                }

                @Override
                public void onError() {

                }
            });
        } else {
            progressBar.setVisibility(ProgressBar.GONE);
        }
        boolean canEdit = getIntent().getBooleanExtra("canEdit", false);
        if(canEdit){
            editButton.setVisibility(ImageView.VISIBLE);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Comments.this, EditPost.class);
                    intent.putExtra("postText",postText);
                    intent.putExtra("imageUrl",imageUrl);
                    intent.putExtra("postId",postId);
                    startActivity(intent);
                }
            });
        }
        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0){
                    addCommentButton.setEnabled(false);
                } else {
                    addCommentButton.setEnabled(true);
                }


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        updateView();
        findkey();
    }


    private  void findkey() {
        final Query query = firebaseDatabaseReference.child(POST_CHILD).orderByChild("id");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    String value = child.getValue().toString();
                    if(postId.equals(value)){
                        nodeKey = key;
                        return;
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addComment(View view){
        realm.beginTransaction();
        String commentText = et.getText().toString();
        long writeTime = new Date().getTime();
        Comment comment = realm.createObject(Comment.class);
        comment.setCommentText(commentText);
        comment.setWriteTime(writeTime);
        comment.setPostId(postId);
        realm.commitTransaction();
        et.setText("");
        updateView();
        firebaseDatabaseReference.child(MainActivity.POST_CHILD).child(nodeKey).child("numComments").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long value = mutableData.getValue(Long.class);
                if (value == null) {
                    mutableData.setValue(0);
                }
                else {
                    mutableData.setValue(value + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d("Comments", "transaction:onComplete:" + databaseError);
            }
        });
    }

    private void updateView() {
        List<Comment> comments = realm.where(Comment.class).equalTo("postId",postId).findAll();
        RecyclerAdapter adapter = new RecyclerAdapter(comments);
        recyclerView.setAdapter(adapter);
    }


}
