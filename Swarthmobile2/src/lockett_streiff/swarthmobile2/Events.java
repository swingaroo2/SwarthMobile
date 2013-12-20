package lockett_streiff.swarthmobile2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Events extends Activity {

	private final static String tag = "Events";

	/* Constants */
	private static final int START_DATE = 0;
	private static final int END_DATE = 1;
	private static final String[] monthsArr = { "January", "February", "March",
		"April", "May", "June", "July", "August", "September", "October",
		"November", "December" };

	/* User emails acquisition */
	static List<String> emails;

	/* Date range selection Dialog stuff */
	static View layout;

	/* Buttons for date range selection */
	static Button fromDate;
	static Button toDate;

	/* Handle events ListView */
	private static ListView eventsView;
	private static List<Event> eventsList;
	static EventAdapter eventsAdapter;

	/* Event constants */
	private final int NAME = 0;
	private final int DATE = 1;
	private final int TIME = 2;
	private final int ALL_DAY = 3;
	private final int LOCATION = 4;
	private final int PAGE = 5;
	private final int DESCRIPTION = 6;
	private final int CONTACT = 7;

	/* For XML parsing */
	String streamTitle = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		eventsList = new ArrayList<Event>();
		eventsView = (ListView) this.findViewById(R.id.events_lv);
		eventsAdapter = new EventAdapter(Events.this, eventsList);
		eventsView.setAdapter(eventsAdapter);

		/* Default to today's campus events */
		defaultEvents();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/* Inflate the menu; this adds items to the action bar if it is present. */
		getMenuInflater().inflate(R.menu.swat_events, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/* Handle item selection */
		switch (item.getItemId()) {
		case R.id.action_get_date_range:
			showDialog();
			break;
		case android.R.id.home:
			onBackPressed();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}

	/*
	 * Default to getting today's campus events 
	 */
	public void defaultEvents() {

		/* Set up Calendar instance */
		final Calendar c = Calendar.getInstance();

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		String date1 = getDateAsString(year, month, day);
		date1 = parseDateString(date1);
		
		getEvents(date1,date1);
	}

	/*
	 * onClick method for the Today shortcut Button
	 */
	public void todayOnClick(View v) {
		/* Get Buttons from inflated Layout */
		Button b1 = (Button) layout.findViewById(R.id.from_date_picker);
		Button b2 = (Button) layout.findViewById(R.id.to_date_picker);

		/* Set up Calendar instance */
		final Calendar c = Calendar.getInstance();

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		String date1 = getDateAsString(year, month, day);
		/* Set Button text to default values */
		b1.setText(date1);
		b2.setText(date1);

		/* Method call to Get Events onClick */
		getEventsOnClick(v);
	}

	/*
	 * onClick method for the This Month shortcut Button
	 */
	public void thisMonthOnClick(View v) {
		/* Get Buttons from inflated Layout */
		Button b1 = (Button) layout.findViewById(R.id.from_date_picker);
		Button b2 = (Button) layout.findViewById(R.id.to_date_picker);

		/* Get Strings for current and last days of month */
		final Calendar c = Calendar.getInstance();

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		/*
		 * Not so fast! There's no point in getting events before the current
		 * day.
		 */
		String date1 = getDateAsString(year, month, day);
		b1.setText(date1);

		/* Get last day of the month */
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		int lastDay = c.get(Calendar.DAY_OF_MONTH);
		String date2 = getDateAsString(year, month, lastDay);
		b2.setText(date2);

		/* Method call to Get Events onClick */
		getEventsOnClick(v);

	}

	/*
	 * onClick method for the Get Events Button
	 */
	public void getEventsOnClick(View v) {

		/* Get text from date-picker buttons */
		String date1 = (String) ((Button) layout.findViewById(R.id.from_date_picker)).getText();
		String date2 = (String) ((Button) layout.findViewById(R.id.to_date_picker)).getText();

		/* Convert to mm/dd/yyyy format for passing into getEvents */
		String pDate1 = parseDateString(date1);
		String pDate2 = parseDateString(date2);

		/* Change text for Events header */
		TextView tv = (TextView) this.findViewById(R.id.events_list_header);
		tv.setText("Events for: " + pDate1 + " - " + pDate2);

		/* Get events for specified date range */
		getEvents(pDate1, pDate2);
	}

	/*
	 * getEventsOnClick helper function Converts date String to mm/dd/yyyy
	 * format
	 */
	private String parseDateString(String date) {
		String[] splitDate = date.replace(",", "").split(" ");
		DateTimeFormatter format = DateTimeFormat.forPattern("MMMMM");
		DateTime instance = format.withLocale(Locale.ENGLISH).parseDateTime(
				splitDate[0]);
		int month_number = instance.getMonthOfYear();
		int[] pDate = new int[] { month_number, Integer.parseInt(splitDate[1]),
				Integer.parseInt(splitDate[2]) };
		String dateStr = pDate[0] + "/" + pDate[1] + "/" + pDate[2];
		return dateStr;
	}

	/*
	 * setUpDateButtons helper function
	 */
	private static String getDateAsString(int year, int month, int day) {
		String myMonth = Events.monthsArr[month];
		String myDay = String.valueOf(day);
		String myYear = String.valueOf(year);
		return myMonth + " " + myDay + ", " + myYear;
	}

	/*
	 * onClick event listener for from date picker
	 */
	public void showFromDatePicker(View v) {
		/* Get the Button that was clicked */
		DialogFragment newFragment = new FromDatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");

	}

	/*
	 * onClick event listener for from date picker
	 */
	public void showToDatePicker(View v) {
		/* Get the Button that was clicked */
		DialogFragment newFragment = new ToDatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");

	}

	/*
	 * Change START DATE Button text to reflect date change
	 */
	public void changeFromDateText(String mDate) {
		Button mFromButton = (Button) findViewById(R.id.from_date_picker);
		mFromButton.setText(mDate);
	}

	/*
	 * Change END DATE Button text to reflect date change
	 */
	public void changeToDateText(String mDate) {
		Button mFromButton = (Button) findViewById(R.id.from_date_picker);
		mFromButton.setText(mDate);
	}

	/*
	 * onClick event listener for buttons
	 */
	public void showEvents(View v) {
		//Log.i(tag, "showEvents");
	}

	/*
	 * Checks if a network connection is present NOTE: Uses ACCESS_NETWORK_STATE
	 * permission
	 */
	private boolean isNetworkOnline() {
		boolean status = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(0);
			if (netInfo != null
					&& netInfo.getState() == NetworkInfo.State.CONNECTED) {
				status = true;
			} else {
				netInfo = cm.getNetworkInfo(1);
				if (netInfo != null
						&& netInfo.getState() == NetworkInfo.State.CONNECTED)
					status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (!status) {
			Toast.makeText(Events.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
		}
		return status;
	}

	/* Shows date selector AlertDialogFragment */
	void showDialog() {
		DialogFragment newFragment = MyAlertDialogFragment.newInstance();
		newFragment.show(getFragmentManager(), "dialog");
	}

	/////////// Event retrieval backend ///////////

	/*
	 * Retrieves XML from calendar.swarthmore.edu
	 */
	private void getEvents(String date1, String date2) {

		/* Only runs if network is online */
		if (!isNetworkOnline()) {
			return;
		}
		String URL = "http://calendar.swarthmore.edu/calendar/RSSSyndicator.aspx?category=&location=&type=N&starting="
				+ date1 + "&ending=" + date2 + "&binary=Y&keywords=&ics=Y";

		new RetreiveFeedTask().execute(URL);

	}

	// /////////////////// Inner Classes /////////////////////
	class RetreiveFeedTask extends AsyncTask<String, Void, Void> {

		protected Void doInBackground(String... urls) {

			try {
				URL rssUrl = new URL(urls[0]);
				SAXParserFactory mySAXParserFactory = SAXParserFactory
						.newInstance();
				SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
				XMLReader myXMLReader = mySAXParser.getXMLReader();
				RSSHandler myRSSHandler = new RSSHandler();
				myXMLReader.setContentHandler(myRSSHandler);
				InputSource myInputSource = new InputSource(rssUrl.openStream());
				myXMLReader.parse(myInputSource);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// TextView result = (TextView)
						// findViewById(R.id.result);
						// result.setText(streamTitle);

					}
				});
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Void voids) {
			// TODO: check this.exception
			// TODO: do something with the feed
		}
	}

	private class RSSHandler extends DefaultHandler {
		private final int stateUnknown = 0;
		private int state = stateUnknown;

		private final int stateTitle = 1;
		private final int stateCategory = 2;
		private final int stateEncoded = 3;
		private final int stateLink = 4;

		private boolean encoded = false;
		private char[] temp;
		private boolean encodedStart = false;
		private boolean encodedEnd = false;

		private String strTitle = "";
		private String strCategory = "";
		private String strEncoded = "";
		private String strElement = "";
		private String strLink = "";
		private String title;
		private String time;
		private String location;
		private String link;

		@Override
		public void startDocument() throws SAXException {
			// TODO Auto-generated method stub
			strTitle = "--- Start Document ---\n";
			Events.eventsList.clear();
		}

		@Override
		public void endDocument() throws SAXException {
			// TODO Auto-generated method stub
			strTitle += "--- End Document ---";
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attr) throws SAXException {
			// TODO Auto-generated method stub

			/* Search for title tags */
			// //Log.i(tag, "TAG: "+localName);
			if (localName.equalsIgnoreCase("title")) {
				state = stateTitle;
				strElement = "";
			} else if (localName.equalsIgnoreCase("category")) {
				state = stateCategory;
				strElement = "";
			} else if (localName.equalsIgnoreCase("link")) {
				state = stateLink;
				strElement = "";
			} else if (localName.equalsIgnoreCase("encoded")) {
				/*
				 * This one's a bit more complicated since HTML tags are
				 * involved
				 */
				state = stateEncoded;
				strEncoded = "";
				encoded = true;
				encodedStart = true;
			} else {
				state = stateUnknown;
			}

		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (strElement.equals("")) {
				return;
			}

			if (localName.equalsIgnoreCase("title")) {
				strTitle += strElement + "\n";
				title = strElement;
			} else if (localName.equalsIgnoreCase("link") && strElement.contains("EventList")) {
				strLink += strElement + "\n";
				link = strElement;
				////Log.i(tag, "Link: "+link);
			} else if (localName.equalsIgnoreCase("category")) {
				/* The last tag in an event */
				////Log.i(tag, "strElement: "+strElement);
				strCategory += strElement + "\n";
				// //Log.i(tag, "TITLE: " + title);
				/*
				 * //Log.i(tag, "DATE: " + strElement); //Log.i(tag, "TIME: " +
				 * time); //Log.i(tag, "LOCATION: " + location); //Log.i(tag,
				 * "-------------------------------");
				 */
				final Event event = new Event(title, time, strElement, location, link);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						/* Create a new Event */
						if (event.getTime().contains("All Day")) {
							event.setTime("All Day");

						}

						/*
						 * //Log.i(tag, " Title: " + event.getTitle()); //Log.i(tag,
						 * " Time: " + event.getTime()); //Log.i(tag, " Date: " +
						 * event.getDate()); //Log.i(tag, " Location: " +
						 * event.getLocation()); //Log.i(tag,
						 * "-------------------------------");
						 */
						Events.eventsList.add(event);
						eventsAdapter.notifyDataSetChanged();

					}
				});

			} else if (localName.equalsIgnoreCase("encoded")) {
				encodedEnd = true;
				encodedStart = false;
				strEncoded += strElement + "\n";
				String[] encodedArr = strEncoded.split("<br />");
				String[] encodedArrLoc = Arrays.copyOfRange(encodedArr, 1,
						encodedArr.length);
				String text = Arrays.toString(encodedArr);
				// //Log.i(tag, "ENCODED: " + text);
				time = getTime(text);
				location = getLocation(encodedArrLoc);

			}
			state = stateUnknown;
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			String strCharacters = new String(ch, start, length);
			if (state == stateTitle
					&& !strCharacters
					.contains("Swarthmore College Events Calendar")) {
				strElement += strCharacters;
				streamTitle += strElement + "\n";
			} else if (state == stateCategory || state == stateLink) {
				strElement += strCharacters;
				streamTitle += strElement + "\n";
			} else if (state == stateEncoded) {
				// //Log.i(tag, "strCharacters: "+strCharacters);
				if (encodedStart) {
					strEncoded += strCharacters;
					// //Log.i(tag, "strEncoded - start: "+strEncoded);
				}
				// //Log.i(tag, "encodedEnd: "+encodedEnd);
				if (encodedEnd) {
					// //Log.i(tag, "strEncoded - end: " + strEncoded);
					encodedEnd = false;
				}
			}

		}

		public String getTime(String html) {
			/* Touch up the text, then split on <td> */

			/* Remove "/" and <b> */
			String startTime = "";
			String endTime = "";
			String time = "";
			String modified = html.replace("/", "").replace("<b>", "")
					.replace("&nbsp", "").replace(";", "").replace(", ", "")
					.replace(",", "");
			String[] parsed = modified.split("<td>");
			parsed = Arrays.copyOfRange(parsed, 1, parsed.length);
			List<String> parsedAL = new ArrayList<String>(Arrays.asList(parsed));
			// //Log.i(tag, "parsed: " + parsedAL.toString());

			/* Case 1: One or two times are specified */
			if (parsedAL.contains("Start Time:")) {
				int index = parsedAL.indexOf("Start Time:") + 2;
				startTime = parsedAL.get(index).trim();
				// //Log.i(tag,
				// "index - start: "+index+" parsedAL[index]: "+startTime);
			}
			if (parsedAL.contains("End Time:")) {
				int index = parsedAL.indexOf("End Time:") + 2;
				endTime = parsedAL.get(index).trim();
				// //Log.i(tag,
				// "index - end: "+index+" parsedAL[index]: "+endTime);
			}

			/* Case 2: All day */
			if (parsedAL.contains("All Day")) {
				startTime = "All Day";
				endTime = "All Day";
				//Log.i(tag, "All Day");
			}

			/* More unspecified time handling */
			if (startTime.equals("")) {
				startTime = "12:00 AM*";
			}
			else if (startTime.contains("Midnight")) {
				startTime = startTime.replace("*", "");
			}

			if (endTime.equals("")) {
				endTime = "11:59 PM*";
			}
			else if (endTime.contains("Midnight")) {
				endTime = endTime.replace("*", "");
			}


			/*//Log.i(tag, "Start Time: "+startTime);
			//Log.i(tag, "End Time: "+endTime);
			//Log.i(tag, "----------------------------------------");*/
			time = startTime + " - " + endTime;
			return time;
		}

		/* Extracting location information */
		public String getLocation(String[] enc) {
			if (enc.length == 0 || enc == null) {
				return "";
			}
			String location = "";
			String myLoc = "";
			String myRoom = "";
			String myRoom2 = "";
			for (String str : enc) {
				if (str.contains("<") && str.contains(">")
						&& str.contains("</")) {
					/*
					 * The String contains HTML tags. That stuff is fluff that
					 * got split away
					 */
					continue;
				}
				if (str.contains("*Swarthmore College - ")) {
					myLoc = str.replace("*Swarthmore College - ", "");
					if (location.equals("")) {
						location += myLoc;
					} else if (location.length() > 0) {
						location += "\n" + myLoc;
					}

				}
				if (str.contains("Room: ")) {
					myRoom = str.replace("Room: ", ": ");
					location += myRoom;
					if (myRoom2.length() == 0) {
						myRoom2 += myRoom.replace(": ", "");
					} else if (myRoom.length() > 0) {
						myRoom2 += "; " + myRoom.replace(": ", "");
					}
					// //Log.i(tag, "myRoom2: "+myRoom2);
				}
			}

			return myRoom2;

		}

	}

	/* Handles DatePicker */
	public static class MyAlertDialogFragment extends DialogFragment {

		public static MyAlertDialogFragment newInstance() {
			MyAlertDialogFragment frag = new MyAlertDialogFragment();
			Bundle args = new Bundle();
			args.putString("Select Date Range", "daterange");
			frag.setArguments(args);

			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			layout = inflater.inflate(R.layout.event_date_selector, null);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setView(layout);
			builder.setTitle("Select Date Range");

			Button b1 = (Button) layout.findViewById(R.id.from_date_picker);
			Button b2 = (Button) layout.findViewById(R.id.to_date_picker);
			Button b3 = (Button) layout.findViewById(R.id.submission_buton);

			setUpDateButtons(b1, b2);

			return builder.create();
		}

		/* Set the Buttons' text to 1-week default */
		private static void setUpDateButtons(Button from, Button to) {

			/* Set up Calendar instances */
			final Calendar c = Calendar.getInstance();
			final Calendar c2 = Calendar.getInstance();
			c2.add(Calendar.DAY_OF_MONTH, 6);
			/* Handle edge cases since the Calendar Object doesn't */

			int year = c.get(Calendar.YEAR);
			int year2 = year;
			int month = c.get(Calendar.MONTH);
			int month2 = month;
			int day = c.get(Calendar.DAY_OF_MONTH);
			int day2 = c2.get(Calendar.DAY_OF_MONTH);

			/* At the end of a month */
			if (day > day2) {
				month2++;
				/* At the end of a year */
				if (month == 11) {
					year2++;
				}
			}

			String date1 = getDateAsString(year, month, day);
			String date2 = getDateAsString(year2, month2, day2);

			/* Set Button text to default values */
			from.setText(date1);
			to.setText(date2);
		}

	}

	/*
	 * Since I have only two DatePickerFragments to manage, this brute-force
	 * method works fine. Eventually, I'd like to optimize this to only use one
	 * DatePickerFragment class.
	 */
	public static class FromDatePickerFragment extends DialogFragment implements
	DatePickerDialog.OnDateSetListener {

		public Button clicked;
		public Button autoSetDate;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			/* Get the Buttons */

			/* Use the current date as the default date in the picker */
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			/* Create a new instance of DatePickerDialog and return it */
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			/* ValiDATE (haha) */
			if (validate(year, (1 + month), day)) {
				/* Get new date for Button */
				String myDate = getDateAsString(year, month, day);
				clicked = (Button) layout.findViewById(R.id.from_date_picker);
				clicked.setText(myDate);
				//view.updateDate(year, month, day);
			}

		}

		/* Returns String to set as DatePicker-launch Button text */
		private String getDateAsString(int year, int month, int day) {
			String myMonth = Events.monthsArr[month];
			String myDay = String.valueOf(day);
			String myYear = String.valueOf(year);
			return myMonth + " " + myDay + ", " + myYear;
		}

		/* Returns int for use in validation */
		private int[] getDateAsInts(String date) {
			String[] splitDate = date.replace(",", "").split(" ");
			DateTimeFormatter format = DateTimeFormat.forPattern("MMMMM");
			DateTime instance = format.withLocale(Locale.ENGLISH)
					.parseDateTime(splitDate[0]);
			int month_number = instance.getMonthOfYear();
			int[] parsedDate = new int[] { month_number,
					Integer.parseInt(splitDate[1]),
					Integer.parseInt(splitDate[2]) };
			return parsedDate;
		}

		/* Compare dates for both Buttons */
		private boolean validate(int selYear, int selMonth, int selDay) {
			Calendar c = Calendar.getInstance();

			String date2 = (String) ((Button) layout
					.findViewById(R.id.to_date_picker)).getText();

			int currYear = c.get(Calendar.YEAR);
			int currMonth = c.get(Calendar.MONTH) + 1;
			int currDay = c.get(Calendar.DAY_OF_MONTH);

			int[] pDate2 = getDateAsInts(date2);

			//Log.i("Events", "Current date: " + currMonth + "/" + currDay + "/"+ currYear);
			//Log.i("Events", "Selected date: " + selMonth + "/" + selDay + "/"+ selYear);
			//Log.i("Events", "End date: " + pDate2[0] + "/" + pDate2[1] + "/"+ pDate2[2]);
			//Log.i("Events", "---------------------------------");



			/* Check: date1 is not before today's date */
			if (selYear < currYear) {
				Toast.makeText(getActivity(), "Start date cannot predate the current day", Toast.LENGTH_SHORT).show();
				return false;
			} else if (selYear == currYear && (selMonth < currMonth || selDay < currDay)) {
				Toast.makeText(getActivity(), "Start date cannot predate the current day", Toast.LENGTH_SHORT).show();
				return false;
			}

			/* Check: date1 is not after date2 */
			if (selMonth > pDate2[0]
					|| ((selMonth >= pDate2[0]) && (selDay > pDate2[1]))
					|| selYear > pDate2[2]) {
				Toast.makeText(getActivity(), "Start date cannot occur after end date", Toast.LENGTH_SHORT).show();
				return false;
			}

			return true;
		}

	}

	public static class ToDatePickerFragment extends DialogFragment implements
	DatePickerDialog.OnDateSetListener {

		public Button clicked;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			/* Use the current date as the default date in the picker */
			final Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			c.add(Calendar.DAY_OF_MONTH, 6);
			int day = c.get(Calendar.DAY_OF_MONTH);

			/* Create a new instance of DatePickerDialog and return it */
			//Log.i(tag, "month: " + month);
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			if (validate(year, (1 + month), day)) {
				/* Get new date for Button */
				String myDate = getDateAsString(year, month, day);
				clicked = (Button) layout.findViewById(R.id.to_date_picker);
				clicked.setText(myDate);
				//view.updateDate(year, month, day);
			}
		}

		/* Returns String to set as DatePicker-launch Button text */
		private String getDateAsString(int year, int month, int day) {
			String myMonth = Events.monthsArr[month];
			String myDay = String.valueOf(day);
			String myYear = String.valueOf(year);
			return myMonth + " " + myDay + ", " + myYear;
		}

		/* Returns int for use in validation */
		private int[] getDateAsInts(String date) {
			String[] splitDate = date.replace(",", "").split(" ");
			DateTimeFormatter format = DateTimeFormat.forPattern("MMMMM");
			DateTime instance = format.withLocale(Locale.ENGLISH)
					.parseDateTime(splitDate[0]);
			int month_number = instance.getMonthOfYear();
			int[] parsedDate = new int[] { month_number,
					Integer.parseInt(splitDate[1]),
					Integer.parseInt(splitDate[2]) };
			return parsedDate;
		}

		/* Compare dates for both Buttons */
		private boolean validate(int selYear, int selMonth, int selDay) {
			Calendar c = Calendar.getInstance();

			String date1 = (String) ((Button) layout
					.findViewById(R.id.from_date_picker)).getText();

			/*
			 * int currYear = c.get(Calendar.YEAR); int currMonth =
			 * c.get(Calendar.MONTH); int currDay =
			 * c.get(Calendar.DAY_OF_MONTH);
			 */

			int[] pDate1 = getDateAsInts(date1);

			// //Log.i("Event",
			// "Current date: "+currMonth+"/"+currDay+"/"+currYear);
			//Log.i("Events", "Start date: " + pDate1[0] + "/" + pDate1[1] + "/"+ pDate1[2]);
			//Log.i("Events", "Selected date: " + selMonth + "/" + selDay + "/"+ selYear);
			//Log.i("Events", "---------------------------------");

			/* NOTE: pDate is indexed [MONTH, DAY, YEAR]*/
			//Log.i("Events", "selYear < pDate1[2]: "+(selYear<pDate1[2]));
			//Log.i("Events", "selMonth < pDate1[0]: "+(selMonth<pDate1[0]));
			//Log.i("Events", "selDay < pDate1[1]: "+(selDay<pDate1[1]));

			/* Check: date2 is not before date1 */
			if (selYear < pDate1[2]) {
				Toast.makeText(getActivity(), "End date cannot occur before start date", Toast.LENGTH_SHORT).show();
				return false;
			} else if (selYear == pDate1[2] && (selMonth < pDate1[0] || selDay < pDate1[1])) {
				Toast.makeText(getActivity(), "End date cannot occur before start date", Toast.LENGTH_SHORT).show();
				return false;
			}
			return true;
		}
	}

}