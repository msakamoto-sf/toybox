package threadbutter.spatula;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

public class ButterRepositry
{
	static Logger logger = Logger.getLogger(ButterRepositry.class);

	static final String BUTTERS_KEY = "tb.butters";
	static final String BUTTER_KEY = "tb.butter.";
	static final String THREADGROUP_NAME = "tbg";

	protected static HashMap<String, ThreadGroup> threadGroups = new HashMap<String, ThreadGroup>();

	protected static HashMap<String, List<ButterInterface>> threads = new HashMap<String, List<ButterInterface>>();

	protected static Properties props = null;

	protected static int multinum = 0;
	
	public static void initRepositry(Properties props_, int multinum_)
	{
		props = props_;
		multinum = multinum_;

		String butters_ = props.getProperty(BUTTERS_KEY, "");
		String[] butters = butters_.split(",");
		if(butters.length == 0) {
			System.out.println("No Butters Definition, Program terminates");
			return;
		}
		NDC.push("initRepositry");
		String tgname__ = System.getProperty("ThreadButterGroupName", THREADGROUP_NAME);
		for(int i = 0; i < multinum; i++) {
			String tgname = tgname__ + "_" + i;
			logger.debug("ThreadGroupName = [" + tgname + "]");

			makeThreadGroup(tgname);
			for(int j = 0; j < butters.length; j++) {
				String btname = butters[j].trim();
				String btkey = BUTTER_KEY + btname + ".";
				logger.debug("ButterName(ThreadName) = [" + btname + "]");

				Map<String, String> config = makeThreadConfig(btkey, props);
				try {
					makeThread(config, tgname, btname, i).start();
					logger.info("ButterName(ThreadName) = [" + btname + "] started.");
				} catch(ButterException e) {
					System.out.println(e.getMessage());
					logger.warn("ButterException has occurred! continue...", e);
				} catch(Exception e) {
					System.out.println(e.getMessage());
					logger.warn("Unknown Exception has occurred! continue...", e);
				}
			}
		}
		NDC.pop();
	}

	public static int getMultiNum()
	{
		return multinum;
	}

	public static Map<String, ThreadGroup> getThreadGroups()
	{
		return threadGroups;
	}

	public static Map<String, List<ButterInterface>> getThreads()
	{
		return threads;
	}

	/**
	 * スレッドグループを作成し、内部リポジトリに登録する。
	 * 
	 * @param name
	 * @return
	 */
	public static ThreadGroup makeThreadGroup(String name)
	{
		synchronized (threadGroups) {
			ThreadGroup tg = new ThreadGroup(name);
			List<ButterInterface> ts = new ArrayList<ButterInterface>();
			threadGroups.put(name, tg);
			threads.put(name, ts);
			return tg;
		}
	}

	/**
	 * 指定されたクラスのインスタンススレッドを作成し、内部リポジトリに登録する。
	 * 
	 * @param config
	 * @param tgname
	 * @param tname
	 * @param tgidx
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ButterException
	 */
	public static Thread makeThread(Map<String, String> config, String tgname, String tname, int tgidx)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, ButterException
	{
		if (config.get("class") == null) {
			throw new ButterException("\"class\" value is NOT SPECIFIED: ["
					+ tgname + "]");
		}
		String clazz = config.get("class").toString();
		ButterInterface butter = (ButterInterface) Class.forName(clazz)
				.newInstance();
		ThreadGroup tg = threadGroups.get(tgname);
		List<ButterInterface> ts = threads.get(tgname);
		if (tg == null || ts == null) {
			throw new ButterException("No such thread group: [" + tgname + "]");
		}
		butter.setButterName(tname);
		butter.setButterConfig(config);
		butter.setThreadGroupName(tgname);
		butter.setThreadGroupIndex(tgidx);
		Thread t = new Thread(tg, butter, tname);
		synchronized (ts) {
			ts.add(butter);
		}
		return t;
	}

	/**
	 * 設定プロパティのエントリを、対象となるButterの設定のみを取り出し、
	 * キー名もButter名の部分までを取り除き、Butter専用のMapを作成する。
	 * 
	 * @param btkey
	 * @param configs
	 * @return
	 */
	public static Map<String, String> makeThreadConfig(String btkey,
			Properties configs)
	{
		NDC.push("makeThreadConfig");
		HashMap<String, String> config = new HashMap<String, String>();
		Enumeration e = configs.keys();
		while (e.hasMoreElements()) {
			String key_ = e.nextElement().toString();
			int idx = key_.indexOf(btkey);
			if (idx == -1)
				continue;
			String key = key_.substring(btkey.length());
			logger.debug("[" + key_ + "] => [" + key + "]");

			config.put(key, configs.get(key_).toString());
		}
		NDC.pop();
		return config;
	}
}
