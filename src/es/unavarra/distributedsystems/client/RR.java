package es.unavarra.distributedsystems.client;

import java.util.ArrayList;

import es.unavarra.distributedsystems.common.Identifier;
import es.unavarra.distributedsystems.common.Logger;
import es.unavarra.distributedsystems.common.MessageType;
import es.unavarra.distributedsystems.common.Request;
import es.unavarra.distributedsystems.communication.Connector;
import es.unavarra.distributedsystems.communication.NetworkNode;
import es.unavarra.distributedsystems.communication.Receiver;

public class RR implements Receiver {

	private ArrayList<NetworkNode> hList;
	private int clSeq;
	private Connector connector;
	private long timeout;
	private String response;

	public RR(Connector connector, ArrayList<NetworkNode> hList, int timeoutMillis) {
		this.connector = connector;
		this.hList = hList;
		this.clSeq = 0;
		this.timeout = timeoutMillis;
	}

	public synchronized String issueReq(String message, int clientId) {
		this.clSeq++;
		Identifier identifier = new Identifier(clientId, this.clSeq);
		Request request = new Request();
		request.setMessageType(MessageType.REQUEST);
		request.setId(identifier);
		request.setMessage(message);

		int i = 0;
		response = null;
		while (response == null) {
			Logger.log("[RR " + clientId + "] Trying to communicate with ARH: " + hList.get(i));
			connector.send(request, hList.get(i));
			try {
				this.wait(timeout);
			} catch (InterruptedException e) {}
			// If exited by timeout, keep sending to next ARH
			// Else if exited by a 'notify' the response will be set to a
			// non-null value and the loop will exit
			i = (i + 1) % hList.size();
		}
		return response;
	}

	@Override
	public void receive(Request request) {
		switch (request.getMessageType()) {
		case REPLY:
			this.handleARHReply(request);
			break;
		default:
			break;
		}
	}

	private synchronized void handleARHReply(Request request) {
		response = request.getMessage();
		this.notify();
	}

}
