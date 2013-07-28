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

			seeds = new ArrayList<SeedBean>(); // SeedBean�̃��X�g
			int line_all = 0; // �S�s��
			int line_valid = 0; // �R�����g�A��s�ȊO�̗L���s��
			while ((line = br.readLine()) != null) {
				
				// "#"�Ŏn�܂邩�Atrim����������0�̍s�̓X�L�b�v
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
					// ��`�s����͂��A�R���e�L�X�g�ɒ�`����ǂݍ���
					parseSeedDef(line, context);
					existDef = true;
					context.addOriginalLines(line);
				} else {
					if(!existDef) {
						// ��`�s����Ƀf�[�^�s���ǂݍ��܂ꂽ�ꍇ
						throw new SeedException("There's NO '!def' lines!!");
					}
					// �f�[�^�s����͂���SeedBean�̃��X�g�ɒǉ�����
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

				// "H(...)" : �w�b�_�[�t�B�[���h��`
				el = el_.substring(2, el_.length() - 1);
				context.addHeaderName(el, i);
				logger.debug("[" + el_ + "] => [" + el_ + "](Header).");

			} else if(el_.startsWith("P(") && el_.endsWith(")")) {

				// "P(...)" : �t�H�[���v�f����`
				el = el_.substring(2, el_.length() - 1);
				context.addFormName(el, i);
				logger.debug("[" + el_ + "] => [" + el_ + "](Form).");

			} else {

				// �f�t�H���g�̓t�H�[���v�f����`�Ƃ��Ĉ���
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

		// 1�J������(index=0)�̓��[�U�ݒ�̃^�O�Ƃ��Ė������A2�J������(index=1)����͂���B
		seed.setUserTag(result[0]);
		for(int i = 1; i < result.length; i++) {
			value = result[i];

			if((name = context.getHeaderName(i)) != null) {

				// �Ή�����J�����ʒu���w�b�_�[�t�B�[���h��`�̏ꍇ
				seed.addHeader(name, value);
				logger.debug("Coulumn[" + i + "], Header[" + name + "], Value[" + value + "] added.");

			} else if((name = context.getFormName(i)) != null) {

				// �Ή�����J�����ʒu���t�H�[���v�f����`�̏ꍇ
				seed.addFormValue(name, value);
				logger.debug("Coulumn[" + i + "], FormValue[" + name + "], Value[" + value + "] added.");
			} else {

				// �Ή�����J�����ʒu�������ꍇ�A��������B
				logger.warn("Coulumn[" + i + "], Value[" + value + "], Invalid column index, ignored.");
				continue;
			}
		}
		seeds.add(seed);
		return true;
	}

}
