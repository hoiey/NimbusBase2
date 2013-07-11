package com.example.nimbusbase_android;

import org.json.JSONException;
import org.json.JSONObject;

public class MetaData {
	
	private String text;
	private String create_time;
	private String tags;
	private String gid;
	private String time;
	private String id;
	
	
	public MetaData(JSONObject jsonobj){
		try {
			setText(jsonobj.getString("text"));
			setCreate_time(jsonobj.getString("create_time"));
			setTags(jsonobj.getString("tags"));
			setGid(jsonobj.getString("gid"));
			setTime(jsonobj.getString("time"));
			setId(jsonobj.getString("id"));	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public String getCreate_time() {
		return create_time;
	}


	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}


	public String getTags() {
		return tags;
	}


	public void setTags(String tags) {
		this.tags = tags;
	}


	public String getGid() {
		return gid;
	}


	public void setGid(String gid) {
		this.gid = gid;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}
	
	

}
