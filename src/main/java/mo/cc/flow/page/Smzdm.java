package mo.cc.flow.page;

import java.net.URLEncoder;

import javax.ws.rs.core.Response;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class Smzdm {
	public static Response search(int page, String keyword) {
		String url;
		int offset = (page - 1) * 10;
		try {
			url = "http://api.smzdm.com/v2/search?limit=10&v=6.3.2&offset="
					+ offset + "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			return Utils.genErrorResponse(500, "HTTP Request Error");
		}
		JSONObject json = Utils.genGetJSONObject(url);
		if(json != null) {
			JSONObject data = json.getJSONObject("data");
			if(!data.containsKey("rows") 
					|| data.getJSONArray("rows").isEmpty()) {
				// 没有内容时返回TextFlow
				return Utils.genTextFlowResponse("没有搜索到内容~"
						, "换个关键词试试吧");
			}
			else {
				ListFlow listFlow = new ListFlow();
				listFlow.description = "以下是什么值得买搜索的结果";
				
				// pagination处理
				listFlow.pagination.page = page;
				listFlow.pagination.pages = (int) Math.ceil(
						data.getIntValue("total_num") / 10.0);
				listFlow.pagination.has_next = page < listFlow.pagination.pages;
				
				JSONArray rows = data.getJSONArray("rows");
				for(int i = 0; i < rows.size(); i ++) {
					ListFlowElement li = new ListFlowElement();
					try {
						JSONObject item = rows.getJSONObject(i);
						li.title = item.getString("article_title");
						li.subtitle = item.getString("article_price") 
								+ " " + item.getString("article_mall");
						li.url = item.getString("article_url");
						li.image.alt = "图片未找到";
						li.image.src = item.getString("article_pic");
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