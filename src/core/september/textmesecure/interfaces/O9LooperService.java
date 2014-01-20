package core.september.textmesecure.interfaces;

import core.september.textmesecure.sql.models.User;

public interface O9LooperService {
	 void connect();
	 void signUpUser(String usernameText, String passwordText,String emailText);
	 void signInUser(String usernameText, String passwordText);
	 User getUser();
}
