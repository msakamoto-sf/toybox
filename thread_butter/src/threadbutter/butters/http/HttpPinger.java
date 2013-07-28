package threadbutter.butters.http;

import java.io.PrintStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

import org.apache.log4j.Logger;

import threadbutter.spatula.ButterBase;

public class HttpPinger extends ButterBase
{
	private String url_ = null;
	private String method_ = null;
	private String ConnectTimeout_ = null;
	private String ReadTimeout_ = null;
	private String ContentType_ = null;

	private long count = 0;

	static Logger logger = Logger.getLogger(HttpPinger.class);

	@Override
	public void setButterConfig(Map<String, String> config_)
	{
		super.setButterConfig(config_);
		this.url_ = config.get("url");
		this.method_ = config.get("method");
		this.ConnectTimeout_ = config.get("ConnectTimeout");
		this.ReadTimeout_ = config.get("ReadTimeout");
		this.ContentType_ = config.get("ContentType");
	}

	@Override
	public void butter()
	{
		if(this.url_ == null) {
			logger.error("\"URL\" is not specified!!");
			return;
		}
		if(this.method_ == null) {
			logger.error("\"method\" is not specified!!");
			return;
		}

		HttpURLConnection con  = null;
		try {
			URL url = new URL(this.url_);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(this.method_);
		} catch(Exception e) {
			logger.error("HTTP Connection Format has occurred!!", e);
			return;
		}

		if(this.ConnectTimeout_ != null) {
			try {
				con.setConnectTimeout(Integer.parseInt(this.ConnectTimeout_));
			} catch(Exception e) {
				logger.warn("\"ConnectTimeout\" is not integer value. ignored.");
			}
		}
		if(this.ReadTimeout_ != null) {
			try {
				con.setReadTimeout(Integer.parseInt(this.ReadTimeout_));
			} catch(Exception e) {
				logger.warn("\"ReadTimeout\" is not integer value. ignored.");
			}
		}
		if(this.ContentType_ != null) {
			con.setRequestProperty("Content-Type", this.ContentType_); 
		}

		try {
			logger.info("Connection start(URL=[" + this.url_ + "],METHOD=[" + this.method_ + "]).");
			con.connect();
			int hrc = con.getResponseCode(); 
			String hrs = con.getResponseMessage();
			
			count++;
			logger.info("HTTP RESPONSE CODE =[" + hrc + " " + hrs + "] (" + count + " times).");
		} catch(ConnectException e) {
			System.out.println("Connection Error has occurred. Check log file.");
			logger.error("Connection Error has occurred. ", e);
		} catch(SocketTimeoutException e) {
			System.out.println("Socket Timeout Error has occurred. Check log file.");
			logger.error("Socket Timeout Error has occurred. ", e);
		} catch(Exception e) {
			System.out.println("Unknown Error has occurred. Check log file.");
			logger.error("Unknown Error has occurred. ", e);
		}
		
	}

	public void commandHelp(PrintStream out)
	{
		out.println("HttpPinger command:");
		out.println("'show' : show HTTP ping configuration.");
	}

	public void commandExec(PrintStream out, String[] args)
	{
		if(args.length == 1 && args[0].trim().equals("show")) {
			out.println("HttpPinger Configuration:");
			out.println("URL = [" + this.url_ + "]");
			out.println("METHOD = [" + this.method_ + "]");
			out.println("Connection Timeout = [" + this.ConnectTimeout_ + "] msecs");
			out.println("Read Timeout = [" + this.ReadTimeout_ + "] msec");
			out.println("ContenntType = [" + this.ContentType_ + "]");
		} else {
			commandHelp(out);
		}
	}

	public void init(PrintStream out)
	{
		// DO NOTHING.
	}
}
