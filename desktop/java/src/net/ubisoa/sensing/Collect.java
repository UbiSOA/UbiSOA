package net.ubisoa.sensing;

import net.tinyos.message.*;
import net.tinyos.packet.BuildSource;
import net.tinyos.util.PrintStreamMessenger;

public class Collect implements MessageListener {
	private MoteIF moteIF;
	
	public Collect(String source) throws Exception {
		if (source != null)
			moteIF = new MoteIF(BuildSource.makePhoenix(source, PrintStreamMessenger.err));
		else moteIF = new MoteIF(BuildSource.makePhoenix(PrintStreamMessenger.err));
	}
	
	private void addMsgType(Message msg) {
		moteIF.registerListener(msg, this);
	}
	
	public void start() {
	}

	public void messageReceived(int to, net.tinyos.message.Message message) {
		long t = System.currentTimeMillis();
		System.out.print("" + t + ": ");
		System.out.println(message);
	}
	
	private static void usage() {
		System.err.println("usage: Collect -comm <source>");
		System.exit(1);
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			if (args[0].compareTo("-comm") != 0) usage();
			if (args.length == 1) usage();
		}
		else usage();
		
		Collect collect = new Collect(args[1]);
		collect.addMsgType(new Message());
		collect.start();
	}
}
