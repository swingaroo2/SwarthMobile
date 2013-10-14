package lockett_streiff.swarthmobile2;



import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

// A class consists of static functions that help the application navigation between activity
// The idea of the class is that many events can call to the same activity,
// so that every time they need to start one particular activity, just call the static function here
// and it will do the rest of the work
// This is for reusable purpose
public class ApplicationNavigationHandler {
	
	// Go to ViewAllGroupsActivity
	public static void showAllGroups(Activity sourceActivity){
		Intent showAllGroupsIntent = new Intent(sourceActivity, ViewAllGroupsActivity.class);
		sourceActivity.startActivity(showAllGroupsIntent);
	}
	
	// Go to ViewTaskDetailActivity
	public static void viewTaskDetail(Activity sourceActivity, Task task){
		// create new intent
		Intent viewTaskDetailIntent = new Intent(sourceActivity, ViewTaskDetailActivity.class);
		// put the Task object into the bundle
		Bundle viewTaskDetailBundle = new Bundle();
		viewTaskDetailBundle.putSerializable(Task.TASK_BUNDLE_KEY, task);
		// put the bundle into the intent
		viewTaskDetailIntent.putExtras(viewTaskDetailBundle);
		// start the activity
		sourceActivity.startActivity(viewTaskDetailIntent);
	}
	
	// Go to ViewAllTasksActivity
	public static void showAllTasks(Activity sourceActivity){
		Intent showAllTasksIntent = new Intent(sourceActivity, ViewAllTasksActivity.class);
		sourceActivity.startActivity(showAllTasksIntent);
	}
	
	// Go to SettingActivity
	public static void showSetting(Activity sourceActivity){
		Intent showSettingIntent = new Intent(sourceActivity, SettingActivity.class);
		sourceActivity.startActivity(showSettingIntent);
	}
	
	// Go to ModifyGroupActivity to add a new group
	public static void addNewGroup(Activity sourceActivity, int resultCode){
		Intent addNewGroupIntent = new Intent(sourceActivity, ModifyGroupActivity.class);
		sourceActivity.startActivityForResult(addNewGroupIntent, resultCode);
	}
	
	// Go to ModifyGroupActivity to edit an existing group
	public static void editExistingGroup(Activity sourceActivity, Group existingGroup){
		Intent editExistingGroupIntent = new Intent(sourceActivity, ModifyGroupActivity.class);
		// put the group to edit into bundle
		Bundle editExistingGroupBundle = new Bundle();
		editExistingGroupBundle.putSerializable(Group.GROUP_BUNDLE_KEY, existingGroup);
		// put the bundle into intent
		editExistingGroupIntent.putExtras(editExistingGroupBundle);
		// start the activity
		sourceActivity.startActivity(editExistingGroupIntent);
	}
	
	// Go to ModifyTaskActivity to edit and existing Task
	public static void editExistingTask(Activity sourceActivity, Task existingTask){
		Intent editExistingTaskIntent = new Intent(sourceActivity, ModifyTaskActivity.class);
		// put the task to edit into the bundle
		Bundle editExistingTaskBundle = new Bundle();
		// editExistingTaskBundle.putSerializable(Task.TASK_BUNDLE_KEY, existingTask);
		editExistingTaskBundle.putSerializable(Task.TASK_BUNDLE_KEY, existingTask);
		// put the bundle into intent
		editExistingTaskIntent.putExtras(editExistingTaskBundle);
		// start the activity
		sourceActivity.startActivityForResult(editExistingTaskIntent, ViewTaskDetailActivity.EDIT_TASK_REQUEST_CODE);
	}
	
	// Go to ModifyTaskActivity to add new task
	public static void addNewTask(Activity sourceActivity, DatabaseAdapter databaseAdapter){
		// Get all groups from database
		Cursor allGroupsCursor = databaseAdapter.getAllGroups();
		// Check if there is no group, then ask user to add group first
		if(allGroupsCursor.getCount() == 0){
			// Ask user to add group first
			MessageDialogHandler.showMessageDialog(sourceActivity, "No group added!\nPlease go back and add group first");
		} else {
			// Start the activity for user to add task
			Intent addNewTaskIntent = new Intent(sourceActivity, ModifyTaskActivity.class);
			sourceActivity.startActivityForResult(addNewTaskIntent, ViewAllTasksActivity.ADD_NEW_TASK_REQUEST_CODE);
		}
	}
}
