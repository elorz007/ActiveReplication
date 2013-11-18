package es.unavarra.distributedsystems.common;

public class Logger {
	public static synchronized void log(String message) {
		System.out.println(message);
	}
	
	public static synchronized void eLog(String message) {
		System.err.println(message);
	}
}
