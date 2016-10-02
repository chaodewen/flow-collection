package mo.cc.flow.page;

import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class Jianshu {
	public static Response search(int page, String keyword) {
		String url;
		try {
			url = "http://www.jianshu.com/search/do?type=notes&page="
					+ page + "&q=" + URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return Utils.genErrorResponse(500, "Param Transform Error");
		}
		Set<Header> headers = new HashSet<Header>();
		headers.add(new BasicHeader("Accept", "*/*"));
		JSONObject json = Utils.genGetJSONObject(url, headers);
		if(json != null) {
			JSONArray entries = json.getJSONArray("entries");
			if(entries.isEmpty()) {
				// 没有内容时返回TextFlow
				return Utils.genTextFlowResponse("没有搜索到内容~", "换个关键词试试吧");
			}
			else {
				ListFlow listFlow = new ListFlow();
				listFlow.description = "以下是简书中的搜索结果";
				
				// pagination处理
				listFlow.pagination.page = page;
				listFlow.pagination.pages = json.getIntValue("total_pages");
				if(page < listFlow.pagination.pages) {
					listFlow.pagination.has_next = true;
				}
				else {
					listFlow.pagination.has_next = false;
				}
				
				for(int i = 0; i < entries.size(); i ++) {
					ListFlowElement li = new ListFlowElement();
					try {
						JSONObject entry = entries.getJSONObject(i);
						li.title = Jsoup.parse(entry.getString("title")).text();
						int likes = entry.getIntValue("likes_count");
						int views = entry.getIntValue("views_count");
						li.subtitle = "浏览·" + views + " " + "喜欢·" + likes;
						li.url = "http://www.jianshu.com/p/" + entry.getString("slug");
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