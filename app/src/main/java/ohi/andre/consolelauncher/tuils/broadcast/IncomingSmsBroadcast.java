package ohi.andre.consolelauncher.tuils.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.TextView;

import ohi.andre.consolelauncher.R;
import ohi.andre.consolelauncher.managers.TerminalManager;

public class IncomingSmsBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Only handle SMS received
        if (!intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            return;
        }

        // Get the SMS message passed in, if any
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }

        // Retrieve the SMS message received.
        // Since we're not interested in content, we can safely discard
        // all records but the first one
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[0]);
    }
}