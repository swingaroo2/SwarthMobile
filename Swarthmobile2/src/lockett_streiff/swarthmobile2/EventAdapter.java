package lockett_streiff.swarthmobile2;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventAdapter extends ArrayAdapter<Event> {
	private List<Event> events;
	private LayoutInflater inflater;

	public EventAdapter(Activity context, List<Event> events) {
		super(context, R.layout.list_item);
		this.events = events;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public Event getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		
		
		View vi = convertView;

		if (convertView == null)
			vi = inflater.inflate(R.layout.list_item, null);
		
		Event event = getItem(position);
		//String contact = event.contact;
		
		/* Need to change String resources to match Event object fields */
		TextView name = (TextView) vi.findViewById(R.id.name);
		TextView time = (TextView) vi.findViewById(R.id.time);
		TextView location = (TextView) vi.findViewById(R.id.location);
		
		return vi;
	}
}
