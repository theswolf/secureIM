package core.september.textmesecure.helpers;

import java.util.List;

import com.niusounds.asd.SQLiteDAO;

import core.september.textmesecure.sql.models.User;
import android.content.Context;

public class DBHelper {
	private static DBHelper instance;
	private Context context;
	
	private DBHelper(Context context) {
		this.context = context;
	}
	
	public static DBHelper getInstance(Context context)  {
		if (instance == null) {
			instance = new DBHelper(context);
		}
		return instance;
	}
	
	public List<User> getUserList() {
		SQLiteDAO dao = SQLiteDAO.getInstance(context, User.class);
        return dao.get(User.class);
	}
}
