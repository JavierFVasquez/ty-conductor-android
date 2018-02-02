package com.imaginamos.taxisya.taxista.model;

public class StepsItem{
	private Duration duration;
	private StartLocation startLocation;
	private Distance distance;
	private String travelMode;
	private String htmlInstructions;
	private EndLocation endLocation;
	private Polyline polyline;

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

	public void setTravelMode(String travelMode){
		this.travelMode = travelMode;
	}

	public String getTravelMode(){
		return travelMode;
	}

	public void setHtmlInstructions(String htmlInstructions){
		this.htmlInstructions = htmlInstructions;
	}

	public String getHtmlInstructions(){
		return htmlInstructions;
	}

	public void setEndLocation(EndLocation endLocation){
		this.endLocation = endLocation;
	}

	public EndLocation getEndLocation(){
		return endLocation;
	}

	public void setPolyline(Polyline polyline){
		this.polyline = polyline;
	}

	public Polyline getPolyline(){
		return polyline;
	}
}
