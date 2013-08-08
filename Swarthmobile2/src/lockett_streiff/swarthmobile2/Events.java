package lockett_streiff.swarthmobile2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class Events extends Activity {

	private final String tag = "Events";

	/* Constants */
	private static final int START_DATE = 0;
	private static final int END_DATE = 1;
	private static final String[] monthsArr = { "January", "February", "March",
		"April", "May", "June", "July", "August", "September",
		"October", "November", "December" };

	/* Buttons for date range selection */
	private Button fromDate;
	private Button toDate;

	/* Handle events ListView */
	private ListView eventsPane;
	private List<Event> eventsList;

	/* Event constants */
	private final int NAME = 0;
	private final int DATE = 1;
	private final int TIME = 2;
	private final int ALL_DAY = 3;
	private final int LOCATION = 4;
	private final int PAGE = 5;
	private final int DESCRIPTION = 6;
	private final int CONTACT = 7;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events);

		/* Get the Buttons */
		fromDate = (Button) this.findViewById(R.id.from_date_picker);
		toDate = (Button) this.findViewById(R.id.to_date_picker);

		/* Handle ListView */
		// eventsPane = (ListView)
		// this.findViewById(R.id.event_viewing_pane_lv);
		eventsList = new ArrayList<Event>();

		/* Add a sample event as a test */
		eventsList.add(new Event("Orchestra Concert", "7:00pm - 10:00pm",
				"Lang Concert Hall", "Andrew Hauze\n(610) 555-3940",
				"David Kim of the Philadelphia orchestra!"));

		/* Setup */
		setUpDateButtons();


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/* Inflate the menu; this adds items to the action bar if it is present. */
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* 
	 * onClick method for the Today shortcut Button 
	 */
	public void todayOnClick(View v) {
		/* Set up Calendar instance */
		final Calendar c = Calendar.getInstance();

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);		

		String date1 = getDateAsString(year, month, day);

		/* Set Button text to default values */
		fromDate.setText(date1);
		toDate.setText(date1);

		/* Method call to Get Events onClick */
		getEventsOnClick(v);
	}

	/* 
	 * onClick method for the This Month shortcut Button 
	 */
	public void thisMonthOnClick(View v) {
		/* Get Strings for current and last days of month */
		final Calendar c = Calendar.getInstance();

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);		

		/* Not so fast! There's no point in getting events before the current day! */
		String date1 = getDateAsString(year, month, day);
		fromDate.setText(date1);

		/* Get last day of the month */
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		int lastDay = c.get(Calendar.DAY_OF_MONTH);
		String date2 = getDateAsString(year, month, lastDay);
		toDate.setText(date2);

		/* Method call to Get Events onClick */
		getEventsOnClick(v);

	}

	/* 
	 * onClick method for the Get Events Button 
	 */
	public void getEventsOnClick(View v) {

		/* Get text from date-picker buttons */
		String date1 = (String) ((Button) this.findViewById(R.id.from_date_picker)).getText();
		String date2 = (String) ((Button) this.findViewById(R.id.to_date_picker)).getText();

		/* Convert to mm/dd/yyyy format for passing into getEvents */
		String pDate1 = parseDateString(date1);
		String pDate2 = parseDateString(date2);
		
		/* Change text for Events header*/
		TextView tv = (TextView) this.findViewById(R.id.events_list_header);
		tv.setText("Events for: "+pDate1+" - "+pDate2);
	}

	/*
	 * getEventsOnClick helper function
	 * Converts date String to mm/dd/yyyy format
	 */
	private String parseDateString(String date) {
		String[] splitDate = date.replace(",","").split(" ");
		DateTimeFormatter format = DateTimeFormat.forPattern("MMMMM");
		DateTime instance = format.withLocale(Locale.ENGLISH).parseDateTime(splitDate[0]);
		int month_number = instance.getMonthOfYear();
		int[] pDate = new int[]{month_number, Integer.parseInt(splitDate[1]), Integer.parseInt(splitDate[2])};
		String dateStr = pDate[0]+"/"+pDate[1]+"/"+pDate[2];
		return dateStr;
	}

	/* Set the Buttons' text to 1-week default */
	private void setUpDateButtons() {
		/* Set up Calendar instances */
		final Calendar c = Calendar.getInstance();
		final Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.DAY_OF_MONTH, 7);

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int day2 = c2.get(Calendar.DAY_OF_MONTH);			

		String date1 = getDateAsString(year, month, day);
		String date2 = getDateAsString(year, month, day2);

		/* Set Button text to default values */
		fromDate.setText(date1);
		toDate.setText(date2);
	}

	/*
	 * setUpDateButtons helper function
	 */
	private String getDateAsString(int year, int month, int day) {
		String myMonth = Events.monthsArr[month];
		String myDay = String.valueOf(day);
		String myYear = String.valueOf(year);
		return myMonth+" "+myDay+", "+myYear;
	}

	/* onClick event listener for from date picker */
	public void showFromDatePicker(View v) {
		/* Get the Button that was clicked */
		DialogFragment newFragment = new FromDatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");

	}

	/* onClick event listener for from date picker */
	public void showToDatePicker(View v) {
		/* Get the Button that was clicked */
		DialogFragment newFragment = new ToDatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");

	}

	/* Change START DATE Button text to reflect date change */
	public void changeFromDateText(String mDate) {
		Button mFromButton = (Button)findViewById(R.id.from_date_picker);
		mFromButton.setText(mDate);
	}

	/* Change END DATE Button text to reflect date change */
	public void changeToDateText(String mDate) {
		Button mFromButton = (Button)findViewById(R.id.from_date_picker);
		mFromButton.setText(mDate);
	}


	/* onClick event listener for buttons */
	public void showEvents(View v) {
		Log.i(tag, "showEvents");
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
			Toast.makeText(Events.this, "Could not find network connection",
					Toast.LENGTH_SHORT).show();
		}
		return status;
	}

	/////////// Event retrieval backend ///////////

	/*
	 * Retrieves HTML from calendar.swarthmore.edu
	 */
	private String getHTML() {
		// Log.i("Events - getHTML", "HTML parsing - 1st round");

		/* Only runs if network is online */
		if (!isNetworkOnline()) {
			return "";
		}
		final AQuery aq = new AQuery(Events.this);
		// final AQuery aq2 = new AQuery(Events.this);
		// final TextView tv = (TextView) this.findViewById(R.id.tv);
		String url = "http://calendar.swarthmore.edu/calendar";
		aq.ajax(url, String.class, new AjaxCallback<String>() {

			@Override
			public void callback(String url, String html, AjaxStatus status) {
				/* First round HTML parsing - All events */

				/* Debugging */
				/*
				 * Log.i(tag, "URL: "+url); Log.i(tag, "HTML: "+html);
				 * tv.setMovementMethod(new ScrollingMovementMethod());
				 * tv.setText(html);
				 */

				// Log.i(tag, "MSG: "+status.getMessage());

				/* Second round HTML parsing - href tags */
				// Do I really need to store a value here? There's only gonna be
				// one PageInfo Object...
				
				//getNestedHTML(html);
				// Log.i(tag, "Nested HTML obtained");
			}
		});
		return "";
	}

	/*
	 * getHTML helper function
	 * 
	 * Navigates the href attributes in each event accessed in getHTML to get
	 * complete event information
	 */
	private String[] getNestedHTML(String html) {
		class PageInfo {

			/* Event fields */
			public String[] eventArr;

			/* Initialize to null */
			PageInfo() {
				eventArr = new String[8];
			}
		}

		/* PageInfo enables us to circumvent scoping issues when scraping HTML */
		final PageInfo event = new PageInfo();

		/* Start parsing HTML */
		NodeVisitor visitor = new NodeVisitor() {
			AQuery aq = new AQuery(Events.this);

			public void visitTag(Tag tag) {
				String name = tag.getTagName();
				String url = "http://calendar.swarthmore.edu/calendar/";

				/* Get event date */
				if ("TD".equals(name)) {
					if (tag.getAttribute("class") != null
							&& tag.getAttribute("class").equals("listheadtext")) {
						event.eventArr[DATE] = tag.toPlainTextString();
						// Log.i("Events", "Date: "+evDate);

						/* Set text of tabs to date */
					}
				}

				/* Get event name */
				if ("A".equals(name)) {
					if (tag.getAttribute("class") != null
							&& tag.getAttribute("class").equals("listtext")) {
						event.eventArr[NAME] = tag.toPlainTextString();
						// Log.i("Events", "Name: "+evName);
					}
				}

				/*
				 * Get event time, page, location, description, and contact
				 * information
				 */
				if ("A".equals(name)) {
					if (tag.getAttribute("class") != null
							&& tag.getAttribute("class").equals("url")) {
						event.eventArr[TIME] = tag.toPlainTextString();

						/*
						 * Log.i("Events", "Time: "+event.time); Log.i("Events",
						 * "------------------------------------------------");
						 */

						/*
						 * Navigate to link in href attribute and parse HTML for
						 * event description and location
						 */
						String eventPage = url + tag.getAttribute("href");
						// Log.i("Events", "Navigating to url: "+eventPage);

						/* Get event page */
						event.eventArr[PAGE] = eventPage;

						/*
						 * Not getting event description - parsing all the <p>
						 * tags would be a computationally-expensive hassle.
						 * Just make a "more info" button
						 */

						/* AQuery AJAX call to navigate into href link */
						aq.ajax(eventPage, String.class,
								new AjaxCallback<String>() {

							@Override
							public void callback(String url,
									String html, AjaxStatus status) {

								/* Debugging */
								/*
								 * TextView tv = (TextView)
								 * findViewById(R.id.tv);
								 * tv.setText(html);
								 * tv.setMovementMethod(new
								 * ScrollingMovementMethod());
								 * Log.i("visitTag",
								 * "Internal HTML status: "
								 * +status.getMessage());
								 */

								/* Get event location */
								NodeVisitor visitor = new NodeVisitor() {
									public void visitTag(Tag tag) {
										String name = tag.getTagName();
										// Log.i("Events", "Tag:"+name);

										/*
										 * Get location (note: can add a
										 * "More info" button to go to
										 * event page in lieu of
										 * description)
										 */
										if ("A".equals(name)) {
											if (tag.getAttribute("class") != null
													&& tag.getAttribute(
															"class")
															.equals("calendartext")) {
												if (tag.toPlainTextString()
														.contains(
																"Swarthmore College")) {
													event.eventArr[LOCATION] = tag
															.toPlainTextString()
															.replace(
																	"*Swarthmore College - ",
																	"");
													// Log.i("Events",
													// "Location: "+event.location);

													/*
													 * How do I navigate
													 * scoping in order
													 * to retrieve the
													 * location? Answer:
													 * PageInfo, it's
													 * actually useful
													 * for something
													 * after all!
													 */
												}
											}
										}

										/* Get event description */
										if (("META").equals(name)) {
											if (tag.getAttribute("name") != null
													&& tag.getAttribute(
															"name")
															.equals("description")) {
												String description;
												if ((description = tag
														.getAttribute("content")) != null) {
													// Log.i("Events",
													// "----------------------------------------------");
													// Log.i("Events",
													// description);
													event.eventArr[DESCRIPTION] = description;
												}
											}
										}

										/* Get event contact information */
										if (("TD").equals(name)) {
											if (tag.getAttribute("class") != null
													&& tag.getAttribute(
															"class")
															.equals("detailsview")) {
												String contactInfo = tag
														.toPlainTextString();
												if (contactInfo
														.contains("Contact Information")) {
													// Log.i("Events","----------------------------------------------");
													/*
													 * Idea: split on
													 * email, phone, and
													 * name, in that
													 * order.
													 */
													// Log.i("Events",contactInfo);
													event.eventArr[CONTACT] = parseContactInfo(contactInfo
															.replace(
																	"Contact Information:",
																	""));
												}
											}
										}
									}
								};
								/* Execute inner HTML parsing */
								Parser parser = new Parser(new Lexer(
										html));
								try {
									parser.visitAllNodesWith(visitor);
								} catch (ParserException e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			}
		};

		/* Execute outer HTML parsing */
		Parser parser = new Parser(new Lexer(html));
		try {
			parser.visitAllNodesWith(visitor);
		} catch (ParserException e) {
			e.printStackTrace();
		}

		/*
		 * Return the String[] in event (the PageInfo class instance). Aren't I
		 * clever?
		 */
		// Log.i("Events", "Event: "+Arrays.toString(event.eventArr));
		return event.eventArr;
	}

	/*
	 * getNestedHTML helper function
	 * 
	 * Contact information is passed in as a messy String that contains up to
	 * three pieces of useful information: name, phone, and email.
	 * 
	 * Splitting in reverse order should produce a cleaner String delimited by
	 * newlines.
	 */
	public String parseContactInfo(String ci) {
		String myCi = "";
		ci = ci.replace(": ", "");
		String[] ciSplit = ci.split("Email|Name|Phone");
		// Log.i("Events - parseContactInfo",Arrays.toString(ciSplit));

		for (String str : ciSplit) {
			/* Emails have the @ symbol */
			if (str.contains("@")) {
				myCi += (str.trim() + "\n");
			}

			/* Phone numbers have the (610) extension */
			else if (str.contains("(610)")) {
				myCi += (str.trim() + "\n");
			}

			/* Otherwise, it's a name */
			else {
				myCi += (str.trim() + "\n");
			}
		}

		myCi = myCi.substring(1, myCi.length() - 1);
		// Log.i("Events - parseContactInfo",myCi);
		// Log.i("Events - parseContactInfo","-----------------------------------------------");

		return myCi;
	}



	///////////////////// Inner Classes /////////////////////

	/* 
	 * Since I have only two DatePickers to manage, this brute-force method 
	 * works. Eventually, I'd like to optimize this to only use one DatePickerFragment class.
	 */
	public static class FromDatePickerFragment extends DialogFragment
	implements DatePickerDialog.OnDateSetListener {

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
			if (validate(year, (1+month), day)) {
				/* Get new date for Button */
				String myDate = getDateAsString(year, month, day);
				clicked = (Button) getActivity().findViewById(R.id.from_date_picker);
				clicked.setText(myDate);
			}

		}

		/* Returns String to set as DatePicker-launch Button text */
		private String getDateAsString(int year, int month, int day) {
			String myMonth = Events.monthsArr[month];
			String myDay = String.valueOf(day);
			String myYear = String.valueOf(year);
			return myMonth+" "+myDay+", "+myYear;
		}

		/* Returns int for use in validation */
		private int[] getDateAsInts(String date) {
			String[] splitDate = date.replace(",","").split(" ");
			DateTimeFormatter format = DateTimeFormat.forPattern("MMMMM");
			DateTime instance = format.withLocale(Locale.ENGLISH).parseDateTime(splitDate[0]);
			int month_number = instance.getMonthOfYear();
			int[] parsedDate = new int[]{month_number, Integer.parseInt(splitDate[1]), Integer.parseInt(splitDate[2])};
			return parsedDate;
		}

		/* Compare dates for both Buttons */
		private boolean validate(int selYear, int selMonth, int selDay) {
			Calendar c = Calendar.getInstance();

			String date2 = (String) ((Button) getActivity().findViewById(R.id.to_date_picker)).getText();

			int currYear = c.get(Calendar.YEAR);
			int currMonth = c.get(Calendar.MONTH);
			int currDay = c.get(Calendar.DAY_OF_MONTH);

			int[] pDate2 = getDateAsInts(date2);

			Log.i("Event", "Selected date: "+selMonth+"/"+selDay+"/"+selYear);
			Log.i("Event", "End date: "+pDate2[0]+"/"+pDate2[1]+"/"+pDate2[2]);
			Log.i("Event", "---------------------------------");

			/* Check: date1 is not before today's date */
			if (selDay < currDay || selMonth < currMonth || selYear < currYear) {
				Toast.makeText(getActivity(), "Start date cannot predate the current day", Toast.LENGTH_SHORT).show();
				return false;
			}

			/* Check: date1 is not after date2 */
			if (selMonth > pDate2[0] || ((selMonth >= pDate2[0]) && (selDay > pDate2[1])) || selYear > pDate2[2]) {
				Toast.makeText(getActivity(), "Start date cannot occur after end date", Toast.LENGTH_SHORT).show();
				return false;
			}

			return true;
		}

	}

	public static class ToDatePickerFragment extends DialogFragment
	implements DatePickerDialog.OnDateSetListener {

		public Button clicked;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			/* Use the current date as the default date in the picker */
			final Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			/* Create a new instance of DatePickerDialog and return it */
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}


		public void onDateSet(DatePicker view, int year, int month, int day) {
			if (validate(year, (1+month), day)) {
				/* Get new date for Button */
				String myDate = getDateAsString(year, month, day);
				clicked = (Button) getActivity().findViewById(R.id.to_date_picker);
				clicked.setText(myDate);
			}
		}

		/* Returns String to set as DatePicker-launch Button text */
		private String getDateAsString(int year, int month, int day) {
			String myMonth = Events.monthsArr[month];
			String myDay = String.valueOf(day);
			String myYear = String.valueOf(year);
			return myMonth+" "+myDay+", "+myYear;
		}

		/* Returns int for use in validation */
		private int[] getDateAsInts(String date) {
			String[] splitDate = date.replace(",","").split(" ");
			DateTimeFormatter format = DateTimeFormat.forPattern("MMMMM");
			DateTime instance = format.withLocale(Locale.ENGLISH).parseDateTime(splitDate[0]);
			int month_number = instance.getMonthOfYear();
			int[] parsedDate = new int[]{month_number, Integer.parseInt(splitDate[1]), Integer.parseInt(splitDate[2])};
			return parsedDate;
		}

		/* Compare dates for both Buttons */
		private boolean validate(int selYear, int selMonth, int selDay) {
			Calendar c = Calendar.getInstance();

			String date1 = (String) ((Button) getActivity().findViewById(R.id.from_date_picker)).getText();

			int currYear = c.get(Calendar.YEAR);
			int currMonth = c.get(Calendar.MONTH);
			int currDay = c.get(Calendar.DAY_OF_MONTH);

			int[] pDate1 = getDateAsInts(date1);

			//Log.i("Event", "Current date: "+currMonth+"/"+currDay+"/"+currYear);
			Log.i("Event", "Start date: "+pDate1[0]+"/"+pDate1[1]+"/"+pDate1[2]);
			Log.i("Event", "Selected date: "+selMonth+"/"+selDay+"/"+selYear);
			Log.i("Event", "---------------------------------");

			/* Check: date2 is not before date1 */
			if (selMonth < pDate1[0] || ((selMonth <= pDate1[0]) && (selDay < pDate1[1])) || selYear < pDate1[2]) {
				Toast.makeText(getActivity(), "End date cannot occur before start date", Toast.LENGTH_SHORT).show();
				return false;
			}

			return true;

		}
	}
}
