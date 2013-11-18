package es.unavarra.distributedsystems.client;

import java.util.ArrayList;

import es.unavarra.distributedsystems.common.Logger;
import es.unavarra.distributedsystems.communication.Connector;
import es.unavarra.distributedsystems.communication.NetworkNode;

public class Client implements Runnable {
	private int numberOfMessages;
	private RR rr;
	private int clientId;

	public Client(int numberOfMessages, int clientId, ArrayList<NetworkNode> middleware, Connector connector) {
		this.numberOfMessages = numberOfMessages;
		this.clientId = clientId;
		this.rr = new RR(connector, middleware);
	}

	public Client(int clientId, ArrayList<NetworkNode> middleware, Connector connector) {
		this(4, clientId, middleware, connector);
	}

	@Override
	public void run() {
		for (int i = 0; i < numberOfMessages; i++) {
			Logger.log("Client " + clientId + " sending message " + i);
			String response = rr.issueReq("Client "+ clientId + " has generated request number " + i, clientId);
			Logger.log("\tClient " + clientId + " received :'" + response + "'");
		}
		Logger.eLog("Client " + clientId + " sent and received everything");
	}
}
