package minor.com.dailylogs;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView recyclerView;
    private ArrayList<LogsProperties> data;
    private RecyclerView.Adapter adapter;
    TextView logsEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LogsandDatabase logsandDatabase = new LogsandDatabase(this);
        logsandDatabase.open();
        Cursor c = logsandDatabase.fetchAllLogs();
        data = new ArrayList<>();
        if (c != null && c.moveToLast()) {
            do {
                LogsProperties logsProperties = new LogsProperties();
                logsProperties.setTitle(c.getString(1));
                logsProperties.setId(c.getLong(0));
                data.add(logsProperties);
            } while (c.moveToPrevious());
        }
        setLayoutManager();
        logsEntry = (TextView) findViewById(R.id.logs_entry);
        logsEntry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateLogs.class);
                startActivity(intent);
                finish();
            }
        });

        if (data.isEmpty()) {
            TextView tv = (TextView) findViewById(R.id.noLogsYetTextView);
            tv.setText("There are no logs created!");

        }

    }

    public void setLayoutManager() {
        recyclerView.setHasFixedSize(true);
        if (((MyApplication) this.getApplication()).getManager())
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        else
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new CardViewDataAdapter(data);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!((MyApplication) this.getApplication()).getLongPressStatus()) {
            ((MyApplication) this.getApplication()).setLongPressStatus(true);
            ((MyApplication) this.getApplication()).idToBeDeleted.clear();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        if (data.isEmpty()) {
            menu.findItem(R.id.action_switch_views).setEnabled(false).setVisible(false);
            menu.findItem(R.id.action_clearAll).setEnabled(false).setVisible(false);
            menu.findItem(R.id.delete_selected).setEnabled(false).setVisible(false);
        } else {
            if (((MyApplication) this.getApplication()).getLongPressStatus()) {
                menu.findItem(R.id.delete_selected).setEnabled(false).setVisible(false);
                menu.findItem(R.id.action_switch_views).setEnabled(true).setVisible(true);
                if (((MyApplication) this.getApplication()).getManager())
                    menu.findItem(R.id.action_switch_views).setIcon(R.drawable.ic_action_view_as_grid);
                else
                    menu.findItem(R.id.action_switch_views).setIcon(R.drawable.ic_action_view_as_list);
            } else {
                menu.findItem(R.id.delete_selected).setEnabled(true).setVisible(true);
                menu.findItem(R.id.action_switch_views).setEnabled(false).setVisible(false);
                menu.findItem(R.id.action_clearAll).setEnabled(false).setVisible(false);
                logsEntry.setVisibility(View.INVISIBLE);
                logsEntry.setEnabled(false);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_clearAll) {
            LogsandDatabase logsandDatabase = new LogsandDatabase(this);
            logsandDatabase.open();
            if (logsandDatabase.deleteAll())
                Toast.makeText(this, "Logs Deleted", Toast.LENGTH_LONG).show();
            else Toast.makeText(this, "Error: Logs not Deleted", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (id == R.id.action_switch_views) {
            ((MyApplication) this.getApplication()).setManager(!((MyApplication) this.getApplication()).getManager());
            setLayoutManager();
            invalidateOptionsMenu();
        } else if (id == R.id.delete_selected) {
            LogsandDatabase logsandDatabase = new LogsandDatabase(this);
            logsandDatabase.open();
            for (int i = 0; i < ((MyApplication) this.getApplication()).idToBeDeleted.size(); i++) {
                logsandDatabase.deleteLog(((MyApplication) this.getApplication()).idToBeDeleted.get(i));
            }
            logsandDatabase.close();
            ((MyApplication) this.getApplication()).setLongPressStatus(true);
            startActivity(new Intent(this, MainActivity.class));
            finish();

        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
