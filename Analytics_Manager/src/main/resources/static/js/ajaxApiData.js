var url, errorMessage;

/************************************************** 알고리즘 **************************************************/
/*알고리즘 조회*/
var fnAlgorithmListByAjax = function(){
	var result;
	url = "/algorithms";
	errorMessage = "알고리즘 목록 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.algorithms;
	});
	return result;
};

/*알고리즘 상세조회*/
var fnAlgorithmByAjax = function(algorithmPk){
	var result;
	url = "/algorithms/"+algorithmPk;
	errorMessage = "알고리즘 상세조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.algorithm;
	});
	return result;
}

/************************************************** 샌드박스 템플릿 **************************************************/

/*템플릿 목록 가져오기*/
var fnGetTemplateListByAjax = function(){
	var result;
	url = "/sandbox/templates";
	errorMessage = "템플릿 목록 가져오기 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response;
	});
	return result;
}

/*템플릿 가져오기*/
var fnGetTemplateByAjax = function(templateId){
	var result;
	url = "/sandbox/templates/"+templateId;
	errorMessage = "템플릿 정보 가져오기 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.template;
	});
	return result;
}

/*템플릿 신청목록 가져오기  */
var fnGetRequestTemplateListByAjax = function(){
	var result;
	url = "/sandbox/customTemplateRequests";
	errorMessage = "템플릿 신청목록 가져오기 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.customTemplateRequests;
	});
	return result;
}

/* 커스텀 템플릿 정보 가져오기*/
var fnGetCustomTemplateByAjax = function(templateId){
	var result;
	var url = "/sandbox/customTemplateRequests/"+templateId;
	var errorMessage = "커스텀 템플릿 정보 가져오기 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.customTemplateRequest;
	});
	return result;
}


/*커스텀 템플릿 상태 변경*/
var fnChangeCustomTemplateByAjax = function(templateId, data){
	var result;
	url = "/sandbox/customTemplateRequests/"+templateId;
	errorMessage = "커스텀 템플릿 상태 변경 에러";
	fnAjaxDataSync(url, "PATCH", JSON.stringify(data), errorMessage, function(response){
		result = response;
	});
	return result;
}

/*템플릿 허용 데이터 가져오기*/
var fnGetRequestTemplateAvailableByAjax = function(option){
	var result;
	url = "/sandbox/availableList";
	errorMessage = "템플릿 허용 목록 가져오기 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.availableList;
	});
	return result;
}

/*선택된 허용가능 데이터 가져오기*/
var fnGetRequestTemplateAvailableDataByAjax = function(id){
	var result;
	url = "/sandbox/availableDataList/"+id;
	errorMessage = "템플릿 허용 데이터 가져오기 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.availableDataList.data;
	});
	return result;
}

/*샌드박스 템플릿 추가 요청*/
var fnRequestCustomTemplateByAjax = function(data){
	var result;
	url = "/sandbox/customTemplateRequests";
	errorMessage = "샌드박스 템플릿 추가 요청 에러";
	fnAjaxDataSync(url, "POST", JSON.stringify(data), errorMessage, function(response){
		result = "success";
	});
	return result;
}


/*스냅샷 목록 가져오기*/
var fnSetTemplateSnapshotByAjax = function(){
	var result;
	var url = "/sandbox/snapshotList";
	var errorMessage = "스냅샷 가져오기 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.snapshotList;
	});
	return result;
}

/*템플릿 생성*/
var fnCreateTemplateByAjax = function(data){
	var result;
	url = "/sandbox/templates";
	errorMessage = "템플릿 생성 에러";
	fnAjaxDataSync(url, "POST", JSON.stringify(data), errorMessage, function(response){
		result = response;
	});
	return result;
}

/*템플릿 삭제*/
var fnDeleteTemplateByAjax = function(templateId){
	var result;
	url = "/sandbox/templates/"+templateId;
	errorMessage = "템플릿 삭제 에러";
	fnAjaxDeleteDataSync(url, errorMessage, function(response){
		result = response;
	});
	return result;
}

/*템플릿 수정*/
var fnUpdateTemplateByAjax = function(templateId, data){
	var result;
	url = "/sandbox/templates/"+templateId;
	errorMessage = "템플릿 수정 에러";
	fnAjaxDataSync(url, "PATCH", JSON.stringify(data), errorMessage, function(response){
		result = response;
	});
	return result;
}

/*session 에 instancePk등록*/
var fnSandboxSetInstancePkInSessionByAjax = function(instanceSequencePk){
	var result;
	url = "/sandbox/sandboxSetInstancePkInSession/"+instanceSequencePk;
	errorMessage = "session 에 instancePk등록 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response;
	});
	return result;
}

/************************************************** 샌드박스 인스턴스 **************************************************/

