package lockett_streiff.swarthmobile2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainMenu extends Activity {

	private static final String tag = "MainMenu";

	/* Use temporary ListView, eventually implement carousel */
	private String[] applets;
	private ListView lv;
	private ArrayAdapter<String> adapter;

	private static int type;

	/* ListView constants */
	private static final int EVENTS = 0;
	private static final int TODO = 1;
	private static final int SHARPLES = 2;
	private static final int TRANSPORTATION = 3;
	private static final int CONCERTS = 4;
	private static final int HOURS = 5;

	/* Copied from v1.0 */
	String train;
	String shuttle;
	String breakfast;
	String lunch;
	String dinner = "";
	ArrayList<String> concerts;
	ArrayList<String> menu;
	ArrayList<String> hours;
	ArrayList<String> transportation;
	ProgressDialog Pdialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_menu);

		/* Set title font */
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/FilosofiaRegular.ttf");
		TextView header = (TextView) this.findViewById(R.id.logo);
		header.setTypeface(tf);

		/* Set up ListView */
		lv = (ListView) this.findViewById(R.id.listview);
		applets = new String[] { "Campus Events", "ToDo List", "Sharples Menu",
				"Van and Train schedules", "Concerts in Philly", "Hours" };
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, applets);
		lv.setAdapter(adapter);

		/* Set up OnItemClickListener */
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
				String clicked = (String) ((TextView) v).getText();
				Log.i(tag, "ID: " + id);

				// if (clicked.equals("Campus Events")) {eventsOnClick();}

				switch ((int) id) {
				case EVENTS:
					eventsOnClick();
					break;
				case TODO:
					todoOnClick();
					break;
				case SHARPLES:
					sharplesOnClick();
					break;
				case TRANSPORTATION:
					transportationOnClick();
					break;
				case CONCERTS:
					concertsOnClick();
					break;
				case HOURS:
					hoursOnClick();
					break;
				}

			}
		});
	}

	/* onClick helper functions for ListView items */
	private void eventsOnClick() {
		startActivity(new Intent(this, Events.class));
	}

	private void todoOnClick() {
		startActivity(new Intent(this, TodoList.class));
	}

	/*
	 * Check if a network connection is enabled (requires ACCESS_NETWORK_STATE)
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
		return status;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	public void sharplesOnClick() {
		type = SHARPLES;
		if (isNetworkOnline()) {
			new GetWebData().execute();
		} else {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle("No Connection");
			alertDialog
					.setMessage("Swarthmobile could not find a valid Internet connection. Please check your wireless settings.");
			alertDialog.setNeutralButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});
			AlertDialog alert = alertDialog.create();
			alert.show();
		}
	}

	public void transportationOnClick() {
		type = TRANSPORTATION;
		if (isNetworkOnline()) {
			new GetWebData().execute();
		} else {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle("No Connection");
			alertDialog
					.setMessage("Swarthmobile could not find a valid Internet connection. Please check your wireless settings.");
			alertDialog.setNeutralButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});
			AlertDialog alert = alertDialog.create();
			alert.show();
		}
	}

	public void concertsOnClick() {
		type = CONCERTS;
		if (isNetworkOnline()) {
			new GetWebData().execute();
		} else {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle("No Connection");
			alertDialog
					.setMessage("Swarthmobile could not find a valid Internet connection. Please check your wireless settings.");
			alertDialog.setNeutralButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});
			AlertDialog alert = alertDialog.create();
			alert.show();
		}
	}

	public void hoursOnClick() {
		if (isNetworkOnline()) {
			type = HOURS;
			new GetWebData().execute();
		} else {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle("No Connection");
			alertDialog
					.setMessage("Swarthmobile could not find a valid Internet connection. Please check your wireless settings.");
			alertDialog.setNeutralButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});
			AlertDialog alert = alertDialog.create();
			alert.show();
		}
	}

	private class GetWebData extends
			AsyncTask<ArrayList<String>, String, String> {

		@Override
		protected void onPreExecute() {
			Pdialog = new ProgressDialog(MainMenu.this);
			Pdialog.setMessage("Loading, please wait...");
			Pdialog.setCancelable(true);
			Pdialog.show();
		}

		@Override
		protected String doInBackground(ArrayList<String>... params) {

			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget;// Create HTTP Client
			if (type == CONCERTS) {
				httpget = new HttpGet(
						"http://www.songkick.com/metro_areas/5202-us-philadelphia");

			} else {
				httpget = new HttpGet("https://secure.swarthmore.edu/dash/"); // Set

			}
			HttpResponse response;
			String result = "";
			try {
				response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} // Execute it

			return result;

		}

		@Override
		protected void onPostExecute(String result) {

			Log.i(tag, "Before processHTML");
			processHTML(result);
			Log.i(tag, "After processHTML");
			switch (type) {
			case SHARPLES:
				AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
						MainMenu.this);
				alertDialog2.setTitle("Sharples Menu");
				// alertDialog2.setMessage("Breakfast: " + "\n\n" + breakfast +
				// "\n\n\n" + "Lunch: " + "\n\n" + lunch + "\n\n\n" + "Dinner: "
				// + "\n\n" + dinner);
				final ArrayAdapter<String> menuadapter = new ArrayAdapter<String>(
						MainMenu.this, R.layout.sharples_dialog_layout, menu);
				alertDialog2.setAdapter(menuadapter, null);
				alertDialog2.setNeutralButton("Return",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				AlertDialog alert2 = alertDialog2.create();
				Pdialog.dismiss();
				alert2.show();
				break;
			case TRANSPORTATION:
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						MainMenu.this);
				alertDialog.setTitle("Transportation");
				final ArrayAdapter<String> trainAdapter = new ArrayAdapter<String>(
						MainMenu.this, R.layout.hours_dialog_layout,
						transportation);
				alertDialog.setAdapter(trainAdapter, null);
				// alertDialog.setMessage("Next Trains to Philly: " + train +
				// "\n" + "Trico Shuttles: " + shuttle);
				alertDialog.setPositiveButton("Catch Train",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								String text = trainAdapter.getItem(0)
										.toString();
								text = text.substring(text.indexOf(":") + 1);
								text.replaceAll("\\s+", "");
								String times[] = text.split(",");
								java.util.List<String> timeList = Arrays
										.asList(times);
								for (int i = 0; i < timeList.size(); i++) {
									String temp = timeList.get(i);
									String sub;
									if (i == timeList.size() - 1) {
										sub = temp.substring(temp.length() - 3);
									} else {
										sub = temp.substring(temp.length() - 2);
									}
									temp = temp.replaceAll(sub,
											" " + sub.toUpperCase()).trim();
									// System.out.println("temp is: " + temp);
									timeList.set(i, temp);
								}
								final ArrayAdapter<String> timesAdapter = new ArrayAdapter<String>(
										MainMenu.this,
										R.layout.hours_dialog_layout, timeList);
								AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
										MainMenu.this);
								alertDialog2
										.setTitle("Please select a train time");
								// alertDialog2.setMessage("Breakfast: " +
								// "\n\n" + breakfast + "\n\n\n" + "Lunch: " +
								// "\n\n" + lunch + "\n\n\n" + "Dinner: " +
								// "\n\n" + dinner);

								alertDialog2.setAdapter(timesAdapter,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												SimpleDateFormat dateFormat = new SimpleDateFormat(
														"MM dd, yyyy");
												// get current date and time
												// with Date
												Date date = new Date();
												// System.out.println(dateFormat.format(date));
												// don't print it, but save it!
												String yourDate = dateFormat
														.format(date);
												String monthString;
												switch (Integer
														.parseInt(yourDate
																.substring(0, 3)
																.trim())) {
												case 1:
													monthString = "January";
													break;
												case 2:
													monthString = "February";
													break;
												case 3:
													monthString = "March";
													break;
												case 4:
													monthString = "April";
													break;
												case 5:
													monthString = "May";
													break;
												case 6:
													monthString = "June";
													break;
												case 7:
													monthString = "July";
													break;
												case 8:
													monthString = "August";
													break;
												case 9:
													monthString = "September";
													break;
												case 10:
													monthString = "October";
													break;
												case 11:
													monthString = "November";
													break;
												case 12:
													monthString = "December";
													break;
												default:
													monthString = "Invalid month";
													break;
												}
												yourDate = yourDate
														.replaceAll(
																yourDate.substring(
																		0, 3)
																		.trim(),
																monthString);
												System.out
														.println("yourDate is:"
																+ yourDate);
												String selectedTrain[];
												if (which != timesAdapter
														.getCount() - 1) {
													selectedTrain = new String[] {
															"Train to Philly",
															"Swarthmore SEPTA Station",
															yourDate,
															timesAdapter
																	.getItem(which),
															timesAdapter
																	.getItem(which + 1) };

												} else {
													selectedTrain = new String[] {
															"Train to Philly",
															"Swarthmore SEPTA Station",
															yourDate,
															timesAdapter
																	.getItem(which),
															"11:59 PM" };
												}
												/*
												 * System.out.println("ITEM: " +
												 * timesAdapter
												 * .getItem(which));
												 * System.out.println("ITEM2: "
												 * + timesAdapter .getItem(which
												 * + 1));
												 */
												return;
											}
										});

								alertDialog2.setNeutralButton("Cancel",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												return;
											}
										});
								AlertDialog alert2 = alertDialog2.create();

								alert2.show();

								return;
							}
						});
				alertDialog.setNeutralButton("Catch Shuttle",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								String text = trainAdapter.getItem(1)
										.toString();
								text = text.substring(text.indexOf(":") + 1);
								text.replaceAll("\\s+", "");
								String times[] = text.split(",");
								java.util.List<String> timeList = Arrays
										.asList(times);
								for (int i = 0; i < timeList.size(); i++) {
									String temp = timeList.get(i).substring(0,
											timeList.get(i).indexOf("("));
									String sub = temp.substring(temp.length() - 3);
									temp = temp.replaceAll(sub,
											" " + sub.toUpperCase()).trim();
									/*
									 * System.out.println("Shuttle temp is: " +
									 * temp);
									 */
									timeList.set(i, temp);
								}
								final ArrayAdapter<String> timesAdapter = new ArrayAdapter<String>(
										MainMenu.this,
										R.layout.hours_dialog_layout, timeList);
								AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
										MainMenu.this);
								alertDialog2
										.setTitle("Please select a shuttle time");
								// alertDialog2.setMessage("Breakfast: " +
								// "\n\n" + breakfast + "\n\n\n" + "Lunch: " +
								// "\n\n" + lunch + "\n\n\n" + "Dinner: " +
								// "\n\n" + dinner);

								alertDialog2.setAdapter(timesAdapter,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {

												SimpleDateFormat dateFormat = new SimpleDateFormat(
														"MM dd, yyyy");
												// get current date time with
												// Date()
												Date date = new Date();
												// System.out.println(dateFormat.format(date));
												// don't print it, but save it!
												String yourDate = dateFormat
														.format(date);
												String monthString;
												switch (Integer
														.parseInt(yourDate
																.substring(0, 3)
																.trim())) {
												case 1:
													monthString = "January";
													break;
												case 2:
													monthString = "February";
													break;
												case 3:
													monthString = "March";
													break;
												case 4:
													monthString = "April";
													break;
												case 5:
													monthString = "May";
													break;
												case 6:
													monthString = "June";
													break;
												case 7:
													monthString = "July";
													break;
												case 8:
													monthString = "August";
													break;
												case 9:
													monthString = "September";
													break;
												case 10:
													monthString = "October";
													break;
												case 11:
													monthString = "November";
													break;
												case 12:
													monthString = "December";
													break;
												default:
													monthString = "Invalid month";
													break;
												}
												yourDate = yourDate
														.replaceAll(
																yourDate.substring(
																		0, 3)
																		.trim(),
																monthString);
												System.out
														.println("yourDate is:"
																+ yourDate);
												String selectedTrain[];
												if (which != timesAdapter
														.getCount() - 1) {
													selectedTrain = new String[] {
															"Trico Shuttle",
															"Parrish Hall/Bond Parking Lot",
															yourDate,
															timesAdapter
																	.getItem(which),
															timesAdapter
																	.getItem(which + 1) };

												} else {
													selectedTrain = new String[] {
															"Trico Shuttle",
															"Parrish Hall/Bond Parking Lot",
															yourDate,
															timesAdapter
																	.getItem(which),
															"11:59 PM" };
												}
												/*
												 * System.out.println("ITEM: " +
												 * timesAdapter
												 * .getItem(which));
												 * System.out.println("ITEM2: "
												 * + timesAdapter .getItem(which
												 * + 1));
												 */
											}
										});

								alertDialog2.setNeutralButton("Cancel",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												return;
											}
										});
								AlertDialog alert2 = alertDialog2.create();

								alert2.show();
								return;
							}
						});
				alertDialog.setNegativeButton("Return",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				AlertDialog alert = alertDialog.create();
				Pdialog.dismiss();
				alert.show();
				break;
			case HOURS:
				hours.remove(5);
				hours.remove(5);
				AlertDialog.Builder alertDialog3 = new AlertDialog.Builder(
						MainMenu.this);
				alertDialog3.setTitle("Hours");
				final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
						MainMenu.this, R.layout.hours_dialog_layout, hours);
				alertDialog3.setAdapter(adapter2, null);
				alertDialog3.setNeutralButton("Return",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				AlertDialog alert3 = alertDialog3.create();
				Pdialog.dismiss();
				alert3.show();

				break;
			case CONCERTS:
				AlertDialog.Builder alertDialog4 = new AlertDialog.Builder(
						MainMenu.this);
				alertDialog4.setTitle("Concerts");

				// alertDialog4.setMessage(concerts);
				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						MainMenu.this, R.layout.concert_dialog_layout, concerts);

				alertDialog4.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								final String message = adapter.getItem(item)
										.toString();
								AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
										MainMenu.this);
								alertDialog2.setTitle("Concert");
								alertDialog2.setMessage(message);
								alertDialog2.setPositiveButton(
										"Add to Calendar",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												String event = message;
												String name = event.substring(
														event.indexOf(':') + 3,
														event.indexOf("\n") - 1);
												String location = event.substring(
														event.indexOf("ue:") + 4,
														event.indexOf("Date") - 2);
												String date = event.substring(event
														.indexOf("Date:") + 6);

												String selectedConcert[] = new String[] {
														name, location, date,
														"12:00 PM", "11:59 PM" };

												/*
												 * Intent concertIntent = new
												 * Intent( MainMenu.this,
												 * SimpleCalendar.class);
												 * concertIntent.putExtra(
												 * "selectedConcert",
												 * selectedConcert);
												 * startActivity(concertIntent);
												 */
												return;
											}
										});
								alertDialog2.setNeutralButton("Search Artist",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												String search = message;
												search = search.substring(
														search.indexOf(':') + 3,
														search.indexOf("\n") - 1);
												String query;
												try {
													query = URLEncoder.encode(
															search, "utf-8");
													String url = "http://www.google.com/search?q="
															+ query;
													Intent intent = new Intent(
															Intent.ACTION_VIEW);
													intent.setData(Uri
															.parse(url));
													startActivity(intent);
												} catch (UnsupportedEncodingException e) {
													e.printStackTrace();
												}
												return;
											}
										});
								alertDialog2.setNegativeButton("Return",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												return;
											}
										});
								AlertDialog alert2 = alertDialog2.create();
								Pdialog.dismiss();

								alert2.show();

								/*
								 * date = date.substring(date.indexOf("day")
								 * +4); String dates[] = date.split(" "); date =
								 * dates[1] + " " + dates[0] + "," + dates[2];
								 */
								// System.out.println("Name:" + name + "\n" +
								// location + "\n" + date);

							}
						});
				alertDialog4.setNeutralButton("Return",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				alertDialog4.setNegativeButton("More Info",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent browserIntent = new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("http://www.songkick.com/metro_areas/5202-us-philadelphia"));
								startActivity(browserIntent);
								;
							}
						});
				final AlertDialog alert4 = alertDialog4.create();
				Pdialog.dismiss();
				alert4.show();

				break;

			}

			// Log.d(LOG_TAG, result);
		}

		private void processHTML(String html) {

			// System.out.println("type is " + type);
			switch ((type)) {

			case SHARPLES:
				menu = new ArrayList<String>();
				NodeVisitor visitor = new NodeVisitor() {
					int count = 0;
					String Class;

					public void visitTag(Tag tag) {

						String name = tag.getTagName();
						// Get date
						if ("DIV".equals(name)) {
							try {
								Class = tag.getAttribute("CLASS");
								// System.out.println(Class);
							} catch (NullPointerException e) {
							}
							if (Class != null) {
								/*if (Class.equals("dashmodule-content clearfix collapsible")) {
									NodeList nl = tag.getChildren();
									String tags = nl.asString();
									for (Node node : nl.toNodeArray()) {
										Log.i(MainMenu.tag, "Node: "+node.toPlainTextString().trim());
									}
									Log.i(MainMenu.tag, "-----------------------------------------------------------------");
									//Log.i(MainMenu.tag, "nl.size: " + nl.size());
									//Log.i(MainMenu.tag, "Tags: " + tags);
								}*/

								if (Class.equals("dining-menu")) {
									// System.out.println("count is: " + count);

									if (count == 0) {
										breakfast = tag.toPlainTextString();
										menu.add(breakfast.replaceAll("\n",
												"\n\n"));
									}
									if (count == 1) {
										lunch = tag.toPlainTextString();
										menu.add(lunch.replaceAll("\n", "\n\n"));

									}
									if (count == 2) {
										dinner = tag.toPlainTextString();
										// System.out.println(dinner);
										menu.add(dinner
												.replaceAll("\n", "\n\n"));
									}
									count++;
								}

							}
						}

					}
				};
				// System.out.println("trying to make parser");
				Parser parser = new Parser(new Lexer(html));
				// System.out.println("made parser");
				try {
					// System.out.println("tTTTT");
					parser.visitAllNodesWith(visitor);
				} catch (ParserException e) {
					e.printStackTrace();
				}
				break;

			case TRANSPORTATION:
				transportation = new ArrayList<String>();
				NodeVisitor visitor2 = new NodeVisitor() {

					String id;

					public void visitTag(Tag tag) {

						// System.out.println("trying to parse");
						String name = tag.getTagName();
						// Log.i(LOG_TAG, "Current Tag: " + name);

						// System.out.println(name);
						// Get date
						if ("DIV".equals(name)) {

							// org.htmlparser.Attribute atr =
							// tag.getAttributeEx(name);
							// System.out.println(atr.toString());
							try {
								id = tag.getAttribute("ID");
								// System.out.println(Class);
							} catch (NullPointerException e) {
								// System.out.println("DSADSd");
								// e.printStackTrace();// do something other
							}
							// System.out.println("KKKKKKKK");
							// System.out.println(Class);

							if (id != null) {
								if (id.equals("train-times")) {
									train = tag.toPlainTextString();
									transportation
											.add("Next Trains to Philly:\n"
													+ train);
									// System.out.println("Train times: " +
									// train);
								}
								if (id.equals("shuttle-times")) {
									shuttle = tag.toPlainTextString();
									transportation.add("Trico Shuttles:\n"
											+ shuttle);
									/*
									 * System.out.println("Shuttle times: " +
									 * shuttle);
									 */
									return;
								}

							}

						}
					}
				};
				// System.out.println("trying to make parser");
				Parser parser2 = new Parser(new Lexer(html));
				// System.out.println("made parser");
				try {
					// System.out.println("tTTTT");
					parser2.visitAllNodesWith(visitor2);
				} catch (ParserException e) {
					e.printStackTrace();
					// System.out.println("RERSERSE");
				}
				break;

			case HOURS:
				// hours = "";
				hours = new ArrayList<String>();
				NodeVisitor visitor3 = new NodeVisitor() {

					String id;
					int count = 0;
					boolean begin = false;

					public void visitTag(Tag tag) {

						// System.out.println("trying to parse");
						String name = tag.getTagName();
						if (tag.toPlainTextString().equals("Hours")) {
							// System.out.println("in here");
							begin = true;
						}
						// Log.i(LOG_TAG, "Current Tag: " + name);

						if (begin) {
							if (name.equals("LI") && count < 16) {

								hours.add(tag.toPlainTextString()
										.replaceAll("more", "")
										.replaceAll("\\s+", " "));
								/*
								 * System.out.println("htag is: " +
								 * tag.toPlainTextString());
								 */
								count++;

							}
							if (count == 16) {
								return;
							}

						}
					}

					// System.out.println(name);
					// Get date

				};
				// System.out.println("trying to make parser");
				Parser parser3 = new Parser(new Lexer(html));
				// System.out.println("made parser");
				try {
					// System.out.println("tTTTT");
					parser3.visitAllNodesWith(visitor3);
				} catch (ParserException e) {
					e.printStackTrace();
					// System.out.println("RERSERSE");
				}
				break;
			case CONCERTS:

				String venues[] = { "Theater of the Living Arts",
						"Bifrost Arts Conference", "Upstairs, World CafŽ Live",
						"The Electric Factory", "Soundgarden Hall",
						"World CafŽ Live", "North Star Bar",
						"The Dolphin Tavern", "Ursa Minor's Cafe",
						"Trocadero Theatre", "Kung Fu Necktie",
						"Magic Pictures", "Union Transfer", "JR's Bar",
						"Red Zone", "The Barbary", "Ortlieb's Lounge",
						"Millcreek Tavern", "Cafe Fulya",
						"The Underground Arts", "The Fire", "Johnny Brenda's",
						"Jolly's Piano Bar", "Gunners Run",
						"First Unitarian Church", "The Blockley",
						"Hard Rock CafZ - Philadelphia", "Milkboy Philly",
						"Tin Angel at Serrano",
						"International House Philadelphia",
						"Tabu Lounge and Sports Bar", "Lit Ultra Bar",
						"Balcony Bar at the Trocadero",
						"Lincoln Financial Field", "Jr's",
						"Verizon Hall, Kimmel Center for the Performing Arts",
						"The Twisted Tail", "Arts Garage",
						"Philadelphia Museum Of Art", "Wooly Mammoth",
						"Rebel Rock Bar & Bites",
						"Kimmel Center for the Performing Arts",
						"The Lancastle", "Highwire Gallery",
						"The Pyramid Club", "G Lounge", "The Legendary Dobbs",
						"The Grape Room", "Rebel Rock Bar", "Voyeur Nightclub",
						"Garage Mahal", "Penn National Armory",
						"3727 Baring St", "Finningans Wake", "Xfinity Live!",
						"Warmdaddy's", "Siren Records", "L'Etage",
						"Gunners Run", "Teri's Diner Bar",
						"Philadelphia Art Alliance", "Tin Angel at Serrano",
						"Mermaid Inn", "Lickety Split", "Red Sky Lounge",
						"Voltage Lounge", "Kingdom Highway Church",
						"The Khyber", "O'reillys", "Temple University",
						"Voyeur", "Philly Homebrew Outlet",
						"International House", "Rotunda" };

				final java.util.List<String> venues2 = Arrays.asList(venues);
				concerts = new ArrayList<String>();

				NodeVisitor visitor4 = new NodeVisitor() {

					public void visitTag(Tag tag) {

						// System.out.println("trying to parse");
						String name = tag.getTagName();

						// Log.i(LOG_TAG, "Current Tag: " + name);

						if (name.equals("LI")) {

							if (tag.getAttribute("TITLE") != null) {
								String t = tag.toPlainTextString();

								if (t != null) {

									t = t.replaceAll("\\s+", " ");
									t = t.substring(0, t.indexOf("&"));
									if (t.contains("Philadelphia,")) {
										int p = t.indexOf("Philadelphia,");
										if (p != -1) {
											t = t.substring(0, p);
										}
										while (true) {
											for (String v : venues2) {
												int v2 = t.indexOf(v);
												if (v2 != -1) {
													t = t.replaceAll(v, "\n"
															+ v);
													break;
												}

											}
											break;
										}
										String item[] = t.split("\n");
										final java.util.List<String> items = Arrays
												.asList(item);
										t = "Artist: " + items.get(0);
										try {
											t = t + "\n" + "Venue: "
													+ items.get(1) + "\n";
										} catch (IndexOutOfBoundsException e) {
											t = t + "\n" + "Venue: " + "\n";
										}
										String date;
										date = tag.getAttribute("TITLE")
												.toString();
										date = date.substring(date
												.indexOf("day") + 4);
										String dates[] = date.split(" ");
										date = dates[1] + " " + dates[0] + ","
												+ " " + dates[2];

										t = t + "Date: " + date + "\n";
										concerts.add(t);

									}

								}

							}

						}

					}

					// System.out.println(name);
					// Get date

				};
				// System.out.println("trying to make parser");
				Parser parser4 = new Parser(new Lexer(html));
				// System.out.println("made parser");
				try {
					// System.out.println("tTTTT");
					parser4.visitAllNodesWith(visitor4);
				} catch (ParserException e) {
					e.printStackTrace();
					// System.out.println("RERSERSE");
				}
				break;

			}

		}

		// public void onActivityResult(int requestCode, int resultCode, Intent
		// intent) {}

	}

}
