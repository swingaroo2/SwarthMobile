package lockett_streiff.swarthmobile2;

/*
 * Used with custom ListView for events
 */
public class Event {
	public String name;
	public String time;
	private boolean allDay;
	public String location;
	public String[] contact;
	public String description;
	
	public Event() {
		super();
	}
	
	public Event(String name, String time, boolean allDay, String location, String contact, String description) {
		super();
		this.name = name;
		this.time = time;
		this.allDay = allDay;
		this.location = location;
		//this.contact = contact;
		this.description = description;
	}
	
}
