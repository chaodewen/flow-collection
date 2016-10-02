package mo.cc.flow.page;

import java.net.URLEncoder;

import javax.ws.rs.core.Response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class Baidu {
	public static Response search(int page, String keyword) {
		int pn = 10 * (page - 1);
		String url;
		try {
			url = "https://m.baidu.com/s?pn=" + pn + "&word="
					+ URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return Utils.genErrorResponse(500, "Param Transform Error");
		}
		Document document = Utils.genGetDocument(url);
		if(document != null) {
			ListFlow listFlow = new ListFlow();
			listFlow.description = "以下是百度搜索的结果";
			
			// pagination处理
			listFlow.pagination.page = page;
			Elements pageNav = document.getElementsByAttributeValueContaining("class"
					, "new-pagenav");
			if(!pageNav.isEmpty() && !pageNav.get(0).getElementsByAttributeValueStarting(
					"class", "new-nextpage").isEmpty()) {
				listFlow.pagination.has_next = true;
			}
			else {
				listFlow.pagination.has_next = false;
			}
			
			Elements list = document.getElementsByAttributeValueContaining(
					"class", "result");
			for(Element listElement : list) {
				ListFlowElement li = new ListFlowElement();
				try {
					li.title = listElement.getElementsByAttributeValueContaining("class"
							, "c-title").text();
					li.subtitle = listElement.getElementsByAttributeValueContaining("class"
							, "c-row").text();
					li.url = listElement.getElementsByAttributeValueContaining("class"
							, "c-blocka").attr("href");
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