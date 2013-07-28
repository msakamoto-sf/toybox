package threadbutter.spatula;

import java.io.PrintStream;
import java.util.Map;

public interface ButterInterface extends Runnable
{
	public static final int STATUS_STOP = 0;
	
	public static final int STATUS_RUNNING = 1;

	public static final int STATUS_EXIT = -1;

	public void setButterName(String name);
	
	public String getButterName();

	public long getButterId();

	public void startButter();
	
	public void stopButter();

	public void terminateButter();

	public int butterStatus();

	public void setButterConfig(Map<String, String> config);

	public void setThreadGroupName(String name);

	public String getThreadGroupName();

	public void setThreadGroupIndex(int index);

	public int getThreadGroupIndex();

	public void commandHelp(PrintStream out);
	
	public void commandExec(PrintStream out, String[] args);

	public void init(PrintStream out);
}
