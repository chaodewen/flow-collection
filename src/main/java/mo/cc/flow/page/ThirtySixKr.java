package mo.cc.flow.page;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.core.Response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class ThirtySixKr {
	public static Response search(int page, String keyword) {
		String url;
		try {
			url = "https://rong.36kr.com/api/mobi/news/search?page="
					+ page + "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return Utils.genErrorResponse(500, "Param Transform Error");
		}
		JSONObject data = Utils.genGetJSONObject(url).getJSONObject("data");
		if(data.getIntValue("totalCount") == 0) {
			return Utils.genTextFlowResponse("没有搜索到内容~", "换个关键词试试吧");
		}
		
		ListFlow listFlow = new ListFlow();
		listFlow.description = "以下是36Kr新闻搜索的结果";
		// pagination处理
		listFlow.pagination.page = page;
		listFlow.pagination.pages = data.getIntValue("totalPages");
		if(page < listFlow.pagination.pages) {
			listFlow.pagination.has_next = true;
		}
		else {
			listFlow.pagination.has_next = false;
		}
		
		JSONArray dataArray = data.getJSONArray("data");
		for(int i = 0; i < dataArray.size(); i ++) {
			ListFlowElement elem = new ListFlowElement();
			try {
				JSONObject jo = dataArray.getJSONObject(i);
				elem.title = jo.getString("title");
				String userName = jo.getJSONObject("user").getString("name");
				DateFormat df = new SimpleDateFormat("yyyy年MM月dd日"); 
				Date date = new Date(jo.getLongValue("publishTime"));
				elem.subtitle = userName + " " + df.format(date);
				elem.url = "http://36kr.com/p/" + jo.getString("feedId")
						+ ".html";
				elem.image.alt = "图片未找到";
				elem.image.src = jo.getString("featureImg");
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