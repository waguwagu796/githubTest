package com.jdbc.springweb;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.jdbc.dao.BoardDAO;
import com.jdbc.dto.BoardDTO;
import com.jdbc.util.MyUtil;

@Controller
public class BoardController {
	
	//의존성 주입
	@Autowired
	@Qualifier("boardDAO")
	BoardDAO dao;
	
	@Autowired
	MyUtil myUtil;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		
		return "index";
		
	}
	
	/*
	@RequestMapping(value = "/created.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String created() {
		
		return "bbs/created";
	}
	*/

	//RequestMapping으로 주소 이름을 직접 설정
	@RequestMapping(value = "/created.action")	//method 생략해도 default가 GET방식
	public ModelAndView created() {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("bbs/created");
		
		return mav;
	}
	
	
	@RequestMapping(value = "/created_ok.action", method = RequestMethod.POST)	//페이지에서 값을 입력하고 넘기니까 POST
	public String created_ok(BoardDTO dto, HttpServletRequest req) {
	
		int maxNum = dao.getMaxNum();
		
		dto.setNum(maxNum + 1);
		dto.setIpAddr(req.getRemoteAddr());
		
		dao.insertData(dto);
		
		return "redirect:/list.action";
	}
	
	
	//GET으로만 쓰면 검색이 안 되는 문제가 발생함 (list.jsp에서 form이 POST방식이므로)
	//이때는 RequestMapping에 RequestMethod.POST를 추가하거나
	//list.jsp에 form의 method를 GET으로 바꿔주면 됨
	@RequestMapping(value = "/list.action", method = {RequestMethod.GET})
	public String list(HttpServletRequest req) throws Exception {
		
		String cp = req.getContextPath();
		
		//넘어온 페이지 번호 받기
		//myUtil, updated, deleted, list(검색)
		String pageNum = req.getParameter("pageNum");
		
		int currentPage = 1;	//default 페이지는 1페이지
		
		if (pageNum != null) {	//2페이지 이후로 실행한 경우 pageNum에는 null이 아닌 값이 있을테니 그 값을 currentPage에 넣음
			currentPage = Integer.parseInt(pageNum);
		}
		
		//검색----------------------------------------------------
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");	//한글을 입력하게 하면 깨짐
		
		if (searchValue == null) {	//검색을 안 했음
			searchKey = "subject";		//searchKey의 기본값
			searchValue = "";			//searchValue의 기본값
		}else {	//검색을 했다면
			if (req.getMethod().equalsIgnoreCase("GET")) { 	//전송 방식이 get방식일 때
				searchValue = URLDecoder.decode(searchValue, "UTF-8");	//복호화해야 한글 안 깨짐
			}
		}
		
		//검색----------------------------------------------------
		
		//전체 데이터의 개수
		int dataCount = dao.getDataCount(searchKey, searchValue);
		
		//한 페이지에 보여줄 데이터의 개수
		int numPerPage = 5;
		
		//전체 페이지의 개수
		int totalPage = myUtil.getPageCount(numPerPage, dataCount);
		
		//삭제로 인한 마지막 페이지 정리
		if (currentPage>totalPage) {
			currentPage = totalPage;
		}
		
		//한 페이지에 가져올 데이터의 시작과 끝 rownum
		int start = (currentPage - 1) * numPerPage + 1;
		int end = currentPage * numPerPage;
		//int end = start + numPerPage - 1;	<- end를 만드는 다른 방법
		
		List<BoardDTO> lists = dao.getLists(start, end, searchKey, searchValue);
		
		String param = "";
		if (searchValue!=null && !searchValue.equals("")) {	//검색을 했다면
			param = "searchKey=" + searchKey;	//사용자 정의이기 때문에 물음표 생략 가능(나중에 붙이면 됨)
			param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");	//단 &은 생략 불가
		}//searchValue를 보낼 것이므로 인코딩
		
		//ex) param = "searchKey=subject&searchValue=2"
		
		//페이징 처리
		String listUrl = cp + "/list.action";
		
		if (!param.equals("")) {
			listUrl += "?" + param;
		}
		
		String pageIndexList = myUtil.pageIndexList(currentPage, totalPage, listUrl);
		
		
		//검색----------------------------------------------------
		
		//글보기 주소
		//주소 정리
		String articleUrl = cp + "/article.action?pageNum=" + currentPage;
		
		if (!param.equals("")) {
			articleUrl += "&" + param;
		}
		
		//검색----------------------------------------------------
		
		
		//포워딩 페이지에 넘길 데이터
		req.setAttribute("lists", lists);
		req.setAttribute("pageIndexList", pageIndexList);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("articleUrl", articleUrl);
		//포워드로 넘어갈 때 위의 네 개의 데이터를 가져감
		
		return "bbs/list";

	}
	
	@RequestMapping(value = "/article.action", method = {RequestMethod.GET})
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		
		String cp = req.getContextPath();
		
		int num = Integer.parseInt(req.getParameter("num"));	//갖고 온 num을 받음
		String pageNum = req.getParameter("pageNum");			//갖고 온 pageNum을 받음

		//검색----------------------------------------------------
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");	//한글을 입력하게 하면 깨짐
		
		if (searchValue!=null) {	//검색을 했다면
			if (req.getMethod().equalsIgnoreCase("GET")) { 	//전송 방식이 get방식일 때
				searchValue = URLDecoder.decode(searchValue,"UTF-8");	//복호화해야 한글 안 깨짐
			}
		}else {		//검색을 안 했음
			searchKey = "subject";		//searchKey의 기본값
			searchValue = "";			//searchValue의 기본값
		}
		//검색----------------------------------------------------
		
		//조회수 증가
		dao.updateHitCount(num);
		
		//한 개의 글 가져오기
		BoardDTO dto = dao.getReadData(num);
		
		if (dto==null) {
			//반환값이 String일 경우
			//return "redirect:/list.action
			
			//ModelAndView인 경우
			ModelAndView mav = new ModelAndView();
			mav.setViewName("redirect:/list.action");
			
			return mav;
		}
		
		//글 라인 수
		int linesu = dto.getContent().split("\n").length;
		
		//글 내용의 enter를 <br/>로 변경
		dto.setContent(dto.getContent().replaceAll("\n", "<br/>"));
		//web은 enter를 인식할 수 없으니까 <br/>로 바꿈
		
		//주소 정리
		String param = "pageNum=" + pageNum;
		if (searchValue!=null && !searchValue.equals("")) {
			param += "&searchKey=" + searchKey;
			param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
		}
		
		//반환값 : String
		/*
		req.setAttribute("dto", dto);
		req.setAttribute("params", param);	//Servlet에서 넘길 때 param은 쓸 수가 없음
		req.setAttribute("linesu", linesu);
		req.setAttribute("pageNum", pageNum);
		*/
		//return "bbs/article";
		
		//반환값 : ModelAndView
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("dto",dto);
		mav.addObject("params", param);
		mav.addObject("linesu", linesu);
		mav.addObject("pageNum", pageNum);	//ModelAndView에서 데이터 넘기기
		
		mav.setViewName("bbs/article");
		
		return mav;
	}
	
	@RequestMapping(value = "/updated.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String updated(HttpServletRequest req, HttpServletResponse resp) throws Exception{

		String cp = req.getContextPath();
		
		int num = Integer.parseInt(req.getParameter("num"));
		String pageNum = req.getParameter("pageNum");
		
		//검색----------------------------------------------------
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");	//한글을 입력하게 하면 깨짐
		
		if (searchValue != null) {	//검색을 했다면
			if(req.getMethod().equalsIgnoreCase("GET"))	{ 	//전송 방식이 get방식일 때
				searchValue = URLDecoder.decode(searchValue, "UTF-8");	//복호화해야 한글 안 깨짐
			}
		}else {		//검색을 안 했음
			searchKey = "subject";		//searchKey의 기본값
			searchValue = "";			//searchValue의 기본값
		}
		//검색----------------------------------------------------
		
		BoardDTO dto = dao.getReadData(num);
		
		if (dto == null) {
			return "redirect:/list.action";
		}
		
		String param = "pageNum=" + pageNum;
		if (searchValue!=null && !searchValue.equals("")) {
			param += "&searchKey=" + searchKey;
			param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
		}
		
		req.setAttribute("dto", dto);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("params", param);
		req.setAttribute("searchKey", searchKey);
		req.setAttribute("searchValue", searchValue);
		
		return "bbs/updated";
		
	}
	
	@RequestMapping(value = "/updated_ok.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String updated_ok(BoardDTO dto,HttpServletRequest req, HttpServletResponse resp) throws Exception{
		
		String cp = req.getContextPath();
		
		String pageNum = req.getParameter("pageNum");
		//updated.jsp에서 hidden으로 보낸 pageNum은 updated_ok에서 dto에 못 들어가므로 따로 받아야함
		
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");	//한글을 입력하게 하면 깨짐
		
		//BoardDTO dto = new BoardDTO();
		/*
		dto.setNum(Integer.parseInt(req.getParameter("num")));
		//hidden으로 보낸 pageNum, searchKey, searchValue, num 모두 받기 완료
		
		dto.setSubject(req.getParameter("subject"));
		dto.setName(req.getParameter("name"));
		dto.setEmail(req.getParameter("email"));
		dto.setPwd(req.getParameter("pwd"));
		dto.setContent(req.getParameter("content"));
		*/
		
		dao.updateData(dto);

		//주소 정리
		String param = "pageNum=" + pageNum;
		if(searchValue!=null && !searchValue.equals("")) {	//인코딩된 한글은 null로도, ""로도 두 번 체크해야함
			param += "&searchKey=" + searchKey;
			param += "&searchValue=" + URLEncoder.encode(searchValue,"UTF-8");
		}
		
		return "redirect:/list.action?" + param;
		
	}
	
	@RequestMapping(value = "/deleted_ok.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String deleted_ok(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		
		int num = Integer.parseInt(req.getParameter("num"));
		
		String pageNum = req.getParameter("pageNum");
		
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		
		dao.deleteData(num);
		
		
		String param = "pageNum=" + pageNum;
		if (searchValue!=null && !searchValue.equals("")) {
			param += "&searchKey=" + searchKey;
			param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
		}
		
		
		return "redirect:/list.action?" + param;
		
		
	}
	
}


























