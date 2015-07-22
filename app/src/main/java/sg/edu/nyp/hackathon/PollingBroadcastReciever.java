package sg.edu.nyp.hackathon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by wei cong on 7/22/2015.
 */
public class PollingBroadcastReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Started SERVICE", Toast.LENGTH_LONG).show();
        context.startService(new Intent(context, PollingService.class));
    }
}
