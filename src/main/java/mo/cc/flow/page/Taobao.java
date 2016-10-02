package mo.cc.flow.page;

import java.net.URLEncoder;

import javax.ws.rs.core.Response;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class Taobao {
	public static Response search(int page, String keyword) {
		String url;
		try {
			url = "http://s.m.taobao.com/search?_input_charset=utf-8&page="
					+ page + "&q=" + URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			return Utils.genErrorResponse(500, "HTTP Request Error");
		}
		JSONObject json = Utils.genGetJSONObject(url);
		if(json != null) {
			if(!json.containsKey("itemsArray") 
					|| json.getJSONArray("itemsArray").isEmpty()) {
				// 没有内容时返回TextFlow
				return Utils.genTextFlowResponse("没有搜索到内容~"
						, "换个关键词试试吧");
			}
			else {
				ListFlow listFlow = new ListFlow();
				listFlow.description = "以下是淘宝搜索的结果";
				
				// pagination处理
				listFlow.pagination.page = page;
				listFlow.pagination.pages = json.getIntValue("totalPage");
				if(page < listFlow.pagination.pages) {
					listFlow.pagination.has_next = true;
				}
				else {
					listFlow.pagination.has_next = false;
				}
				
				JSONArray itemsArray = json.getJSONArray("itemsArray");
				for(int i = 0; i < itemsArray.size(); i ++) {
					ListFlowElement li = new ListFlowElement();
					try {
						JSONObject item = itemsArray.getJSONObject(i);
						li.title = item.getString("title");
						li.subtitle = "售价" + item.getDoubleValue("priceWap") 
								+ " " + "销量" + item.getLongValue("sold")
								+ " " + "评论数" + item.getLongValue("commentCount");
						li.url = item.getString("auctionURL");
						li.image.alt = "图片未找到";
						li.image.src = item.getString("pic_path");
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