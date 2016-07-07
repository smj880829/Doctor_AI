package DiseaseInfoCrawler;

import java.util.ArrayList;

public class DiseaseFrequency {
	private String name;
	private int frequency;
	private int checkForFrequency = 0;
	
	public DiseaseFrequency(String name){
		this.name = name;
		this.frequency = 1;
	}
	
	public void setName(String name){ this.name = name; }
	public void addFrequency(){ this.frequency++; checkForFrequency = 1; }
	public String getName(){ return name; }
	public int getFrequency(){ return frequency; }
	public int getCheckForFrequency(){ return checkForFrequency; }
	public void setCheckForFrequency(){ checkForFrequency = 0; }
	public void print(){
		System.out.println("병명 : " + name + " // 빈도수 : " + frequency);
	}
}
