package threadbutter.butters.http.poster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PosterContext
{
	protected Map<Integer, String> headerNames = null;
	
	protected Map<Integer, String> formNames = null;

	protected List<SeedBean> seeds = null;

	protected List<String> originalLines = null;

	protected String currentFileName = null;
	
	public PosterContext()
	{
		headerNames = new HashMap<Integer, String>();
		formNames = new HashMap<Integer, String>();
		originalLines = new ArrayList<String>(500);
	}

	public void addHeaderName(String name, int idx)
	{
		headerNames.put(idx, name);
	}

	public void addFormName(String name, int idx)
	{
		formNames.put(idx, name);
	}

	public String getHeaderName(int idx)
	{
		return headerNames.get(idx);
	}

	public String getFormName(int idx)
	{
		return formNames.get(idx);
	}

	public int countAllNames()
	{
		return headerNames.size() + formNames.size();
	}

	public void setSeedBeans(List<SeedBean> seeds_)
	{
		this.seeds = seeds_;
	}

	public List<SeedBean> getSeedBeans()
	{
		return this.seeds;
	}

	public void addOriginalLines(String line)
	{
		this.originalLines.add(line);
	}

	public List<String> getOriginalLines()
	{
		return this.originalLines;
	}

	public void setCurrentFileName(String v)
	{
		this.currentFileName = v; 
	}

	public String getCurrentFileName()
	{
		return this.currentFileName;
	}
	
	public void clear()
	{
		this.headerNames.clear();
		this.formNames.clear();
		if(this.seeds != null) {
			this.seeds.clear();
		}
		this.originalLines.clear();
		this.currentFileName = null;
	}
}
