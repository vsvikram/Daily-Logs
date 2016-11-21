package minor.com.dailylogs;

import android.app.Application;

/**
 * Created by vicky on 11/21/2016.
 */

public class MyApplication extends Application {

    private boolean linearLayoutManager = true;

    public void setManager(boolean linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    public boolean getManager() {
        return linearLayoutManager;
    }


}
