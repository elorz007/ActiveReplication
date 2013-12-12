package es.unavarra.distributedsystems.middleware;

import java.util.ArrayList;

import es.unavarra.distributedsystems.common.Identifier;
import es.unavarra.distributedsystems.common.Logger;
import es.unavarra.distributedsystems.common.MessageType;
import es.unavarra.distributedsystems.common.Request;
import es.unavarra.distributedsystems.communication.Connector;
import es.unavarra.distributedsystems.communication.NetworkNode;
import es.unavarra.distributedsystems.communication.Receiver;

public class ARH implements Receiver {

	private int lastServedReq;
	private DSS sequencer;
	private ArrayList<NetworkNode> replicas;
	private Connector connector;
	private Request replyFromReplica;
	private int id;

	public ARH(int id, Connector connector, DSS sequencer, ArrayList<NetworkNode> replicas) {
		this.id = id;
		this.connector = connector;
		this.replicas = replicas;
		this.lastServedReq = 0;
		this.sequencer = sequencer;
	}

	@Override
	public void receive(Request request) {
		switch (request.getMessageType()) {
		case REQUEST:
			this.handleClientRequest(request);
			break;
		case TORREPLY:
			this.handleTORReply(request);
			break;
		default: // Ignore the rest
			break;
		}
	}

	private synchronized void handleTORReply(Request request) {
		replyFromReplica = request;
		this.notify();
	}

	private synchronized void handleClientRequest(Request request) {
		NetworkNode originalClient = request.getFrom();
		Logger.log("[ARH " + this.id + "] Handle client request: " + request);
		int nSeq = sequencer.getSeq(request);
		Logger.log("[ARH " + this.id + "] Assigned sequence number: " + nSeq);
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
		connector.send(reply, originalClient);
		Logger.log("[ARH " + this.id + "] Replied to " + originalClient + ": " + reply);
	}

	public void sendTORRequest(int sequence, Request sourceRequest) {
		for (NetworkNode r : replicas) {
			Request request = new Request();
			request.setId(new Identifier(sourceRequest.getId().getSenderId(), sequence));
			request.setMessage(sourceRequest.getMessage());
			request.setMessageType(MessageType.TORREQUEST);
			connector.send(request, r);
		}
	}

}
