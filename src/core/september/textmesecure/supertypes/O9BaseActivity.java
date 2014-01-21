package core.september.textmesecure.supertypes;

import core.september.textmesecure.R;
import core.september.textmesecure.SignIn;
import core.september.textmesecure.SignUp;
import core.september.textmesecure.SplashActivity;
import core.september.textmesecure.UsersListActivity;
import core.september.textmesecure.R.string;
import core.september.textmesecure.configs.Config;
import core.september.textmesecure.interfaces.IAppManager;
import core.september.textmesecure.services.O9IMService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public abstract class O9BaseActivity  extends Activity{
	
protected O9IMService imService;
	
protected ServiceConnection mConnection= new ServiceConnection() {
	
	public void onServiceConnected(ComponentName className, IBinder service) {          
		imService = ((O9IMService.IMBinder)service).getService();   		
	}
	public void onServiceDisconnected(ComponentName className) {          
		imService = null;
		Toast.makeText(O9BaseActivity.this, R.string.local_service_stopped,
				Toast.LENGTH_SHORT).show();
	}
}; 

protected String TAG() {
	return this.getClass().getSimpleName();
}
protected abstract void onReceiveBrodcast(Context context, Intent intent);
protected abstract String[] getAction();

public BroadcastReceiver mReceiver = new BroadcastReceiver() {
	
	@Override
	public void onReceive(Context context, Intent intent) {
    	try {
            
    		onReceiveBrodcast(context, intent);
            
        } catch (Exception e) {
            android.util.Log.e(TAG(),e.getMessage(),e);
        }
	}
};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	 getApplicationContext().startService(new Intent(this,  O9IMService.class));
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		getApplicationContext().bindService(new Intent(this,O9IMService.class), this.mConnection , Context.BIND_AUTO_CREATE); 
		IntentFilter intentFilter = new IntentFilter();
		if(getAction() != null) {
			for(String action: getAction()) {
				intentFilter.addAction(action);
			}
			LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,intentFilter);
		}
		
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		getApplicationContext().unbindService(this.mConnection);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
		
	}
	

	
	
	


}
