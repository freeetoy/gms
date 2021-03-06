package com.gms.web.admin.service.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.mapper.manage.UserMapper;


@Service
public class UserServiceImpl implements UserService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UserMapper userMapper;
	
	@Override
	@Transactional
	public boolean registerUser(UserVO param) {
		boolean successFlag = false;

		// 정보 등록
		int result = 0;
		logger.info("****** registerUser.getUserId()()) *****===*"+param.getUserId());
		
		result = userMapper.insertUser(param);
		if (result > 0) {
			successFlag = true;
		}
		
		return successFlag;
	}

	@Override
	@Transactional
	public boolean modifyUser(UserVO param) {
		boolean successFlag = false;

		// 가스정보 등록
		int result = 0;
		logger.info("****** modifyUser()()) *****===*"+param.getUserId());
		
			result = userMapper.updateUser(param);
			if (result > 0) {
				successFlag = true;
			}	
		
		return successFlag;
		
	}
	
	@Override
	public UserVO getUserDetails(String userId) {
		return userMapper.selectUserDetail(userId);	
	}

	@Override
	@Transactional
	public boolean deleteUser(String userId) {
		UserVO user = userMapper.selectUserDetail(userId);
		
		logger.info("****** deleteUser.userId *****===*"+userId);
		
		if (user == null || "Y".equals(user.getDeleteYn())) {
			return false;
		}

		int result = userMapper.deleteUser(userId);
		if (result < 1) {
			return false;
		}
		
		return true;
	}
/*
	@Override
	public int getUserCount() {
		logger.info("****** getUserCount *****===*");
		return userMapper.selectUserCount();
	}
	*/
	@Override
	public Map<String, Object> checkUserIdDuplicate(UserVO param) {
		// 중복체크
		int count 							= userMapper.selectUserIdCheck(param);
		// 결과 변수
		Map<String, Object> result 			= new HashMap<String, Object>();
		if(count > 0){
			result.put("result", "fail");
			result.put("message", "아이디가 존재 합니다. 확인 후 입력해 주세요.");
			return result;
		}
		
		result.put("result", "success");
		
		return result;
	}
/*
	@Override
	public int getUserCount(String searchUserNm) {
		logger.info("****** getUserCount ***searchUserNm**===*"  +searchUserNm);
		return userMapper.selectUserCount(searchUserNm);
	}
	

	@Override
	public List<UserVO> getUserList(Criteria cri) {
		logger.info("****** getUserList *****start===*"+cri.getRowStart());
		logger.info("****** getUserList *****end ===*"+cri.getRowEnd());
		return userMapper.selectUserList(cri);
	}

	@Override
	public List<UserVO> getUserList(Criteria cri, String searchUserNm) {
		logger.info("****** getUserList *****===*");
		return userMapper.selectUserList(cri, searchUserNm);
	}
*/
	@Override
	public Map<String , Object> getUserList(UserVO param) {
		logger.info("****** getUserList *****start===*");
		
		
		logger.info("****** getUserList *****param.getSearchUserNm===*" + param.getSearchUserNm());
		logger.info("****** getUserList *****param.getRowPerPage===*" + param.getRowPerPage());
		
		int currentPage = param.getCurrentPage();
		int ROW_PER_PAGE = param.getRowPerPage();
		
		int starPageNum =1;
		
		int lastPageNum = ROW_PER_PAGE;
		
		if(currentPage > (ROW_PER_PAGE/2)) {
			lastPageNum += (starPageNum-1);
		}
		
		int startRow = (currentPage-1) * ROW_PER_PAGE;
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		
		map.put("startRow", startRow);
		map.put("rowPerPage", ROW_PER_PAGE);	
		map.put("searchUserNm", param.getSearchUserNm());	
		
		if(param.getUserPartCd() != null) {
			map.put("userPartCd", param.getUserPartCd());
			logger.info("****** getUserList *****userPartCd===*"+param.getUserPartCd());
		}
		logger.info("****** getUserList *****currentPage===*"+currentPage);
		
		
		int userCount = userMapper.selectUserCount(map);
		
		logger.info("****** getUserList.userCount *****===*"+userCount);
		
		//int lastPage = (int)(Math.ceil(userCount/ROW_PER_PAGE));
		int lastPage = (int)((double)userCount/ROW_PER_PAGE+0.95);
		
		logger.info("****** getUserList.lastPage *****===*"+lastPage);
		
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<UserVO> userList = userMapper.selectUserList(map);
		
		logger.info("****** getUserList.userList *****===*"+userList.size());
		
		resutlMap.put("list",  userList);
		resutlMap.put("searchUserNm", param.getSearchUserNm());
		resutlMap.put("currentPage", currentPage);
		resutlMap.put("lastPage", lastPage);
		resutlMap.put("startPageNum", starPageNum);
		resutlMap.put("lastPageNum", lastPageNum);
		resutlMap.put("totalCount", userCount);
		
		return resutlMap;
		//return userMapper.selectUserList(currentPage);
	}
}
