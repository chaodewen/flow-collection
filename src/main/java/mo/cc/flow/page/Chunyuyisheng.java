package mo.cc.flow.page;

import java.net.URLEncoder;

import javax.ws.rs.core.Response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class Chunyuyisheng {
	private static boolean hasNext(int page, String keyword) {
		String url;
		try {
			url = "https://api.chunyuyisheng.com/api/v5/similar_problem/?"
					+ "count=20&start_num=0&type=all&page=" + (page + 1)
					+ "&query=" + URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			return false;
		}
		JSONArray json = Utils.genGetJSONArray(url);
		if(json != null && !json.isEmpty()) {
			return true;
		}
		return false;
	}
	private static String genShareUrl(String id) {
		try {
			String url = "https://api.chunyuyisheng.com/api/problem/" + id + "/detail";
			JSONObject json = Utils.genGetJSONObject(url);
			return json.getString("share_link");
		} catch (Exception e) {}
		return "";
	}
	public static Response search(int page, String keyword) {
		String url;
		try {
			url = "https://api.chunyuyisheng.com/api/v5/similar_problem/?"
					+ "type=all&page=" + page
					+ "&query=" + URLEncoder.encode(keyword, "UTF-8");
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
				listFlow.description = "以下是春雨医生搜索到的问答";
				
				// pagination处理
				listFlow.pagination.page = page;
				listFlow.pagination.has_next = hasNext(page, keyword);
				
				for(int i = 0; i < json.size(); i ++) {
					ListFlowElement li = new ListFlowElement();
					try {
						JSONObject item = json.getJSONObject(i);
						JSONObject doctor = item.getJSONObject("doctor");
						li.title = doctor.getString("name") + "-"
								+ doctor.getString("title");
						li.subtitle = item.getString("ask");
						li.url = genShareUrl(item.getString("id"));
						li.image.alt = "图片未找到";
						li.image.src = doctor.getString("image");
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