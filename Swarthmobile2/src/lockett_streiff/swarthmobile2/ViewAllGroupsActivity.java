package lockett_streiff.swarthmobile2;

import lockett_streiff.swarthmobile2.R;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class ViewAllGroupsActivity extends GeneralActivity {
	
	// VARIABLES DEFINED HERE
	
	// DatabaseAdapter for interacting with database
	private DatabaseAdapter databaseAdapter;
	
	// The cursor for query all groups from database
	private Cursor allGroupsCursor;
	
	// Adapter for All Groups List View
	private SimpleCursorAdapter allGroupsListViewAdapter;
	
	// All Groups List View
	private ListView allGroupsListView;
	
	// 
	public static final int ADD_NEW_GROUP_REQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_all_groups);
		
		// Retrieve the all groups list view
		this.allGroupsListView = (ListView) findViewById(R.id.activity_view_all_groups_Listview_all_groups);

		// Open the connection to database
		databaseAdapter = new DatabaseAdapter(this);
		databaseAdapter.open();
		// Retrieve all data and put it to 
		initAllGroupsListView();
		
	}
	
	
	// UTILITY METHODS
	
	// Get all groups from database and then put it to list view
	private void initAllGroupsListView(){
		// Check if the databaseAdapter is not null
		if(this.databaseAdapter != null){
			// Get all groups
			allGroupsCursor = databaseAdapter.getAllGroups();
			// TODO replace the deprecated startManagingCursor method with an alternative one
			startManagingCursor(allGroupsCursor);
			// Get data from which column
			String[] from = new String[]{DatabaseAdapter.GROUP_TABLE_COULMN_TITLE};
			// Put data to which components in layout
			int[] to = new int[]{R.id.activity_view_all_groups_listview_all_groups_layout_textview_group_title};
			// Init the adapter for list view
			// TODO replace the deprecated SimpleCursorAdapter with an alternative one
			allGroupsListViewAdapter = new SimpleCursorAdapter(this,
					R.layout.activity_view_all_groups_listview_all_groups_layout, allGroupsCursor, from, to);
			// Set the adapter for the All Groups List View
			this.allGroupsListView.setAdapter(allGroupsListViewAdapter);
		}
	}
	
	
	// OVERRIDE METHODS

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_all_groups, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case R.id.activity_view_all_groups_Menu_actionbar_Item_add_group:
			ApplicationNavigationHandler.addNewGroup(this, ViewAllGroupsActivity.ADD_NEW_GROUP_REQUEST_CODE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		// Check if the requestCode is ADD_NEW_GROUP_RESULT_CODE
		if(requestCode == ViewAllGroupsActivity.ADD_NEW_GROUP_REQUEST_CODE){
			// re-init the list view
			initAllGroupsListView();
		}
	}

}
