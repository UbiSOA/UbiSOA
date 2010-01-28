package net.ubisoa.geolocation.data;

import java.util.Comparator;

import net.ubisoa.geolocation.LocationCore;

public class SignalComparator implements Comparator<Location> {
	private String signalToEstimate;
	
	public SignalComparator(String signalToEstimate) {
		this.signalToEstimate = signalToEstimate;
	}
	
	public int compare(Location signalA, Location signalB) {
		return Math.round(LocationCore.evaluateSignal(signalA, signalToEstimate) * 1000 -
				LocationCore.evaluateSignal(signalB, signalToEstimate) * 1000);
	}

}
