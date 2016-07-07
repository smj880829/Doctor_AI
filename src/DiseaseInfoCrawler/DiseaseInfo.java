package DiseaseInfoCrawler;

import javax.swing.text.html.HTMLEditorKit; 
import javax.swing.text.html.HTML; 

import java.io.InputStreamReader; 
import java.io.UnsupportedEncodingException;
import java.net.URL; 
import java.net.HttpURLConnection; 
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.text.MutableAttributeSet; 
import javax.swing.text.html.parser.ParserDelegator;

import KnowledgeInCrawler.GetURL;
import kr.ac.kaist.swrc.jhannanum.demo.WorkflowNounExtractor;

public class DiseaseInfo { 
	private static int key = 0;
	private static int num = 0;
	private static int PartsFlag = 0;
	private static String word = null;
	private static String uri = "http://health.naver.com/medical/search.nhn?selectedTab=all&keyword=";
	private static final String arr[] = {"정의", "원인", "증상", "진단", "검사", "치료", "경과/합병증", "예방방법"};
										//, "��Ȱ ���̵�", "���̿��", "�����"};
	private static ArrayList<DiseaseFrequency> diseaseFrequency = new ArrayList<>();
	
	private class CallbackHandler extends HTMLEditorKit.ParserCallback { 
		int flag = 0;
		int definition = 0;
        
        public void handleStartTag(HTML.Tag tag, MutableAttributeSet a, int pos) { 
        	if (tag == HTML.Tag.A) {
        		String str = String.valueOf(a.getAttribute(javax.swing.text.html.HTML.Attribute.HREF));
        		
			    if(flag == 1){
                    flag = 0;
			    	String temp = "http://health.naver.com/";
			    	temp += str;
                    word = temp;
				}
        	}
                    
        } 
        
        public void handleText(char[] data, int pos) {
        	String str = new String(data);
        	
        	if(str.equals("의학 정보")){
        		flag = 1;
        	}
        	
        	if(key == 1){
        		//System.out.println(str);
        		
        		if(PartsFlag >= 2){
            		System.out.printf("[진료과]\n" + str + "\n\n");
            		PartsFlag = 0;
            	} else if(str.equals("진료과")){
            		PartsFlag += 1;
            	} 
        		
        		if(num > 7){
        			return;
        		} else if(str.equals(arr[num])){
        			definition += 1;
        		} else if(str.equals("맨위로")){
        			definition = 2;
        			System.out.printf("\n\n");
        			num++;
        			return;
        		}
        	}
        	
        	if(definition >= 2){
        		if(str.equals(arr[num])){
        			System.out.println("[" + str + "]");
        		}
        		else{        			
        			System.out.print(str);
        		}
        	}
        	
        	
        } 
		
	} 
    
    public void parse(String str) {
    	try { 
        	URL url = new URL(str); 
        	HttpURLConnection con = (HttpURLConnection)url.openConnection(); 
        	InputStreamReader reader = new InputStreamReader(con.getInputStream(),"UTF-8"); 
        	new ParserDelegator().parse(reader, new CallbackHandler(), true); 
        	con.disconnect();
        } catch(Exception e) { 
            	e.printStackTrace(); 
        }
	}
    
    public int getKey(){
    	return key;
    }
    
    public void setKey(int key){
    	DiseaseInfo.key = key;
    }
    
    public String getWord(){
    	return word;
    }
    
    public static void Startup(String Keyword){
    	DiseaseInfo parser = new DiseaseInfo();
		
		try {
			uri += URLEncoder.encode(Keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.err.println(e);
		}
		parser.parse(uri);
		parser.setKey(1);
		
		parser.parse(parser.getWord());
    }
    
    public static void addDisease(String name){
    	int temp;
    	//System.out.println(diseaseFrequency.size());
    	
    	if((temp = serchDiseaseList(name)) == -1){
    		diseaseFrequency.add(new DiseaseFrequency(name));
    	}
    	else if( (temp = serchDiseaseList(name)) != -1 && diseaseFrequency.get(temp).getCheckForFrequency() != 1){
    		diseaseFrequency.get(temp).addFrequency();
    	}
    }
    
    public static String printDiseaseList(){
    	int key;
    	String temp;
    	
    	Scanner in = new Scanner(System.in);
    	
    	for(int i=0; i<diseaseFrequency.size(); i++){
    		System.out.print("[" + i + "] ");
    		diseaseFrequency.get(i).print();
    	}
    	System.out.println("----------------------------------------------병명과 빈도수 끝----------------------------------------------");
    	System.out.println();
    	
    	System.out.println("----------------------------------------------병명 정보를 보기위한 번호 입력----------------------------------------------");
    	System.out.print("번호 입력 ==> ");
    	key = in.nextInt();
    	
    	temp = diseaseFrequency.get(key).getName();
    	
    	System.out.println("선택 한것 [" + key + "] " + temp);
    	
    	return temp;
    }
    
    public static int serchDiseaseList(String keyWord){
    	for(int i=0; i<diseaseFrequency.size(); i++){
    		if(keyWord.equals(diseaseFrequency.get(i).getName()))
    			return i;
    	}
    	
    	return -1;
    }
    
    public static void clearCheckForFrequency(){
    	for(int i=0; i<diseaseFrequency.size(); i++){
    		diseaseFrequency.get(i).setCheckForFrequency();
    	}
    	
    }
}
