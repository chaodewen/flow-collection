package mo.cc.flow.page;

import javax.ws.rs.core.Response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class Hupu {
	public static Response getNbaNews(int page) {
		String url = "http://m.hupu.com/nba/news/" + page;
		Document document = Utils.genGetDocument(url);
		if(document != null) {
			ListFlow listFlow = new ListFlow();
			listFlow.description = "以下是最新的NBA新闻";
			
			// pagination处理
			listFlow.pagination.page = page;
			Elements nextPage = document.getElementsByAttributeValueEnding("dace-node"
					, "nextpage");
			if(!nextPage.isEmpty() && nextPage.get(0).getElementsByAttributeValueStarting(
					"class", "disabled").isEmpty()) {
				listFlow.pagination.has_next = true;
			}
			else {
				listFlow.pagination.has_next = false;
			}
			
			Elements list = document.getElementsByAttributeValueContaining(
					"class", "news-list").get(0).getElementsByTag("ul").get(0)
					.getElementsByTag("li");
			for(Element listElement : list) {
				ListFlowElement li = new ListFlowElement();
				try {
					li.title = listElement.getElementsByAttributeValueContaining(
							"class", "news-txt").get(0).getElementsByTag("h3").text();
					li.subtitle = listElement.getElementsByAttributeValueContaining(
							"class", "news-status-bar").text();
					li.url = listElement.getElementsByAttributeValueContaining(
							"class", "news-link").attr("href");
					li.image.alt = "图片无法显示";
					String srcRaw = listElement.getElementsByAttributeValueContaining(
							"class", "img-wrap").attr("style");
					li.image.src = srcRaw.substring(srcRaw.indexOf("http://c")
							, srcRaw.lastIndexOf(")"));
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