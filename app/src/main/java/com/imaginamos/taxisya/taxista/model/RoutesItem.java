package com.imaginamos.taxisya.taxista.model;

import java.util.List;

public class RoutesItem{
	private String summary;
	private String copyrights;
	private List<LegsItem> legs;
	private List<Object> warnings;
	private Bounds bounds;
	private OverviewPolyline overviewPolyline;
	private List<Object> waypointOrder;

	public void setSummary(String summary){
		this.summary = summary;
	}

	public String getSummary(){
		return summary;
	}

	public void setCopyrights(String copyrights){
		this.copyrights = copyrights;
	}

	public String getCopyrights(){
		return copyrights;
	}

	public void setLegs(List<LegsItem> legs){
		this.legs = legs;
	}

	public List<LegsItem> getLegs(){
		return legs;
	}

	public void setWarnings(List<Object> warnings){
		this.warnings = warnings;
	}

	public List<Object> getWarnings(){
		return warnings;
	}

	public void setBounds(Bounds bounds){
		this.bounds = bounds;
	}

	public Bounds getBounds(){
		return bounds;
	}

	public void setOverviewPolyline(OverviewPolyline overviewPolyline){
		this.overviewPolyline = overviewPolyline;
	}

	public OverviewPolyline getOverviewPolyline(){
		return overviewPolyline;
	}

	public void setWaypointOrder(List<Object> waypointOrder){
		this.waypointOrder = waypointOrder;
	}

	public List<Object> getWaypointOrder(){
		return waypointOrder;
	}
}