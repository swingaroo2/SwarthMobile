package lockett_streiff.swarthmobile2;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuAdapter extends ArrayAdapter<String> {

	private static final String tag = "MainMenuAdapter";
	private List<String> applets;
	private LayoutInflater inflater;
	private static Activity context;
	private static ListView lv;
	private static int type;
	
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
	
	/* ListView constants */
	private static final int EVENTS = 0;
	private static final int TODO = 1;
	private static final int SHARPLES = 2;
	private static final int TRANSPORTATION = 3;
	private static final int CONCERTS = 4;
	private static final int HOURS = 5;
	private static final int ABOUT = 6;

	public MainMenuAdapter(Activity context, List<String> applets) {
		super(context, R.layout.event_list_item);
		MainMenuAdapter.context = context;
		this.applets = applets;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.lv = (ListView) context.findViewById(R.id.listview);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return applets.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return applets.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		TextView tv;
		Toast.makeText(context, "getView", Toast.LENGTH_SHORT).show();

		if (v == null) {
			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());
			v = vi.inflate(R.layout.main_menu_list, null);

		}

		String p = applets.get(position);

		if (p != null) {
			
			tv = (TextView) v.findViewById(R.id.main_menu_tv);
			
			if (tv != null) {
				tv.setText(applets.get(position));
			}
			
			// Put conditional code from MainMenu.java here
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> av, View v, int position,
						long id) {
					String clicked = (String) ((TextView) v).getText();
					////Log.i(tag, "ID: " + id);

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
					case ABOUT:
						aboutOnClick();
						break;
					}

				}
			});
			
		}



		return v;
	}
	
	/*
	 * Check if a network connection is enabled (requires ACCESS_NETWORK_STATE)
	 * permission
	 */
	private boolean isNetworkOnline() {
		boolean status = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(0);
			if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
				status = true;
			} else {
				netInfo = cm.getNetworkInfo(1);
				if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
					status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return status;
	}
	
	/* onClick helper functions for ListView items */
	private void eventsOnClick() {
		context.startActivity(new Intent(context, Events.class));
	}

	private void todoOnClick() {
		context.startActivity(new Intent(context, ViewAllTasksActivity.class));
	}
	
	public void sharplesOnClick() {
		type = SHARPLES;
		if (isNetworkOnline()) {
			new GetWebData().execute();
		} else {
			Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
		}
	}

	public void transportationOnClick() {
		type = TRANSPORTATION;
		if (isNetworkOnline()) {
			new GetWebData().execute();
		} else {
			Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
		}
	}

	public void concertsOnClick() {
		if (isNetworkOnline()) {
			Intent browserIntent = new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("http://www.songkick.com/metro_areas/5202-us-philadelphia"));
			context.startActivity(browserIntent);
		} else {
			Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
			
		}
	}

	public void hoursOnClick() {
		if (isNetworkOnline()) {
			type = HOURS;
			new GetWebData().execute();
		} else {
			Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
		}
	}

	public void aboutOnClick() {
		context.startActivity(new Intent(context, About.class));
	}

	private class GetWebData extends
	AsyncTask<ArrayList<String>, String, String> {

		@Override
		protected void onPreExecute() {
			Pdialog = new ProgressDialog(context);
			Pdialog.setMessage("Loading...");
			Pdialog.setCancelable(true);
			Pdialog.show();
		}

		@Override
		protected String doInBackground(ArrayList<String>... params) {

			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet("https://secure.swarthmore.edu/dash/");
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

			processHTML(result);
			switch (type) {
			case SHARPLES:
				AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(context);
				alertDialog2.setTitle("Sharples Menu");
				final ArrayAdapter<String> menuadapter = new ArrayAdapter<String>(
						context, R.layout.sharples_dialog_layout, menu);
				alertDialog2.setAdapter(menuadapter, null);
				alertDialog2.setNeutralButton("Return",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
				AlertDialog alert2 = alertDialog2.create();
				Pdialog.dismiss();
				alert2.show();
				break;
			case TRANSPORTATION:
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
				alertDialog.setTitle("Transportation");
				final ArrayAdapter<String> trainAdapter = new ArrayAdapter<String>(
						context, R.layout.hours_dialog_layout,
						transportation);
				alertDialog.setAdapter(trainAdapter, null);
				alertDialog.setPositiveButton("Catch Train",
						new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog,
							int which) {
						String text = trainAdapter.getItem(0)
								.toString();
						text = text.substring(text.indexOf(":") + 1);
						text.replaceAll("\\s+", "");
						String times[] = text.split(",");
						java.util.List<String> timeList = Arrays.asList(times);
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
							// //Log.i(MainMenu.tag,"temp is: " + temp);
							timeList.set(i, temp);
						}
						final ArrayAdapter<String> timesAdapter = new ArrayAdapter<String>(context, R.layout.hours_dialog_layout, timeList);
						AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(context);
						alertDialog2.setTitle("Please select a train time");

						alertDialog2.setAdapter(timesAdapter, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd, yyyy");
								// get current date and time with Date
								Date date = new Date();

								// don't print it, but save it!
								String yourDate = dateFormat.format(date);
								String monthString;
								switch (Integer.parseInt(yourDate.substring(0, 3).trim())) {
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
								yourDate = yourDate.replaceAll(yourDate.substring(0, 3).trim(),monthString);
								//Log.i(MainMenuAdapter.tag,"yourDate: "+ yourDate);
								//Log.i(MainMenuAdapter.tag, "Times: "+timesAdapter.getItem(which)+" - "+timesAdapter.getItem(which+1));
								String selectedTrain[];
								if (which != timesAdapter.getCount() - 1) {
									selectedTrain = new String[] {
											"Train to Philly",
											"Swarthmore SEPTA Station",
											yourDate,
											timesAdapter.getItem(which),
											timesAdapter.getItem(which + 1) };
								} else {
									selectedTrain = new String[] {
											"Train to Philly",
											"Swarthmore SEPTA Station",
											yourDate,
											timesAdapter.getItem(which),
									"11:59 PM" };
								}


								/* Handle addition to calendar */

								String name = selectedTrain[0];
								String location = selectedTrain[1];

								/* Process Time */

								try {
									Calendar cal = Calendar.getInstance(Locale.US);
									String trainTime = Event.convertTo24HoursFormat(timesAdapter.getItem(which).replace(" ", ""));
									String[] pTrainTime = trainTime.split(":");
									//Log.i(tag, "trainTime: "+trainTime);
									cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(pTrainTime[0]));
									cal.set(Calendar.MINUTE, Integer.parseInt(pTrainTime[1]));
									long time = cal.getTimeInMillis();
									/* Get email */
									if (UserEmailFetcher.getEmail(context) == null) {
										Toast.makeText(
												context,
												"Create a Google account on your device to access calendar functionality", 
												Toast.LENGTH_SHORT)
												.show();
									} else {
										String email = UserEmailFetcher.getEmail(context);
										CalendarClient trainClient = new CalendarClient(context);
										trainClient.newEvent(new Event(), name, location, time, time, "Swarthmobile", email);
									}

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

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
					public void onClick(DialogInterface dialog,int which) {

						String text = trainAdapter.getItem(1)
								.toString();
						text = text.substring(text.indexOf(":") + 1);
						text.replaceAll("\\s+", "");
						String times[] = text.split(",");
						java.util.List<String> timeList = Arrays.asList(times);
						for (int i = 0; i < timeList.size(); i++) {
							String temp = timeList.get(i).substring(0,timeList.get(i).indexOf("("));
							String sub = temp.substring(temp.length() - 3);
							temp = temp.replaceAll(sub," " + sub.toUpperCase()).trim();
							/*
							 * //Log.i(MainMenu.tag,"Shuttle temp is: " +
							 * temp);
							 */
							timeList.set(i, temp);
						}
						final ArrayAdapter<String> timesAdapter = new ArrayAdapter<String>(
								context,
								R.layout.hours_dialog_layout, timeList);
						AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(context);
						alertDialog2
						.setTitle("Please select a shuttle time");
						// alertDialog2.setMessage("Breakfast: " +"\n\n" + breakfast + "\n\n\n" + "Lunch: " + "\n\n" + lunch + "\n\n\n" + "Dinner: " + "\n\n" + dinner);

						alertDialog2.setAdapter(timesAdapter,
								new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog,
									int which) {

								SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd, yyyy");
								// get current date time with
								// Date()
								Date date = new Date();
								// //Log.i(MainMenu.tag,dateFormat.format(date));
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
								yourDate = yourDate.replaceAll(
												yourDate.substring(0, 3).trim(),
												monthString);
								System.out.println("yourDate is:"+ yourDate);
								String selectedShuttle[];
								if (which != timesAdapter
										.getCount() - 1) {
									selectedShuttle = new String[] {
											"Trico Shuttle",
											"Parrish Hall/Bond Parking Lot",
											yourDate,
											timesAdapter.getItem(which),
											timesAdapter.getItem(which + 1) };

								} else {
									selectedShuttle = new String[] {
											"Trico Shuttle",
											"Parrish Hall/Bond Parking Lot",
											yourDate,
											timesAdapter.getItem(which),
											"11:59 PM" };
								}

								/* Handle addition to calendar */

								String name = selectedShuttle[0];
								String location = selectedShuttle[1];

								/* Process Time */

								try {
									Calendar cal = Calendar.getInstance(Locale.US);
									String trainTime = Event.convertTo24HoursFormat(timesAdapter.getItem(which).replace(" ", ""));
									String[] pTrainTime = trainTime.split(":");
									//Log.i(tag, "trainTime: "+trainTime);
									cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(pTrainTime[0]));
									cal.set(Calendar.MINUTE, Integer.parseInt(pTrainTime[1]));
									long time = cal.getTimeInMillis();
									/* Get email */
									if (UserEmailFetcher.getEmail(context) == null) {
										Toast.makeText(
												context,
												"Create a Google account on your device to access calendar functionality", 
												Toast.LENGTH_SHORT)
												.show();
									} else {
										String email = UserEmailFetcher.getEmail(context);
										CalendarClient trainClient = new CalendarClient(context);
										trainClient.newEvent(new Event(), name, location, time, time, "Swarthmobile", email);
									}

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

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
				alertDialog.setNegativeButton("Cancel",
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
				AlertDialog.Builder alertDialog3 = new AlertDialog.Builder(context);
				alertDialog3.setTitle("Hours");
				final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
						context, R.layout.hours_dialog_layout, hours);
				alertDialog3.setAdapter(adapter2, null);
				alertDialog3.setNeutralButton("Cancel",
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
			}

			// //Log.d(LOG_TAG, result);
		}

		private void processHTML(String html) {

			// //Log.i(MainMenu.tag,"type is " + type);
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
								// //Log.i(MainMenu.tag,Class);
							} catch (NullPointerException e) {
							}
							if (Class != null) {
								/*if (Class.equals("dashmodule-content clearfix collapsible")) {
									NodeList nl = tag.getChildren();
									String tags = nl.asString();
									for (Node node : nl.toNodeArray()) {
										//Log.i(MainMenu.tag, "Node: "+node.toPlainTextString().trim());
									}
									//Log.i(MainMenu.tag, "-----------------------------------------------------------------");
									////Log.i(MainMenu.tag, "nl.size: " + nl.size());
									////Log.i(MainMenu.tag, "Tags: " + tags);
								}*/

								if (Class.equals("dining-menu")) {
									// //Log.i(MainMenu.tag,"count is: " + count);
									SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
									Date d = new Date();
									String day = sdf.format(d).trim();
									//Log.i(MainMenuAdapter.tag, day);
									if (count == 0) {
										if (!day.contains("Sunday")) {
											breakfast = "BREAKFAST:" + "\n" + tag.toPlainTextString();
											menu.add(breakfast.replaceAll("\n","\n\n"));
										} else {
											breakfast = "CONTINENTAL BREAKFAST:" + "\n" + tag.toPlainTextString();
											menu.add(breakfast.replaceAll("\n","\n\n"));
										}
									}
									if (count == 1) {
										
										if (!day.contains("Sunday") && !day.contains("Saturday")) {
											lunch = "LUNCH:" + "\n" + tag.toPlainTextString();
											menu.add(lunch.replaceAll("\n","\n\n"));
										} else {
											lunch = "BRUNCH:" + "\n" + tag.toPlainTextString();
											menu.add(lunch.replaceAll("\n","\n\n"));
										}

									}
									if (count == 2) {
										dinner = "DINNER:" + "\n" + tag.toPlainTextString();
										menu.add(dinner.replaceAll("\n", "\n\n"));
									}
									count++;
								}

							}
						}

					}
				};
				// //Log.i(MainMenu.tag,"trying to make parser");
				Parser parser = new Parser(new Lexer(html));
				// //Log.i(MainMenu.tag,"made parser");
				try {
					// //Log.i(MainMenu.tag,"tTTTT");
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

						String name = tag.getTagName();

						// Get date
						if ("DIV".equals(name)) {
							try {
								id = tag.getAttribute("ID");
							} catch (NullPointerException e) {
							}

							if (id != null) {
								if (id.equals("train-times")) {
									train = tag.toPlainTextString();
									transportation.add("Next Trains to Philly:\n" + train);
								}
								if (id.equals("shuttle-times")) {
									shuttle = tag.toPlainTextString();
									transportation.add("Trico Shuttles:\n" + shuttle);
									return;
								}

							}

						}
					}
				};
				Parser parser2 = new Parser(new Lexer(html));
				try {
					parser2.visitAllNodesWith(visitor2);
				} catch (ParserException e) {
					e.printStackTrace();
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

						// //Log.i(MainMenu.tag,"trying to parse");
						String name = tag.getTagName();
						if (tag.toPlainTextString().equals("Hours")) {
							// //Log.i(MainMenu.tag,"in here");
							begin = true;
						}
						// //Log.i(LOG_TAG, "Current Tag: " + name);

						if (begin) {
							if (name.equals("LI") && count < 16) {

								hours.add(tag.toPlainTextString()
										.replaceAll("more", "")
										.replaceAll("\\s+", " "));
								/*
								 * //Log.i(MainMenu.tag,"htag is: " +
								 * tag.toPlainTextString());
								 */
								count++;
							}
							if (count == 16) {
								return;
							}
						}
					}
					// //Log.i(MainMenu.tag,name);
					// Get date

				};
				// //Log.i(MainMenu.tag,"trying to make parser");
				Parser parser3 = new Parser(new Lexer(html));
				// //Log.i(MainMenu.tag,"made parser");
				try {
					// //Log.i(MainMenu.tag,"tTTTT");
					parser3.visitAllNodesWith(visitor3);
				} catch (ParserException e) {
					e.printStackTrace();
					// //Log.i(MainMenu.tag,"RERSERSE");
				}
				break;
			}
		}
	}
	
}
