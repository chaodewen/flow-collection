package mo.cc.flow.page;

import java.net.URLEncoder;

import javax.ws.rs.core.Response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;
import mo.cc.flow.TextFlow;

public class Youdao {
	public static Response searchYoudao(String keyword) {
		String url;
		try {
			url = "http://dict.youdao.com/jsonapi?q="
					 + URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			return Utils.genErrorResponse(500, "HTTP Request Error");
		}
		JSONObject json = Utils.genGetJSONObject(url);
		if(json != null) {
			if(!json.containsKey("ec") && !json.containsKey("ce")) {
				// 没有内容时返回TextFlow
				return Utils.genTextFlowResponse("没有搜索到内容~"
						, "换个关键词试试吧");
			}
			else {
				try {
					// 区分中英文
					if(json.containsKey("ce")) {
						// 中文
						JSONObject word = json.getJSONObject("ce").getJSONArray(
								"word").getJSONObject(0);
						StringBuilder text = new StringBuilder();
						ListFlow listFlow = new ListFlow();
						listFlow.description = keyword;
						listFlow.pagination.page = 1;
						listFlow.pagination.pages = 1;
						listFlow.pagination.has_next = false;
						
						text.append(keyword + " [" + word.getString("phone") + "]");
						// 单词解释
						JSONArray trs = word.getJSONArray("trs");
						for(int i = 0; i < trs.size(); i ++) {
							ListFlowElement elem = new ListFlowElement();
							try {
								JSONObject item = trs.getJSONObject(i).getJSONArray("tr")
										.getJSONObject(0);
								elem.subtitle = "点击进入";
								elem.title = item.getJSONObject("l").getJSONArray("i")
										.getJSONObject(1).getString("#text");
								elem.url = "http://m.youdao.com/dict?q=" + elem.title;
							} catch (Exception e) {}
							if(elem.isValidated()) {
								listFlow.list.add(elem);
							}
						}
						if(listFlow.isValidated()) {
							return Utils.genResponse(listFlow);
						}
					}
					else {
						// 英文
						JSONObject word = json.getJSONObject("ec").getJSONArray(
								"word").getJSONObject(0);
						StringBuilder text = new StringBuilder();
						TextFlow textFlow = new TextFlow();
						textFlow.description = "点击进入" + keyword + "词条";
						text.append(keyword + " UK[" + word.getString("ukphone") + "]"
								+ " US[" + word.getString("ukphone") + "]\n");
						// 单词解释
						JSONArray trs = word.getJSONArray("trs");
						for(int i = 0; i < trs.size(); i ++) {
							JSONArray tr = trs.getJSONObject(i).getJSONArray("tr");
							for(int j = 0; j < tr.size(); j ++) {
								JSONObject item = tr.getJSONObject(j);
								if(item.containsKey("tr")) {
									for(int k = 0; k < item.getJSONArray("tr").size(); k ++) {
										JSONObject childItem = item.getJSONArray("tr")
												.getJSONObject(k);
										text.append("\n" + childItem.getJSONObject("l")
												.getJSONArray("i").getString(0) + "\n");
									}
								}
								else {
									text.append("\n" + item.getJSONObject("l").getJSONArray("i")
											.getString(0) + "\n");
								}
							}
						}
						textFlow.text = text.toString();
						textFlow.url = "http://m.youdao.com/dict?q=" + keyword;
						return Utils.genResponse(textFlow);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return Utils.genErrorResponse(500, "JSON Resolve Error");
	}
	public static Response searchEc21(String keyword) {
		String url;
		try {
			url = "http://dict.youdao.com/jsonapi?q="
					 + URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			return Utils.genErrorResponse(500, "HTTP Request Error");
		}
		JSONObject json = Utils.genGetJSONObject(url);
		if(json != null) {
			if(!json.containsKey("ec21")) {
				// 没有内容时返回TextFlow
				return Utils.genTextFlowResponse("没有搜索到内容~"
						, "换个关键词试试吧");
			}
			else {
				try {
					String description = keyword;
					// 预处理
					JSONObject word = json.getJSONObject("ec21").getJSONArray(
							"word").getJSONObject(0);
					StringBuilder text = new StringBuilder(
							keyword + " [" + word.getString("phone") + "]\n");
					
					// 单词解释
					JSONArray trs = word.getJSONArray("trs");
					for(int i = 0; i < trs.size(); i ++) {
						JSONArray tr = trs.getJSONObject(i).getJSONArray("tr");
						text.append("\n" + trs.getJSONObject(i).getString("pos") + "\n");
						for(int j = 0, cnt = 0; j < tr.size(); j ++) {
							JSONObject item = tr.getJSONObject(j);
							text.append(++ cnt + ". ");
							if(item.containsKey("tr")) {
								for(int k = 0; k < item.getJSONArray("tr").size(); k ++) {
									JSONObject childItem = item.getJSONArray("tr")
											.getJSONObject(k);
									text.append(childItem.getJSONObject("l").getJSONArray("i")
											.getString(0) + "\n");
								}
							}
							else {
								text.append(item.getJSONObject("l").getJSONArray("i")
										.getString(0) + "\n");
							}
							if(item.containsKey("exam")) {
								JSONObject exam = item.getJSONArray("exam")
										.getJSONObject(0).getJSONObject("i");
								text.append(exam.getJSONObject("f").getJSONObject("l")
										.getString("i") + "\n");
								text.append(exam.getJSONObject("n").getJSONObject("l")
										.getString("i") + "\n");
							}
						}
					}
					return Utils.genTextFlowResponse(text.toString(), description);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return Utils.genErrorResponse(500, "JSON Resolve Error");
	}
}