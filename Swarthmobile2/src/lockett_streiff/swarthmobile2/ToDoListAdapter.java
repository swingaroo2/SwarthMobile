package lockett_streiff.swarthmobile2;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ToDoListAdapter extends BaseAdapter {

	Context context;
    List<String> data;
    private LayoutInflater inflater = null;
	
	public ToDoListAdapter(Context context, List<String> items) {
		this.context = context;
		this.data = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return this.data.size();
	}

	@Override
	public Object getItem(int position) {
		return this.data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.todo_list_item, null);
        TextView text = (TextView) vi.findViewById(R.id.todo_list_item_tv);
        text.setText(this.data.get(position));
        return vi;
		
	}

}
