package es.unavarra.distributedsystems.common;

import java.io.Serializable;

import es.unavarra.distributedsystems.communication.NetworkNode;

public class Request implements Serializable {

	private static final long serialVersionUID = 2103093584891058824L;
	private Identifier id;
	private String message;
	private MessageType messageType;
	private NetworkNode from;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Identifier getId() {
		return id;
	}

	public void setId(Identifier id) {
		this.id = id;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	@Override
	public String toString() {
		return getId() + " from: " + this.getFrom() + " message: " + getMessage();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Request) {
			return equalsToRequest((Request) obj);
		} else {
			return false;
		}
	}

	private boolean equalsToRequest(Request other) {
		if (this.getId() != null && other != null) {
			return this.getId().equalsToIdentifier(other.getId());
		} else {
			return false;
		}
	}

	public NetworkNode getFrom() {
		return from;
	}

	public void setFrom(NetworkNode from) {
		this.from = from;
	}
}
