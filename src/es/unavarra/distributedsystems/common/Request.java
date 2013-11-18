package es.unavarra.distributedsystems.common;

public class Request {
	private Identifier id;
	private String message;
	private MessageType messageType;

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
		return getId() + " message: " + getMessage();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Request) {
			return equalsToRequest((Request) obj);
		}
		else {
			return false;
		}
	}

	private boolean equalsToRequest(Request other) {
		if (this.getId() != null && other != null) {
			return this.getId().equalsToIdentifier(other.getId());
		}
		else {
			return false;
		}
	}
}
