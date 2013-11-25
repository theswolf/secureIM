package core.september.textmesecure.supertypes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import core.september.textmesecure.interfaces.IAppManager;
import core.september.textmesecure.services.O9IMService;

public abstract class O9BaseFragment extends ListFragment {

protected Handler handler;
	
    
//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		if(activity instanceof O9BaseFragmentActivity) {
//			imService = ((O9BaseFragmentActivity)getActivity()).getService();
//		}
//		
//	}
	
	
	protected O9IMService getService() {
		return ((O9BaseFragmentActivity)getActivity()).getService();
	}
	
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
    	handler = new Handler();
		super.onActivityCreated(savedInstanceState);
	}
    
    @Override
	public void onResume() {
    	//new Intent(this,O9IMService.class), this.mConnection , Context.BIND_AUTO_CREATE);  
    	getActivity().bindService(new Intent(getActivity(),O9IMService.class), ((O9BaseFragmentActivity)getActivity()).getConnection() , Context.BIND_AUTO_CREATE);
		super.onResume();
	}
//	
	@Override
	public void onPause() 
	{
		getActivity().unbindService(((O9BaseFragmentActivity)getActivity()).getConnection());
		super.onPause();
	}
}
