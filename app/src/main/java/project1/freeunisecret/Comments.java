package project1.freeunisecret;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
//    private FirebaseRecyclerAdapter<Comment, Comments.CommentViewHolder>
//            mFirebaseAdapter;
    private DatabaseReference firebaseDatabaseReference;
    private String postId;
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
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        realm = Realm.getDefaultInstance();
        recyclerView.setLayoutManager(linearLayoutManager);
        updateView();
        findkey();
    }
    

    private void findkey() {
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
