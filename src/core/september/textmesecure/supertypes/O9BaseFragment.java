package core.september.textmesecure.supertypes;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.ListFragment;
import core.september.textmesecure.R;
import core.september.textmesecure.interfaces.IAppManager;
import core.september.textmesecure.services.O9IMService;

public abstract class O9BaseFragment extends ListFragment{

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
}
