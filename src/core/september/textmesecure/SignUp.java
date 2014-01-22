package core.september.textmesecure;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import core.september.textmesecure.configs.Config;
import core.september.textmesecure.fragments.UserListFragment;
import core.september.textmesecure.interfaces.IAppManager;
import core.september.textmesecure.services.O9IMService;
import core.september.textmesecure.supertypes.O9BaseActivity;

public class SignUp  extends O9BaseActivity{
	
	private static final int FILL_ALL_FIELDS = 0;
	protected static final int TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS = 1;
	private static final int SIGN_UP_FAILED = 2;
	private static final int SIGN_UP_USERNAME_CRASHED = 3;
	private static final int SIGN_UP_SUCCESSFULL = 4;
	protected static final int USERNAME_AND_PASSWORD_LENGTH_SHORT = 5;
	
	
//	private static final String SERVER_RES_SIGN_UP_FAILED = "0";
	private static final String SERVER_RES_RES_SIGN_UP_SUCCESFULL = "1";
	private static final String SERVER_RES_SIGN_UP_USERNAME_CRASHED = "2";
	
	private EditText usernameText;
	private EditText passwordText;
	private EditText eMailText;
	private EditText passwordAgainText;

	@Override
	protected void onReceiveBrodcast(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String[] getAction() {
		// TODO Auto-generated method stub
		return null;
	} 
	
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);    


           
    setContentView(R.layout.activity_signup);
    setTitle("Sign up");
    
    Button signUpButton = (Button) findViewById(R.id.registerButton);
    Button signInButton = (Button) findViewById(R.id.signInButton);
    //Button cancelButton = (Button) findViewById(R.id.cancel_signUp);
    usernameText = (EditText) findViewById(R.id.loginEdit);
    passwordText = (EditText) findViewById(R.id.passwordEdit);  
    passwordAgainText = (EditText) findViewById(R.id.passwordConfirm);  
    eMailText = (EditText) findViewById(R.id.emailEdit);
    
    signUpButton.setOnClickListener(new OnClickListener(){
		public void onClick(View arg0) 
		{						
			if (usernameText.length() > 0 &&		
				passwordText.length() > 0 && 
				passwordAgainText.length() > 0 &&
				eMailText.length() > 0
				)
			{
				//TODO check email adress is valid
				
				if (passwordText.getText().toString().equals(passwordAgainText.getText().toString())){
				
					if (usernameText.length() >= 5 && passwordText.length() >= 5) {
					
							Thread thread = new Thread(){
								String result = new String();
								@Override
								public void run() {
									while(imService == null) {}
									imService.signUpUser(usernameText.getText().toString(), 
											passwordText.getText().toString(), 
											eMailText.getText().toString());
								}

							};
							thread.start();
					}
					else{
						showDialog(USERNAME_AND_PASSWORD_LENGTH_SHORT);
					}							
				}
				else {
					showDialog(TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS);
				}
				
			}
			else {
				showDialog(FILL_ALL_FIELDS);
				
			}				
		}       	
    });
    
    signInButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(SignUp.this, SignIn.class);
            startActivity(intent);
			
		}
	});
    
    
}
//	O9BaseActivity {
//	
//	
//	private static final int FILL_ALL_FIELDS = 0;
//	protected static final int TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS = 1;
//	private static final int SIGN_UP_FAILED = 2;
//	private static final int SIGN_UP_USERNAME_CRASHED = 3;
//	private static final int SIGN_UP_SUCCESSFULL = 4;
//	protected static final int USERNAME_AND_PASSWORD_LENGTH_SHORT = 5;
//	
//	
////	private static final String SERVER_RES_SIGN_UP_FAILED = "0";
//	private static final String SERVER_RES_RES_SIGN_UP_SUCCESFULL = "1";
//	private static final String SERVER_RES_SIGN_UP_USERNAME_CRASHED = "2";
//	
//	private final static String TAG = SignUp.class.getSimpleName();
//	
//	private EditText usernameText;
//	private EditText passwordText;
//	private EditText eMailText;
//	private EditText passwordAgainText;
//	
//	
//	public BroadcastReceiver loggedInReceiver = new BroadcastReceiver() {
//		
//		@Override
//		public void onReceive(Context context, Intent intent) {
//        	try {
//	            
//        		Intent intentTo = new Intent(SignUp.this, UsersListActivity.class);
//				Bundle extras = intent.getExtras();
//				intentTo.putExtras(extras);
//				startActivity(intentTo);
//	            
//	        } catch (Exception e) {
//	            android.util.Log.e(TAG,e.getMessage(),e);
//	        }
//		}
//	};
//	
//	@Override
//	public void onResume() {
//		//super.onResume();
//		super.onResume();
//		LocalBroadcastManager.getInstance(this).registerReceiver(loggedInReceiver,new IntentFilter(Config.LOGIN_SUCCESS));
//		
//	}
//	
//	@Override
//	public void onPause() 
//	{ 
//		super.onPause();
//		LocalBroadcastManager.getInstance(this).unregisterReceiver(loggedInReceiver);
//	}
//	
//	@Override
//	public void onBackPressed() {}
//	
//	public void onCreate(Bundle savedInstanceState) {
//	        super.onCreate(savedInstanceState);    
//
//	    
//	               
//	        setContentView(R.layout.activity_signup);
//	        setTitle("Sign up");
//	        
//	        Button signUpButton = (Button) findViewById(R.id.registerButton);
//	        Button signInButton = (Button) findViewById(R.id.signInButton);
//	        //Button cancelButton = (Button) findViewById(R.id.cancel_signUp);
//	        usernameText = (EditText) findViewById(R.id.loginEdit);
//	        passwordText = (EditText) findViewById(R.id.passwordEdit);  
//	        passwordAgainText = (EditText) findViewById(R.id.passwordConfirm);  
//	        eMailText = (EditText) findViewById(R.id.emailEdit);
//	        
//	        signUpButton.setOnClickListener(new OnClickListener(){
//				public void onClick(View arg0) 
//				{						
//					if (usernameText.length() > 0 &&		
//						passwordText.length() > 0 && 
//						passwordAgainText.length() > 0 &&
//						eMailText.length() > 0
//						)
//					{
//						//TODO check email adress is valid
//						
//						if (passwordText.getText().toString().equals(passwordAgainText.getText().toString())){
//						
//							if (usernameText.length() >= 5 && passwordText.length() >= 5) {
//							
//									Thread thread = new Thread(){
//										String result = new String();
//										@Override
//										public void run() {
//											while(imService == null) {}
//											imService.signUpUser(usernameText.getText().toString(), 
//													passwordText.getText().toString(), 
//													eMailText.getText().toString());
//										}
//		
//									};
//									thread.start();
//							}
//							else{
//								showDialog(USERNAME_AND_PASSWORD_LENGTH_SHORT);
//							}							
//						}
//						else {
//							showDialog(TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS);
//						}
//						
//					}
//					else {
//						showDialog(FILL_ALL_FIELDS);
//						
//					}				
//				}       	
//	        });
//	        
//	        signInButton.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					// TODO Auto-generated method stub
//					Intent intent = new Intent(SignUp.this, SignIn.class);
//		            startActivity(intent);
//					
//				}
//			});
//	        
//	        
//	    }
//	
//	
//	protected Dialog onCreateDialog(int id) 
//	{    	
//		  	
//		switch (id) 
//		{
//			case TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS:			
//				return new AlertDialog.Builder(SignUp.this)       
//				.setMessage(R.string.signup_type_same_password_in_password_fields)
//				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						/* User clicked OK so do some stuff */
//					}
//				})        
//				.create();			
//			case FILL_ALL_FIELDS:				
//				return new AlertDialog.Builder(SignUp.this)       
//				.setMessage(R.string.signup_fill_all_fields)
//				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						/* User clicked OK so do some stuff */
//					}
//				})        
//				.create();
//			case SIGN_UP_FAILED:
//				return new AlertDialog.Builder(SignUp.this)       
//				.setMessage(R.string.signup_failed)
//				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						/* User clicked OK so do some stuff */
//					}
//				})        
//				.create();
//			case SIGN_UP_USERNAME_CRASHED:
//				return new AlertDialog.Builder(SignUp.this)       
//				.setMessage(R.string.signup_username_crashed)
//				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						/* User clicked OK so do some stuff */
//					}
//				})        
//				.create();
//			case SIGN_UP_SUCCESSFULL:
//				return new AlertDialog.Builder(SignUp.this)       
//				.setMessage(R.string.signup_successfull)
//				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						finish();
//					}
//				})        
//				.create();	
//			case USERNAME_AND_PASSWORD_LENGTH_SHORT:
//				return new AlertDialog.Builder(SignUp.this)       
//				.setMessage(R.string.username_and_password_length_short)
//				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						/* User clicked OK so do some stuff */
//					}
//				})        
//				.create();
//			default:
//				return null;
//				
//		}
//
//	
//	}

}
