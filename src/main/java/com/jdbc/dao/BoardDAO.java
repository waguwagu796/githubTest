package com.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionTemplate;

import com.jdbc.dto.BoardDTO;

// JDBC로 만든 코드
public class BoardDAO {

	//의존성 주입
	private SqlSessionTemplate sessionTemplate;
	
	public void setSessionTemplate(SqlSessionTemplate sessionTemplate) {
		this.sessionTemplate = sessionTemplate;
	}
	
	//num의 최대값
	public int getMaxNum() {
		
		int maxNum = 0;
		
		maxNum = sessionTemplate.selectOne("com.board.maxNum");
		
		return maxNum;
	}
	
	//삽입
	public void insertData(BoardDTO dto) {
		
		sessionTemplate.insert("com.board.insertData",dto);
		
	}
	
	//검색된 데이터의 개수 (아래에 searchValue에 %를 붙이기 때문에 검색창이 비어있다면 전체를 검색한다는 뜻)
	public int getDataCount(String searchKey, String searchValue) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("searchKey", searchKey);
		params.put("searchValue", searchValue);
		
		int totalCount = sessionTemplate.selectOne("com.board.getDataCount",params);
		
		return totalCount;
		
	}
	
	//검색한 데이터 가져오기 (검색한 게 없다면 전체 데이터 가져오기)
	public List<BoardDTO> getLists(int start, int end, String searchKey, String searchValue){
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("start", start);
		params.put("end", end);
		params.put("searchKey", searchKey);
		params.put("searchValue", searchValue);
		
		List<BoardDTO> lists = sessionTemplate.selectList("com.board.getLists",params);
		
		return lists;
	}
	
	//조회수 증가
	public void updateHitCount(int num) {
		
		sessionTemplate.update("com.board.updateHitCount",num);

	}
	
	//num으로 조회한 한 개의 데이터
	public BoardDTO getReadData(int num) {
		
		BoardDTO dto = sessionTemplate.selectOne("com.board.getReadData",num);
		
		return dto;
	}
	
	
	//수정
	public void updateData(BoardDTO dto) {
		
		sessionTemplate.update("com.board.updateData",dto);
		
	}
	
	
	//삭제
	public void deleteData(int num) {
		
		sessionTemplate.delete("com.board.deleteData",num);
		
	}
}














