package es.unavarra.distributedsystems.end;

import java.util.ArrayList;

import es.unavarra.distributedsystems.common.Identifier;
import es.unavarra.distributedsystems.common.Logger;
import es.unavarra.distributedsystems.common.MessageType;
import es.unavarra.distributedsystems.common.Request;
import es.unavarra.distributedsystems.communication.Connector;
import es.unavarra.distributedsystems.communication.Receiver;

public class FO implements Receiver {

	private ArrayList<String> executed;
	private int expectedSeq;
	private Connector connector;
	private int id;

	public FO(int id, Connector connector) {
		this.id = id;
		this.connector = connector;
		this.expectedSeq = 1;
		this.executed = new ArrayList<String>();
	}

	private synchronized void handleTORRquest(Request request) {
		int seq = request.getId().getSeq();
		while (seq > expectedSeq) {
			try {
				this.wait();
			} catch (InterruptedException e) {
			}
		}
		if (seq == expectedSeq) {
			String result = compute(request);
			String emptyString = new String();
			while (executed.size() <= expectedSeq) {
				executed.add(emptyString);
			}
			executed.set(expectedSeq, result);
			expectedSeq++;
		}
		this.notify(); // Notify so other handleTORRequest can continue their execution

		Request reply = new Request();
		reply.setMessage(executed.get(seq));
		reply.setId(new Identifier(request.getId().getSenderId(), seq));
		reply.setMessageType(MessageType.TORREPLY);
		connector.send(reply, request.getFrom());
	}

	private String compute(Request request) {
		Logger.log("[FO " + this.id + "] Computed request: " + request);
		return "Executed request: (" + request + ")";
	}

	@Override
	public void receive(Request request) {
		switch (request.getMessageType()) {
		case TORREQUEST:
			this.handleTORRquest(request);
			break;
		default: // Ignore the rest
			break;
		}
	}

}
