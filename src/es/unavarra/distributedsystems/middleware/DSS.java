package es.unavarra.distributedsystems.middleware;

import java.util.ArrayList;

import es.unavarra.distributedsystems.common.Request;
import es.unavarra.distributedsystems.communication.Connector;
import es.unavarra.distributedsystems.communication.Receiver;

public class DSS implements Receiver {

	private int localSeq;
	private ArrayList<Request> sequenced;
	private Connector connector;

	public DSS(Connector connector) {
		this.connector = connector;
		this.localSeq = 1;
		this.sequenced = new ArrayList<Request>();
	}

	public synchronized int getSeq(Request request) {
		int nSeq = sequenced.indexOf(request);
		if (nSeq == -1) {
			this.connector.bCast(request, this);
		}
		while (nSeq == -1) { 
			try {
				this.wait();
			} catch (InterruptedException e) {}
		}
		
		return nSeq;
	}
	
	public Request getReq(int seq) {
		return sequenced.get(seq);
	}

	@Override
	public void receive(Request request, Receiver from) {
		switch (request.getMessageType()) {
		case TODELIVER:
			this.handleTODeliver(request, from);
			break;
		default:
			break;
		}
	}

	private synchronized void handleTODeliver(Request request, Receiver from) {
		int nSeq = sequenced.indexOf(request);
		if (nSeq == -1) {
			sequenced.set(localSeq, request);
			localSeq++;
		}
	}

}
