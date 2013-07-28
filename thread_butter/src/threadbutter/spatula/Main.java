package threadbutter.spatula;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Main
{
	static Logger logger = Logger.getLogger(Main.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length < 2) {
			System.out.println("usage: java threadbutter.ThreadButter [property file] [multinum]");
			return;
		}

		Properties props = new Properties();
		int multinum = 0;
		try {
			props.load(new FileInputStream(args[0]));
			multinum = Integer.parseInt(args[1]);
			ButterRepositry.initRepositry(props, multinum);
			Main tb = new Main();
			tb.stir();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Main()
	{
	}

	public void stir() throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		System.out.println("Welcome to \"Thread Butter\" !!");
		ButterCommands.command("help");
		System.out.print("ThreadButter>");
		while((line = in.readLine()) != null) {
			if(!ButterCommands.command(line)) {
				break;
			}
			System.out.print("ThreadButter>");
		}
	}

}
