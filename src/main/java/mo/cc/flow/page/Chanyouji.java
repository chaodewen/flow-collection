package mo.cc.flow.page;

import java.net.URLEncoder;

import javax.ws.rs.core.Response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class Chanyouji {
	private static boolean hasNext(int page, String keyword) {
		String url;
		try {
			url = "https://chanyouji.com/api/search/trips.json?page="
					+ (page + 1) + "&q=" + URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			return false;
		}
		JSONArray json = Utils.genGetJSONArray(url);
		if(json != null && !json.isEmpty()) {
			return true;
		}
		return false;
	}
	public static Response search(int page, String keyword) {
		String url;
		try {
			url = "https://chanyouji.com/api/search/trips.json?page="
					+ page + "&q=" + URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			return Utils.genErrorResponse(500, "HTTP Request Error");
		}
		JSONArray json = Utils.genGetJSONArray(url);
		if(json != null) {
			if(json.isEmpty()) {
				// 没有内容时返回TextFlow
				return Utils.genTextFlowResponse("没有搜索到内容~"
						, "换个关键词试试吧");
			}
			else {
				ListFlow listFlow = new ListFlow();
				listFlow.description = "以下是在蝉游记找到的结果";
				
				// pagination处理
				listFlow.pagination.page = page;
				listFlow.pagination.has_next = hasNext(page, keyword);
				
				for(int i = 0; i < json.size(); i ++) {
					ListFlowElement li = new ListFlowElement();
					try {
						JSONObject item = json.getJSONObject(i);
						li.title = item.getString("name");
						li.subtitle = item.getIntValue("days") + "天"
								+ " " + item.getString("start_date")
								+ " " + "浏览量：" + item.getLongValue("views_count");
						li.url = "http://chanyouji.com/trips/" + item.getString("id");
						li.image.alt = "图片未找到";
						li.image.src = item.getString("front_cover_photo_url");
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