package project1.freeunisecret;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer_layout_id)  DrawerLayout drawerLayout;
    @BindView(R.id.main_toolbar_id)  Toolbar toolbar;
    @BindView(R.id.recycler_view_id)  RecyclerView recyclerView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private String userName;
    private String ANONYMOUS = "anonymous";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Post, PostViewHolder>
            mFirebaseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        userName = ANONYMOUS;
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API)
//                .build();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            userName = mFirebaseUser.getDisplayName();

        }
        initToolbar();
        initDrawer();
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
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void initDrawer(){

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(MainActivity.this,
                drawerLayout,toolbar,R.string.open,R.string.close);
        drawerToggle.syncState();
    }

    public void Comments(View view){

    }

    public void Heart(View view){

    }

    public class PostViewHolder extends RecyclerView.ViewHolder{

        private TextView postText;
        private TextView postTime;
        private TextView numComments;
        private TextView numHearts;

        public PostViewHolder(View itemView) {
            super(itemView);
            postText = (TextView) itemView.findViewById(R.id.post_text_id);
            postTime = (TextView) itemView.findViewById(R.id.post_time_id);
            numComments = (TextView) itemView.findViewById(R.id.num_comments_id);
            numHearts = (TextView) itemView.findViewById(R.id.num_loves);

        }
    }
}
