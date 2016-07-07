package KnowledgeInCrawler;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.parser.ParserDelegator;

import kr.ac.kaist.swrc.jhannanum.demo.WorkflowNounExtractor;
import DiseaseInfoCrawler.DiseaseInfo;

public class GetURL {

	static ArrayDeque<String> qu;
	static boolean dr_Reply = true;
	static int check = 0;
	
	// 파서는 콜백 형식으로 되어 있다. 각 태그가 들어 올때 적절한 메소드가 호출됨
	private class CallbackHandler extends HTMLEditorKit.ParserCallback {
		
		// 태그가 시작할 때 호출 되는 메소드
		public void handleStartTag(HTML.Tag tag, MutableAttributeSet a, int pos) {

			// <A href 인 경우...A태그를 찾는다...
			if (tag == HTML.Tag.A) {
				
				String str = "kin.naver.com";
				String arr[];
				String url = String.valueOf(a.getAttribute(javax.swing.text.html.HTML.Attribute.HREF));

				arr = url.split("/");

				if (arr.length >= 5)
				{
					if (arr[2].equals(str))
					{
						check++;
						
						//답변이 있을때는 bar_count가 2 가된다.
						if(!dr_Reply)
						{
							qu.removeLast();
						}
						dr_Reply = false;
						
						//중복된 url을 큐에 넣지 않도록 url을 잘라서 rank번호 비교
						String sec_url[] = url.split("&");
						
						if((sec_url.length >= 7) && qu.size() == Integer.parseInt(sec_url[6].substring(5)))
						{ return; }
						
						qu.addLast(url);
						
						return;
					}
				}
			}
		}
		
		
		//단일테그일때 호출 ex <img src="abc" />
		public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet a, int pos)
		{ 
			if (tag == HTML.Tag.IMG)
			{
				if(String.valueOf(a.getAttribute(javax.swing.text.html.HTML.Attribute.ALT)).equals("의사답변"))
				{
					dr_Reply = true;
					return;
				}
			}
		}
		
	}

	public int parse(String str) {
		String content = null;

		try {

			// 입력받은 URL에 연결하여 InputStream을 통해 읽은 후 파싱 한다.
			URL url = new URL(str);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			InputStreamReader reader = new InputStreamReader(
					con.getInputStream(), "UTF-8");

			new ParserDelegator().parse(reader, new GetURL.CallbackHandler(), true);

			con.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return check;
	}

	public static void Startup(String searchQuery){
		String uri = "";

		System.out.println(searchQuery);
		
		qu = new ArrayDeque<String>();
		
		try {
			uri = "http://kin.naver.com/search/list.nhn?cs=utf8&query="
					+ URLEncoder.encode(searchQuery, "UTF-8") + "&x=0&y=0&page=";
		} catch (UnsupportedEncodingException e) {
			System.err.println(e);
		}
		
		for(int i=1; i<=10; i++)
		{
			int tempCheck = 0;
			
			GetURL parser = new GetURL();
			tempCheck = parser.parse(uri+i);
			
			if(tempCheck < 10){ break; }
		}
		
		DoctorAnswer rp = new DoctorAnswer();
		
		System.out.println("검색된 지식인 갯수 : " + qu.size());
		
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i=0; !qu.isEmpty(); i++){
			result.add(rp.parse(qu.removeFirst()));
		}
		
		WorkflowNounExtractor Analy = new WorkflowNounExtractor();
		
		for(int i = 0; i < result.size()-1; i++){
			Analy.Analysor(result.get(i));
			DiseaseInfo.clearCheckForFrequency();
		}
	}
}
