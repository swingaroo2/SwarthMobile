package lockett_streiff.swarthmobile2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainMenu extends Activity {
	
	private final String tag = "MainMenu";
	
	/* Use temporary ListView, eventually implement carousel */
	private String[] applets;
	private ListView lv;
	private ArrayAdapter<String> adapter;
	
	/* Intents for navigating to different applets */
	private Intent iEvents;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		/* Set up ListView */
		lv = (ListView) this.findViewById(R.id.listview);
		applets = new String[]{"Campus Events"};
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, applets);
		lv.setAdapter(adapter);
		
		/* Set up OnItemClickListener */
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
				// Will likely have to change this when setting up carousel
				String clicked = (String) ((TextView) v).getText();
				//Log.i(tag, clicked);
				
				if (clicked.equals("Campus Events")) {eventsOnClick();}
 				
				
			}
		});
	}

	/* onClick helper functions for ListView/carousel items */
	private void eventsOnClick() { startActivity(new Intent(this, Events.class)); }
	
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
