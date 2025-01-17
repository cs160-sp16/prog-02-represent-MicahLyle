package na.knowyourreps;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 * Modified by Micah on 03/02/16 for use with Know Your Reps
 */
public class WatchToPhoneService extends Service implements GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mWatchApiClient;
    private List<Node> nodes = new ArrayList<>();
    private String infoForPhone;
    private String whatIsBeingSent;
    final Service _this = this; // Thanks to https://piazza.com/class/ijddlu9pcyk1sk?cid=422

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize the googleAPIClient for message passing
        mWatchApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(this)
                .build();
        //and actually connect it
        mWatchApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWatchApiClient.disconnect();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Which view do we want to display on the watch? Grab this info from INTENT
        // which was passed over when we called startService
        Bundle extras = intent.getExtras();
        if (extras.getString("watch_to_phone_detailed") != null) {
            infoForPhone = extras.getString("watch_to_phone_detailed");
            whatIsBeingSent = "watch_to_phone_detailed";
        } else if (extras.get("shake_selection") != null) {
            infoForPhone = extras.getString("randomlyGeneratedCounty");
            whatIsBeingSent = "shake_selection";
        }

        return START_STICKY;
    }

    // alternate method to connecting; no longer creates this in a new thread, but as a callback
    @Override
    public void onConnected(Bundle bundle) {
        Log.d("T", "in onconnected");
        Wearable.NodeApi.getConnectedNodes(mWatchApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        nodes = getConnectedNodesResult.getNodes();
                        Log.d("T", "found nodes");
                        // when we find a connected node, we populate the list declared above
                        // finally, we can send a message
                        if (whatIsBeingSent.equals("shake_selection")) {
                            sendMessage("/send_random_county", infoForPhone);
                        } else if (whatIsBeingSent.equals("watch_to_phone_detailed")) {
                            sendMessage("/send_rep_position", infoForPhone);
                        }
                        Log.d("T", whatIsBeingSent + "sent");
                        _this.stopSelf();
                    }
                });
    }

    @Override //we need this to implement GoogleApiClient.ConnectionsCallback
    public void onConnectionSuspended(int i) {}

    private void sendMessage(final String path, final String text ) {
        for (Node node : nodes) {
            Wearable.MessageApi.sendMessage(
                    mWatchApiClient, node.getId(), path, text.getBytes());
        }
    }

}
