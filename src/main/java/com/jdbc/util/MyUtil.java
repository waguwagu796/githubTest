package com.jdbc.util;

public class MyUtil {	//페이징 처리
	
	//전체 페이지 개수
	public int getPageCount(int numPerPage, int dataCount) {
		
		int pageCount = 0;
		pageCount = dataCount / numPerPage;
		
		if (dataCount % numPerPage != 0) {
			pageCount++;
		}
		
		return pageCount;
		
	}
	
	//페이징 처리 메소드
	public String pageIndexList(int currentPage, int totalPage, String listUrl) {	//currentPage : 보고자 하는 페이지
		
		int numPerBlock = 5;	//◀이전 6 7 8 9 10 다음▶ <- 여기에 나오는 개수
		int currentPageSetup;	//◀이전 버튼을 눌렀을 때 나오는 페이지
		int page;				// 시작값 (ex.6)
		
		StringBuffer sb = new StringBuffer();
		
		if (currentPage==0 || totalPage==0) {	//Checking
			return "";
		}
		
		//list.jsp
		//list.jsp?searchKey=name&searchValue=suzi
		//listUrl에는 위의 두 주소가 들어가게 됨 (후자는 검색한 것)
		
		if (listUrl.indexOf("?")!=-1) {	//?가 있다면 (검색한 것이라면)
			listUrl = listUrl + "&";
		}else {	//검색한게 아니라면
			listUrl = listUrl + "?";
		}
		
		//이전의 Page Number 구하기
		currentPageSetup = (currentPage/numPerBlock)*numPerBlock;
		
		if (currentPage % numPerBlock == 0) {
			currentPageSetup = currentPageSetup - numPerBlock;
		}
		
		
		//◀이전
		if (totalPage>numPerBlock && currentPageSetup>0) {
			
			sb.append("<a href=\"" + listUrl + "pageNum=" + currentPageSetup + "\">◀이전</a>&nbsp;");
			//\"에서 \는 "가 문자로 쓰였다는 것을 나타냄
			//<a href="list.jsp?pageNum=5">◀이전</a>&nbsp; 와 같음
			
		}
		
		//바로가기 페이지
		page = currentPageSetup + 1;
		
		while (page <= totalPage && page <= (currentPageSetup+numPerBlock)) {
			
			if (page == currentPage) {	//현재 페이지는 색을 칠하고 링크를 걸지 않음
				sb.append("<font color=\"Fuchsia\">" + page + "</font>&nbsp;");
				//<font color="Fuchsia">9</font>&nbsp;
			}else {						//그 외 페이지에는 링크를 걺
				sb.append("<a href=\"" + listUrl + "pageNum=" + page + "\">" + page + "</a>&nbsp;");
				//<a href="list.jsp?pageNum=7">7</a>&nbsp;
			}
			
			page++;
			
		}
		
		//다음▶
		if (totalPage - currentPageSetup > numPerBlock) {
			//if의 조건 생각하기 -  ex) total: 12, currentPageSetup: 5, numPerBlock: 5 -> true
			//ex) total: 12, currentPageSetup: 10, numPerBlock: 5 -> false
			
			sb.append("<a href=\"" + listUrl + "pageNum=" + page + "\">다음▶</a>&nbsp;");
			//<a href="list.jsp?pageNum=11>다음▶</>&nbsp;"
		}
		
		return sb.toString();
		
	}
	
	
	
}
