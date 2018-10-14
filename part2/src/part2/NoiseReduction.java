package part2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class NoiseReduction implements ContentProcessor {
	
	public NoiseReduction() {
		this.adList = getAdListList();
	}
	
	public static List<String> getAdListList() {
		List<String> adList = new ArrayList<String>();
		BufferedReader reader = null;
		long timeToExpire = 1 * 24 * 60 * 60 * 1000;
		boolean refreshAdListFileFlag = false;
		File adListFile= new File("adList.txt");
		try {
			if(!adListFile.exists()) {
				refreshAdListFileFlag = true;
				adListFile.createNewFile();
			}
			long adListFreshness = new Date().getTime() - adListFile.lastModified();
			if (!refreshAdListFileFlag && adListFreshness < timeToExpire) {
				reader = new BufferedReader(new FileReader(adListFile));
			}
			else {
				refreshAdListFileFlag = true;
				reader= new BufferedReader(new InputStreamReader(new URL("https://easylist-downloads.adblockplus.org/easylist.txt").openStream()));
			}

			String line="";
			StringBuilder newAdList = new StringBuilder();
			while ((line=reader.readLine())!=null){
				if(line.startsWith("##")){
					newAdList.append(line+"\n");
					adList.add(line.substring(2));
				}
			}
			if(refreshAdListFileFlag) {
				FileWriter newAdListWriter = new FileWriter(adListFile, false);
				newAdListWriter.write(newAdList.toString());
				newAdListWriter.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return adList;
	}
	
	public String processContent(String html) {
		
		
		Document htmlPage = Jsoup.parse(html);
		
		//Removing nav bars
		htmlPage.select("nav, .nav, .navbar, [class*=nav], [id*=mw-panel], [class*=navigation], [id*=navigation], [class*=recirculation], [id*=recirculation]").remove();
		//Removing headers
		//htmlPage.select("header, .header, .head, #header, #head, [id*=mw-head]").remove();
		htmlPage.select("[id*=mw-head]").remove();
		//Removing footers
		htmlPage.select("footer, .footer, #footer, [class*=footer], [class*=bottom], [id*=bottom]").remove();
		//Removing side bars
		htmlPage.select(".sidebar, #sidebar, [class^=sidebar], aside, [class^=aside]").remove();
		//Removing social media buttons
		htmlPage.select("[class*=social], [id*=social], [class*=shar], [id*=share], [class*=tool], [id*=tool]").remove();
		htmlPage.select("[class*=breadcrumb], [id*=breadcrumb]").remove();
		htmlPage.select("[href*=facebook], [href*=twitter], [href*=youtube], [href*=pintrest]").remove();
		//Removing iframe, embed, script, head tags
		htmlPage.select("iframe, embed, script, head").remove();
		//Removing subscribe bar
		htmlPage.select("[class*=newsletter], [class*=subscribe], [id*=newsletter], [id*=subscribe]").remove();
		//Removing comment and feed area
		htmlPage.select("[class=post-feeds], [class=blog-pager], [id*=comment], [role*=button], [class*=button], [id*=button]").remove();

		////Removing ads from available list
		for(String t:adList){
			try{
				htmlPage.select(t).remove();
			} catch(Exception e){

			}
		}
		
		return htmlPage.toString();

	}
	
	private List<String> adList;
}
