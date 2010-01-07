package skylight1.nycevents;

import java.util.Date;

/**
 * Intended to hold all the data for the various event types (art, music, park)
 *
 * @author Rob
 *
 */
public class EventData {
	private String title;
	private String category;
	private String description;
	private String location;
	private Date startTime;
	private Date endTime;
	private String website;
	private String telephone;
    private String location2;

	public EventData(String title, String category, String description, String location,
			Date startTime, Date endTime, String website, String telephone) {
		this.title = title;
		this.category = category;
		this.description = description;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
		this.website = website;
		this.telephone = telephone;
	}

	public EventData() {}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public void setLocation2(String location2) {
		this.location2 = location2;
	}

	public String getLocation2() {
		return location2;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

}
