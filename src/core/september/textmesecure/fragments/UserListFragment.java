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
import core.september.textmesecure.services.O9IMService;
import core.september.textmesecure.supertypes.O9BaseFragment;
import core.september.textmesecure.supertypes.O9BaseFragmentActivity;

public class UserListFragment extends O9BaseFragment {
	private ListView usersList;
	private ProgressDialog progressDialog;
	private View view;
	private boolean loggedIn;
	private ArrayList<Map<String, String>> usersListForAdapter;

	private final static String TAG = UserListFragment.class.getSimpleName();
	
	public BroadcastReceiver presenceReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Presence availability = getService().getPresence();
			Map<String, String> umap = new HashMap<String, String>();
			Mode userMode = availability.getMode();
			umap.put(Config.USER_STATUS,retrieveState_mode(userMode, availability.isAvailable()));
			usersListForAdapter.add(umap);
			
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
    				QBUser friendUser = getService().getRosterEntries().get(i);

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
			
		}
		
	};
	
	public BroadcastReceiver usersRetrived = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
        	try {
	            
	            final List<QBUser> users = getService().getRosterEntries();
	    		usersListForAdapter = new ArrayList<Map<String, String>>();

	    		for (QBUser u : users) {
	    			getService().getPresence(u.getLogin());
	    			
	    		}
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
	};
	
	
//	private  AsyncTask<Void, Void, Void> friendListTask = new AsyncTask<Void, Void, Void>(){
//
//		@Override
//		protected Void doInBackground(Void... arg0) {
//			while(getService() == null) { }
//				
//				
//				try {
//					
//					final List<QBUser> users = getService().getFriendList();
//					ArrayList<Map<String, String>> usersListForAdapter = new ArrayList<Map<String, String>>();
//
//					for (QBUser u : users) {
//						Map<String, String> umap = new HashMap<String, String>();
//						umap.put(Config.USER_LOGIN, u.getLogin());
//						// umap.put(Config.CHAT_LOGIN, QBChat.getChatLoginFull(u));
//						Presence availability = getService().getPresence(u.getLogin());
//						Mode userMode = availability.getMode();
//						umap.put(Config.USER_STATUS,
//								retrieveState_mode(userMode, availability.isAvailable()));
//						usersListForAdapter.add(umap);
//					}
//
//					// Put users list into adapter.
//					SimpleAdapter usersAdapter = new SimpleAdapter(getActivity(),
//							usersListForAdapter, android.R.layout.simple_list_item_2,
//							new String[] { Config.USER_LOGIN, Config.USER_STATUS },
//							new int[] { android.R.id.text1, android.R.id.text2 });
//
//					usersList.setAdapter(usersAdapter);
//					usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//						@Override
//						public void onItemClick(AdapterView<?> adapterView, View view,
//								int i, long l) {
//
//							// Prepare QBUser objects to pass it into next activities using
//							// bundle.
//							QBUser friendUser = users.get(i);
//
//							Intent intent = new Intent(getActivity(), ChatActivity.class);
//							Bundle extras = getActivity().getIntent().getExtras();
//							intent.putExtra(Config.FRIEND_ID, friendUser.getId());
//							intent.putExtra(Config.FRIEND_LOGIN, friendUser.getLogin());
//							intent.putExtra(Config.FRIEND_PASSWORD,friendUser.getPassword());
//							// Add extras from previous activity.
//							intent.putExtras(extras);
//
//							startActivity(intent);
//						}
//					});
//					
//				}
//				
//				catch(Throwable t) {
//					android.util.Log.e(TAG, t.getMessage(),t);
//				}
//				
//			
//			return null;
//		}
//	};
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		loggedIn = false;
		view = inflater.inflate(R.layout.fragment_user_list_layout, null);
		usersList = (ListView) view.findViewById(R.layout.fragment_user_list_layout);
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage("Loading fiends list");
		progressDialog.show();
		
		//friendListTask.execute();
		(new AsyncTask<Void, Void, O9IMService>() {

			@Override
			protected O9IMService doInBackground(Void... params) {
				while(getService() == null) {
					android.util.Log.d(TAG,"Waiting service up");
				}
				
				return getService();
	
			}
			
			@Override
			protected void onPostExecute(O9IMService service) {
				service.getFriendList();
			}
			
		}).execute();
		

		
		return view;
	}
	
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		//((O9BaseFragmentActivity)getActivity()).getService().getFriendList();
		super.onActivityCreated(savedInstanceState);
	}
	
	
	
	
	@Override
	public void onResume() {
		//super.onResume();
		progressDialog.hide();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(usersRetrived,new IntentFilter(Config.USER_RETRIVED));
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(presenceReceiver,new IntentFilter(Config.PRESENCE_RETRIVED));
		super.onResume();
	}
	
	@Override
	public void onPause() 
	{ 
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(usersRetrived);
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(presenceReceiver);
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