/*인스턴스 목록 가져오기*/
var fnGetInstanceListByAjax = function(){
	var result;
	var url = "/sandbox/instances";
	
	console.log("[fnGetInstanceListByAjax] url ==>" + url);
	
	var errorMessage = "인스턴스 목록 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.instances;
	});
	return result;
}

/*인스턴스 서버사양 가져오기*/
var fnGetInstanceServerByAjax = function(serverId){
	var result;
	url = "/sandbox/specifications/"+serverId
		
	errorMessage = "인스턴스 서버사양 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response;
	});
	return result;
}


/*인스턴스 생성용 서버 종류 가져오기*/
var fnGetServerListByAjax = function(){
	var result;
	url = "/sandbox/specifications/";
	errorMessage = "인스턴스 서버 종류 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.data.flavors;
	});
	return result;
}

/*인스턴스 생성*/
var fnCreateInstanceByAjax = function(data){
	var result;
	url = "/sandbox/instances";
	errorMessage = "인스턴스 생성 에러";
	fnAjaxDataSync(url, "POST", JSON.stringify(data), errorMessage, function(response){
		result = response;
	});
	return result;
}

/*인스턴스 시작/정지*/
var fnStartNStopInstanceByAjax = function(checkId){
	var result;
	url = "/sandbox/instances/"+checkId;
	errorMessage = "인스턴스 시작/정지 에러";
	fnAjaxDataSync(url, "PATCH", "", errorMessage, function(response){
		result = response;
	});
	return result;
}

/*인스턴스 삭제*/
var fnDeleteInstanceByAjax = function(checkId){
	var result;
	url = "/sandbox/instances/"+checkId;
	errorMessage = "인스턴스 삭제 에러";
	fnAjaxDeleteDataSync(url, errorMessage, function(response){
		result = response;
	});
	return result;
}

/************************************************** 프로젝트 **************************************************/

/*프로젝트 목록 조회*/
var fnGetProjectListByAjax = function(){
	var result;
	url = "/projects";
	errorMessage = "프로젝트 목록 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.projects;
	});
	return result;
}

/*프로젝트 상세정보 가져오기*/
var fnGetProjectByAjax = function(projectSequencePk){
	var result;
	url = "/projects/"+projectSequencePk;
	errorMessage = "프로젝트 상세조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.project;
	});
	return result;
}

/*프로젝트 등록/수정*/
var fnSaveProjectByAjax = function(url, method, data){
	errorMessage = "프로젝트 등록/수정 에러";
	fnAjaxDataSync(url, method, JSON.stringify(data), errorMessage, function(response){
		result = response;
	});
	return result;
}

/*프로젝트 삭제*/
var fnDeleteProjcetByAjax = function(checkId){
	var result;
	// 체크된 항목 가져오기
	url = "/projects/"+checkId;
	errorMessage = "프로젝트 삭제 에러";
	fnAjaxDeleteDataSync(url, errorMessage, function(response){
		result = response;
	});
	return result;
}

/*원본 리스트 가져오기*/
var fnGetOriginalDataListByAjax = function(projectSequencePk){
	var result;
	url = "/projects/"+projectSequencePk+"/originalData";
	errorMessage = "원본 리스크 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.originalDataList;
	});
	return result;
}

/* 원본데이터 가져오기 */
var fnGetOriginalDataByAjax = function(projectSequencePk, selectedOriginalDataPk){
	var result;
	url = "/projects/"+projectSequencePk+"/originalData/"+selectedOriginalDataPk;
	errorMessage = "원본데이터 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.originalData;
	});
	return result;
}

/* 원본데이터 삭제*/
var fnDeleteOriginalDataByAjax = function(projectSequencePk, selectedOriginalDataPk){
	var result;
	url = "/projects/"+projectSequencePk+"/originalData/"+selectedOriginalDataPk;
	errorMessage = "원본데이터 삭제 에러";
	fnAjaxDeleteDataSync(url, errorMessage, function(response){
		result = "success";
	});
	return result;
}

/*샌드박스 파일브라우저 가져오기*/
var fnGetSandboxFileBrowserByAjax = function(selectedInstancePk){
	var result;
	url = "/sandbox/instances/"+selectedInstancePk+"/localFiles";
	errorMessage = "샌드박스 로컬파일 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.localFiles;
	});
	return result;
}

/*샌드박스 파일브라우저 샘플 미리보기*/
var fnGetLocalFileSampleByAjax = function(selectedInstancePk, localFile){
	var result;
	url = "/sandbox/instances/"+selectedInstancePk+"/localFiles/"+localFile;
	errorMessage = "샌드박스 로컬파일 샘플조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.localFile;
	});
	return result;
}

