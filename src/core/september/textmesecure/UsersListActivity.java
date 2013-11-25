package core.september.textmesecure;






import com.quickblox.module.users.model.QBUser;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import core.september.textmesecure.configs.Config;
import core.september.textmesecure.services.O9IMService;
import core.september.textmesecure.supertypes.FragmentFactory;
import core.september.textmesecure.supertypes.O9BaseFragmentActivity;

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
	        bundle = savedInstanceState == null ? new Bundle() : savedInstanceState;
	       
	        //QBUser user = new QBUser(getIntent().getExtras().getString(Config.MY_LOGIN), getIntent().getExtras().getString(Config.MY_PASSWORD));
	       
	        
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
	        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	        getSupportActionBar().setHomeButtonEnabled(true);

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
	            	getSupportActionBar().setTitle(mTitle);
	                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	            }

	            public void onDrawerOpened(View drawerView) {
	            	getSupportActionBar().setTitle(mTitle);
	            	supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
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
	    
	    
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        //MenuInflater inflater = getSupportMenuInflater();
	        //inflater.inflate(R.menu.main, menu);
	        return super.onCreateOptionsMenu(menu);
	    }
//
//	    /* Called whenever we call invalidateOptionsMenu() */
	    @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
	        // If the nav drawer is open, hide action items related to the content view
	        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
	        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
	        return super.onPrepareOptionsMenu(menu);
	    }
//
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	         // The action bar home/up action should open or close the drawer.
	         // ActionBarDrawerToggle will take care of this.
	        if (mDrawerToggle.onOptionsItemSelected(item)) {
	            return true;
	        }
	        else  return super.onOptionsItemSelected(item);
	        // Handle action buttons
//	        switch(item.getItemId()) {
//	        case R.id.action_websearch:
//	            // create intent to perform web search for this planet
//	            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//	            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
//	            // catch event that there's no activity to handle intent
//	            if (intent.resolveActivity(getPackageManager()) != null) {
//	                startActivity(intent);
//	            } else {
//	                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//	            }
//	            return true;
//	        default:
//	            return super.onOptionsItemSelected(item);
//	        }
	    }
	    
	    @Override
	    public void selectItem(int position) {
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
