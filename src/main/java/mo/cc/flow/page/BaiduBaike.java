package mo.cc.flow.page;

import java.net.URLEncoder;

import javax.ws.rs.core.Response;

import org.jsoup.Jsoup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;
import mo.cc.flow.TextFlow;

public class BaiduBaike {
	public static Response chooseResponse(int page, String keyword) {
		if(page == 1) {
			String url;
			try {
				url = "http://baike.baidu.com/client/search?app=3&ver=2.01&word="
						+ URLEncoder.encode(keyword, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
				return Utils.genErrorResponse(500, "Param Transform Error");
			}
			JSONObject json = Utils.genGetJSONObject(url);
			if("view".equals(json.getString("type"))) {
				TextFlow textFlow = new TextFlow();
				textFlow.description = "百度百科搜索结果";
				textFlow.text = "点击进入“" + keyword + "”";
				textFlow.url = "http://baike.baidu.com/client/view/"
						+ json.getString("lemmaId")
						+ ".htm?app=3&ver=2.01&font=2&net=1&flow=1";
				return Utils.genResponse(textFlow);
			}
		}
		return search(page, keyword);
	}
	public static Response search(int page, String keyword) {
		String url;
		try {
			url = "http://baike.baidu.com/client/searchresult?app=3&ver=2.01&page="
					+ (page - 1) + "&word=" + URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return Utils.genErrorResponse(500, "Param Transform Error");
		}
		JSONObject json = Utils.genGetJSONObject(url);
		JSONArray searchResult = json.getJSONArray("searchResult");
		
		// 处理没有结果
		if(searchResult.isEmpty()) {
			return Utils.genTextFlowResponse("没有找到结果~", "搜个别的吧");
		}
		
		// 开始获取结果
		ListFlow listFlow = new ListFlow();
		listFlow.description = "以下是百度百科的搜索结果";
		// pagination处理
		int total = json.getIntValue("total");
		listFlow.pagination.pages = total;
		listFlow.pagination.page = page;
		if(page < total) {
			listFlow.pagination.has_next = true;
		}
		else {
			listFlow.pagination.has_next = false;
		}
		for(int i = 0; i < searchResult.size(); i ++) {
			ListFlowElement elem = new ListFlowElement();
			try {
				JSONObject jo = searchResult.getJSONObject(i);
				elem.title = Jsoup.parse(jo.getString("title")).text();
				elem.subtitle = jo.getString("summary");
				elem.url = "http://baike.baidu.com/client/view/"
						+ jo.getString("lemmaId")
						+ ".htm?app=3&ver=2.01&font=2&net=1&flow=1";
			} catch (Exception e) {}
			if(elem.isValidated()) {
				listFlow.list.add(elem);
			}
		}
		if(listFlow.isValidated()) {
			return Utils.genResponse(listFlow);
		}
		return Utils.genErrorResponse(500, "Web Crawler Error");
	}
}