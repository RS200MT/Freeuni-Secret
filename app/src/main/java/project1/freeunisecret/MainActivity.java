package project1.freeunisecret;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer_layout_id) private DrawerLayout drawerLayout;
    @BindView(R.id.main_toolbar_id) private Toolbar toolbar;
    @BindView(R.id.recycler_view_id) private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        initDrawer();
    }


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
}
