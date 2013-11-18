package es.unavarra.distributedsystems.common;

public class Identifier {
	private int senderId;
	private int seq;

	public int getSeq() {
		return seq;
	}

	public Identifier(int clientId, int seq) {
		super();
		this.senderId = clientId;
		this.seq = seq;
	}

	public int getSenderId() {
		return senderId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Identifier) {
			return equalsToIdentifier((Identifier) obj);
		} else {
			return false;
		}
	}
	
	public boolean equalsToIdentifier(Identifier other) {
		return other.getSenderId() == this.getSenderId() && other.getSeq() == this.getSeq();
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "< Sender: " + getSenderId() + ", seq: " + getSeq() + ">"; 
	}
}
