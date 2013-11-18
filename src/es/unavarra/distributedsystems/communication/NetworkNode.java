package es.unavarra.distributedsystems.communication;


public class NetworkNode {
	private String address;
	private int port;

	public NetworkNode(String address, int port) {
		super();
		this.address = address;
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
