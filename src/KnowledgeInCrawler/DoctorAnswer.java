package KnowledgeInCrawler;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayDeque;
import java.util.Scanner;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.parser.ParserDelegator;

public class DoctorAnswer
{
	boolean flag = false;
	boolean indexFlag = false;
	String result;

	// 파서는 콜백 형식으로 되어 있다. 각 태그가 들어 올때 적절한 메소드가 호출됨
	private class CallbackHandler extends HTMLEditorKit.ParserCallback {
		// 태그가 시작할 때 호출 되는 메소드
		public void handleText(char[] data, int pos) {
			String str = new String(data);

			// 지식인 의사답변 시작
			if (data.length >= 10 && str.substring(0, 10).equals("지식iN 의사 답변"))
			{
				flag = true;

				return;
			}

			// 질문 or 답변 끝나는 지점
			if ((data.length >= 9 && str.substring(0, 9).equals("국민건강 길라잡이"))
					|| (data.length >= 8 && str.substring(0, 8).equals("국민보건향상과 "))
					|| (data.length == 6 && str.equals("홈페이지 :")))
			{
				flag = false;
				return;
			}

			if (flag)
			{
				// '?'나 공백으로된 행 건너뜀
				if (data.length < 3)
				{ return; }
			
				if (data.length >= 6 && str.substring(0,6).equals("대한의사협회"))
				{ return; }
				
				String strSub = str.substring(0,3);
				
				if (strSub.equals("안녕하") || strSub.equals("하이닥") || strSub.equals("네이버"))
				{ return; }
				
				result += str;
			}
		}
	}

	public String parse(String str) {
		String content = null;
		result = "";
		
		try {

			// 입력받은 URL에 연결하여 InputStream을 통해 읽은 후 파싱 한다.
			URL url = new URL(str);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			InputStreamReader reader = new InputStreamReader(
					con.getInputStream(), "UTF-8");

			new ParserDelegator().parse(reader, new DoctorAnswer.CallbackHandler(), true);

			con.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}