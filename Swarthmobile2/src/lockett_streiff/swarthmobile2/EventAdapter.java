package lockett_streiff.swarthmobile2;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventAdapter extends ArrayAdapter<Event> {
	private static final String tag = "EventAdapter";
	private List<Event> events;
	private LayoutInflater inflater;

	public EventAdapter(Activity context, List<Event> events) {
		super(context, R.layout.list_item);
		this.events = events;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//Log.i(tag, "constructor");
	}

	@Override
	public int getCount() {
		//Log.i(tag, "getCount: "+events.size());
		return events.size();
	}

	@Override
	public Event getItem(int position) {
		//Log.i(tag, "getItem: "+position);
		return this.events.get(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		//Log.i(tag, "getView");
		View vi = convertView;
		Event event = getItem(position);

		//Log.i(tag,"convertView == null: "+(convertView == null));
		
		if (convertView == null) {
			vi = inflater.inflate(R.layout.list_item, null);
			/* Need to change String resources to match Event object fields */
			TextView name = (TextView) vi.findViewById(R.id.name);
			TextView time = (TextView) vi.findViewById(R.id.time);
			TextView location = (TextView) vi.findViewById(R.id.location);
			//Log.i(tag,String.format("Get view %d", position));
			name.setText(event.getTitle());
			time.setText(event.getTime());
			location.setText(event.getLocation());
		} else {
			LinearLayout view = (LinearLayout) convertView;
			TextView name = (TextView) view.findViewById(R.id.name);
			TextView time = (TextView) view.findViewById(R.id.time);
			TextView location = (TextView) view.findViewById(R.id.location);
			//Log.i(tag,String.format("Get view %d", position));
			name.setText(event.getTitle());
			time.setText(event.getTime());
			location.setText(event.getLocation());
			return convertView;
			
		}

		return vi;
	}
}
