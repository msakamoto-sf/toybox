package threadbutter.butters.http.poster;

import java.util.HashMap;
import java.util.Map;

/**
 * @author FengJing
 * HttpPoster���g�p����HTTP POST ���N�G�X�g�����p�̃p�����[�^��ێ�����B
 */
/**
 * @author FengJing
 *
 */
public class SeedBean
{
	/**
	 * �w�b�_�[�t�B�[���h�����L�[�Ƃ����ƒl�̃n�b�V���}�b�v
	 */
	private HashMap<String, String> headers = new HashMap<String, String>();

	/**
	 * �t�H�[���v�f�����L�[�Ƃ����l�̃n�b�V���}�b�v
	 */
	private HashMap<String, String> formValues = new HashMap<String, String>();

	/**
	 * HTTP���N�G�X�g�ɂ͎g�p���ꂸ�A���O�⃁�b�Z�[�W���ł̂ݎg�p�����^�O
	 */
	private String userTag = "";
	
	/**
	 * �w�b�_�[�t�B�[���h���ƒl�̃y�A��ǉ�����B
	 * 
	 * @param k �w�b�_�[�t�B�[���h��
	 * @param v �l
	 */
	public void addHeader(String k, String v)
	{
		headers.put(k, v);
	}

	/**
	 * �w�b�_�[�t�B�[���h�����L�[�Ƃ����l�̃}�b�v���擾����B
	 * 
	 * @return �w�b�_�[�t�B�[���h�����L�[�Ƃ����l�̃}�b�v
	 */
	public Map<String, String> getHeaders()
	{
		return headers;
	}

	/**
	 * �t�H�[���v�f���ƒl�̃y�A��ǉ�����B
	 * 
	 * @param k �t�H�[���v�f��
	 * @param v �l
	 */
	public void addFormValue(String k, String v)
	{
		formValues.put(k, v);
	}

	/**
	 * �t�H�[���v�f�����L�[�Ƃ����l�̃}�b�v���擾����B
	 * 
	 * @return �t�H�[���v�f�����L�[�Ƃ����l�̃}�b�v
	 */
	public Map<String, String> getFormValues()
	{
		return formValues;
	}

	/**
	 * �^�O��ݒ肷��B
	 *  
	 * @param v �^�O������
	 */
	public void setUserTag(String v)
	{
		this.userTag = v;
	}

	/**
	 * �^�O���擾����B
	 * 
	 * @return �^�O������
	 */
	public String getUserTag()
	{
		return this.userTag;
	}
}
