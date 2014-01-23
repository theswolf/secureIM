package core.september.textmesecure;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
	private boolean signalSent = false;

	@Override
	protected void onReceiveBrodcast(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onPostConnect()
	{
		// TODO: Implement this method
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
					
						Timer timer = new Timer();
						timer.scheduleAtFixedRate(new TimerTask() {
							
							@Override
							public void run() {
								if(imService != null && !signalSent) {
									imService.signUpUser(usernameText.getText().toString(), 
											passwordText.getText().toString(), 
											eMailText.getText().toString());
									signalSent = true;
								}
								
							}
						}, 0, 2000);
							
							
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

}
