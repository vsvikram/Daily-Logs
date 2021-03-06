package minor.com.dailylogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView recyclerView;
    Toolbar toolbar;
    private ArrayList<LogsProperties> data;
    private RecyclerView.Adapter adapter;
    private TextView logsEntry;
    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(((MyApplication) this.getApplication()).labelName);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView name = (TextView) findViewById(R.id.name);
        TextView email = (TextView) findViewById(R.id.email);
        if (getIntent().hasExtra("Name"))
            name.setText(getIntent().getExtras().getCharSequence("Name"));
        if (getIntent().hasExtra("Email"))
            email.setText(getIntent().getExtras().getCharSequence("Email"));
        menu = navigationView.getMenu();

        addMenuItems();
        LogsandDatabase logsandDatabase = new LogsandDatabase(this);
        logsandDatabase.open();
        Cursor c = logsandDatabase.fetchAllLogs(((MyApplication) this.getApplication()).labelName);
        data = new ArrayList<>();
        if (c != null && c.moveToLast()) {
            do {
                LogsProperties logsProperties = new LogsProperties();
                logsProperties.setLogs(c.getString(1));
                logsProperties.setId(c.getLong(0));
                logsProperties.setTitle(c.getString(2));
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
            tv.setText("There are no logs created under this label!");

        }

    }

    public void addMenuItems() {
        for (int i = (menu.size() - 2); i < ((MyApplication) this.getApplication()).menuList.size(); i++)
            menu.add(R.id.newLableGroup, Menu.NONE, Menu.NONE, ((MyApplication) this.getApplication()).menuList.get(i))
                    .setIcon(android.R.drawable.btn_star);

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
        if (!((MyApplication) this.getApplication()).labelName.equals("Daily Logs")) {
            menu.add("Delete Label");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String title = item.getTitle().toString();

        if (id == R.id.action_clearAll) {
            LogsandDatabase logsandDatabase = new LogsandDatabase(this);
            logsandDatabase.open();
            if (logsandDatabase.deleteAll(((MyApplication) this.getApplication()).labelName))
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
                logsandDatabase.deleteLog(((MyApplication) this.getApplication()).idToBeDeleted.get(i), ((MyApplication) this.getApplication()).labelName);
            }
            logsandDatabase.close();
            ((MyApplication) this.getApplication()).setLongPressStatus(true);
            startActivity(new Intent(this, MainActivity.class));
            finish();

        } else if (title.equals("Delete Label")) {
            LogsandDatabase logsandDatabase = new LogsandDatabase(this);
            logsandDatabase.open();
            if (logsandDatabase.deleteAll(((MyApplication) this.getApplication()).labelName))
                Toast.makeText(this, "Logs Deleted", Toast.LENGTH_LONG).show();
            else Toast.makeText(this, "Error: Logs not Deleted", Toast.LENGTH_LONG).show();
            ((MyApplication) this.getApplicationContext()).menuList.remove(((MyApplication) this.getApplication()).labelName);
            addMenuItems();
            Toast.makeText(this, "Label Removed", Toast.LENGTH_LONG).show();
            ((MyApplication) this.getApplication()).labelName = "Daily Logs";
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
        ((MyApplication) this.getApplication()).labelName = item.getTitle().toString();
        if (id == R.id.nav_addLabel) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final Context context = this;
            builder.setTitle("New Label");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MyApplication) context.getApplicationContext()).menuList.add(input.getText().toString());
                    addMenuItems();
                    Toast.makeText(context, "Label Created", Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
