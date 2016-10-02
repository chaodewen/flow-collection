package mo.cc.flow.page;

import java.net.URLEncoder;

import javax.ws.rs.core.Response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class ZJUSearch {
	private static String genUrl(String frame) {
		Document document = Utils.genGetDocument(frame, "192.168.0.1", 1366);
		if(document != null) {
			return document.getElementsByAttributeValue("class", "link")
					.last().getElementsByTag("a").attr("href");
		}
		return null;
	}
	public static Response search(int page, String keyword) {
		String url;
		try {
			url = "http://10.203.3.55/web/search.do?user=&uid&loadList=true&pageNo="
					+ page + "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return Utils.genErrorResponse(500, "Param Transform Error");
		}
		Document document = Utils.genGetDocument(url, "192.168.0.1", 1366);
		if(document != null) {
			Elements pages = document.getElementsByAttributeValue(
					"class", "pages");
			if(pages.isEmpty()) {
				// 没有内容时返回TextFlow
				return Utils.genTextFlowResponse("没有搜索到内容~", "换个关键词试试吧");
			}
			else {
				ListFlow listFlow = new ListFlow();
				listFlow.description = "以下是浙大搜索的结果";
				
				// pagination处理
				listFlow.pagination.page = page;
				if("下一页".equals(pages.first().getElementsByTag("a")
						.last().text())) {
					listFlow.pagination.has_next = true;
				}
				else {
					listFlow.pagination.has_next = false;
				}
				
				Elements dls = document.getElementsByTag("dl");
				for(Element listElement : dls) {
					ListFlowElement li = new ListFlowElement();
					try {
						li.title = listElement.getElementsByTag("a").first().text();
						li.subtitle = listElement.getElementsByTag("dd").first().text();
						String urlRaw = listElement.getElementsByTag("a").first()
								.attr("onclick");
						String frame = "http://10.203.3.55/web/detail.do?keyword=" 
								+ keyword + "&docNo=" + urlRaw.substring(
										urlRaw.indexOf('\'') + 1
										, urlRaw.lastIndexOf('\''));
						li.url = genUrl(frame);
					} catch (Exception e) {}
					if(li.isValidated()) {
						listFlow.list.add(li);
					}
				}
				if(listFlow.isValidated()) {
					return Utils.genResponse(listFlow);
				}
			}
		}
		return Utils.genErrorResponse(500, "Web Crawler Error");
	}
}