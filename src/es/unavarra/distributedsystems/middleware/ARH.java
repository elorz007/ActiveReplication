package es.unavarra.distributedsystems.middleware;

import java.util.ArrayList;

import es.unavarra.distributedsystems.common.Identifier;
import es.unavarra.distributedsystems.common.MessageType;
import es.unavarra.distributedsystems.common.Request;
import es.unavarra.distributedsystems.communication.Connector;
import es.unavarra.distributedsystems.communication.Receiver;
import es.unavarra.distributedsystems.end.FO;

public class ARH implements Receiver {

	private int lastServedReq;
	private DSS sequencer;
	private ArrayList<FO> replicas;
	private Connector connector;
	private Request replyFromReplica;

	public ARH(Connector connector, DSS sequencer, ArrayList<FO> replicas) {
		this.connector = connector;
		this.replicas = replicas;
		this.lastServedReq = 0;
		this.sequencer = sequencer;
	}

	@Override
	public void receive(Request request, Receiver from) {
		switch (request.getMessageType()) {
		case REQUEST:
			this.handleClientRequest(request, from);
			break;
		case TORREPLY:
			this.handleTORReply(request, from);
			break;
		default: // Ignore the rest
			break;
		}
	}

	private synchronized void handleTORReply(Request request, Receiver from) {
		replyFromReplica = request;
		this.notify();
	}

	private synchronized void handleClientRequest(Request request, Receiver client) {
		int nSeq = sequencer.getSeq(request);
		if (nSeq > lastServedReq + 1) {
			for (int j = lastServedReq + 1; j < nSeq; j++) {
				Request oldRequest = sequencer.getReq(j);
				sendTORRequest(j, oldRequest);
			}
		}
		sendTORRequest(nSeq, request);
		lastServedReq = Math.max(lastServedReq, nSeq);

		while (replyFromReplica == null) {
			try {
				this.wait(); // wait just for one replica to respond
			} catch (InterruptedException e) {
			}
		}

		Request reply = new Request();
		reply.setId(request.getId());
		reply.setMessage(replyFromReplica.getMessage());
		reply.setMessageType(MessageType.REPLY);
		connector.send(reply, this, client);
	}

	public void sendTORRequest(int sequence, Request sourceRequest) {
		for (FO r : replicas) {
			Request request = new Request();
			request.setId(new Identifier(sourceRequest.getId().getSenderId(), sequence));
			request.setMessage(sourceRequest.getMessage());
			request.setMessageType(MessageType.TORREQUEST);
			connector.send(request, this, r);
		}
	}

}
