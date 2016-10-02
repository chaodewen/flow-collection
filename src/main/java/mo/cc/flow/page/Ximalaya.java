package mo.cc.flow.page;

import java.net.URLEncoder;

import javax.ws.rs.core.Response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class Ximalaya {
	public static Response logicThinking(int page) {
		return search(page, "逻辑思维", "recent");
	}
	public static Response xiaosongMystery(int page) {
		return search(page, "晓松奇谈", "recent");
	}
	/**
	 * condition可以填relation、recent和play（最多播放）
	 */
	public static Response search(int page, String keyword, String condition) {
		String url;
		try {
			url = "http://search.ximalaya.com/front/v1?condition=" + condition
					+ "&core=track&rows=20&page=" + page + "&kw=" 
					+ URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			return Utils.genErrorResponse(500, "HTTP Request Error");
		}
		JSONObject json = Utils.genGetJSONObject(url);
		if(json != null) {
			JSONObject response = json.getJSONObject("response");
			int numFound = response.getIntValue("numFound");
			if(numFound == 0) {
				// 没有内容时返回TextFlow
				return Utils.genTextFlowResponse("没有搜索到内容~"
						, "换个关键词试试吧");
			}
			else {
				JSONArray docs = response.getJSONArray("docs");
				
				ListFlow listFlow = new ListFlow();
				listFlow.description = "以下是喜马拉雅搜索到的声音";
				
				// pagination处理
				listFlow.pagination.page = page;
				listFlow.pagination.pages = (int) Math.ceil(numFound / 20.0);
				if(page < listFlow.pagination.pages) {
					listFlow.pagination.has_next = true;
				}
				else {
					listFlow.pagination.has_next = false;
				}
				
				for(int i = 0; i < docs.size(); i ++) {
					ListFlowElement li = new ListFlowElement();
					try {
						JSONObject item = docs.getJSONObject(i);
						li.title = item.getString("title");
						li.subtitle = "by " + item.getString("nickname") 
								+ " 播放量" + item.getLongValue("count_play");
						li.url = item.getString("play_path");
						li.image.alt = "图片未找到";
						li.image.src = item.getString("cover_path");
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