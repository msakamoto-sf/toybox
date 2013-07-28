package threadbutter.butters.http.poster;

import java.util.HashMap;
import java.util.Map;

/**
 * @author FengJing
 * HttpPosterが使用するHTTP POST リクエスト生成用のパラメータを保持する。
 */
/**
 * @author FengJing
 *
 */
public class SeedBean
{
	/**
	 * ヘッダーフィールド名をキーとしたと値のハッシュマップ
	 */
	private HashMap<String, String> headers = new HashMap<String, String>();

	/**
	 * フォーム要素名をキーとした値のハッシュマップ
	 */
	private HashMap<String, String> formValues = new HashMap<String, String>();

	/**
	 * HTTPリクエストには使用されず、ログやメッセージ内でのみ使用されるタグ
	 */
	private String userTag = "";
	
	/**
	 * ヘッダーフィールド名と値のペアを追加する。
	 * 
	 * @param k ヘッダーフィールド名
	 * @param v 値
	 */
	public void addHeader(String k, String v)
	{
		headers.put(k, v);
	}

	/**
	 * ヘッダーフィールド名をキーとした値のマップを取得する。
	 * 
	 * @return ヘッダーフィールド名をキーとした値のマップ
	 */
	public Map<String, String> getHeaders()
	{
		return headers;
	}

	/**
	 * フォーム要素名と値のペアを追加する。
	 * 
	 * @param k フォーム要素名
	 * @param v 値
	 */
	public void addFormValue(String k, String v)
	{
		formValues.put(k, v);
	}

	/**
	 * フォーム要素名をキーとした値のマップを取得する。
	 * 
	 * @return フォーム要素名をキーとした値のマップ
	 */
	public Map<String, String> getFormValues()
	{
		return formValues;
	}

	/**
	 * タグを設定する。
	 *  
	 * @param v タグ文字列
	 */
	public void setUserTag(String v)
	{
		this.userTag = v;
	}

	/**
	 * タグを取得する。
	 * 
	 * @return タグ文字列
	 */
	public String getUserTag()
	{
		return this.userTag;
	}
}
