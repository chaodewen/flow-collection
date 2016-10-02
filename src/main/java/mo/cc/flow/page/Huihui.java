package mo.cc.flow.page;

import java.net.URLEncoder;

import javax.ws.rs.core.Response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class Huihui {
	private static boolean hasNext(int page, String keyword) {
		String url;
		try {
			url = "http://app.huihui.cn/app/search/inland?page="
					+ (page + 1) + "&q=" + URLEncoder.encode(keyword, "UTF-8");
			JSONObject json = Utils.genGetJSONObject(url);
			if(!json.getJSONObject("data").getJSONArray("inland").isEmpty()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public static Response search(int page, String keyword) {
		String url;
		try {
			url = "http://app.huihui.cn/app/search/inland?page="
					+ page + "&q=" + URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			return Utils.genErrorResponse(500, "HTTP Request Error");
		}
		JSONObject json = Utils.genGetJSONObject(url);
		if(json != null) {
			JSONArray inland = json.getJSONObject("data").getJSONArray("inland");
			if(inland.isEmpty()) {
				// 没有内容时返回TextFlow
				return Utils.genTextFlowResponse("没有搜索到内容~"
						, "换个关键词试试吧");
			}
			else {
				ListFlow listFlow = new ListFlow();
				listFlow.description = "以下是惠惠的比价结果";
				
				// pagination处理
				listFlow.pagination.page = page;
				listFlow.pagination.has_next = hasNext(page, keyword);
				
				for(int i = 0; i < inland.size(); i ++) {
					ListFlowElement li = new ListFlowElement();
					try {
						JSONObject item = inland.getJSONObject(i);
						li.title = item.getString("title");
						li.subtitle = "售价" + item.getString("price");
						li.url = item.getString("purchase_url");
						li.image.alt = "图片未找到";
						li.image.src = item.getString("image_url");
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