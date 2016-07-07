package kr.ac.kaist.swrc.jhannanum.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.hannanum.Workflow;
import kr.ac.kaist.swrc.jhannanum.hannanum.WorkflowFactory;

import DiseaseInfoCrawler.DiseaseInfo;

public class WorkflowNounExtractor {
	//총 비교될 단어 개수를 위한 인트형 전역변수
	private static int num = 0;

	public static void Analysor(String answer) {
		Workflow workflow = WorkflowFactory.getPredefinedWorkflow(WorkflowFactory.WORKFLOW_NOUN_EXTRACTOR);
		
		try {
			workflow.activateWorkflow(true);
			
			workflow.analyze(answer);
			
			LinkedList<Sentence> resultList = workflow.getResultOfDocument(new Sentence(0, 0, false));
			
			for (Sentence s : resultList) {
				Eojeol[] eojeolArray = s.getEojeols();
				
				for (int i = 0; i < eojeolArray.length; i++) {
					
					if (eojeolArray[i].length > 0) {
						String[] morphemes = eojeolArray[i].getMorphemes();
						
						for (int j = 0; j < morphemes.length; j++) {
							ArrayList list = divideKoreanTypo(morphemes[j]);
							
							if(list.isEmpty() != true){
								matchingDisease(((Map)(list.get(0))).get("cho").toString(), ((Map)(list.get(0))).get("jung").toString(), morphemes[j]);
							}
							num++;
						}
						//System.out.print("  / ");
					}
				}
			}
			
			workflow.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		/* Shutdown the work flow */
		workflow.close();
	}
	
	 private static ArrayList divideKoreanTypo(String typo) {
		  // 리스트로 받아오기 위한 변수
		  ArrayList list = new ArrayList();
		  
		  //typo스트링의 글자수 만큼 list에 담아둡니다.
		  for ( int i = 0; i < typo.length(); i++) {
		   Map map = new HashMap();
		   char comVal = typo.charAt(i);
		   
		   // 초성만 입력 했을 시엔 초성은 무시해서 List에 추가합니다.
		   //if(comVal >= 0xAC00) { 
		    char uniVal = (char) (comVal - 0xAC00);
		  
		    
		    //유니코드 표에 맞추어 초성 중성 종성을 분리합니다..
		    char cho = (char) ((((uniVal - (uniVal % 28)) / 28) / 21) + 0x1100) ;
		    char jung = (char) ((((uniVal - (uniVal % 28)) / 28) % 21) + 0x1161);  
		    char jong = (char) ((uniVal % 28)+ 0x11a7); 
		    
		    map.put("cho", cho);
		    map.put("jung", jung);
		    map.put("jong", jong);
		    
		    list.add(map);
		   //}
		  }
		  
		  return list;
	 }
	 
	 private static boolean matchingDisease(String cho, String jung, String keyWord) throws IOException{
		 String path = "E:\\STUDY\\Doctor.AI\\DiseaseList";
		 File directory = new File(path);
		 File file;
		 File fileList[];
		 File directoryList[] = directory.listFiles();
		 FileReader fileRead;
		 BufferedReader bufferFileRead;
		 ArrayList list;
		 String temp;
		 
		 //System.out.println(keyWord);
		 
		 for(int i=0; i<directoryList.length; i++){
			 list = divideKoreanTypo(directoryList[i].getName());
			 
			 if( cho.equals((((Map)(list.get(0))).get("cho").toString())) ){
				 file = new File(directoryList[i].getPath());
				 fileList = file.listFiles();
				 
				 for(int j=0; j<fileList.length; j++){
					 list = divideKoreanTypo(fileList[j].getName());
					 
					 if( jung.equals((((Map)(list.get(0))).get("jung").toString())) ){
						 
						 fileRead = new FileReader(fileList[j].getPath());
						 bufferFileRead = new BufferedReader(fileRead);
						 
						 while( (temp = bufferFileRead.readLine()) != null ){
							 System.out.println(temp);
							 if(keyWord.equals(temp)){
								 // 찾았다 병명
								 //System.out.println(keyWord);
								 //System.out.println(fileList[j].getPath());
								 //System.out.println("찾았다 병명!!!!");
								 
								 DiseaseInfo.addDisease(keyWord);
								 break;
							 }
						 }
						 break;
					 }
				 }
				 break;
			 }
		 }
		 
		 return false;
	 }
	 
	 public static int getSerchWordNumber(){ return num; }
}