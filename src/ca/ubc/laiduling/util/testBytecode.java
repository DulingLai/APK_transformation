package ca.ubc.laiduling.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class testBytecode extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent dulingServiceIntent = new Intent(this, ca.ubc.laiduling.util.dulingActivityAwareLocation.class);
        this.startService(dulingServiceIntent);
    }
}
