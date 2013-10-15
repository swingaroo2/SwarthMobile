package lockett_streiff.swarthmobile2;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ToDoListAdapter extends SimpleCursorAdapter {
	private final Context context;
	private final int layout;
	private final Cursor cursor;
	private final LayoutInflater mLayoutInflater;
	private final int mTitleIndex;
	private final String mTitleStr;
	
	private final class ViewHolder {
		public TextView text;
		public Button delete;
	}

	public ToDoListAdapter(Context context, int layout, Cursor c, String[] from,int[] to) {
		super(context, layout, c, from, to);
		this.context = context;
		this.layout = layout;
		this.cursor = c;
		this.mTitleIndex = c.getColumnIndex(DatabaseAdapter.TASK_TABLE_COLUMN_TITLE);
		this.mTitleStr = c.getString(this.mTitleIndex);
		this.mLayoutInflater = LayoutInflater.from(this.context);
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
		Cursor c = cursor;

		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(layout, parent, false);

		int nameCol = c.getColumnIndex(DatabaseAdapter.TASK_TABLE_COLUMN_TITLE);;
		String name = c.getString(this.mTitleIndex);

		/**
		 * Next set the name of the entry.
		 */    
		TextView text = (TextView) v.findViewById(R.id.todo_list_item_tv);
		if (text != null) {
			text.setText(name);
		}

		return v;
	}

	@Override
	public void bindView(View v, Context context, Cursor c) {
		ViewHolder vh = new ViewHolder();
		
		int nameCol = this.mTitleIndex;
		String name = this.mTitleStr;

		/**
		 * Next set the name of the entry.
		 */    
		TextView text = (TextView) v.findViewById(R.id.todo_list_item_tv);
		if (text != null) {
			text.setText(name);
		}
	}

}
