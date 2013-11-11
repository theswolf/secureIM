package core.september.textmesecure;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ProgressBar;

import com.niusounds.asd.SQLiteDAO;
import com.quickblox.core.QBCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;

import core.september.textmesecure.interfaces.IAppManager;
import core.september.textmesecure.services.O9IMService;
import core.september.textmesecure.sql.models.User;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class SplashActivity extends Activity  implements QBCallback {

	private final static String TAG = SplashActivity.class.getSimpleName();
	private ProgressBar progressBar;
	private IAppManager imService;
	private enum Route {
		NEED_SIGNUP,
		NEED_SIGNIN,
		READY_TO_START
	}
	
	private User user = null;
	private Route route;
	
	  private ServiceConnection mConnection = new ServiceConnection() {
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
	        	imService = null;
	        	android.util.Log.d(TAG, getResources().getString(R.string.local_service_stopped));
	        }
	    };
		

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);


//        ImageView qbLinkPanel = (ImageView) findViewById(R.id.splash_qb_link);
//        qbLinkPanel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("http://quickblox.com/developers/Android"));
//                startActivity(browserIntent);
//            }
//        });

        // ================= QuickBlox ===== Step 1 =================
        // Initialize QuickBlox application with credentials.
        // Getting app credentials -- http://quickblox.com/developers/Getting_application_credentials
        
        startService(new Intent(SplashActivity.this,  O9IMService.class));
        
        SQLiteDAO dao = SQLiteDAO.getInstance(this, User.class);
        List<User> list = dao.get(User.class);
        
        if(list != null && list.size() > 0) {
        	user = list.get(0);
        	if(user.getPassword() != null && user.getPassword().trim().length() > 0) {
        		route = Route.READY_TO_START;
        	}
        	else {
        		route = Route.NEED_SIGNIN;
        	}
        }
        
        else {
        	route = Route.NEED_SIGNUP;
        }
        
        QBSettings.getInstance().fastConfigInit("1028", "jCr7OwnvgV5wFmm", "4JmKPAnwN7ps5Xt");

        // ================= QuickBlox ===== Step 2 =================
        // Authorize application.
        QBAuth.createSession(this);
    }

    @Override
    public void onComplete(Result result) {
        progressBar.setVisibility(View.GONE);

        if (result.isSuccess()) {
        	Intent intent;	
        switch (route) {
		case READY_TO_START:
			 intent = new Intent(this, UsersListActivity.class);
	            startActivity(intent);
			break;
		case NEED_SIGNIN:
			intent = new Intent(this, SignIn.class);
	            startActivity(intent);
			break;
		case NEED_SIGNUP:
			 intent = new Intent(this, SignUp.class);
	            startActivity(intent);
			break;
		
		}
        	
           
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Error(s) occurred. Look into DDMS log for details, " +
                    "please. Errors: " + result.getErrors()).create().show();
        }

    }

    @Override
    public void onComplete(Result result, Object context) {
    }
    
	@Override
	protected void onPause() 
	{
		unbindService(mConnection);
		super.onPause();
	}

	@Override
	protected void onResume() 
	{		
		bindService(new Intent(SplashActivity.this, O9IMService.class), mConnection , Context.BIND_AUTO_CREATE);
	    		
		super.onResume();
	}
}
