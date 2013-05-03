package org.skylight1.neny.model;

import static java.lang.String.format;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Restaurant {

	@Id
	private String camis;

	private String doingBusinessAs;

	private Borough borough;

	private Address address;

	private String phone;

	private String cuisineCode;

	private Grade currentGrade;

	private Date gradeDate;

	private Date inspectionDate;

	private Date discoveredDate;

	public Restaurant(String aCamis, String aDoingBusinessAs, Borough aBorough, Address aAddress, String aPhone, String aCuisineCode, Grade aCurrentGrade,
			Date aGradeDate, Date anInspectionDate, Date aDiscoveredDate) {
		camis = aCamis;
		doingBusinessAs = aDoingBusinessAs;
		borough = aBorough;
		address = aAddress;
		phone = aPhone;
		cuisineCode = aCuisineCode;
		currentGrade = aCurrentGrade;
		gradeDate = aGradeDate;
		inspectionDate = anInspectionDate;
		discoveredDate = aDiscoveredDate;
	}

	public Date getInspectionDate() {
		return inspectionDate;
	}

	public void setInspectionDate(Date inspectionDate) {
		this.inspectionDate = inspectionDate;
	}

	public String getCamis() {
		return camis;
	}

	public void setCamis(String aCamis) {
		camis = aCamis;
	}

	public String getDoingBusinessAs() {
		return doingBusinessAs;
	}

	public void setDoingBusinessAs(String aDoingBusinessAs) {
		doingBusinessAs = aDoingBusinessAs;
	}

	public Borough getBorough() {
		return borough;
	}

	public void setBorough(Borough aBorough) {
		borough = aBorough;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address aAddress) {
		address = aAddress;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String aPhone) {
		phone = aPhone;
	}

	public String getCuisineCode() {
		return cuisineCode;
	}

	public void setCuisineCode(String aCuisineCode) {
		cuisineCode = aCuisineCode;
	}

	public Grade getCurrentGrade() {
		return currentGrade;
	}

	public void setCurrentGrade(Grade aCurrentGrade) {
		currentGrade = aCurrentGrade;
	}

	public Date getGradeDate() {
		return gradeDate;
	}

	public void setGradeDate(Date aGradeDate) {
		gradeDate = aGradeDate;
	}

	public void setDiscoveredDate(Date aDiscoveredDate) {
		discoveredDate = aDiscoveredDate;
	}

	public Date getDiscoveredDate() {
		return discoveredDate;
	}

	@Override
	public String toString() {
		return format("%s[camis=%s,doingBusinessAs=%s,borough=%s,address=%s,phone=%s,cuisineCode=%s,currentGrade=%s,gradeDate=%tF,inspectionDate=%tF,discoveredDate=%tF]", getClass()
				.getSimpleName(), camis, doingBusinessAs, borough, address, phone, cuisineCode, currentGrade, gradeDate, inspectionDate, discoveredDate);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((borough == null) ? 0 : borough.hashCode());
		result = prime * result + ((camis == null) ? 0 : camis.hashCode());
		result = prime * result + ((cuisineCode == null) ? 0 : cuisineCode.hashCode());
		result = prime * result + ((currentGrade == null) ? 0 : currentGrade.hashCode());
		result = prime * result + ((discoveredDate == null) ? 0 : discoveredDate.hashCode());
		result = prime * result + ((doingBusinessAs == null) ? 0 : doingBusinessAs.hashCode());
		result = prime * result + ((gradeDate == null) ? 0 : gradeDate.hashCode());
		result = prime * result + ((inspectionDate == null) ? 0 : inspectionDate.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Restaurant other = (Restaurant) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (borough != other.borough)
			return false;
		if (camis == null) {
			if (other.camis != null)
				return false;
		} else if (!camis.equals(other.camis))
			return false;
		if (cuisineCode == null) {
			if (other.cuisineCode != null)
				return false;
		} else if (!cuisineCode.equals(other.cuisineCode))
			return false;
		if (currentGrade != other.currentGrade)
			return false;
		if (discoveredDate == null) {
			if (other.discoveredDate != null)
				return false;
		} else if (!discoveredDate.equals(other.discoveredDate))
			return false;
		if (doingBusinessAs == null) {
			if (other.doingBusinessAs != null)
				return false;
		} else if (!doingBusinessAs.equals(other.doingBusinessAs))
			return false;
		if (gradeDate == null) {
			if (other.gradeDate != null)
				return false;
		} else if (!gradeDate.equals(other.gradeDate))
			return false;
		if (inspectionDate == null) {
			if (other.inspectionDate != null)
				return false;
		} else if (!inspectionDate.equals(other.inspectionDate))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		return true;
	}
}