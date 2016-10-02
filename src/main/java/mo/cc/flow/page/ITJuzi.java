package mo.cc.flow.page;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import mo.cc.Utils;
import mo.cc.flow.ListFlow;
import mo.cc.flow.ListFlowElement;

public class ITJuzi {
	// ITJuzi的token
	private static String itJuziToken;
	// 更新token并存储在全局变量itJuziToken中
	private static boolean resetITJuziToken() {
		try {
			String client_secret = "7fed6221f1ecad2721e280319bf1cca6";
			String grant_type = "client_credentials";
			String client_id = "2";
			String tokenUrl = "http://cobra.itjuzi.com/oauth/access_token";

			List<NameValuePair> pairList = new ArrayList<NameValuePair>();
			pairList.add(new BasicNameValuePair("client_secret", client_secret));
			pairList.add(new BasicNameValuePair("grant_type", grant_type));
			pairList.add(new BasicNameValuePair("client_id", client_id));
			JSONObject tokenJson = Utils.genPostJSONObject(tokenUrl, pairList);
			String access_token = tokenJson.getString("access_token");
			String token_type = tokenJson.getString("token_type");
			itJuziToken = token_type + " " + access_token;
			return access_token != null && !access_token.isEmpty() 
					&& token_type != null && !token_type.isEmpty();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public static Response search(int page, String keyword) {
		String url;
		try {
			url = "https://cobra.itjuzi.com/api/search?type=juzi_company&key="
					+ URLEncoder.encode(keyword, "UTF-8") + "&page="
					+ page;
		} catch (Exception e) {
			e.printStackTrace();
			return Utils.genErrorResponse(500, "Param Transform Error");
		}
		Set<Header> headers = new HashSet<Header>();
		headers.add(new BasicHeader("Authorization", itJuziToken));
		CloseableHttpResponse response = Utils.sendGet(url, headers);
		int statusCode = response.getStatusLine().getStatusCode();
		// 检查是否因为token过期而得到错误结果
		// token过期时重新获取token
		if(statusCode < 200 || statusCode >= 300) {
			// 重新获取token
			if(resetITJuziToken()) {
				headers.clear();
				headers.add(new BasicHeader("Authorization", itJuziToken));
				response = Utils.sendGet(url, headers);
				statusCode = response.getStatusLine().getStatusCode();
			}
			else {
				return Utils.genErrorResponse(500, "Token Acquiring Error");
			}
		}
		// 检查是否得到正确结果
		if(statusCode >= 200 || statusCode < 300) {
			String entity;
			try {
				entity = EntityUtils.toString(response.getEntity()
						, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
				return Utils.genErrorResponse(500, "Content Acquiring Error");
			}
			JSONObject json = JSON.parseObject(entity);
			if(json != null) {
				JSONObject company = json.getJSONObject("juzi_company");
				JSONArray detail = company.getJSONArray("detail");
				ListFlow listFlow = new ListFlow();
				listFlow.description = "以下是IT桔子中搜到的公司";
				
				// pagination处理
				listFlow.pagination.page = page;
				int totalNews = company.getIntValue("total");
				listFlow.pagination.pages = (int) Math.ceil(totalNews / 15.0);
				// 目前展示的新闻数目
				int totalNow = detail.size() + (page - 1) * 15;
				// 已全部展示
				if(totalNow >= totalNews) {
					listFlow.pagination.has_next = false;
				}
				else {
					listFlow.pagination.has_next = true;
				}
				
				for(int i = 0; i < detail.size(); i ++) {
					ListFlowElement li = new ListFlowElement();
					JSONObject item = detail.getJSONObject(i);
					try {
						li.title = item.getString("com_name");
						li.subtitle = item.getString("com_des");
						li.url = "http://www.itjuzi.com/company/"
								+ item.getString("com_id");
						li.image.alt = "无法显示鸟~";
						li.image.src = "http://itjuzi.com/images/" 
								+ item.getString("com_logo_archive");
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