package na.knowyourreps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 * Modified by Micah on 03/02/16 for use with Know Your Reps instead of Catnip
 */
public class PhoneListenerService extends WearableListenerService {

    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String REP_POSITION_PATH = "/send_rep_position";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(REP_POSITION_PATH) ) {

            // Value contains the String we sent over in WatchToPhoneService, "good job"
            String repPosition = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            Intent detailedViewIntent = new Intent(this, DisplayRepresentatives.class);
            detailedViewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            detailedViewIntent.putExtra("position", repPosition);
            startActivity(detailedViewIntent);

            // Lol... @below text
            // so you may notice this crashes the phone because it's
            //''sending message to a Handler on a dead thread''... that's okay. but don't do this.
            // replace sending a toast with, like, starting a new activity or something.
            // who said skeleton code is untouchable? #breakCSconceptions

        } else {
            super.onMessageReceived( messageEvent );
        }
    }
}