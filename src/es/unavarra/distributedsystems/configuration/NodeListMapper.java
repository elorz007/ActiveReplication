package es.unavarra.distributedsystems.configuration;

import java.util.ArrayList;

import es.unavarra.distributedsystems.common.Logger;
import es.unavarra.distributedsystems.communication.NetworkNode;

public class NodeListMapper {
	public ArrayList<NetworkNode> map(String raw) {
		ArrayList<NetworkNode> result = new ArrayList<NetworkNode>();
		String[] hosts = raw.split(",");
		for (int i = 0; i < hosts.length; i++) {
			String host = hosts[i];
			String[] components = host.split(":");
			if (components.length == 2) {
				NetworkNode networkNode = null;
				try {
					networkNode = new NetworkNode(components[0], Integer.parseInt(components[1]));
				} catch (NumberFormatException e) {
					Logger.log("Error parsing network node: " + host);
				}
				if (networkNode != null) {
					result.add(networkNode);
				}
			} else {
				Logger.log("Error parsing network node: " + host);
			}
		}
		return result;
	}
}
