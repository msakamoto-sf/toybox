package threadbutter.spatula;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ButterCommands
{

	public static boolean command(String line)
	{
		String args[] = line.split(" ");
		if (args.length == 0) {
			return true;
		}
		if (args[0].equals("status") || args[0].equals("start")
				|| args[0].equals("stop")) {
			walkAroundThreads(args);
			return true;
		}
		if (args[0].equals("exit")) {
			walkAroundThreads(args);
			return false;
		}
		if(args.length > 1) {
			walkAroundThreads(args);
			return true;
			
		}
		help(args);
		return true;
	}

	public static void walkAroundThreads(String args[])
	{
		String command = args[0];
		String tgname_ = "";
		String tname_ = "";
		if (args.length > 1) {
			tgname_ = args[1];
		}
		if (args.length > 2) {
			tname_ = args[2];
		}
		Iterator<String> i = ButterRepositry.getThreadGroups().keySet()
				.iterator();
		String tgname = "";
		String tname = "";
		List<ButterInterface> threads = null;
		while (i.hasNext()) {
			tgname = i.next();
			if (tgname_ != "" && !tgname_.equals(tgname)) {
				continue;
			}
			System.out.println("ThreadGroups[" + tgname + "]:");
			threads = ButterRepositry.getThreads().get(tgname);
			Iterator<ButterInterface> i_ = threads.iterator();
			while (i_.hasNext()) {
				ButterInterface butter = i_.next();
				tname = butter.getButterName();
				if (tname_ != "" && !tname_.equals(tname)) {
					continue;
				}
				if (command.equals("status")) {
					status(tgname, tname, butter);
				} else if (command.equals("start")) {
					start(tgname, tname, butter);
				} else if (command.equals("stop")) {
					stop(tgname, tname, butter);
				} else if (command.equals("help")) {
					butterHelp(tgname, tname, butter);
				} else if (command.equals("command")) {
					butterCommand(tgname, tname, butter, args);
				} else if (command.equals("exit")) {
					terminate(tgname, tname, butter);
				}
			}
			System.out.println("");
		}
	}

	public static void start(String tgname, String tname, ButterInterface butter)
	{
		butter.startButter();
		System.out.println("--[" + tname + "]: starts.");
	}

	public static void stop(String tgname, String tname, ButterInterface butter)
	{
		butter.stopButter();
		System.out.println("--[" + tname + "]: stops.");
	}

	public static void terminate(String tgname, String tname,
			ButterInterface butter)
	{
		butter.terminateButter();
		System.out.println("--[" + tname + "]: terminates.");
	}

	public static void status(String tgname, String tname,
			ButterInterface butter)
	{
		System.out.print("--[" + tname + "](ThreadID:" + butter.getButterId()
				+ "): status=[");
		switch (butter.butterStatus()) {
		case ButterInterface.STATUS_STOP:
			System.out.print("STOP");
			break;
		case ButterInterface.STATUS_RUNNING:
			System.out.print("RUNNING");
			break;
		case ButterInterface.STATUS_EXIT:
			System.out.print("TERMINATING");
			break;
		default:
			System.out.print("UNKNOWN");
		}
		System.out.println("]");
	}

	public static void butterHelp(String tgname, String tname, ButterInterface butter)
	{
		butter.commandHelp(System.out);
	}

	public static void butterCommand(String tgname, String tname, ButterInterface butter, String[] args)
	{
		if(args.length < 3) {
			System.out.println("Invalid butter command arguments.");
			return;
		}
		if(args.length == 3) {
			butter.commandHelp(System.out);
			return;
		}
		String args_[] = new String[args.length - 3];
		for(int i = 0; i < args_.length; i++) {
			args_[i] = args[i + 3];
		}
		butter.commandExec(System.out, args_);
	}
	
	public static void help(String args[])
	{
		System.out.println("commands: ");
		System.out.println("start [ThreadGruopName] [ThreadName] : start specified threads.");
		System.out.println("stop [ThreadGruopName] [ThreadName] : stop specified threads.");
		System.out.println("status [ThreadGruopName] [ThreadName] : show status of specified threads.");
		System.out.println("command [ThreadGroupName] [ThreadName] args... : execute thread butter specific commands.");
		System.out.println("help : show this message.");
		System.out.println("help [ThreadGroupName] [ThreadName] : show thread butter specific command help.");
		System.out.println("exit : terminate ThreadButter.");
		System.out.println("");
	}
}
