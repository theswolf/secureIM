package core.september.textmesecure.supertypes;

import core.september.textmesecure.fragments.UserListFragment;
import android.support.v4.app.Fragment;

public class FragmentFactory {

	public static Fragment getFragment(int position) {
		Fragment fragmet = null;
		switch (position) {
		case 0:
			//fragmet = new UserListFragment();
			break;

		default:
			break;
		}
		return fragmet;
	}
}
