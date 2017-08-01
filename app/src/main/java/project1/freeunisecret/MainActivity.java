package project1.freeunisecret;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final String POST_CHILD = "Posts";
    private static final CharSequence FREEUNI_EMAIL = "freeuni.edu.ge";
    private static final int FREEUNI_EMAIL_LENGTH = 14;
    // @BindView(R.id.drawer_layout_id)  DrawerLayout drawerLayout;
    @BindView(R.id.main_toolbar_id)  Toolbar toolbar;
    @BindView(R.id.recycler_view_id)  RecyclerView recyclerView;
    @BindView(R.id.add_post_id)  FloatingActionButton addPostButton;
    @BindView(R.id.log_out_btn_id) Button logoutBtn;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private String userName;
    private String ANONYMOUS = "anonymous";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Post, PostViewHolder>
            mFirebaseAdapter;
    private LinearLayoutManager linearLayoutManager;
    private String TAG = "MainActivity";
    private String nodeKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        userName = ANONYMOUS;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            logoutBtn.setVisibility(Button.GONE);
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }
        checkEmail();
        userName = mFirebaseUser.getDisplayName();
        logoutBtn.setVisibility(Button.VISIBLE);
        addPosts();
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,CreatePost.class));
            }
        });
		logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(MainActivity.this,SignInActivity.class));

            }
        });
        initToolbar();
//        initDrawer();
    }

    private void checkEmail() {
        String userEmail = mFirebaseUser.getEmail();
        String domain = userEmail.substring(userEmail.length()-FREEUNI_EMAIL_LENGTH,userEmail.length());
        if(!domain.equals(FREEUNI_EMAIL)){
            startActivity(new Intent(this,ErrorPage.class));
        }
    }

    private void addPosts() {
        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Post,
                PostViewHolder>(
                Post.class,
                R.layout.recycler_item,
                PostViewHolder.class,
                mFirebaseDatabaseReference.child(POST_CHILD)) {

            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder,
                                              final Post post, int position) {
                //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                String imageUrl = post.getImageUrl();
                if (!post.getText().isEmpty()) {
                    viewHolder.postText.setText(post.getText());
                    viewHolder.postImage.setVisibility(ImageView.GONE);
                    if(!imageUrl.isEmpty()){
                        setImage(imageUrl,viewHolder,post,true);
                    }

                } else if (!imageUrl.isEmpty()){
                    setImage(imageUrl, viewHolder,post,false);
                }
                viewHolder.postTime.setText(post.getDate());
                viewHolder.numComments.setText(Integer.toString(post.getNumComments()));
                viewHolder.numHearts.setText(Integer.toString(post.getNumHearts()));
                final String id = post.getId();
                View.OnClickListener onclick = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean canEdit = post.getPostAuthorId().equals(mFirebaseUser.getUid()) ? true : false;
                        Comments(id,post.getText(),post.getImageUrl(), post.getDate(),canEdit);
                    }
                };
                viewHolder.comment.setOnClickListener(onclick);
                viewHolder.card.setOnClickListener(onclick);
                viewHolder.heart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        heart(id);
                    }
                });
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mFirebaseAdapter);
    }

    private void setImage(String imageUrl, final PostViewHolder viewHolder, Post post,boolean withText) {
        if (imageUrl.startsWith("gs://")) {
            StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReferenceFromUrl(imageUrl);
            storageReference.getDownloadUrl().addOnCompleteListener(
                    new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String downloadUrl = task.getResult().toString();
                                Picasso.with(viewHolder.postImage.getContext())
                                        .load(downloadUrl)
                                        .into(viewHolder.postImage);
                            } else {
                                Log.w(TAG, "Getting download url was not successful.",
                                        task.getException());
                            }
                        }
                    });
        } else {
            Picasso.with(viewHolder.postImage.getContext())
                    .load(post.getImageUrl())
                    .into(viewHolder.postImage);
        }
        viewHolder.postImage.setVisibility(ImageView.VISIBLE);
        if (!withText)
            viewHolder.postText.setVisibility(TextView.GONE);
    }

    //@Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.sign_out_menu:
//                mFirebaseAuth.signOut();
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//                userName = ANONYMOUS;
//                startActivity(new Intent(this, SignInActivity.class));
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }


    private void initToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
    }

//    private void initDrawer(){
//
//        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(MainActivity.this,
//                drawerLayout,toolbar,R.string.open,R.string.close);
//        drawerToggle.syncState();
//    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void Comments(String postId,String postText, String imageUrl,String postTime, boolean canEdit){
        Intent intent = new Intent(this,Comments.class);
        intent.putExtra("postId", postId);
        intent.putExtra("postText", postText);
        intent.putExtra("imageUrl", imageUrl);
        intent.putExtra("postTime",postTime);
        intent.putExtra("canEdit",canEdit);
        startActivity(intent);

    }

    private void heart(String postId){
        findkey(postId);

    }

    private void updateHeartNum(final int n){
        mFirebaseDatabaseReference.child(MainActivity.POST_CHILD).child(nodeKey).child("numHearts").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long value = mutableData.getValue(Long.class);
                if (value == null) {
                    mutableData.setValue(0);
                }
                else {
                    mutableData.setValue(value + n);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d("Comments", "transaction:onComplete:" + databaseError);
            }
        });
    }

    private void findkey(final String postId) {
        final Query query = mFirebaseDatabaseReference.child(POST_CHILD).orderByChild("id");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    String value = child.getValue().toString();
                    if(postId.equals(value)){
                        nodeKey = key;
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        String userId = mFirebaseUser.getUid();
                        List<RealmString> lovers = realm.where(Heart.class).equalTo("postId",postId).findFirst().getLovers();
                        boolean alreadyHearted = false;

                        for(RealmString str : lovers){
                            if(str.getStr().equals(userId))
                                alreadyHearted = true;
                        }
                        if(alreadyHearted){
                            for(RealmString rs : lovers){
                                if(rs.getStr().equals(userId)){
                                    lovers.remove(rs);
                                    break;
                                }
                            }
                            updateHeartNum(-1);
                        } else{
                            RealmString st = realm.createObject(RealmString.class);
                            st.setStr(userId);
                            lovers.add(st);
                            updateHeartNum(1);
                        }
                        realm.commitTransaction();
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

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        private TextView postText;
        private TextView postTime;
        private TextView numComments;
        private TextView numHearts;
        private ImageView postImage;
        private ImageView comment;
        private ImageView heart;
        private CardView card;
        private ProgressBar progressbar;
        public PostViewHolder(View itemView) {
            super(itemView);
            postText = (TextView) itemView.findViewById(R.id.post_text_id);
            postTime = (TextView) itemView.findViewById(R.id.post_time_id);
            numComments = (TextView) itemView.findViewById(R.id.num_comments_id);
            numHearts = (TextView) itemView.findViewById(R.id.num_loves);
            postImage = (ImageView) itemView.findViewById(R.id.post_image_id);
            comment = (ImageView) itemView.findViewById(R.id.show_comments_id);
            heart = (ImageView) itemView.findViewById(R.id.heart_image);
            card = (CardView) itemView.findViewById(R.id.post_id);
            progressbar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }
}
