package core.september.textmesecure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.quickblox.module.chat.QBChat;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;

import core.september.textmesecure.configs.Config;
import core.september.textmesecure.interfaces.IAppManager;
import core.september.textmesecure.services.O9IMService;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class UsersListActivity extends O9BaseActivity {
	private final static String TAG = UsersListActivity.class.getSimpleName();
	private IAppManager imService;
	 private ListView usersList;
	 private ProgressDialog progressDialog;

	
	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.users_list);

	        usersList = (ListView) findViewById(R.id.usersList);

	        progressDialog = new ProgressDialog(this);
	        progressDialog.setMessage("Loading fiends list");
	        progressDialog.show();

	        // ================= QuickBlox ===== Step 4 =================
	        // Get all users of QB application.
	        imService.getFriendList();
	        
	        final List<QBUser> users = imService.getFriendList();
            ArrayList<Map<String, String>> usersListForAdapter = new ArrayList<Map<String, String>>();
           
            for (QBUser u : users) {
                Map<String, String> umap = new HashMap<String, String>();
                umap.put(Config.USER_LOGIN, u.getLogin());
                umap.put(Config.CHAT_LOGIN, QBChat.getChatLoginFull(u));
                usersListForAdapter.add(umap);
            }

            // Put users list into adapter.
            SimpleAdapter usersAdapter = new SimpleAdapter(this, usersListForAdapter,
                    android.R.layout.simple_list_item_2,
                    new String[]{Config.USER_LOGIN, Config.CHAT_LOGIN},
                    new int[]{android.R.id.text1, android.R.id.text2});

            usersList.setAdapter(usersAdapter);
            usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    // Prepare QBUser objects to pass it into next activities using bundle.
                    QBUser friendUser = users.get(i);

                    Intent intent = new Intent(UsersListActivity.this, ChatActivity.class);
                    Bundle extras = getIntent().getExtras();
                    intent.putExtra(Config.FRIEND_ID, friendUser.getId());
                    intent.putExtra(Config.FRIEND_LOGIN, friendUser.getLogin());
                    intent.putExtra(Config.FRIEND_PASSWORD, friendUser.getPassword());
                    // Add extras from previous activity.
                    intent.putExtras(extras);

                    startActivity(intent);
                }
            });
	        
	    }
}
