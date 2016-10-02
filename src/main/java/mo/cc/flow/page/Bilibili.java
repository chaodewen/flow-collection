package mo.cc.flow.page;

import java.net.URLEncoder;

import javax.ws.rs.core.Response;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class Bilibili {
	public static Response search(int page, String keyword) {
		CloseableHttpResponse responseRaw;
		try {
			String searchURL = "http://www.bilibili.com/search?action=autolist"
					+ "&main_ver=v1&pagesize=20&page=" + page + "&keyword=" 
					+ URLEncoder.encode(keyword, "UTF-8");
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
			
			JSONObject res = JSON.parseObject(entity).getJSONObject("res");
			
			// pagination处理
			listFlow.pagination.page = page;
			listFlow.pagination.pages = res.getIntValue("numPages");
			if(page < listFlow.pagination.pages) {
				listFlow.pagination.has_next = true;
			}
			else {
				listFlow.pagination.has_next = false;
			}
			
			listFlow.description = "以下是哔哩哔哩的搜索结果";
			
			JSONArray results = res.getJSONArray("result");
			for(int i = 0; i < results.size(); i ++) {
				JSONObject result = results.getJSONObject(i);
				ListFlowElement li = new ListFlowElement();
				try {
					li.title = result.getString("title");
					li.subtitle = result.getString("description");
					li.url = result.getString("arcurl");
					li.image.alt = "图片不见鸟";
					li.image.src = result.getString("pic");
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