package lockett_streiff.swarthmobile2;

/*
 * Used with custom ListView for events
 */
public class Event {
	private String title;
	private String time;
	private String date;
	private boolean allDay;
	private String location;
	
	public Event() {
		super();
	}
	
	public Event(String title, String time, String date, String location) {
		super();
		this.title = title;
		this.time = time;
		this.date = date;
		this.allDay = time.contains("All Day");
		this.location = location;
	}

	public String getTitle() {
		return title;
	}

	public void settitle(String name) {
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
