package es.unavarra.distributedsystems.end;

import java.util.ArrayList;

import es.unavarra.distributedsystems.common.Identifier;
import es.unavarra.distributedsystems.common.MessageType;
import es.unavarra.distributedsystems.common.Request;
import es.unavarra.distributedsystems.communication.Connector;
import es.unavarra.distributedsystems.communication.NetworkNode;
import es.unavarra.distributedsystems.communication.Receiver;

public class FO implements Receiver {

	private ArrayList<String> executed;
	private int expectedSeq;
	private Connector connector;

	public FO(Connector connector) {
		this.connector = connector;
		this.expectedSeq = 1;
		this.executed = new ArrayList<String>();
	}

	private synchronized void handleTORRquest(Request request, NetworkNode from) {
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
		connector.send(reply, from);
	}

	private String compute(Request request) {
		return "Executed request: (" + request + ")";
	}

	@Override
	public void receive(Request request, NetworkNode from) {
		switch (request.getMessageType()) {
		case TORREQUEST:
			this.handleTORRquest(request, from);
			break;
		default: // Ignore the rest
			break;
		}
	}

}
