package minor.com.dailylogs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DetailLogs extends AppCompatActivity implements View.OnClickListener {
    long row_id;
    String log;
    EditText editText;
    boolean disableUpdateflag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_logs);
        editText = (EditText) findViewById(R.id.detail_logs_edittext);
        Intent intent = getIntent();
        log = intent.getStringExtra("data");
        row_id = intent.getLongExtra("id", 0);
        editText.setText(log);
        editText.setOnClickListener(this);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                disableUpdateflag = false;
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_log_menu, menu);
        if (disableUpdateflag)
            menu.findItem(R.id.action_update).setEnabled(false).setVisible(false);

        else menu.findItem(R.id.action_update).setEnabled(true).setVisible(true);

        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            LogsandDatabase logsandDatabase = new LogsandDatabase(this);
            logsandDatabase.open();
            if (logsandDatabase.deleteLog(row_id)) {
                Toast.makeText(this, "Delete Successful", Toast.LENGTH_LONG).show();
            } else Toast.makeText(this, "Delete failed" + row_id, Toast.LENGTH_LONG).show();
            logsandDatabase.close();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_update) {
            LogsandDatabase logsandDatabase = new LogsandDatabase(this);
            logsandDatabase.open();
            if (logsandDatabase.updateLogs(row_id, editText.getText().toString()))
                Toast.makeText(this, "Log Updated", Toast.LENGTH_LONG).show();
            else Toast.makeText(this, "Update Failed", Toast.LENGTH_LONG).show();
            logsandDatabase.close();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.detail_logs_edittext) {
            editText.setCursorVisible(true);
        }
    }
}
