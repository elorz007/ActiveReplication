package es.unavarra.distributedsystems.communication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import es.unavarra.distributedsystems.common.Request;

public class Connector {
	private ArrayList<NetworkNode> broadCastGroup;
	private NetworkNode from;
	
	public Connector(NetworkNode myself) {
		this.setFrom(myself);
	}

	public void send(Request request, NetworkNode to) {
		if (to != null && request != null) {
			try {
				Socket clientSocket = new Socket(to.getAddress(), to.getPort());
				ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
				outToServer.writeObject(request);
				outToServer.writeObject(getFrom());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void bCast(Request request) {
		for (NetworkNode to : broadCastGroup) {
			send(request, to);
		}
	}

	public ArrayList<NetworkNode> getBroadCastGroup() {
		return broadCastGroup;
	}

	public void setBroadCastGroup(ArrayList<NetworkNode> broadCastGroup) {
		this.broadCastGroup = broadCastGroup;
	}

	public NetworkNode getFrom() {
		return from;
	}

	public void setFrom(NetworkNode from) {
		this.from = from;
	}
}
