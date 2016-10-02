package mo.cc.flow.resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import mo.cc.flow.page.Baidu;
import mo.cc.flow.page.BaiduBaike;
import mo.cc.flow.page.Bilibili;
import mo.cc.flow.page.Chanyouji;
import mo.cc.flow.page.Chunyuyisheng;
import mo.cc.flow.page.Huihui;
import mo.cc.flow.page.Hupu;
import mo.cc.flow.page.ITJuzi;
import mo.cc.flow.page.Jianshu;
import mo.cc.flow.page.Smzdm;
import mo.cc.flow.page.StackOverflow;
import mo.cc.flow.page.Taobao;
import mo.cc.flow.page.ThirtySixKr;
import mo.cc.flow.page.Vodtw;
import mo.cc.flow.page.Weibo;
import mo.cc.flow.page.Ximalaya;
import mo.cc.flow.page.Youdao;
import mo.cc.flow.page.ZJULibrary;
import mo.cc.flow.page.ZJUSearch;
import mo.cc.flow.page.Zhihu;

@Path("")
public class FlowResource {
	@GET
	@Path("/itjuzi/search")
	@Produces("application/json")
	public Response getITJuziSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return ITJuzi.search(page, keyword);
	}
	@GET
	@Path("/zju/search")
	@Produces("application/json")
	public Response getZJUSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return ZJUSearch.search(page, keyword);
	}
	@GET
	@Path("/zju/library/search")
	@Produces("application/json")
	public Response getZJULibSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return ZJULibrary.search(page, keyword);
	}
	@GET
	@Path("/baidu/baike/search")
	@Produces("application/json")
	public Response getBaiduBaikeSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return BaiduBaike.chooseResponse(page, keyword);
	}
	@GET
	@Path("/36kr/search")
	@Produces("application/json")
	public Response get36KrSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return ThirtySixKr.search(page, keyword);
	}
	@GET
	@Path("/weibo/search")
	@Produces("application/json")
	public Response getWeiboSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return Weibo.search(page, keyword);
	}
	@GET
	@Path("/jianshu/search")
	@Produces("application/json")
	public Response getJianshuSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return Jianshu.search(page, keyword);
	}
	@GET
	@Path("/baidu/search")
	@Produces("application/json")
	public Response getBaiduSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return Baidu.search(page, keyword);
	}
	@GET
	@Path("/bilibili/search")
	@Produces("application/json")
	public Response getBilibiliSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return Bilibili.search(page, keyword);
	}
	@GET
	@Path("/hupu/nba/news")
	@Produces("application/json")
	public Response getHupuNbaNews(@DefaultValue("1") @QueryParam("page") int page) {
		return Hupu.getNbaNews(page);
	}
	@GET
	@Path("/novel/month_rank")
	@Produces("application/json")
	public Response getNovelMonthRank(@DefaultValue("1") @QueryParam("page") int page) {
		return Vodtw.getMonthRank(page);
	}
	@GET
	@Path("/zhihu/search")
	@Produces("application/json")
	public Response getZhihuSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return Zhihu.search(page, keyword);
	}
	@GET
	@Path("/taobao/search")
	@Produces("application/json")
	public Response getTaobaoSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return Taobao.search(page, keyword);
	}
	@GET
	@Path("/chanyouji/search")
	@Produces("application/json")
	public Response getChanyoujiSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return Chanyouji.search(page, keyword);
	}
	@GET
	@Path("/ximalaya/search")
	@Produces("application/json")
	public Response getXimalayaSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return Ximalaya.search(page, keyword, "relation");
	}
	@GET
	@Path("/ximalaya/logic_thinking")
	@Produces("application/json")
	public Response getXimalayaLogicThinking(
			@DefaultValue("1") @QueryParam("page") int page) {
		return Ximalaya.logicThinking(page);
	}
	@GET
	@Path("/ximalaya/xiaosong_mystery")
	@Produces("application/json")
	public Response getXimalayaMystery(
			@DefaultValue("1") @QueryParam("page") int page) {
		return Ximalaya.xiaosongMystery(page);
	}
	@GET
	@Path("/chunyuyisheng/search")
	@Produces("application/json")
	public Response getChunyuyishengSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return Chunyuyisheng.search(page, keyword);
	}
	@GET
	@Path("/dictionary/search")
	@Produces("application/json")
	public Response getEc21Search(@QueryParam("keyword") String keyword) {
		return Youdao.searchEc21(keyword);
	}
	@GET
	@Path("/youdao/search")
	@Produces("application/json")
	public Response getYoudaoSearch(@QueryParam("keyword") String keyword) {
		return Youdao.searchYoudao(keyword);
	}
	@GET
	@Path("/stackoverflow/search")
	@Produces("application/json")
	public Response getStackOverflowSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return StackOverflow.search(page, keyword);
	}
	@GET
	@Path("/smzdm/search")
	@Produces("application/json")
	public Response getSmzdmSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return Smzdm.search(page, keyword);
	}
	@GET
	@Path("/huihui/search")
	@Produces("application/json")
	public Response getHuihuiSearch(@DefaultValue("1") @QueryParam("page") int page, 
			@QueryParam("keyword") String keyword) {
		return Huihui.search(page, keyword);
	}
}