/*원본데이서 생성*/
var fnCreateOriginalDataByAjax = function(projectSequencePk, data){
	var result;
	url = "/projects/"+projectSequencePk+"/originalData/";
	errorMessage = "원본데이서 생성 에러";
	fnAjaxDataSync(url, "POST", JSON.stringify(data), errorMessage, function(response){
		result = response;
	});
	return result;
}

/*처리방식 가져오기*/
var fnGetPreprocessFunctionByAjax = function(){
	var result;
	url = "/preprocessFunctions";
	errorMessage = "처리방식 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.preprocessFunctionList;
	});
	return result;
}

/*파라미터 가져오기*/
var fnGetPreprocessFunctionParametersByAjax = function(preprocessFunctionSequencePk){
	var result;
	url = "/preprocessFunctions/"+preprocessFunctionSequencePk;
	errorMessage = "처리방식 파라미터 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.preprocessFunction.PARAMETERS;
	});
	return result;
}

/*전처리 테스트*/
var fnPreprocessTestByAjax = function(projectSequencePk, selectedOriginalDataPk, data){
	var result;
	url = "/projects/"+projectSequencePk+"/originalData/"+selectedOriginalDataPk;
	
	console.log("[ajaxApiData] fnPreprocessTestByAjax url:"+url);
	
	errorMessage = "전처리 테스트 에러";
	fnAjaxDataSync(url, "PATCH", JSON.stringify(data), errorMessage, function(response){
		result = response;
	});
	return result;
}

/*전처리 생성*/
var fnCreatePreprocessByAjax = function(projectSequencePk, data){
	var result;
	url = "/projects/"+projectSequencePk+"/preprocessedData";
	errorMessage = "전처리 생성 에러";
	fnAjaxDataSync(url, "POST", JSON.stringify(data), errorMessage, function(response){
		result = response;
	});
	return result;
}


/*전처리 목록 가져오기*/
var fnGetPreprocessedDataListByAjax = function(selectedInstancePk, selectedOriginalData){
	var result;
	url = "/"+selectedInstancePk+"/originalData/"+selectedOriginalData+"/preprocessedData";
	errorMessage = "전처리 리스크 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.preprocessedDataList;
	});
	return result;
}

/* 전처리 데이터 가져오기*/
var fnGetPreprocessedDataByAjax = function(selectedInstancePk, selectedOriginalData, selectedPreprocessedData){
	var result;
	url = "/"+selectedInstancePk+"/originalData/"+selectedOriginalData+"/preprocessedData/"+selectedPreprocessedData;
	errorMessage = "전처리데이터 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.preprocessedData;
	});
	return result;
}



/*전처리 삭제*/
var fnDeletePreprocessedDataByAjax = function(projectSequencePk, selectedPreprocessedData){
	var result;
	url = "/projects/"+projectSequencePk+"/preprocessedData/"+selectedPreprocessedData;
	errorMessage = "전처리 데이터 삭제 에러";
	fnAjaxDeleteDataSync(url, errorMessage, function(response){
		result = "success";
	});
	return result;
}

/*알고리즘 검색 조회 */
var fnSearchAlgorithmByAjax = function(searchValue){
	var result;
	url = "/searchAlgorithms";
	errorMessage = "알고리즘 검색 조회 에러";
	var data = {"searchValue":searchValue};
	fnAjaxDataSync(url, "POST", JSON.stringify(data), errorMessage, function(response){
		result = response.algorithms;
	});
	return result;
}

/*학습모델 생성 */
var fnModelsByAjax = function(projectSequencePk, data){
	var result;
	url = "/projects/"+projectSequencePk+"/models";
	
	console.log("[fnModelsByAjax] url ==>" + url);
	
	errorMessage = "학습모델 생성 에러";
	fnAjaxDataSync(url, "POST", JSON.stringify(data), errorMessage, function(response){
		result = response;
	});
	return result;
}

/*모델 목록 조회*/
var fnGetModelsByAjax = function(projectSequencePk, preprocessedDataSequencePk){
	var result;
	url = "/projects/"+projectSequencePk+"/models?preprocessedDataSequencePk="+preprocessedDataSequencePk;
	errorMessage = "모델 목록 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.modelsList;
	});
	return result;
}

/*모델 조회*/
var fnGetModeslByAjax = function(projectSequencePk, modelSequencePk){
	var result;
	url = "/projects/"+projectSequencePk+"/models/"+modelSequencePk;
	errorMessage = "모델 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.model;
	});
	return result;
}

/*모델 삭제*/
var fnDeleteModelByAjax = function(projectSequencePk, modelSequencePk){
	var result;
	url = "/projects/"+projectSequencePk+"/models/"+modelSequencePk;
	errorMessage = "모델 삭제 에러";
	fnAjaxDeleteDataSync(url, errorMessage, function(response){
		result = "success";
	});
	return result;
}

