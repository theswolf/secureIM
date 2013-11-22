package core.september.textmesecure.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.quickblox.module.users.model.QBUser;

import core.september.textmesecure.ChatActivity;
import core.september.textmesecure.R;
import core.september.textmesecure.configs.Config;
import core.september.textmesecure.supertypes.O9BaseFragment;

public class UserListFragment extends O9BaseFragment {
	private ListView usersList;
	private ProgressDialog progressDialog;
	private View view;
	private boolean loggedIn;

	private final static String TAG = UserListFragment.class.getSimpleName();
	
	public BroadcastReceiver loggedInReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
        	try {
	            
	            final List<QBUser> users = getService().getFriendList();
	    		ArrayList<Map<String, String>> usersListForAdapter = new ArrayList<Map<String, String>>();

	    		for (QBUser u : users) {
	    			Map<String, String> umap = new HashMap<String, String>();
	    			umap.put(Config.USER_LOGIN, u.getLogin());
	    			// umap.put(Config.CHAT_LOGIN, QBChat.getChatLoginFull(u));
	    			Presence availability = getService().getPresence(u.getLogin());
	    			Mode userMode = availability.getMode();
	    			umap.put(Config.USER_STATUS,
	    					retrieveState_mode(userMode, availability.isAvailable()));
	    			usersListForAdapter.add(umap);
	    		}

	    		// Put users list into adapter.
	    		SimpleAdapter usersAdapter = new SimpleAdapter(getActivity(),
	    				usersListForAdapter, android.R.layout.simple_list_item_2,
	    				new String[] { Config.USER_LOGIN, Config.USER_STATUS },
	    				new int[] { android.R.id.text1, android.R.id.text2 });

	    		usersList.setAdapter(usersAdapter);
	    		usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    			@Override
	    			public void onItemClick(AdapterView<?> adapterView, View view,
	    					int i, long l) {

	    				// Prepare QBUser objects to pass it into next activities using
	    				// bundle.
	    				QBUser friendUser = users.get(i);

	    				Intent intent = new Intent(getActivity(), ChatActivity.class);
	    				Bundle extras = getActivity().getIntent().getExtras();
	    				intent.putExtra(Config.FRIEND_ID, friendUser.getId());
	    				intent.putExtra(Config.FRIEND_LOGIN, friendUser.getLogin());
	    				intent.putExtra(Config.FRIEND_PASSWORD,friendUser.getPassword());
	    				// Add extras from previous activity.
	    				intent.putExtras(extras);

	    				startActivity(intent);
	    			}
	    		});
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		loggedIn = false;
		view = inflater.inflate(R.layout.fragment_user_list_layout, null);
		usersList = (ListView) view.findViewById(R.layout.fragment_user_list_layout);
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage("Loading fiends list");
		progressDialog.show();
		return view;
	}
	
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	
	
	
	@Override
	public void onResume() {
		//super.onResume();
		progressDialog.hide();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(loggedInReceiver,new IntentFilter(UserListFragment.class.getName()));
		super.onResume();
	}
	
	@Override
	public void onPause() 
	{ 
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(loggedInReceiver);
		super.onPause();
	}

	private static String retrieveState_mode(Mode userMode, boolean isOnline) {
		int userState = 0;
		/** 0 for offline, 1 for online, 2 for away,3 for busy */
		if (userMode == Mode.dnd) {
			userState = 3;
		} else if (userMode == Mode.away || userMode == Mode.xa) {
			userState = 2;
		} else if (isOnline) {
			userState = 1;
		}
		// return userState;
		String state = "";
		switch (userState) {
		case 0:
			state = "OFFLINE";
			break;
		case 1:
			state = "ONLINE";
			break;
		case 2:
			state = "AWAY";
			break;
		case 3:
			state = "BUSY";
			break;
		default:
			state = "UNKNOW";
			break;
		}
		return state;
	}

}
