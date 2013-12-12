package es.unavarra.distributedsystems.communication;

import es.unavarra.distributedsystems.common.Request;

public interface Receiver {
	public void receive(Request request);
}