/*모델 학습 중지*/
var fnStopAndRestartModelByAjax = function(projectSequencePk, modelSequencePk, data){
	var result;
	url = "/projects/"+projectSequencePk+"/models/"+modelSequencePk;
	errorMessage = "모델 학습 에러";
	fnAjaxDataSync(url, "PATCH", JSON.stringify(data), errorMessage, function(response){
		result = "success";
	});
	return result;
}

/*모델 테스트*/
var fnModelTestByAjax = function(projectSequencePk, modelSequencePk, data){
	var result;
	url = "/projects/"+projectSequencePk+"/modelsTest/"+modelSequencePk;
	errorMessage = "모델 테스트 에러";
	fnAjaxDataSync(url, "PATCH", JSON.stringify(data), errorMessage, function(response){
		result = response.data;
	});
	return result;
}

/* 인스턴스별 모델 목록 조회*/
var fnGetModelsOfInstancePkByAjax = function(instanceSequencePk){
	var result;
	url = "/modelsOfInstancePk/"+instanceSequencePk;
	errorMessage = "모델 목록 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.modelsList;
	});
	return result;
}


/************************************************** 배치 **************************************************/

/*배치신청 목록 조회*/
var fnGetbatchServiceRequestsByAjax = function(){
	var result;
	url = "/batchServiceRequests/";
	errorMessage = "배치신청 목록 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.batchServiceRequests;
	});
	return result;
}

/*배치신청 조회 */
var fnGetbatchServiceRequestByAjax = function(batchServiceSequencePk){
	var result;
	url = "/batchServiceRequests/"+batchServiceSequencePk;
	errorMessage = "배치신청 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.batchServiceRequest;
	});
	return result;
}


/*배치신청 등록/수정*/
var fnbatchServiceRequestsByAjax = function(url, method, data){
	errorMessage = "배치신청 등록/수정 에러";
	fnAjaxDataSync(url, method, JSON.stringify(data), errorMessage, function(response){
		result = response;
	});
	return result;
}

/*배치신청 삭제*/
var fnDeleteBatchRequestByAjax = function(batchServiceRequestSequencePk){
	var result;
	url = "/batchServiceRequests/"+batchServiceRequestSequencePk;
	errorMessage = "배치신청 삭제 에러";
	fnAjaxDeleteDataSync(url, errorMessage, function(response){
		result = response;
	});
	return result;
}

/*배치 목록 조회*/
var fnGetBatchServicesByAjax = function(){
	var result;
	url = "/batchServices/";
	errorMessage = "배치 목록 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.batchServices;
	});
	return result;
}

/*배치 조회*/
var fnGetBatchServiceByAjax = function(batchServiceSequencePk){
	var result;
	url = "/batchServices/"+batchServiceSequencePk;
	errorMessage = "배치 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.batchService;
	});
	return result;
}

/*배치 등록/수정*/
var fnbatchServicesByAjax = function(url, method, data){
	errorMessage = "배치신청 등록/수정 에러";
	fnAjaxDataSync(url, method, JSON.stringify(data), errorMessage, function(response){
		result = response;
	});
	return result;
}

/*배치 삭제*/
var fnDeleteBatchByAjax = function(batchServiceSequencePk){
	var result;
	url = "/batchServices/"+batchServiceSequencePk;
	errorMessage = "배치 삭제 에러";
	fnAjaxDeleteDataSync(url, errorMessage, function(response){
		result = response;
	});
	return result;
}


/*배치서버 목록 가져오기*/
var fnGetBatchServerListByAjax = function(){
	var result;
	var url = "/batchServers";
	var errorMessage = "배치서버 목록 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.batchServers;
	});
	return result;
}

/*배치서버 생성*/
var fnCreateBatchServerByAjax = function(data){
	var result;
	url = "/batchServers";
	errorMessage = "배치서버 생성 에러";
	fnAjaxDataSync(url, "POST", JSON.stringify(data), errorMessage, function(response){
		result = response;
	});
	return result;
}

/*배치 시작&정지*/
var fnStartAStopBatchByAjax = function(data){
	var result;
	url = "/batchServices/startAndStop";
	errorMessage = "배치 시작/정지 에러";
	fnAjaxDataSync(url, "PATCH", JSON.stringify(data), errorMessage, function(response){
		result = response;
	});
	return result;
}

/*배치로그 가져오기*/
var fnGetBatchLogListByAjax = function(startDate, endDate){
	var result;
	var url = "/batchLogs?startDate="+startDate+"&endDate="+endDate;
	var errorMessage = "배치로그 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response;
	});
	return result;
}

/*배치로그 가져오기*/
var fnGetBatchLogByAjax = function(logBatchSequencePk){
	var result;
	var url = "/batchLog/"+logBatchSequencePk;
	var errorMessage = "배치로그 조회 에러";
	fnAjaxGetDataSync(url, errorMessage, function(response){
		result = response.batchLog;
	});
	return result;
}




