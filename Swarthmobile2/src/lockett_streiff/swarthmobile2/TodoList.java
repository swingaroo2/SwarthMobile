package lockett_streiff.swarthmobile2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;

public class TodoList extends Activity {

	private static final String tag = "ToDoList";
	DragSortListView listView;
	private static ArrayAdapter<String> adapter;
	EditText et;

	FileOutputStream fos1;
	FileOutputStream fos2;
	FileInputStream fis1;
	FileInputStream fis2;
	private static ArrayList<String> list;
	private static String checkboxresult;
	CheckBox DoNotShowAgain;

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			Log.i(tag, "onDrop");
			if (from != to) {
				String item = adapter.getItem(from);
				adapter.remove(item);
				adapter.insert(item, to);
			}
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			Log.i(tag, "onRemove");
			adapter.remove(adapter.getItem(which));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		if (!restoreData()) {
			Log.i(tag, "Unable to restore data");
			listView = (DragSortListView) findViewById(R.id.listview);
			list = new ArrayList<String>();
			adapter = new ArrayAdapter<String>(this, R.layout.event_list_item,
					R.id.grid_item_label, list);
			listView.setAdapter(adapter);
		}
		listView.setDropListener(onDrop);
		listView.setRemoveListener(onRemove);
		et = (EditText) this.findViewById(R.id.input_et);

		DragSortController controller = new DragSortController(listView);
		controller.setDragHandleId(R.id.grid_item_label);
		// controller.setClickRemoveId(R.id.);
		controller.setRemoveEnabled(true);
		controller.setSortEnabled(true);
		controller.setDragInitMode(0);

		// controller.setRemoveMode(removeMode);

		listView.setFloatViewManager(controller);
		listView.setOnTouchListener(controller);
		listView.setDragEnabled(true);

		if (!checkboxresult.equals("skipped")) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					TodoList.this);
			LayoutInflater adbInflater = LayoutInflater.from(this);
			View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
			DoNotShowAgain = (CheckBox) eulaLayout.findViewById(R.id.skip);
			alertDialog.setView(eulaLayout);
			alertDialog.setTitle("Instructions");
			alertDialog
			.setMessage("Drag items to sort them by priority (place finger on text) and swipe completed tasks to delete them (place finger on blank space)");
			alertDialog.setNeutralButton("OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (DoNotShowAgain.isChecked())
						checkboxresult = "skipped";
					Log.i(tag, "Checkbox is" + checkboxresult);
					return;
				}
			});
			AlertDialog alert = alertDialog.create();
			alert.show();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/* Handle item selection */
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}
	
	@Override
	public void onPause() {

		saveData();
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		// Save to Internal Storage
		saveData();
		super.onBackPressed();
	}

	@Override
	public void onDestroy() {
		// Save to Internal Storage
		saveData();
		super.onDestroy();
	}

	private void saveData() {
		// Toast.makeText(SimpleCalendar.this,
		// "Saving Calendar data to Internal Storage",
		// Toast.LENGTH_SHORT).show();

		try {
			fos1 = openFileOutput("todoList_Swarthmobile", Context.MODE_PRIVATE);
			fos2 = openFileOutput("Checkbox2_Swarthmobile",
					Context.MODE_PRIVATE);
			// fos3 = openFileOutput("coursesAdapter_file",
			// Context.MODE_PRIVATE);
			ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
			ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
			// ObjectOutputStream oos3 = new ObjectOutputStream(fos3);
			oos1.writeObject(TodoList.list);
			oos1.close();
			fos1.close();
			oos2.writeObject(TodoList.checkboxresult);
			oos2.close();
			fos2.close();
			// oos3.writeObject(this.coursesAdapter); oos3.close();
			// fos3.close();
		} catch (FileNotFoundException e) {
			Log.i(tag, "FAILLLLL!  1");
			e.printStackTrace();
		} catch (IOException e) {
			Log.i(tag, "FAILLLL!  2");
			e.printStackTrace();
		}
	}

	public void addItem(View v) {
		Log.i(tag, "addItem");
		adapter.add(et.getText().toString());
		et.setText("");
	}

	private boolean restoreData() {
		try {
			File dir1 = getBaseContext().getDir("todoList_Swarthmobile",
					Context.MODE_PRIVATE);
			File dir2 = getBaseContext().getDir("Checkbox2_Swarthmobile",
					Context.MODE_PRIVATE);
			int files = dir1.listFiles().length + dir2.listFiles().length;
			if (files < 0) {
				Log.i(tag, "failed here 1");
				return false;
			}
			TodoList.list = new ArrayList<String>();
			TodoList.checkboxresult = new String();
			File file1 = getBaseContext().getFileStreamPath(
					"todoList_Swarthmobile");
			File file2 = getBaseContext().getFileStreamPath(
					"Checkbox2_Swarthmobile");
			if (!file1.exists() || !file2.exists()) {
				Log.i(tag, "failed here 2");
				return false;
			}
			fis1 = openFileInput("todoList_Swarthmobile");
			fis2 = openFileInput("Checkbox2_Swarthmobile");
			if (fis1 == null || fis2 == null) {
				Log.i(tag, "failed here 3");
				return false;
			}
			ObjectInputStream ois1 = new ObjectInputStream(fis1);
			ObjectInputStream ois2 = new ObjectInputStream(fis2);
			if (ois1 == null || ois2 == null) {
				Log.i(tag, "failed here 4");
				return false;
			}

			Log.i(tag, "reading object");

			TodoList.list = (ArrayList<String>) ois1.readObject();

			Log.i(tag, "readObject worked");

			TodoList.checkboxresult = (String) ois2.readObject();

			fis1.close();
			fis2.close();
			ois1.close();
			ois2.close();

			TodoList.adapter = new ArrayAdapter<String>(this,
					R.layout.event_list_item, R.id.grid_item_label, list);

			listView = (DragSortListView) this.findViewById(R.id.listview);
			listView.setAdapter(adapter);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.i(tag, "failed here 5");
			return false;
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
			Log.i(tag, "failed here 6");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Log.i(tag, "failed here 7");
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Log.i(tag, "failed here 8");
			return false;
		}

		// Add events to calendar

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list, menu);
		return true;
	}

}
