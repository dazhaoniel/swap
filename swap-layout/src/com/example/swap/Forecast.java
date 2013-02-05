package com.example.swap;

public class Forecast {
	private double latitude, longitude;
	private float temperature, humidity;
	
	// Latitude
	public double getLatitude() {
		return latitude;
	}
	
	// Longitude
	public double getLongitude() {
		return longitude;
	}
	public void setLatLong(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	// Temperature
	public float getTemperature() {
		return temperature;
	}
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
	
	// Humidity
	public float getHumidity() {
		return humidity;
	}
	public void setHumidity(float humidity) {
		this.humidity = humidity;
	}
	
	
	
}
