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
	
	//������ ����
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

	//RequestMapping���� �ּ� �̸��� ���� ����
	@RequestMapping(value = "/created.action")	//method �����ص� default�� GET���
	public ModelAndView created() {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("bbs/created");
		
		return mav;
	}
	
	
	@RequestMapping(value = "/created_ok.action", method = RequestMethod.POST)	//���������� ���� �Է��ϰ� �ѱ�ϱ� POST
	public String created_ok(BoardDTO dto, HttpServletRequest req) {
	
		int maxNum = dao.getMaxNum();
		
		dto.setNum(maxNum + 1);
		dto.setIpAddr(req.getRemoteAddr());
		
		dao.insertData(dto);
		
		return "redirect:/list.action";
	}
	
	
	//GET���θ� ���� �˻��� �� �Ǵ� ������ �߻��� (list.jsp���� form�� POST����̹Ƿ�)
	//�̶��� RequestMapping�� RequestMethod.POST�� �߰��ϰų�
	//list.jsp�� form�� method�� GET���� �ٲ��ָ� ��
	@RequestMapping(value = "/list.action", method = {RequestMethod.GET})
	public String list(HttpServletRequest req) throws Exception {
		
		String cp = req.getContextPath();
		
		//�Ѿ�� ������ ��ȣ �ޱ�
		//myUtil, updated, deleted, list(�˻�)
		String pageNum = req.getParameter("pageNum");
		
		int currentPage = 1;	//default �������� 1������
		
		if (pageNum != null) {	//2������ ���ķ� ������ ��� pageNum���� null�� �ƴ� ���� �����״� �� ���� currentPage�� ����
			currentPage = Integer.parseInt(pageNum);
		}
		
		//�˻�----------------------------------------------------
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");	//�ѱ��� �Է��ϰ� �ϸ� ����
		
		if (searchValue == null) {	//�˻��� �� ����
			searchKey = "subject";		//searchKey�� �⺻��
			searchValue = "";			//searchValue�� �⺻��
		}else {	//�˻��� �ߴٸ�
			if (req.getMethod().equalsIgnoreCase("GET")) { 	//���� ����� get����� ��
				searchValue = URLDecoder.decode(searchValue, "UTF-8");	//��ȣȭ�ؾ� �ѱ� �� ����
			}
		}
		
		//�˻�----------------------------------------------------
		
		//��ü �������� ����
		int dataCount = dao.getDataCount(searchKey, searchValue);
		
		//�� �������� ������ �������� ����
		int numPerPage = 5;
		
		//��ü �������� ����
		int totalPage = myUtil.getPageCount(numPerPage, dataCount);
		
		//������ ���� ������ ������ ����
		if (currentPage>totalPage) {
			currentPage = totalPage;
		}
		
		//�� �������� ������ �������� ���۰� �� rownum
		int start = (currentPage - 1) * numPerPage + 1;
		int end = currentPage * numPerPage;
		//int end = start + numPerPage - 1;	<- end�� ����� �ٸ� ���
		
		List<BoardDTO> lists = dao.getLists(start, end, searchKey, searchValue);
		
		String param = "";
		if (searchValue!=null && !searchValue.equals("")) {	//�˻��� �ߴٸ�
			param = "searchKey=" + searchKey;	//����� �����̱� ������ ����ǥ ���� ����(���߿� ���̸� ��)
			param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");	//�� &�� ���� �Ұ�
		}//searchValue�� ���� ���̹Ƿ� ���ڵ�
		
		//ex) param = "searchKey=subject&searchValue=2"
		
		//����¡ ó��
		String listUrl = cp + "/list.action";
		
		if (!param.equals("")) {
			listUrl += "?" + param;
		}
		
		String pageIndexList = myUtil.pageIndexList(currentPage, totalPage, listUrl);
		
		
		//�˻�----------------------------------------------------
		
		//�ۺ��� �ּ�
		//�ּ� ����
		String articleUrl = cp + "/article.action?pageNum=" + currentPage;
		
		if (!param.equals("")) {
			articleUrl += "&" + param;
		}
		
		//�˻�----------------------------------------------------
		
		
		//������ �������� �ѱ� ������
		req.setAttribute("lists", lists);
		req.setAttribute("pageIndexList", pageIndexList);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("articleUrl", articleUrl);
		//������� �Ѿ �� ���� �� ���� �����͸� ������
		
		return "bbs/list";

	}
	
	@RequestMapping(value = "/article.action", method = {RequestMethod.GET})
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		
		String cp = req.getContextPath();
		
		int num = Integer.parseInt(req.getParameter("num"));	//���� �� num�� ����
		String pageNum = req.getParameter("pageNum");			//���� �� pageNum�� ����

		//�˻�----------------------------------------------------
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");	//�ѱ��� �Է��ϰ� �ϸ� ����
		
		if (searchValue!=null) {	//�˻��� �ߴٸ�
			if (req.getMethod().equalsIgnoreCase("GET")) { 	//���� ����� get����� ��
				searchValue = URLDecoder.decode(searchValue,"UTF-8");	//��ȣȭ�ؾ� �ѱ� �� ����
			}
		}else {		//�˻��� �� ����
			searchKey = "subject";		//searchKey�� �⺻��
			searchValue = "";			//searchValue�� �⺻��
		}
		//�˻�----------------------------------------------------
		
		//��ȸ�� ����
		dao.updateHitCount(num);
		
		//�� ���� �� ��������
		BoardDTO dto = dao.getReadData(num);
		
		if (dto==null) {
			//��ȯ���� String�� ���
			//return "redirect:/list.action
			
			//ModelAndView�� ���
			ModelAndView mav = new ModelAndView();
			mav.setViewName("redirect:/list.action");
			
			return mav;
		}
		
		//�� ���� ��
		int linesu = dto.getContent().split("\n").length;
		
		//�� ������ enter�� <br/>�� ����
		dto.setContent(dto.getContent().replaceAll("\n", "<br/>"));
		//web�� enter�� �ν��� �� �����ϱ� <br/>�� �ٲ�
		
		//�ּ� ����
		String param = "pageNum=" + pageNum;
		if (searchValue!=null && !searchValue.equals("")) {
			param += "&searchKey=" + searchKey;
			param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
		}
		
		//��ȯ�� : String
		/*
		req.setAttribute("dto", dto);
		req.setAttribute("params", param);	//Servlet���� �ѱ� �� param�� �� ���� ����
		req.setAttribute("linesu", linesu);
		req.setAttribute("pageNum", pageNum);
		*/
		//return "bbs/article";
		
		//��ȯ�� : ModelAndView
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("dto",dto);
		mav.addObject("params", param);
		mav.addObject("linesu", linesu);
		mav.addObject("pageNum", pageNum);	//ModelAndView���� ������ �ѱ��
		
		mav.setViewName("bbs/article");
		
		return mav;
	}
	
	@RequestMapping(value = "/updated.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String updated(HttpServletRequest req, HttpServletResponse resp) throws Exception{

		String cp = req.getContextPath();
		
		int num = Integer.parseInt(req.getParameter("num"));
		String pageNum = req.getParameter("pageNum");
		
		//�˻�----------------------------------------------------
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");	//�ѱ��� �Է��ϰ� �ϸ� ����
		
		if (searchValue != null) {	//�˻��� �ߴٸ�
			if(req.getMethod().equalsIgnoreCase("GET"))	{ 	//���� ����� get����� ��
				searchValue = URLDecoder.decode(searchValue, "UTF-8");	//��ȣȭ�ؾ� �ѱ� �� ����
			}
		}else {		//�˻��� �� ����
			searchKey = "subject";		//searchKey�� �⺻��
			searchValue = "";			//searchValue�� �⺻��
		}
		//�˻�----------------------------------------------------
		
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
		//updated.jsp���� hidden���� ���� pageNum�� updated_ok���� dto�� �� ���Ƿ� ���� �޾ƾ���
		
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");	//�ѱ��� �Է��ϰ� �ϸ� ����
		
		//BoardDTO dto = new BoardDTO();
		/*
		dto.setNum(Integer.parseInt(req.getParameter("num")));
		//hidden���� ���� pageNum, searchKey, searchValue, num ��� �ޱ� �Ϸ�
		
		dto.setSubject(req.getParameter("subject"));
		dto.setName(req.getParameter("name"));
		dto.setEmail(req.getParameter("email"));
		dto.setPwd(req.getParameter("pwd"));
		dto.setContent(req.getParameter("content"));
		*/
		
		dao.updateData(dto);

		//�ּ� ����
		String param = "pageNum=" + pageNum;
		if(searchValue!=null && !searchValue.equals("")) {	//���ڵ��� �ѱ��� null�ε�, ""�ε� �� �� üũ�ؾ���
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


























