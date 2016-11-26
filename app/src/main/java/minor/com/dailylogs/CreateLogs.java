package minor.com.dailylogs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateLogs extends AppCompatActivity implements View.OnClickListener {

    EditText enterLogsEditText;
    EditText title;
    boolean disableCreateFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_logs);
        title = (EditText) findViewById(R.id.enter_title_edittext);
        title.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        enterLogsEditText = (EditText) findViewById(R.id.enter_logs_edittext);
        enterLogsEditText.setOnClickListener(this);
        title.setOnClickListener(this);
        enterLogsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                disableCreateFlag = false;
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_logs_menu, menu);
        if (disableCreateFlag)
            menu.findItem(R.id.action_save).setVisible(false).setEnabled(false);
        else menu.findItem(R.id.action_save).setVisible(true).setEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            String logs = enterLogsEditText.getText().toString();
            String titles = title.getText().toString();
            LogsandDatabase logsandDatabase = new LogsandDatabase(this);
            logsandDatabase.open();
            if (logsandDatabase.createLogs(logs, titles, ((MyApplication) this.getApplication()).labelName))
                Toast.makeText(this, "Log Saved", Toast.LENGTH_LONG).show();
            else Toast.makeText(this, "Error: Log not saved", Toast.LENGTH_LONG).show();
            logsandDatabase.close();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.enter_logs_edittext) {
            enterLogsEditText.setCursorVisible(true);
        } else if (id == R.id.enter_title_edittext) title.setCursorVisible(true);
    }
}
