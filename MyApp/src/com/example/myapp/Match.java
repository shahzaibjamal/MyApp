package com.example.myapp;

import java.io.Serializable;

/////////////////////////////////////////////////////////
//
//	Class just for storing busID and the index of found
//	location(Nearest)
//
////////////////////////////////////////////////////////



public class Match implements Serializable{

	private String indexStart;
	private String indexFinish;
	private String busID;
	public double div;
	
	public Match(String index, String busID,double div){
		this.setIndexStart(index);
		this.setBusID(busID);
		this.div = div;
	}

	public String getIndexStart() {
		return indexStart;
	}

	public void setIndexStart(String index) {
		this.indexStart = index;
	}

	public String getBusID() {
		return busID;
	}

	public void setBusID(String busID) {
		this.busID = busID;
	}

	public String getIndexFinish() {
		return indexFinish;
	}

	public void setIndexFinish(String indexFinish) {
		this.indexFinish = indexFinish;
	}
	
}
