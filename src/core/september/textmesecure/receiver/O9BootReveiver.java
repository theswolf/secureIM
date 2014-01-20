package core.september.textmesecure.receiver;

import core.september.textmesecure.services.O9IMService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class O9BootReveiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Intent service = new Intent(context, O9IMService.class);
    context.startService(service);
  }
}