package core.september.textmesecure.fragments;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.XMPPException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.quickblox.core.QBCallback;
import com.quickblox.core.result.Result;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.users.result.QBUserPagedResult;

import core.september.textmesecure.R;
import core.september.textmesecure.configs.Utils;
import core.september.textmesecure.fragments.adapters.CheckBoxAdapter;
import core.september.textmesecure.fragments.models.CheckBoxModel;
import core.september.textmesecure.supertypes.O9BaseFragment;
import core.september.textmesecure.supertypes.O9BaseFragmentActivity;

public class AddUserFragment extends O9BaseFragment implements QBCallback{
	private ListView usersList;
	private ProgressDialog progressDialog;
	private Button addFriendButton;
	private final static String TAG = AddUserFragment.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_add_friend_layout, null);

		addFriendButton = (Button) view.findViewById(R.id.searchFriendButton);
		usersList = (ListView) view.findViewById(android.R.id.list);
		final TextView textView = (TextView) view.findViewById(R.id.textView);
		
		addFriendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!Strings.isNullOrEmpty((""+textView.getText()).trim())) {
					String value = (""+textView.getText()).trim();
					boolean isMail = Utils.isEmail(value);
					if(isMail) {
						QBUsers.getUserByEmail(value,AddUserFragment.this);
					}
					else {
						QBUsers.getUsersByFullName(value,AddUserFragment.this);
					}
				}
				
			}
		});
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onComplete(Result result) {
		if(result.isSuccess()) {
			QBUserPagedResult pagedResult = (QBUserPagedResult) result;
			if(pagedResult.getTotalEntries() > 0 ) {
				
				final List<CheckBoxModel> modelList = new ArrayList<CheckBoxModel>();
				for(QBUser user: pagedResult.getItems()) {
					CheckBoxModel model = new CheckBoxModel(user.getFullName(),user.getLogin());
					modelList.add(model);
				}
				
				ArrayAdapter<CheckBoxModel> adapter = new CheckBoxAdapter(getActivity(),modelList);
				usersList.setAdapter(adapter);
				
				addFriendButton.setText("Add checked");
				addFriendButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						for(CheckBoxModel model: modelList) {
							if(model.isSelected()) {
//								try {
//									getService().addFriend(model.getLogin());
//								} catch (XMPPException e) {
//									// TODO Auto-generated catch block
//									AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//						            dialog.setMessage("Error(s) occurred. Look into DDMS log for details, " +
//						                    "please. Errors: " + e.getMessage()).create().show();
//								}
							}
						}
						
						((O9BaseFragmentActivity)getActivity()).selectItem(0);
					}
				});
			}
			
			
			
		}
		
		else {
			 AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
	            dialog.setMessage("Error(s) occurred. Look into DDMS log for details, " +
	                    "please. Errors: " + result.getErrors()).create().show();
		}
		
	}

	@Override
	public void onComplete(Result arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	

}
