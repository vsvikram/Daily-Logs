package minor.com.dailylogs;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by vicky on 11/21/2016.
 */

public class MyApplication extends Application {

    private boolean linearLayoutManager = true;
    private boolean longPressDisable = true;
    public ArrayList<Long> idToBeDeleted = new ArrayList<>();
    public ArrayList<String> menuList = new ArrayList<>();
    public String labelName = "Daily Logs";

    public void setManager(boolean linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    public void setLongPressStatus(boolean status) {
        this.longPressDisable = status;
    }

    public void setLongPressId(long id) {
        idToBeDeleted.add(id);
    }

    public boolean getManager() {
        return linearLayoutManager;
    }

    public boolean getLongPressStatus() {
        return longPressDisable;
    }


}
