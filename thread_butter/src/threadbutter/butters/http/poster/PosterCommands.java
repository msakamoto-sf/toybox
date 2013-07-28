package threadbutter.butters.http.poster;

import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class PosterCommands
{
	static Logger logger = Logger.getLogger(PosterCommands.class);

	public static void load(PrintStream out, PosterContext context, String encode, String filename)
	{
		File file = new File(filename);
		if(!file.exists()) {
			String msg = "File[" + filename + "] is NOT Exists!!";
			out.println(msg);
			logger.error(msg);
			return;
		}
		if(!file.canRead()) {
			String msg = "File[" + filename + "] is NOT Readable!!";
			out.println(msg);
			logger.error(msg);
			return;
		}
		try {
			context.setSeedBeans(SeedLoader.load(file, encode, context));
		} catch(SeedException e) {
			out.println(e.getMessage());
		}
	}

	public static void unload(PrintStream out, PosterContext context)
	{
		context.clear();
		out.println("Data is all cleared.");
	}

	public static void showdatas(PrintStream out, PosterContext context)
	{
		Iterator<String> i = context.getOriginalLines().iterator();
		while(i.hasNext()) {
			out.println(i.next());
		}
	}
}
