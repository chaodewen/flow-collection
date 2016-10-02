package mo.cc.flow.page;

import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class Weibo {
	public static Response search(int page, String keyword) {
		String url;
		try {
			url = "http://s.weibo.com/weibo/" + URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return Utils.genErrorResponse(500, "Param TransfoHeaderor");
		}
		Set<Header> headers = new HashSet<Header>();
		headers.add(new BasicHeader("User-Agent"
				, "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) "
						+ "AppleWebKit/601.1.46 (KHTML, like Gecko) "
						+ "Version/9.0 Mobile/13B143 Safari/601.1"));
		Document document = Utils.genGetDocument(url, headers);
		if(document != null) {
			Elements weibos = document.getElementsByAttributeValueContaining(
					"class", "item_weibo");
			if(weibos.isEmpty()) {
				// 没有内容时返回TextFlow
				return Utils.genTextFlowResponse("没有搜索到内容~", "换个关键词试试吧");
			}
			else {
				ListFlow listFlow = new ListFlow();
				listFlow.description = "以下是微博搜索的结果";
				
				// pagination处理
				listFlow.pagination.page = 1;
				// 微博搜索只有一页
				listFlow.pagination.has_next = false;
				
				for(Element listElement : weibos) {
					ListFlowElement li = new ListFlowElement();
					try {
						li.title = listElement.getElementsByAttributeValueContaining(
								"class", "tit_m").text();
						Elements contents = listElement.getElementsByAttributeValueContaining(
								"class", "s_weibo").first().children();
						for(Element p : contents) {
							if("p".equals(p.tagName())) {
								li.subtitle = p.text();
								break;
							}
						}
						String action = listElement.attr("action-data");
						String mid = action.substring(action.indexOf("mid=") + 4
								, action.lastIndexOf('&'));
						String uid = action.substring(action.indexOf("uid=") + 4);
						li.url = "http://m.weibo.cn/" + uid + "/" + mid;
						li.image.alt = "未找到图片";
						li.image.src = listElement.getElementsByAttributeValueContaining(
								"class", "w_user_img").first().getElementsByTag("img")
								.attr("src");
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