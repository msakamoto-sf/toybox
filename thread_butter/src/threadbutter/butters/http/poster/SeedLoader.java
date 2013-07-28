package threadbutter.butters.http.poster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SeedLoader
{
	static Logger logger = Logger.getLogger(SeedLoader.class);

	public static List<SeedBean> load(File file, String encode, PosterContext context) throws SeedException
	{
		BufferedReader br = null;
		List<SeedBean> seeds = null;
		String line = null;
		boolean existDef = false;
		try {
			if(encode == null || encode.trim().length() == 0) {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			} else {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encode));
			}
			logger.info("Loading data file [" + file.getName() + "] starts.");

			seeds = new ArrayList<SeedBean>(); // SeedBeanのリスト
			int line_all = 0; // 全行数
			int line_valid = 0; // コメント、空行以外の有効行数
			while ((line = br.readLine()) != null) {
				
				// "#"で始まるか、trimした長さが0の行はスキップ
				if(line.startsWith("#")) {
					logger.debug("This line is comment, skipped. [" + line + "]");
					continue;
				}
				if(line.trim().length() == 0) {
					continue;
				}

				if(line.startsWith("!def")) {
					if(existDef) {
						logger.warn("Definition line are already loaded, ignored. [" + line + "]");
						continue;
					}
					// 定義行を解析し、コンテキストに定義情報を読み込む
					parseSeedDef(line, context);
					existDef = true;
					context.addOriginalLines(line);
				} else {
					if(!existDef) {
						// 定義行より先にデータ行が読み込まれた場合
						throw new SeedException("There's NO '!def' lines!!");
					}
					// データ行を解析してSeedBeanのリストに追加する
					if(parseSeedLine(line, seeds, context)) {
						line_valid++;
						context.addOriginalLines(line);
					}
				}
				line_all++;
			}
			String msg = "Data file loading COMPLETES. all[" + line_all + "] lines/valid["
			+ line_valid + "] lines.";
			System.out.println(msg);
			logger.info(msg);
		} catch(SeedException e) {
			logger.error("File Loading Error has occurred.", e);
			throw e;
		} catch(Exception e) {
			logger.error("File Loading Error has occurred.", e);
			throw new SeedException("File Loading Error. Check log file.");
		} finally {
			if(br != null) {
				try { 
					br.close();
				} catch(Exception e) {
				}
			}
		}
		return seeds;
	}

	protected static void parseSeedDef(String line, PosterContext context
		) throws SeedException
	{
		String result[] = StringUtils.splitPreserveAllTokens(line, ",");
		if(result.length == 1) {
			throw new SeedException("Not valid '!def' line. [" + line + "]");
		}
		String el_ = null;
		String el = null;
		for(int i = 1; i < result.length; i++) {
			el_ = result[i];
			if(el_.trim().length() == 0) {
				continue;
			}

			if(el_.startsWith("H(") && el_.endsWith(")")) {

				// "H(...)" : ヘッダーフィールド定義
				el = el_.substring(2, el_.length() - 1);
				context.addHeaderName(el, i);
				logger.debug("[" + el_ + "] => [" + el_ + "](Header).");

			} else if(el_.startsWith("P(") && el_.endsWith(")")) {

				// "P(...)" : フォーム要素名定義
				el = el_.substring(2, el_.length() - 1);
				context.addFormName(el, i);
				logger.debug("[" + el_ + "] => [" + el_ + "](Form).");

			} else {

				// デフォルトはフォーム要素名定義として扱う
				el = el_;
				context.addFormName(el, i);
				logger.debug("[" + el_ + "] => [" + el_ + "](Form).");
			}
		}
	}

	protected static boolean parseSeedLine(
		String line, List<SeedBean> seeds, PosterContext context
		) throws SeedException
	{
		String result[] = StringUtils.splitPreserveAllTokens(line, ",");
		if(result.length < 1 + context.countAllNames()) {
			logger.warn("Invalid column count for line [" + line + "], ignored.");
			return false;
		}
		String name = null;
		String value = null;
		SeedBean seed = new SeedBean();

		// 1カラム目(index=0)はユーザ設定のタグとして無視し、2カラム目(index=1)より解析する。
		seed.setUserTag(result[0]);
		for(int i = 1; i < result.length; i++) {
			value = result[i];

			if((name = context.getHeaderName(i)) != null) {

				// 対応するカラム位置がヘッダーフィールド定義の場合
				seed.addHeader(name, value);
				logger.debug("Coulumn[" + i + "], Header[" + name + "], Value[" + value + "] added.");

			} else if((name = context.getFormName(i)) != null) {

				// 対応するカラム位置がフォーム要素名定義の場合
				seed.addFormValue(name, value);
				logger.debug("Coulumn[" + i + "], FormValue[" + name + "], Value[" + value + "] added.");
			} else {

				// 対応するカラム位置が無い場合、無視する。
				logger.warn("Coulumn[" + i + "], Value[" + value + "], Invalid column index, ignored.");
				continue;
			}
		}
		seeds.add(seed);
		return true;
	}

}
