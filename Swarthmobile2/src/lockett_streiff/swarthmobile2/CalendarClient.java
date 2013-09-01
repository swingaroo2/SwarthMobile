package lockett_streiff.swarthmobile2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TimeZone;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.widget.Toast;

public class CalendarClient {

	private static final String tag = "HelloAndroidCalendar";
	private List<String> calendars;
	private Activity context;
	
	public CalendarClient(Activity context) {
		this.context = context;
		this.calendars = getCalendars();
		Log.i(tag, "Calendars: " + calendars);
		// newCalendar("Swarthmobile","zachls17@gmail.com");
		Log.i(tag, "Calendar ID: " + getCalendarId("Swarthmore Campus Events"));
	}
	
	private ArrayList<String> getCalendars() {
		LinkedHashSet<String> lhs = new LinkedHashSet<String>();
		String[] projection = new String[] { Calendars._ID, Calendars.NAME,
				Calendars.ACCOUNT_NAME, Calendars.ACCOUNT_TYPE };
		Cursor calCursor = this.context.getContentResolver().query(Calendars.CONTENT_URI,
				projection, Calendars.VISIBLE + " = 1", null,
				Calendars._ID + " ASC");
		if (calCursor.moveToFirst()) {
			do {
				long id = calCursor.getLong(0);
				String displayName = calCursor.getString(1);
				lhs.add(displayName);
			} while (calCursor.moveToNext());
		}
		return new ArrayList<String>(lhs);
	}

	private long getCalendarId(String cal) {
		String[] projection = new String[] { Calendars._ID };
		String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
				+ Calendars.ACCOUNT_TYPE + " = ?))";
		// use the same values as above:
		String[] selArgs = new String[] { cal,
				CalendarContract.ACCOUNT_TYPE_LOCAL };
		Cursor cursor = this.context.getContentResolver().query(Calendars.CONTENT_URI,
				projection, selection, selArgs, null);
		if (cursor.moveToFirst()) {
			return cursor.getLong(0);
		}
		return -1;
	}

	private void newCalendar(String cal, String email) {

		ContentValues values = new ContentValues();
		values.put(Calendars.ACCOUNT_NAME, cal);
		values.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
		values.put(Calendars.NAME, cal);
		values.put(Calendars.CALENDAR_DISPLAY_NAME, cal);
		values.put(Calendars.CALENDAR_COLOR,
				this.context.getResources().getColor(R.color.garnet));
		values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
		values.put(Calendars.OWNER_ACCOUNT, email);
		values.put(Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().getID());
		Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI
				.buildUpon();
		builder.appendQueryParameter(Calendars.ACCOUNT_NAME, "cal");
		builder.appendQueryParameter(Calendars.ACCOUNT_TYPE,
				CalendarContract.ACCOUNT_TYPE_LOCAL);
		builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,
				"true");
		Uri uri = this.context.getContentResolver().insert(builder.build(), values);
	}

	public void newEvent(Event event, String title, String location, long start, long end, String myCal, String email) {
		
		Log.i(tag, "calendars: " + this.calendars.toString());
		if (!calendars.contains(myCal)) {
			newCalendar(myCal, email);
		}
		Log.i(tag, "email: "+email);
		long calId = getCalendarId(myCal);
		if (calId == -1) {
			// no calendar account; react meaningfully
			return;
		}
		
		 //Check if event is all day 
		int allDay = (event.getTime().contains("All Day"))? 1 : 0;
		Log.i(tag, "allDay: "+allDay);
		
		ContentValues values = new ContentValues();
		/* Under the hood stuff */ 
		values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/New_York");
		values.put(Events.CALENDAR_ID, calId);
		
		//values.put(Events.RRULE,"FREQ=DAILY;COUNT=20;BYDAY=MO,TU,WE,TH,FR;WKST=MO");
		
		/* Displayed event content (except for date handled with the cal variable) */ 
		values.put(Events.TITLE, title);
		values.put(Events.EVENT_LOCATION, location);
		values.put(Events.DTSTART, start);
		values.put(Events.DTEND, end);
		values.put(Events.ALL_DAY, allDay);
		Uri uri = this.context.getContentResolver().insert(Events.CONTENT_URI, values);
		Toast.makeText(this.context, "Added to calendar: "+title, Toast.LENGTH_SHORT).show();
		long eventId = Long.valueOf(uri.getLastPathSegment());
	}


}
