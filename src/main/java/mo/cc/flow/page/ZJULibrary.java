package mo.cc.flow.page;

import java.net.URLEncoder;

import javax.ws.rs.core.Response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;
import mo.cc.flow.TextFlow;

public class ZJULibrary {
	private static boolean isEmpty(Document document) {
		return document.getElementsByTag("li").isEmpty();
	}
	private static boolean hasNext(Document document, int page) {
		Element pagination = document.getElementsByAttributeValue(
				"class", "pagination").first();
		if(!pagination.getElementsByTag("a").isEmpty()) {
			String url = pagination.getElementsByTag("a")
					.first().attr("href");
			if(!url.contains("page=" + page)) {
				return true;
			}
		}
		return false;
	}
	public static Response search(int page, String keyword) {
		String url;
		try {
			url = "http://m1.lib.zju.edu.cn:8080/search?kw="
					+ URLEncoder.encode(keyword, "UTF-8") + "&page="
					+ page;
		} catch (Exception e) {
			e.printStackTrace();
			return Utils.genErrorResponse(500, "Param Transform Error");
		}
		Document document = Utils.genGetDocument(url);
		if(document != null) {
			if(isEmpty(document)) {
				// 没有内容时返回TextFlow
				TextFlow textFlow = new TextFlow();
				textFlow.description = "换个关键词试试吧";
				textFlow.text = "没有搜索到内容~";
				return Utils.genResponse(textFlow);
			}
			ListFlow listFlow = new ListFlow();
			listFlow.description = "以下是浙大图书馆的馆藏图书";
			// pagination处理
			listFlow.pagination.page = page;
			listFlow.pagination.has_next = hasNext(document, page);
			Elements lis = document.getElementsByTag("li");
			for(Element li : lis) {
				ListFlowElement elem = new ListFlowElement();
				try {
					elem.title = li.getElementsByTag("a").text();
					elem.subtitle = li.getElementsByTag("div").text();
					elem.url = "http://m1.lib.zju.edu.cn:8080/"
							+ li.getElementsByTag("a").attr("href");
				} catch (Exception e) {}
				if(elem.isValidated()) {
					listFlow.list.add(elem);
				}
			}
			if(listFlow.isValidated()) {
				return Utils.genResponse(listFlow);
			}
		}
		return Utils.genErrorResponse(500, "Web Acquiring Error");
	}
}