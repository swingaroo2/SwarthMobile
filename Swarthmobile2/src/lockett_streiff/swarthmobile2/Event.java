package lockett_streiff.swarthmobile2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import android.util.Log;

/*
 * Used with custom ListView for events
 */
public class Event {
	private String title;
	private String time;
	private String date;
	private String link;
	private boolean allDay;
	private String location;

	private final static DateFormat TWELVE_TF = new SimpleDateFormat("hh:mma");
	private final static DateFormat TWENTY_FOUR_TF = new SimpleDateFormat("HH:mm");
	
	public Event() {
		super();
		this.title = "";
		this.time = "";
		this.date = "";
		this.link = "";
		this.allDay = false;
		this.location = "";
	}

	public Event(String title, String time, String date, String location, String link) {
		super();
		this.title = title;
		this.time = time.replace("Midnight -", "12:00 AM -").replace("- Midnight", "- 11:59 PM");
		this.date = date;
		this.link = link;
		this.allDay = time.contains("All Day");
		this.location = location;
	}

	private static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	private int[] processLine(String[] strings) {
		int[] intArray = new int[strings.length];
		int i = 0;
		for (String str : strings) {
			intArray[i] = Integer.parseInt(str.trim());
			i++;
		}
		return intArray;
	}

	public static String convertTo24HoursFormat(String twelveHourTime)
			throws ParseException {
		return TWENTY_FOUR_TF.format(TWELVE_TF.parse(twelveHourTime));
	}

	public int[] getTimeAsMillis() {
		/* Sample "input" is "7:00 PM - 10:00 PM" */
		try {
			String[] time1;
			String[] time2;
			if (!this.time.contains("All Day")) {
				String[] pTimes = this.time.replace("*","").replace(" ", "").split("-");
				//Log.i("Event", "pTimes: "+Arrays.toString(pTimes));
				time1 = convertTo24HoursFormat(pTimes[0]).split(":");
				time2 = convertTo24HoursFormat(pTimes[1]).split(":");
			} else {
				time1 = new String[]{"00","00"};
				time2 = new String[]{"23","59"};
			}
			
			String[] times = new String[]{time1[0],time1[1],time2[0],time2[1]};
			//System.out.println("times: "+Arrays.toString(times));
			return processLine(times);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new int[] { -1 };
	}

	public int[] getDateAsMillis() {
		String rawDate = this.title.substring(title.lastIndexOf("(") + 1,
				this.title.lastIndexOf(")"));
		String[] pDate = new String[] {};
		if (rawDate.contains(" - ")) {
			pDate = rawDate.split(" - ");
			String[] pDate1 = pDate[0].split("/");
			String[] pDate2 = pDate[1].split("/");
			pDate = concat(pDate1, pDate2);
			// System.out.println("pDate: "+Arrays.toString(pDate));
		} else {
			pDate = rawDate.split("/");
			// System.out.println("pDate: "+Arrays.toString(pDate));
		}
		return processLine(pDate);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String name) {
		this.title = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
