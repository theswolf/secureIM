package core.september.textmesecure.supertypes;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;
import core.september.textmesecure.R;
import core.september.textmesecure.interfaces.IAppManager;
import core.september.textmesecure.services.O9IMService;

public abstract class O9BaseFragmentActivity extends ActionBarActivity{
	protected O9IMService imService;
	
	protected ServiceConnection mConnection= new ServiceConnection() {
		
		public void onServiceConnected(ComponentName className, IBinder service) {          
			imService = ((O9IMService.IMBinder)service).getService();
		}
		public void onServiceDisconnected(ComponentName className) {          
			imService = null;
			Toast.makeText(O9BaseFragmentActivity.this, R.string.local_service_stopped,
					Toast.LENGTH_SHORT).show();
		}
	}; 
	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	    	 getApplicationContext().startService(new Intent(this,  O9IMService.class));
	    	 //getApplicationContext().bindService(new Intent(this,O9IMService.class), this.mConnection , Context.BIND_AUTO_CREATE); 
	    }
	    
		@Override
		protected void onResume() {
			getApplicationContext().bindService(new Intent(this,O9IMService.class), this.mConnection , Context.BIND_AUTO_CREATE);   
			super.onResume();
		}
		
		@Override
		protected void onPause() 
		{
			getApplicationContext().unbindService(this.mConnection);
			super.onPause();
		}
	
	public O9IMService getService() {
		return imService;
	}
	
	public ServiceConnection getConnection() {
		return mConnection;
	}

	public abstract void selectItem(int position) ;
	   
		
}
