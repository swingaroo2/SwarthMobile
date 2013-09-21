package lockett_streiff.swarthmobile2;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EventAdapter extends ArrayAdapter<Event> {
	private static final String tag = "EventAdapter";
	private List<Event> events;
	private LayoutInflater inflater;
	private static Activity context;

	/* Email address associated with Swarthmobile calendar */
	private static String email = "";

	public EventAdapter(Activity context, List<Event> events) {
		super(context, R.layout.event_list_item);
		EventAdapter.context = context;
		this.events = events;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Log.i(tag, "constructor");
	}

	@Override
	public int getCount() {
		// Log.i(tag, "getCount: "+events.size());
		return events.size();
	}

	@Override
	public Event getItem(int position) {
		// Log.i(tag, "getItem: "+position);
		return this.events.get(position);
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		// Log.i(tag, "getView");
		View vi = convertView;
		Button addToCal;
		Button moreInfo;
		final Event event = getItem(position);

		if (convertView == null) {
			vi = inflater.inflate(R.layout.event_list_item, null);
			addToCal = (Button) vi.findViewById(R.id.calendar_button);
			moreInfo = (Button) vi.findViewById(R.id.more_info);
			/* Need to change String resources to match Event object fields */
			TextView name = (TextView) vi.findViewById(R.id.name);
			TextView time = (TextView) vi.findViewById(R.id.time);
			TextView location = (TextView) vi.findViewById(R.id.location);
			// Log.i(tag,String.format("Get view %d", position));
			name.setText(event.getTitle());
			//Log.i(tag, "Time: "+event.getTime());
			time.setText(event.getTime());
			location.setText(event.getLocation());
		} else {
			vi = convertView;
			LinearLayout vi2 = (LinearLayout) vi;
			addToCal = (Button) vi2.findViewById(R.id.calendar_button);
			moreInfo = (Button) vi2.findViewById(R.id.more_info);
			TextView name = (TextView) vi2.findViewById(R.id.name);
			TextView time = (TextView) vi2.findViewById(R.id.time);
			TextView location = (TextView) vi2.findViewById(R.id.location);
			// Log.i(tag,String.format("Get view %d", position));
			name.setText(event.getTitle());
			//Log.i(tag, "Time: "+event.getTime());
			time.setText(event.getTime());
			location.setText(event.getLocation());

		}
		moreInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getLink()));
				context.startActivity(browserIntent);
				
			}});
		
		addToCal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SimpleDateFormat format = new SimpleDateFormat(
						"MM/dd/yyyy, hh:mma");
				SimpleDateFormat formatAllDay = new SimpleDateFormat(
						"MM/dd/yyyy");
				String evTitle = event.getTitle();
				String evLocation = event.getLocation();
				int[] dates = event.getDateAsMillis();
				Log.i(tag,"dates["+dates.length+"]: "+Arrays.toString(dates));
				long time1;
				long time2;
				Calendar c1 = new GregorianCalendar(dates[2], dates[0] - 1,
						dates[1]);
				Calendar c2 = new GregorianCalendar(dates[2], dates[0] - 1,
						dates[1]);
				if (dates.length > 3) {
					// Log.i(tag,
					// "New calendar at date: "+(dates[3]-1)+"/"+dates[4]+"/"+dates[5]);
					c2 = new GregorianCalendar(dates[5], dates[3]-1, dates[4]);
				}
				int[] times = event.getTimeAsMillis();
				Log.i(tag, "Time: "+Arrays.toString(times));
				c1.setTimeZone(TimeZone.getTimeZone("America/New_York"));
				c1.set(Calendar.HOUR, times[0]);
				c1.set(Calendar.MINUTE, times[1]);
				c1.set(Calendar.SECOND, 0);
				c1.set(Calendar.MILLISECOND, 0);
				//Log.i(tag, "dates.length: " + dates.length);
				c2.setTimeZone(TimeZone.getTimeZone("America/New_York"));
				c2.set(Calendar.HOUR, times[2]);
				c2.set(Calendar.MINUTE, times[3]);
				c2.set(Calendar.SECOND, 0);
				c2.set(Calendar.MILLISECOND, 0);
				time1 = c1.getTimeInMillis();
				time2 = c2.getTimeInMillis();
				// Log.i(tag,"times: "+Arrays.toString(times));
				Log.i(tag, "What: " + evTitle);
				Log.i(tag, "Where: " + evLocation);
				if (!event.getTime().contains("All Day")) {
					if (format.format(c1.getTime()) != format.format(c2.getTime())) {
						Log.i(tag, "When: " + format.format(c1.getTime())+ " - " + format.format(c2.getTime()));
					} else {
						Log.i(tag, "When: " + format.format(c1.getTime()));
					}
				} else {
					/* All day event: Time setting doesn't apply */
					if (formatAllDay.format(c1.getTime()) != formatAllDay
							.format(c2.getTime())) {
						Log.i(tag, "When: " + formatAllDay.format(c1.getTime())
								+ " - " + formatAllDay.format(c2.getTime()));
					} else {
						Log.i(tag, "When: " + formatAllDay.format(c1.getTime()));
					}
				}
				Log.i(tag, "-----------------");

				/*
				 * Log.i(tag,"c1 time: " + c1.get(Calendar.HOUR) + ":"+
				 * c1.get(Calendar.MINUTE)); Log.i(tag,"c2 time: " +
				 * c2.get(Calendar.HOUR) + ":"+ c2.get(Calendar.MINUTE));
				 */

				if (EventAdapter.email.equals("")) {
					String uEmail = UserEmailFetcher.getEmail(context);
					Log.i(tag, "Email: " + uEmail);
					if (uEmail != null) {
						// confirmEmail(uEmail);
						/* Just add the event to the calendar */
						CalendarClient client = new CalendarClient(EventAdapter.context);
						Log.i(tag, "Null checks:\nevent: " + event
								+ "\nevTitle: " + evTitle + "\nevLocation: "
								+ evLocation + "\ntime1: " + time1
								+ "\ntime2: " + time2 + "\nuEmail: " + uEmail);
						client.newEvent(event, evTitle, evLocation, time1, time2, "Swarthmobile", uEmail);
					} else {
						setEmail();
					}
				}
			}
		});

		return vi;
	}

	private void setEmail() {

		final AlertDialog.Builder alert = new AlertDialog.Builder(this.context);
		final EditText input = new EditText(this.context);
		alert.setTitle("Please create a Google account on this device.");
		alert.setMessage("Google Calendar requires an account with an email address on your Android device to create events."
				+ "To view your accounts and create a new one, see your device settings page."
				+ "If you need further assistance, please contact ITS.\n\n"
				+ "Swarthmobile and its developers will never sell, compromise or use your email address for anything other than creating calendar events.");
		alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		alert.show();
	}

	/* User email address retrieval */
	private void confirmEmail(final String email) {

		final AlertDialog.Builder alert = new AlertDialog.Builder(this.context);
		final EditText input = new EditText(this.context);
		alert.setTitle("Confirm email address");
		alert.setMessage("Google Calendar requires an account with an email address on your Android device to create events.\n\n"
				+ "We found this email address: "
				+ email
				+ "\n\n"
				+ "Swarthmobile and its developers will never sell, compromise or use your email address for anything other than creating calendar events.");
		alert.setPositiveButton("Use this email address",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Toast.makeText(context.getApplicationContext(),
								"Saving email address: " + email,
								Toast.LENGTH_SHORT).show();
						EventAdapter.email = email;

					}
				});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.show();
	}

}
