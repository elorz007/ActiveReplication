package es.unavarra.distributedsystems.client;

import es.unavarra.distributedsystems.common.Logger;

public class Client implements Runnable {
	private int numberOfMessages;
	private RR rr;
	private int clientId;

	public Client(RR rr, int numberOfMessages, int clientId) {
		this.numberOfMessages = numberOfMessages;
		this.clientId = clientId;
		this.rr = rr;
	}

	@Override
	public void run() {
		if (rr != null) {
			for (int i = 0; i < numberOfMessages; i++) {
				Logger.log("[Client " + clientId + "] Sending message: " + i);
				String response = rr.issueReq("Client "+ clientId + " has generated request number " + i, clientId);
				Logger.log("[Client " + clientId + "] Received: '" + response + "'");
			}
			Logger.log("**** Client " + clientId + " sent and received everything ****");
		}
		else {
			Logger.eLog("No RR given. Shutting down.");
		}
	}
}
