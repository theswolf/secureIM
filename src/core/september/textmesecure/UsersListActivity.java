package core.september.textmesecure;



import core.september.textmesecure.configs.Config;
import core.september.textmesecure.supertypes.FragmentFactory;
import core.september.textmesecure.supertypes.O9BaseFragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UsersListActivity extends O9BaseFragmentActivity{

	 private DrawerLayout mDrawerLayout;
	    private ListView mDrawerList;
	    private ActionBarDrawerToggle mDrawerToggle;

	    private CharSequence mDrawerTitle;
	    private CharSequence mTitle;
	    private String[] mMenuTitles;
	    private Bundle bundle;
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_users_list);
	        bundle = savedInstanceState;
	        mTitle = mDrawerTitle = getTitle();
	        mMenuTitles = getResources().getStringArray(R.array.lis_activity_menu);
	        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	        mDrawerList = (ListView) findViewById(R.id.left_drawer);

	        // set a custom shadow that overlays the main content when the drawer opens
	        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	        // set up the drawer's list view with items and click listener
	        mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, mMenuTitles));
	        
	        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

	        // enable ActionBar app icon to behave as action to toggle nav drawer
	        //getActionBar().setDisplayHomeAsUpEnabled(true);
	        //getActionBar().setHomeButtonEnabled(true);

	        // ActionBarDrawerToggle ties together the the proper interactions
	        // between the sliding drawer and the action bar app icon
	        mDrawerToggle = new ActionBarDrawerToggle(
	                this,                  /* host Activity */
	                mDrawerLayout,         /* DrawerLayout object */
	                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
	                R.string.drawer_open,  /* "open drawer" description for accessibility */
	                R.string.drawer_close  /* "close drawer" description for accessibility */
	                ) {
	            public void onDrawerClosed(View view) {
	                //getActionBar().setTitle(mTitle);
	                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	            }

	            public void onDrawerOpened(View drawerView) {
	                //getActionBar().setTitle(mDrawerTitle);
	                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	            }
	        };
	        mDrawerLayout.setDrawerListener(mDrawerToggle);

	        if (savedInstanceState == null) {
	            selectItem(0);
	        }
	    }
	    
	    @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        // Sync the toggle state after onRestoreInstanceState has occurred.
	        mDrawerToggle.syncState();
	    }

	    
	    private class DrawerItemClickListener implements ListView.OnItemClickListener {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            selectItem(position);
	        }
	    }
	    
	    private void selectItem(int position) {
	        // update the main content by replacing fragments
	        Fragment fragment = FragmentFactory.getFragment(position);
	        //((Bundle args = new Bundle();
	        bundle.putInt(Config.USER_LIST_MENU, position);
	        fragment.setArguments(bundle);

	        FragmentManager fragmentManager = getSupportFragmentManager();
	        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

	        // update selected item and title, then close the drawer
	        mDrawerList.setItemChecked(position, true);
	        setTitle(mMenuTitles[position]);
	        mDrawerLayout.closeDrawer(mDrawerList);
	    }
	    
	    
}
