package com.imaginamos.taxisya.taxista.model;

import java.util.List;

public class LegsItem{
	private Duration duration;
	private StartLocation startLocation;
	private Distance distance;
	private String startAddress;
	private EndLocation endLocation;
	private String endAddress;
	private List<Object> viaWaypoint;
	private List<StepsItem> steps;
	private List<Object> trafficSpeedEntry;

	public void setDuration(Duration duration){
		this.duration = duration;
	}

	public Duration getDuration(){
		return duration;
	}

	public void setStartLocation(StartLocation startLocation){
		this.startLocation = startLocation;
	}

	public StartLocation getStartLocation(){
		return startLocation;
	}

	public void setDistance(Distance distance){
		this.distance = distance;
	}

	public Distance getDistance(){
		return distance;
	}

	public void setStartAddress(String startAddress){
		this.startAddress = startAddress;
	}

	public String getStartAddress(){
		return startAddress;
	}

	public void setEndLocation(EndLocation endLocation){
		this.endLocation = endLocation;
	}

	public EndLocation getEndLocation(){
		return endLocation;
	}

	public void setEndAddress(String endAddress){
		this.endAddress = endAddress;
	}

	public String getEndAddress(){
		return endAddress;
	}

	public void setViaWaypoint(List<Object> viaWaypoint){
		this.viaWaypoint = viaWaypoint;
	}

	public List<Object> getViaWaypoint(){
		return viaWaypoint;
	}

	public void setSteps(List<StepsItem> steps){
		this.steps = steps;
	}

	public List<StepsItem> getSteps(){
		return steps;
	}

	public void setTrafficSpeedEntry(List<Object> trafficSpeedEntry){
		this.trafficSpeedEntry = trafficSpeedEntry;
	}

	public List<Object> getTrafficSpeedEntry(){
		return trafficSpeedEntry;
	}
}