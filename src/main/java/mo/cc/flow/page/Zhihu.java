package mo.cc.flow.page;

import java.net.URLEncoder;

import javax.ws.rs.core.Response;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class Zhihu {
	public static Response search(int page, String keyword) {
		CloseableHttpResponse responseRaw;
		try {
			String searchURL = "http://www.zhihu.com/r/search?type=content&offset="
					+ (page - 1) * 10 + "&q=" + URLEncoder.encode(keyword, "UTF-8");
			responseRaw = Utils.sendGet(searchURL);
		} catch (Exception e) {
			return Utils.genErrorResponse(500, "HTTP Request Error");
		}
		int httpCode = responseRaw.getStatusLine().getStatusCode();
		if(httpCode >= 200 && httpCode < 300) {
			ListFlow listFlow = new ListFlow();
			String entity;
			try {
				entity = EntityUtils.toString(responseRaw.getEntity(), "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
				return Utils.genErrorResponse(500, "Entity Parsing Error");
			}
			
			// pagination处理
			listFlow.pagination.page = page;
			JSONObject paging = JSON.parseObject(entity).getJSONObject("paging");
			if(paging.getString("next").isEmpty()) {
				listFlow.pagination.has_next = false;
			}
			else {
				listFlow.pagination.has_next = true;
			}
			
			JSONArray htmls = JSON.parseObject(entity)
					.getJSONArray("htmls");
			for(int i = 0; i < htmls.size(); i ++) {
				String html = htmls.getString(i);
				Document listElement = Jsoup.parse(html);
				ListFlowElement li = new ListFlowElement();
				try {
					li.title = listElement.getElementsByAttributeValueContaining(
							"class", "title").text();
					String like = listElement.getElementsByAttributeValueContaining(
							"class", "zm-item-vote-count").first().text();
					String author = listElement.getElementsByAttributeValueMatching(
							"class", "entry-meta").first().text();
					li.subtitle = like + "赞，" + author;
					li.url = "http://www.zhihu.com" + listElement
							.getElementsByAttributeValueContaining("class"
									, "title").first().getElementsByTag("a").attr("href");
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