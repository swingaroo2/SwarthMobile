package lockett_streiff.swarthmobile2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

	/* ListView constants */
	private static final int EVENTS = 0;
	private static final int TODO = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_menu);

		/* Set title font */
		Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/FilosofiaRegular.ttf");
		TextView header = (TextView) this.findViewById(R.id.logo);
		header.setTypeface(tf);

		/* Set up ListView */
		lv = (ListView) this.findViewById(R.id.listview);
		applets = new String[]{"Campus Events", "Todo List"};
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, applets);
		lv.setAdapter(adapter);

		/* Set up OnItemClickListener */
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
				String clicked = (String) ((TextView) v).getText();
				Log.i(tag, "ID: "+id);

				//if (clicked.equals("Campus Events")) {eventsOnClick();}

				switch((int)id) {
				case EVENTS:
					eventsOnClick();
					break;
				case TODO:
					todoOnClick();
					break;
				}


			}
		});
	}

	/* onClick helper functions for ListView items */
	private void eventsOnClick() { startActivity(new Intent(this, Events.class)); }
	private void todoOnClick() { startActivity(new Intent(this, TodoList.class)); }

	/* Check if a network connection is enabled (requires ACCESS_NETWORK_STATE) permission */
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

}
