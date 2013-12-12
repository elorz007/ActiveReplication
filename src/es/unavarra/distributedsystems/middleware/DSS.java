package es.unavarra.distributedsystems.middleware;

import java.util.ArrayList;

import es.unavarra.distributedsystems.common.Logger;
import es.unavarra.distributedsystems.common.MessageType;
import es.unavarra.distributedsystems.common.Request;
import es.unavarra.distributedsystems.communication.Connector;
import es.unavarra.distributedsystems.communication.Receiver;

public class DSS implements Receiver {

	private int localSeq;
	private ArrayList<Request> sequenced;
	private Connector connector;
	private int id;

	public DSS(int id, Connector connector) {
		this.id = id;
		this.connector = connector;
		this.localSeq = 1;
		this.sequenced = new ArrayList<Request>();
	}

	public synchronized int getSeq(Request request) {
		int nSeq = sequenced.indexOf(request);
		if (nSeq == -1) {
			request.setMessageType(MessageType.TODELIVER);
			this.connector.bCast(request);
		}
		nSeq = sequenced.indexOf(request);
		while (nSeq == -1) { 
			try {
				this.wait();
				nSeq = sequenced.indexOf(request);
			} catch (InterruptedException e) {}
		}
		return nSeq;
	}
	
	public Request getReq(int seq) {
		return sequenced.get(seq);
	}

	@Override
	public void receive(Request request) {
		switch (request.getMessageType()) {
		case TODELIVER:
			this.handleTODeliver(request);
			break;
		default:
			break;
		}
	}

	private synchronized void handleTODeliver(Request request) {
		int nSeq = sequenced.indexOf(request);
		if (nSeq == -1) {
			Request emptyRequest = new Request();
			while (sequenced.size() <= localSeq) {
				sequenced.add(emptyRequest);
			}
			sequenced.set(localSeq, request);
			localSeq++;
			Logger.log("[DSS " + this.id + "] Updated local sequence number to: " + localSeq);
		}
		this.notify();
	}

}
