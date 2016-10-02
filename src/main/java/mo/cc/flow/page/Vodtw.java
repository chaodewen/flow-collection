package mo.cc.flow.page;

import javax.ws.rs.core.Response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class Vodtw {
	public static Response getMonthRank(int page) {
		String url = "http://m.vodtw.com/top-monthvisit-" + page + "/";
		Document document = Utils.genGetDocument(url);
		if(document != null) {
			ListFlow listFlow = new ListFlow();
			listFlow.description = "以下是本月的小说点击排行榜";
			
			// pagination处理
			listFlow.pagination.page = page;
			if(page < 20) {
				listFlow.pagination.has_next = true;
			}
			else {
				listFlow.pagination.has_next = false;
			}
			
			Elements list = document.getElementsByAttributeValueContaining(
					"class", "cover").get(0).getElementsByTag("p");
			for(Element listElement : list) {
				ListFlowElement li = new ListFlowElement();
				try {
					li.title = listElement.getElementsByAttributeValueContaining(
							"class", "blue").text();
					li.subtitle = listElement.getElementsByTag("a").get(2).text();
					li.url = "http://m.vodtw.com" + listElement.getElementsByTag(
							"a").get(1).attr("href");
				} catch (Exception e) {
					continue;
				}
				if(li.isValidated()) {
					listFlow.list.add(li);
				}
			}
			if(listFlow.isValidated()) {
				return Utils.genResponse(listFlow);
			}
		}
		return Utils.genErrorResponse(500, "Web Crawler Error");
	}
}