package lockett_streiff.swarthmobile2;

/*
 * Used with custom ListView for events
 */
public class Event {
	public String name;
	public String time;
	public String location;
	public String contact;
	public String description;
	
	public Event() {
		super();
	}
	
	public Event(String name, String time, String location, String contact, String description) {
		super();
		this.name = name;
		this.time = time;
		this.location = location;
		this.contact = contact;
		this.description = description;
	}
}
