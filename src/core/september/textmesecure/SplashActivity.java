package core.september.textmesecure;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import core.september.textmesecure.configs.Route;
import core.september.textmesecure.supertypes.O9BaseActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * //@see SystemUiHider
 */
public class SplashActivity extends O9BaseActivity {


	private ProgressBar progressBar;
//	private enum Route {
//		NEED_SIGNUP,
//		NEED_SIGNIN,
//		READY_TO_START
//	}
//	
//	private User user = null;
//	private Route rou
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				try{
//					Thread.sleep(5000);
//				}
//				catch(Exception e) {
//					android.util.Log.e(TAG(),e.getMessage(),e);
//				}
//				
//			}
//		});
	}

	@Override
	protected void onPostConnect()
	{
		final Route route = imService.getRoute();
		progressBar.setVisibility(View.GONE);
;
		
		new Handler().postDelayed(new Runnable() {
			 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, SignUp.class);
                startActivity(i);
 
                // close this activity
                finish();
            }
        }, 5000);
	}
	


	@Override
	protected void onReceiveBrodcast(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected String[] getAction() {
		// TODO Auto-generated method stub
		return null;
	}


   
}
