package com.jdbc.util;

public class MyUtil {	//����¡ ó��
	
	//��ü ������ ����
	public int getPageCount(int numPerPage, int dataCount) {
		
		int pageCount = 0;
		pageCount = dataCount / numPerPage;
		
		if (dataCount % numPerPage != 0) {
			pageCount++;
		}
		
		return pageCount;
		
	}
	
	//����¡ ó�� �޼ҵ�
	public String pageIndexList(int currentPage, int totalPage, String listUrl) {	//currentPage : ������ �ϴ� ������
		
		int numPerBlock = 5;	//������ 6 7 8 9 10 ������ <- ���⿡ ������ ����
		int currentPageSetup;	//������ ��ư�� ������ �� ������ ������
		int page;				// ���۰� (ex.6)
		
		StringBuffer sb = new StringBuffer();
		
		if (currentPage==0 || totalPage==0) {	//Checking
			return "";
		}
		
		//list.jsp
		//list.jsp?searchKey=name&searchValue=suzi
		//listUrl���� ���� �� �ּҰ� ���� �� (���ڴ� �˻��� ��)
		
		if (listUrl.indexOf("?")!=-1) {	//?�� �ִٸ� (�˻��� ���̶��)
			listUrl = listUrl + "&";
		}else {	//�˻��Ѱ� �ƴ϶��
			listUrl = listUrl + "?";
		}
		
		//������ Page Number ���ϱ�
		currentPageSetup = (currentPage/numPerBlock)*numPerBlock;
		
		if (currentPage % numPerBlock == 0) {
			currentPageSetup = currentPageSetup - numPerBlock;
		}
		
		
		//������
		if (totalPage>numPerBlock && currentPageSetup>0) {
			
			sb.append("<a href=\"" + listUrl + "pageNum=" + currentPageSetup + "\">������</a>&nbsp;");
			//\"���� \�� "�� ���ڷ� �����ٴ� ���� ��Ÿ��
			//<a href="list.jsp?pageNum=5">������</a>&nbsp; �� ����
			
		}
		
		//�ٷΰ��� ������
		page = currentPageSetup + 1;
		
		while (page <= totalPage && page <= (currentPageSetup+numPerBlock)) {
			
			if (page == currentPage) {	//���� �������� ���� ĥ�ϰ� ��ũ�� ���� ����
				sb.append("<font color=\"Fuchsia\">" + page + "</font>&nbsp;");
				//<font color="Fuchsia">9</font>&nbsp;
			}else {						//�� �� ���������� ��ũ�� ��
				sb.append("<a href=\"" + listUrl + "pageNum=" + page + "\">" + page + "</a>&nbsp;");
				//<a href="list.jsp?pageNum=7">7</a>&nbsp;
			}
			
			page++;
			
		}
		
		//������
		if (totalPage - currentPageSetup > numPerBlock) {
			//if�� ���� �����ϱ� -  ex) total: 12, currentPageSetup: 5, numPerBlock: 5 -> true
			//ex) total: 12, currentPageSetup: 10, numPerBlock: 5 -> false
			
			sb.append("<a href=\"" + listUrl + "pageNum=" + page + "\">������</a>&nbsp;");
			//<a href="list.jsp?pageNum=11>������</>&nbsp;"
		}
		
		return sb.toString();
		
	}
	
	
	
}
