package es.unavarra.distributedsystems.communication;

import java.io.Serializable;


public class NetworkNode implements Serializable {

	private static final long serialVersionUID = -8603066441030340714L;
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
	
	@Override
	public String toString() {
		return this.getAddress() + ":" + this.getPort();
	}
}
