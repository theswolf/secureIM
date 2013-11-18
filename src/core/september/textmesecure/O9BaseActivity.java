package core.september.textmesecure;

import core.september.textmesecure.interfaces.IAppManager;
import core.september.textmesecure.services.O9IMService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public abstract class O9BaseActivity  extends Activity{
	
protected IAppManager imService;
	
protected ServiceConnection mConnection = new ServiceConnection() {
        

		public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            imService = ((O9IMService.IMBinder)service).getService();  
            
            
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
        	String TAG = this.getClass().getSimpleName();
        	imService = null;
        	android.util.Log.d(TAG, getResources().getString(R.string.local_service_stopped));
        }
    };
    
	@Override
	protected void onResume() {
		bindService(new Intent(this, this.getClass()), mConnection , Context.BIND_AUTO_CREATE);   
		super.onResume();
	}
	
	@Override
	protected void onPause() 
	{
		unbindService(mConnection);
		super.onPause();
	}
	


}
