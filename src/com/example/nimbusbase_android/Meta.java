package com.example.nimbusbase_android;

import com.google.api.client.util.DateTime;

public class Meta {
	
	private DateTime createdDate;
	private DateTime modifiedDate;
	private DateTime modifiedByMeDate;
	public DateTime getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(DateTime createdDate) {
		this.createdDate = createdDate;
	}
	public DateTime getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(DateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public DateTime getModifiedByMeDate() {
		return modifiedByMeDate;
	}
	public void setModifiedByMeDate(DateTime modifiedByMeDate) {
		this.modifiedByMeDate = modifiedByMeDate;
	}

}
