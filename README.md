Swarthmobile
============

An Android App for Swarthmore students

SwarthMobile aims to provide useful information to Swarthmore College students in a convenient format. Accessing the Dash is cumbersome from a mobile device and checking from a laptop or larger computer cannot be done while in transit. SwarthMobile is the fix to this issue. I have plans to add more features such as a map, a more thoroughly fleshed-out ToDo list, improved layout design, and various small, but meaningful improvements in future updates. It's hard finding time for side projects in college.

VERSION 1.0.0
===========

Contains the following applets

Campus Events:
-----------------------------

Users can select an ActionBar menu option to choose a date range in which to see campus events. These events are pulled from the XML in the calendar.swarthmore.edu RSS feed. XML parsing is drastically faster than HMTL scraping, my method of choice for the class project version of Swarthmobile. Pressing "Get Events" or either of the two shortcut buttons populates a ListView of campus events. Each list item has two options: "Add to Calendar" and "?". "Add to Calendar" adds the event to the user's Calendar app, provided a Google profile exists on their device. "?" redirects the user to the corresponding event at www.calendar.swarthmore.edu. This option is useful if the user wants more information about the event or is confused about something in the list item text. If a time has an asterisk next to it, the actual start and/or end time did not exist in the original event. The given time is just a placeholder for the calendar and can be modified to the user's liking. Events spanning multiple days are added in aggregate in the user's calendar and can be easily deleted in aggregate as well.

ToDo List:
-----------------------------

Uses a SQLite database to store user-defined tasks. Each task has a title, due date, notes, and a priority level. Notifcations for due dates and priority categorization are not implemented in version 1.0.0. I tried extending SimpleCursorAdapter to add a delete Button to each task, but was unable to do so. This will be implemented in a future update.

Sharples Menu
-----------------------------

Simple Dialog displaying meal options at Sharples. Future versions may have a more elaborate layout design.

Van and Train Schedules
-----------------------------

Simple Dialog displaying SEPTA train and Trico shuttle departure times from the Dash. Any times displayed may be added to the user's Calendar app with a half-hour reminder.

Concerts in Philly
-----------------------------

Redirects user to songkick.com to view over 1000 concerts in the Philadelphia area. The class project version of SwarthMobile had a Dialog displaying a subset of concerts. This Dialog may be resurrected in future versions if a concert filtering system is implemented which lets the user see concerts within a certain date range. The current obstacle is the sheer number of concerts which may be included in even a small date range.

Hours
-----------------------------

Simple Dialog displays hours of operation for various locations on campus.

About the Developers
-----------------------------

Users can email app support and see short bios of the developers who made SwarthMobile as well as the app's origin story.
