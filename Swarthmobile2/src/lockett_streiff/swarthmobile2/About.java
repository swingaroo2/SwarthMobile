package lockett_streiff.swarthmobile2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class About extends Activity {

	String aboutTheApp;
	String aboutZach;
	String aboutSid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		/* Set title font */
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/FilosofiaRegular.ttf");
		TextView header = (TextView) this.findViewById(R.id.logo);
		header.setTypeface(tf);
		
		aboutTheApp = "What do you get when you combine Professor Adam Aviv's \"Software Engineering: Mobile Development\" course" +
				" and the need for an Android counterpart to the iPhone's iSwat app? SwarthMobile!\n\n" +
				"We made a beta version of SwarthMobile as our final project and continued our work beyond the classroom " +
				"in order to produce what you're currently using. We wanted to give Android users (for starters) the ability " +
				"to access key content on the Dash and the Swarthmore webpage on-the-go.\n\nThere's more we can add to make " +
				"this app even better, but in any event, please consider this our gift to the current and future students of " +
				"Swarthmore College\n\n\t\t-- Zach and Sid";
		
		aboutZach = "Zach Lockett Streiff, '14, is a Computer Science major/Statistics minor. He is also a cellist, " +
				"swing dancer, expert pun slinger, and wannabe bargain hunter.";
		
		aboutSid = "Sid Reddy, '15, is a cool guy, rugby player, and a great secondary contributer.";
		
		((TextView)this.findViewById(R.id.about_the_app)).setText(aboutTheApp);
		((TextView)this.findViewById(R.id.about_zach)).setText(aboutZach);
		((TextView)this.findViewById(R.id.about_sid)).setText(aboutSid);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/* Handle item selection */
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		case R.id.action_contact_app_support:
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"swarthmobile@gmail.com"});
			i.putExtra(Intent.EXTRA_SUBJECT, "SwarthMobile: support requested");
			try {
			    startActivity(Intent.createChooser(i, "Send mail..."));
			} catch (android.content.ActivityNotFoundException ex) {
			    Toast.makeText(About.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}
	
	private boolean isNetworkOnline() {
		boolean status = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
	
}
