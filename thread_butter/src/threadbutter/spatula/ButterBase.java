package threadbutter.spatula;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public abstract class ButterBase extends Thread implements ButterInterface
{
	protected int status = STATUS_STOP;

	protected Map<String, String> config = null;

	protected String threadGroupName = "";

	protected int threadGroupIndex = 0;

	public String getButterName()
	{
		return getName();
	}

	public void setButterName(String name)
	{
		setName(name);
	}

	public void setButterConfig(Map<String, String> config_)
	{
		config = config_;
	}

	public long getButterId()
	{
		return getId();
	}
	
	public int butterStatus()
	{
		return status;
	}

	public void startButter()
	{
		synchronized (this) {
			status = STATUS_RUNNING;
		}
	}

	public void stopButter()
	{
		synchronized (this) {
			status = STATUS_STOP;
		}
	}

	public void terminateButter()
	{
		synchronized (this) {
			status = STATUS_EXIT;
		}
	}
	
	public void setThreadGroupName(String name)
	{
		threadGroupName = name;
	}

	public void setThreadGroupIndex(int index)
	{
		threadGroupIndex = index;
	}

	public String getThreadGroupName()
	{
		return threadGroupName;
	}

	public int getThreadGroupIndex()
	{
		return threadGroupIndex;
	}
	
	@Override
	public void run()
	{
		MDC.put("ThreadGroupName", threadGroupName);
		MDC.put("ThreadId", getId());

		int sleep = 0;
		try {
			sleep = Integer.parseInt(config.get("sleep"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		while (status != STATUS_EXIT) {
			if (status == STATUS_RUNNING) {
				butter();
			}
			try {
				sleep(sleep);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public abstract void butter();
}
