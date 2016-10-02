package mo.cc.flow.page;

import java.net.URLEncoder;

import javax.ws.rs.core.Response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class StackOverflow {
	public static Response search(int page, String keyword) {
		String url;
		try {
			url = "https://api.stackexchange.com/2.2/search?order=desc"
					+ "&sort=relevance&site=stackoverflow&page=" + page 
					+ "&intitle=" + URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			return Utils.genErrorResponse(500, "HTTP Request Error");
		}
		JSONObject json = Utils.genGetJSONObject(url);
		if(json != null) {
			if(json.getJSONArray("items").isEmpty()) {
				// 没有内容时返回TextFlow
				return Utils.genTextFlowResponse("没有搜索到内容~"
						, "换个关键词试试吧");
			}
			else {
				ListFlow listFlow = new ListFlow();
				listFlow.description = "Stack Overflow 搜索的结果";
				
				// pagination处理
				listFlow.pagination.page = page;
				listFlow.pagination.has_next = json.getBooleanValue("has_more");
				
				JSONArray itemsArray = json.getJSONArray("items");
				for(int i = 0; i < itemsArray.size(); i ++) {
					ListFlowElement li = new ListFlowElement();
					try {
						JSONObject item = itemsArray.getJSONObject(i);
						li.title = item.getString("title");
						li.subtitle = "回答" + item.getLongValue("answer_count") 
								+ " " + "赞成" + item.getLongValue("score")
								+ " " + "浏览" + item.getLongValue("view_count");
						li.url = item.getString("link");
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