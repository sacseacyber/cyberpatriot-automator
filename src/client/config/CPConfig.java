package client.config;

import client.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CPConfig {
	private String remoteDataServerHost;
	private int remoteDataServerPort;

	private List<Consumer<CPConfig>> changeListeners = new ArrayList<>();

	public static File GetDefaultFileLocation() {
		return new File(Util.isWindows() ? "C:\\CPConfig.dat" : "/CPConfig.dat");
	}

	public static CPConfig ReadFromFile(File location) {
		/*
			Simple protocol
			4 bytes for IP
			2 bytes for port
			Add more later
		 */

		int host1 = 192, host2 = 168, host3 = 45, host4 = 85;
		int port = 54248;

		try {
			String text = Util.readFromFile(location);

			host1 = (byte)text.charAt(0);
			host2 = (byte)text.charAt(1);
			host3 = (byte)text.charAt(2);
			host4 = (byte)text.charAt(3);

			port = (text.charAt(4) << 8 | text.charAt(5));
		} catch(IOException ignore) {}

		return new CPConfig(
				host1 + "." + host2 + "." + host3 + "." + host4,
				port
		);
	}

	public CPConfig(String remoteDataServerHost, int remoteDataServerPort) {
		this.remoteDataServerHost = remoteDataServerHost;
		this.remoteDataServerPort = remoteDataServerPort;
	}

	public int getRemoteDataServerPort() {
		return remoteDataServerPort;
	}

	public String getRemoteDataServerHost() {
		return remoteDataServerHost;
	}

	public void setRemoteDataServerHost(String remoteDataServerHost) {
		this.remoteDataServerHost = remoteDataServerHost;
	}

	public void setRemoteDataServerPort(int remoteDataServerPort) {
		this.remoteDataServerPort = remoteDataServerPort;
	}

	public void saveToFile() {
		this.saveToFile(CPConfig.GetDefaultFileLocation());
	}

	public void saveToFile(File location) {
		String[] hostBits = this.getRemoteDataServerHost().split("\\.");
		byte[] hostBitsInts = new byte[] {
				(byte)Integer.parseInt(hostBits[0]),
				(byte)Integer.parseInt(hostBits[1]),
				(byte)Integer.parseInt(hostBits[2]),
				(byte)Integer.parseInt(hostBits[3])
		};

		byte[] fileData = new byte[] {
				hostBitsInts[0],
				hostBitsInts[1],
				hostBitsInts[2],
				hostBitsInts[3],
				(byte)((this.getRemoteDataServerPort() >> 8) & 0xFF),
				(byte)((this.getRemoteDataServerPort()     ) & 0xFF)
		};

		try {
			Util.writeToFile(location, fileData);
		} catch(IOException ignore) {}

		for (Consumer<CPConfig> callback : this.changeListeners) {
			callback.accept(this);
		}
	}

	public void addChangeListener(Consumer<CPConfig> callback) {
		this.changeListeners.add(callback);
	}
}
