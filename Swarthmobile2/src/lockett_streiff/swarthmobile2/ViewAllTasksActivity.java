package lockett_streiff.swarthmobile2;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ViewAllTasksActivity extends GeneralActivity {

	// The List View that shows all Tasks
	private ListView allTasksListView;

	// Custom SimpleCursorAdapter
	private ToDoListAdapter tdAdapter;
	
	// DatabaseAdapter for interacting with database
	private DatabaseAdapter databaseAdapter;

	// The cursor for query all groups from database
	private Cursor allTasksCursor;

	// Adapter for All Tasks List View
	private SimpleCursorAdapter allTasksListViewAdapter;

	private static final String tag = "ViewAllTasksActivity";

	// The Add New Task request code
	public static final int ADD_NEW_TASK_REQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_all_tasks);

		// set action listener for allTasksListView
		
		allTasksListView = (ListView) findViewById(R.id.activity_view_all_tasks_Listview_all_tasks);
		allTasksListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				allTaskListViewItemClickHandler(arg0, arg1, arg2);
			}
		});

		// Open the connection to database
		databaseAdapter = new DatabaseAdapter(this);
		databaseAdapter.open();
		
		// Init all tasks and add them to list view
		initAllTasksListView();
	}

	// Init the Tasks List View
	// Load all Tasks from database and put them to list view
	public void initAllTasksListView(){
		// Check if the databaseAdapter is not null
		if(this.databaseAdapter != null){
			// Get all Tasks
			allTasksCursor = databaseAdapter.getAllTasks();
			// TODO replace the deprecated startManagingCursor method with an alternative one
			startManagingCursor(allTasksCursor);
			// Select DB columns to use in this Activity
			String[] from = new String[]{DatabaseAdapter.TASK_TABLE_COLUMN_TITLE};
			// XML-defined Views to which DB data is bound
			int[] to = new int[]{R.id.activity_view_all_groups_listview_all_groups_layout_textview_group_title};
			//Log.i(tag , Arrays.toString(to));
			
			//tdAdapter = new ToDoListAdapter(this, R.layout.todo_list_item, allTasksCursor, from, to);
			allTasksListViewAdapter = new SimpleCursorAdapter(this,R.layout.activity_view_all_groups_listview_all_groups_layout, allTasksCursor, from, to);
			
			// Set the adapter for the list view
			this.allTasksListView.setAdapter(allTasksListViewAdapter);
			//this.allTasksListView.setAdapter(tdAdapter);
		}
	}

	// Handle the item clicked event of allTasksListView
	private void allTaskListViewItemClickHandler(AdapterView<?> adapterView, View listView, int selectedItemId){
		// Create a new Task object and init the data
		// After that pass it to the next activity to display detail
		Task selectedTask = new Task();
		// move the cursor to the right position
		allTasksCursor.moveToFirst();
		allTasksCursor.move(selectedItemId);
		// set the data for selectedTask
		// set id
		selectedTask.setId(allTasksCursor.getString(allTasksCursor.getColumnIndex(DatabaseAdapter.TASK_TABLE_COLUMN_ID)));
		// set title
		selectedTask.setTitle(allTasksCursor.getString(allTasksCursor.getColumnIndex(DatabaseAdapter.TASK_TABLE_COLUMN_TITLE)));
		// set due date
		selectedTask.getDueDate().setTimeInMillis(allTasksCursor.getLong(allTasksCursor.getColumnIndex(DatabaseAdapter.TASK_TABLE_COLUMN_DUE_DATE)));
		// set note
		selectedTask.setNote(allTasksCursor.getString(allTasksCursor.getColumnIndex(DatabaseAdapter.TASK_TABLE_COLUMN_NOTE)));
		// set priority level
		selectedTask.setPriorityLevel(allTasksCursor.getInt(allTasksCursor.getColumnIndex(DatabaseAdapter.TASK_TABLE_COLUMN_PRIORITY)));
		
		// start the activity
		ApplicationNavigationHandler.viewTaskDetail(this, selectedTask);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case R.id.activity_view_all_tasks_Menu_actionbar_Item_add_task:
			ApplicationNavigationHandler.addNewTask(this, this.databaseAdapter);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_all_tasks, menu);
		return true;
	}

}
