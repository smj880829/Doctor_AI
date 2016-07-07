import KnowledgeInCrawler.GetURL;
import DiseaseInfoCrawler.DiseaseInfo;
import kr.ac.kaist.swrc.jhannanum.demo.*;

public class testMain {
	public static void main(String[] args){
		String temp;
		
		System.out.println("----------------------------------------------검색 시작----------------------------------------------");
		GetURL.Startup("자폐가 있어요");
		System.out.println("총 비교 단어 개수 : " + WorkflowNounExtractor.getSerchWordNumber());
		System.out.println("----------------------------------------------검색 끝----------------------------------------------");
		System.out.println();
		
		System.out.println("----------------------------------------------병명과 빈도수 출력----------------------------------------------");
		temp = DiseaseInfo.printDiseaseList();
		System.out.println();
		
		DiseaseInfo.Startup(temp);
	}
}
