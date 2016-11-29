package team1028.plannertravelassistant;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class MyServiceIntent extends IntentService {

    public static final String TAG = "MyServiceIntent";

    public MyServiceIntent(String name) {
        super(name);
    }

    public MyServiceIntent() {
        super("MyServiceIntent");
    }


    @Override
    protected void onHandleIntent(Intent workIntent) { // do stuff when alarm starts this intent
        Log.d(TAG, "onHandleIntent: Do stuff");
    }


}