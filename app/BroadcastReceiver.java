import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class WifiStatusReceiver extends BroadcastReceiver {
    private DatabaseReference wifiStatusRef;

    public WifiStatusReceiver(DatabaseReference wifiStatusRef) {
        this.wifiStatusRef = wifiStatusRef;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            wifiStatusRef.setValue("on");
        } else {
            wifiStatusRef.setValue("off");
        }
    }
}
