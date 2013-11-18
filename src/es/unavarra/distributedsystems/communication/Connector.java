package es.unavarra.distributedsystems.communication;

import java.util.ArrayList;

import es.unavarra.distributedsystems.common.Request;

public class Connector {
	private ArrayList<Receiver> toGroup;

	public void send(Request request, Receiver from, Receiver to) {
		if (to != null && request != null) {
			to.receive(request, from);
		}
	}

	public void bCast(Request request, Receiver from) {
		for (Receiver to : toGroup) {
			send(request, from, to);
		}
	}
}
