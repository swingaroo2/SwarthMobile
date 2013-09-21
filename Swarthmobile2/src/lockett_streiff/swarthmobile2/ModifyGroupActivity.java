package lockett_streiff.swarthmobile2;

import lockett_streiff.swarthmobile2.R;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


// This activity is used for 2 purposes: Creating new Group and Editing existing Group
// Every time it's loaded, the program should check whether the Group object in the bundle is exist
// If it's exist, this means the activity is going to edit that group
// Otherwise, the activity is going to create a new group
public class ModifyGroupActivity extends GeneralActivity {

	// a Group object to hold the data about the current group being processed
	// If the activity is creating a new group, this object will be null
	// Otherwise, it will be retrieved from the bundle
	private Group group = null;

	// DatabaseAdapter for interacting with database
	private DatabaseAdapter databaseAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_group);

		// Check whether this activity is going to create a new Group or edit an existing group
		// First, retrieve the Group object from the bundle
		Bundle modifyGroupBundle = this.getIntent().getExtras();
		try {
			this.group = (Group) modifyGroupBundle.getSerializable(Group.GROUP_BUNDLE_KEY);
		} catch (Exception ex){
			ex.printStackTrace();
		}
		// Next, check if it is null
		if (group != null){
			// If the Group object exist, load data from it and put to the form
			putDataToForm();
			// After that, change the title of the activity to "Edit group " + group name
		} else {
			// If the Group object not exist, change the title of the activity to "Create new Group"
		}

		// Open the connection to database
		databaseAdapter = new DatabaseAdapter(this);
		databaseAdapter.open();

	}

	// This function is used to load data from this.group object and put it to form
	private void putDataToForm(){
		// First check if the group object is exist
		if (group != null){
			// Load group title into text field
			EditText groupTitleEditText = (EditText) findViewById(R.id.activity_modify_group_Edittext_group_title);
			groupTitleEditText.setText(this.group.getTitle());
		}
	}

	@Override
	public void onBackPressed() {
		// Show the confirmation dialog
		// The rest (leave activity or not), the function ApplicationDialogHandler.showConfirmCancelDialog() will handle
		ConfirmCancelDialogHandler.showConfirmCancelDialog(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){

		// When user select Cancel or Home button, show a confirmation dialog
		case R.id.activity_modify_group_Menu_actionbar_Item_cancel:
		case android.R.id.home:
			// Show the confirmation dialog
			// The rest (leave activity or not), the function ApplicationDialogHandler.showConfirmCancelDialog() will handle
			ConfirmCancelDialogHandler.showConfirmCancelDialog(this);
			return true;

			// When user select Save button, save the data to database
		case R.id.activity_modify_group_Menu_actionbar_Item_save:
			insertGroup();
			return true;
			// default case, return the base class function
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Insert the current group into database 
	private void insertGroup(){
		// Get the group title
		EditText groupTitle = (EditText) findViewById(R.id.activity_modify_group_Edittext_group_title);
		// insert new group
		databaseAdapter.insertGroup(databaseAdapter.getNewGroupId(), groupTitle.getText().toString());
		// kill this activity and return to the last activity
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.modify_group, menu);
		return true;
	}

}
