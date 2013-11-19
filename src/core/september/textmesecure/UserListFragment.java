package core.september.textmesecure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserListFragment extends Fragment {
	  public static final String ARG_OS= "OS";
	  private String string;
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_layout, null);
	    TextView textView = (TextView) view.findViewById(R.id.fargmentTextView);
	    textView.setText(string);
	    return view;
	  }
	  @Override
	  public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);  
	  }
	  @Override
	  public void setArguments(Bundle args) {
	    string = args.getString(ARG_OS);
	  }

}
