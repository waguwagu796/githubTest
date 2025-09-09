// 내용의 값의 빈공백을 trim(앞/뒤)
String.prototype.trim = function() {
		var TRIM_PATTERN = /(^\s*)|(\s*$)/g;		//공백 찾기
		return this.replace(TRIM_PATTERN, "");		//공백 지우기 (null로 바꾸기)
};

// E-Mail 검사
function isValidEmail(email) {
	var format = /^((\w|[\-\.])+)@((\w|[\-\.])+)\.([A-Za-z]+)$/;
    if (email.search(format) != -1)
        return true; //올바른 포맷 형식
    return false;
}

// 한글 필터링
function isValidKorean(data){
     // UTF-8 코드 중 AC00부터 D7A3 값이지 검사
	var format = /^[\uac00-\ud7a3]*$/g;
    if (data.search(format) != -1)
        return true; //올바른 포맷 형식
    return false;
}
