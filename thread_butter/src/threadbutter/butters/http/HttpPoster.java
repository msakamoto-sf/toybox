package threadbutter.butters.http;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import threadbutter.butters.http.poster.PosterCommands;
import threadbutter.butters.http.poster.PosterContext;
import threadbutter.spatula.ButterBase;

public class HttpPoster extends ButterBase
{
	private String url_ = null;
	private String method_ = null;
	private String ConnectTimeout_ = null;
	private String ReadTimeout_ = null;
	private String ContentType_ = null;
	private String DataFileEncode_ = null;
	private String HttpPostEncode_ = null;

	private PosterContext context = null;

	private long count = 0;

	static Logger logger = Logger.getLogger(HttpPinger.class);

	public HttpPoster()
	{
		context = new PosterContext();
	}
	
	@Override
	public void setButterConfig(Map<String, String> config_)
	{
		super.setButterConfig(config_);
		this.url_ = config.get("url");
		this.method_ = config.get("method");
		this.ConnectTimeout_ = config.get("ConnectTimeout");
		this.ReadTimeout_ = config.get("ReadTimeout");
		this.ContentType_ = config.get("ContentType");
		this.DataFileEncode_ = config.get("encode.datafile");
		this.HttpPostEncode_ = config.get("encode.httppost");
		this.context.setCurrentFileName(config.get("csvdata"));
	}

	@Override
	public void butter()
	{
		
	}

	public void commandHelp(PrintStream out)
	{
		out.println("HttpPoster commands:");
		out.println("'load <file>' : load HttpPoster data file(comma separated).");
		out.println("'unload' : clear loaded data file.");
		out.println("'show' : show HTTP Poster configuration.");
		out.println("'show data' : show loaded datas.");
	}

	public void commandExec(PrintStream out, String[] args)
	{
		if(args.length < 1) {
			commandHelp(out);
			return;
		}
		String cmd = args[0].trim();
		int argc = args.length;
		if(cmd.equals("show")) {
			if(argc == 2 && args[1].trim().equals("data")) {
				out.println("Show data : current file = [" + this.context.getCurrentFileName() + "]");
				PosterCommands.showdatas(out, this.context);
				out.println("");
			} else {
				out.println("HttpPinger Configuration:");
				out.println("URL = [" + this.url_ + "]");
				out.println("METHOD = [" + this.method_ + "]");
				out.println("Connection Timeout = [" + this.ConnectTimeout_ + "] msecs");
				out.println("Read Timeout = [" + this.ReadTimeout_ + "] msec");
				out.println("ContenntType = [" + this.ContentType_ + "]");
				out.println("Data file Encoding = [" + this.DataFileEncode_ + "]");
				out.println("HTTP POST Encoding = [" + this.HttpPostEncode_ + "]");
				if(this.context.getCurrentFileName() != null) {
					out.println("Current File = [" + this.context.getCurrentFileName() + "]");
				}
			}
		} else if(cmd.equals("load")) {
			if(argc != 2) {
				out.println("Argument Error.");
				commandHelp(out);
				return;
			}
			String filename = args[1].trim();
			if(filename.length() == 0) {
				out.println("File name is not specified.");
				commandHelp(out);
			}
			PosterCommands.load(out, this.context, this.DataFileEncode_, filename);
			
		} else if(cmd.equals("unload")) {
			out.println("Unload data of file[" + this.context.getCurrentFileName() + "]...");
			this.context.clear();
			out.println("Unload completed.");
		}
	}

	public void init(PrintStream out)
	{
		String filename = this.context.getCurrentFileName();
		if(filename != null && filename.trim().length() != 0) {
			PosterCommands.load(out, this.context, this.DataFileEncode_, filename);
		}
	}
}
