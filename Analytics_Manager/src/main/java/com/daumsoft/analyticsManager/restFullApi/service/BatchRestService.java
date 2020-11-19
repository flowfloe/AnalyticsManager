package com.daumsoft.analyticsManager.restFullApi.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.daumsoft.analyticsManager.common.service.HttpService;
import com.daumsoft.analyticsManager.common.utils.FileUtil;
import com.daumsoft.analyticsManager.common.utils.MakeUtil;
import com.daumsoft.analyticsManager.common.utils.ZipUtils;
import com.daumsoft.analyticsManager.restFullApi.domain.Batch;
import com.daumsoft.analyticsManager.restFullApi.domain.Instance;
import com.daumsoft.analyticsManager.restFullApi.domain.SearchData;
import com.daumsoft.analyticsManager.restFullApi.mapper.BatchRestMapper;
import com.daumsoft.analyticsManager.restFullApi.mapper.SandboxRestMapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@SuppressWarnings("static-access")
public class BatchRestService {

	private static Logger logger = LoggerFactory.getLogger(SandboxRestService.class);

	@Autowired
	private BatchRestMapper batchRestMapper;

	@Autowired
	private SandboxRestService sandboxRestService;

	@Autowired
	private SandboxRestMapper sandboxRestMapper;


	@Autowired
	private HttpService httpService;


	@Value("${openstacApi.osUrl}")
	private String osUrl;

	@Value("${openstacApi.apiPort}")
	private String apiPort;

	@Value("${openstacApi.apiMethod}")
	private String apiMethod;

	@Value("${openstacApi.projectId}")
	private String projectId;

	@Value("${openstacApiBatch.imageRef}")
	private String imageRef;

	@Value("${openstacApiBatch.flavorRef}")
	private String flavorRef;

	@Value("${openstacApiBatch.keyName}")
	private String keyName;

	@Value("${openstacApiBatch.availabilityZone}")
	private String availabilityZone;

	@Value("${openstacApiBatch.diskConfig}")
	private String diskConfig;

	@Value("${openstacApiBatch.maxCount}")
	private String maxCount;

	@Value("${openstacApiBatch.minCount}")
	private String minCount;

	@Value("${openstacApiBatch.networks}")
	private String networks;

	@Value("${openstacApiBatch.securityGroups}")
	private String securityGroups;

	@Value("${nfs.path}")
	private String nfsPath;

	@Value("${nfs.resultPath}")
	private String nfsResultPath;

	@Value("${batchServer.url}")
	private String batchServerUrl;

	@Value("${batchServer.isDevTest}")
	private boolean batchServerIsDevTest;

	/**
	 * 배치 요청 목록 조회
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public JSONObject batchServiceRequests(String userId) throws Exception {
		JSONObject resultJson = new JSONObject();
		JSONArray jsonArr = new JSONArray();

    System.out.println("=======================================================");
    System.out.println("[BatchService] batchServiceRequests ==>" + userId);
    System.out.println("=======================================================");


		List<Map<String, Object>> list = batchRestMapper.batchServiceRequests(userId);
		for (Map<String, Object> map : list) {
			if( MakeUtil.isNotNullAndEmpty(map) )	jsonArr.add(MakeUtil.nvlJson(new JSONObject().fromObject(map)));
		}


		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		resultJson.put("batchServiceRequests", jsonArr);
		return resultJson;
	}

	/**
	 * 배치 요청 조회
	 * @param batchServiceSequencePk
	 * @return
	 * @throws Exception
	 */
	public JSONObject batchServiceRequest(Integer batchServiceRequestSequencePk) throws Exception {
		JSONObject resultJson = new JSONObject();

		Map<String, Object> detail = batchRestMapper.batchServiceRequest(batchServiceRequestSequencePk);
		if( MakeUtil.isNotNullAndEmpty(detail) )	resultJson.put("batchServiceRequest", MakeUtil.nvlJson(new JSONObject().fromObject(detail)));

		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		return resultJson;
	}

	/**
	 * 배치신청 등록
	 * @param batch
	 * @return
	 * @throws Exception
	 */
	public JSONObject batchServiceRequestsAsPost(Batch batch) throws Exception {
		JSONObject resultJson = new JSONObject();
		// 배치 명 중복 체크
		if( batchRestMapper.checkBatchName(batch) > 0 ) {
			resultJson.put("result", "fail");
			resultJson.put("type", "4100");
			resultJson.put("detail", "duplicateName");
			return resultJson;

		}else {
			// 배치신청 등록
			batchRestMapper.insertBatchServiceRequest(batch);

			Map<String, Object> detail = batchRestMapper.batchServiceRequest(batch.getBatchServiceRequestSequencePk());
			if( MakeUtil.isNotNullAndEmpty(detail) )	resultJson.put("batchServiceRequest", MakeUtil.nvlJson(new JSONObject().fromObject(detail)));

			resultJson.put("result", "success");
			resultJson.put("type", "2001");
			return resultJson;
		}
	}

	/**
	 * 배치신청 수정
	 * @param batch
	 * @return
	 * @throws Exception
	 */
	public JSONObject batchServiceRequestsAsPatch(Batch batch) throws Exception {
		JSONObject resultJson = new JSONObject();
		// 템플릿 명 중복 체크
		if( batchRestMapper.checkBatchName(batch) > 0 ) {
			resultJson.put("result", "fail");
			resultJson.put("type", "4100");
			resultJson.put("detail", "duplicateName");
			return resultJson;

		}else {
			// 배치신청 수정
			batchRestMapper.updateBatchServiceRequest(batch);

			Map<String, Object> detail = batchRestMapper.batchServiceRequest(batch.getBatchServiceRequestSequencePk());
			if( MakeUtil.isNotNullAndEmpty(detail) )	resultJson.put("batchServiceRequest", MakeUtil.nvlJson(new JSONObject().fromObject(detail)));

			resultJson.put("result", "success");
			resultJson.put("type", "2001");
			return resultJson;
		}
	}

	/**
	 * 배치 신청 삭제
	 * @param batchServiceRequestSequencePk
	 * @return
	 * @throws Exception
	 */
	public JSONObject batchServiceRequestSequencePk(Integer batchServiceRequestSequencePk) throws Exception {
		JSONObject resultJson = new JSONObject();
		// 배치 신청 수정
		Batch batch = new Batch();
		batch.setBatchServiceRequestSequencePk(batchServiceRequestSequencePk);
		batch.setDeleteFlag(true);
		batchRestMapper.updateBatchServiceRequest(batch);

		resultJson.put("result", "success");
		resultJson.put("type", "2001");
		return resultJson;
	}

	/**
	 * 배치 목록 조회
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public JSONObject batchServices(String userId) throws Exception {
		JSONObject resultJson = new JSONObject();
		JSONArray jsonArr = new JSONArray();

		List<Map<String, Object>> list = batchRestMapper.batchServices(userId);
		for (Map<String, Object> map : list) {
			if( MakeUtil.isNotNullAndEmpty(map) )	jsonArr.add(MakeUtil.nvlJson(new JSONObject().fromObject(map)));
		}

		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		resultJson.put("batchServices", jsonArr);
		return resultJson;
	}

	/**
	 * 배치 조회
	 * @param batchServiceSequencePk
	 * @return
	 * @throws Exception
	 */
	public JSONObject batchService(Integer batchServiceSequencePk) throws Exception {
		JSONObject resultJson = new JSONObject();

		Map<String, Object> detail = batchRestMapper.batchService(batchServiceSequencePk);
		if( MakeUtil.isNotNullAndEmpty(detail) )	resultJson.put("batchService", MakeUtil.nvlJson(new JSONObject().fromObject(detail)));

		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		return resultJson;
	}


	/**
	 * 배치 등록
	 * @param batch
	 * @return
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	public JSONObject batchServicesAsPost(Batch batch) throws NumberFormatException, Exception {
		JSONObject resultJson = new JSONObject();
		JSONObject httpJson = new JSONObject();

		// 배치 명 중복 체크
		if( batchRestMapper.checkBatchName(batch) > 0 ) {
			resultJson.put("result", "fail");
			resultJson.put("type", "4100");
			resultJson.put("detail", "duplicateName");
			return resultJson;

		}else {

			/* 저장될 파일 위치 생성  ex)/data/parkingarea/parking{yyyyMMddHH}.json */
			String applyDataPath = batch.getApplyDataPath();
			if( !"/".equals(applyDataPath.substring(0,1)) )
				applyDataPath = "/"+applyDataPath;
			FileUtil.mkdir(nfsResultPath + applyDataPath);


			/****************************** 학습 모듈에 배치 전송 ************************************/
			// 인스턴스 내부IP 가져오기
			String ip = sandboxRestService.getInstanceIp(batch.getSandboxInstanceSequenceFk1());

			String listUrl = ip + "/batchInfo?model_id="+batch.getModelSequenceFk4();
			httpJson = httpService.httpServiceGET(listUrl, null);

			if( "200".equals(httpJson.get("type")) ) {

				// 배치신청 업데이트
				if( MakeUtil.isNotNullAndEmpty(batch.getBatchServiceRequestSequencePk()) ) {
					Batch batchRequest = new Batch();
					batchRequest.setBatchServiceRequestSequencePk(batch.getBatchServiceRequestSequencePk());
					batchRequest.setProgressState("done");
					batchRestMapper.updateBatchServiceRequest(batchRequest);
				}

				// 배치 등록
				if( batch.getEnrollementId() == null ) batch.setEnrollementId(batch.getUserId());
				batchRestMapper.insertBatchServices(batch);


				/*************** 배치분석모듈에 BATCH_INFO 전송 *********************/
				String batchIp = sandboxRestService.getInstanceIp(batch.getBatchInstanceSequenceFk2());
				if( batchServerIsDevTest ) {
					// 배치서버 IP
					//batchIp = "http://133.186.162.212:8000/modules/analyticsModule";
				  batchIp = "http://192.168.1.219:8000/modules/analyticsModule";
				}

				listUrl = batchIp+"/batchService";
				JSONObject batchInfoParam = JSONObject.fromObject(httpJson.get("data"));
				batchInfoParam.put("BATCH_SERVICE_SEQUENCE_PK", batch.getBatchServiceSequencePk());
				JSONObject batchHttpJson = httpService.httpServicePOST(listUrl, batchInfoParam.toString(), null);
				if( "201".equals(batchHttpJson.get("type")) ) {
					logger.info("send batchModeul BATCH_INFO complete...");
//					JSONObject batchModuleJson = new JSONObject().fromObject(batchHttpJson.get("data"));
				}else {
					logger.error("Error send batchModeul BATCH_INFO..."+batchHttpJson.toString());
					throw new RuntimeException("Error send batchModeul BATCH_INFO");
				}


				/*************** 전처리파일, 학습된 모델파일 API로 가져와서 NFS에 폴더 생성후 저장 *********************/
				// 전처리
				listUrl = ip + "/preprocessedData/"+batchInfoParam.get("PREPROCESSED_DATA_SEQUENCE_FK2")+"/download";
				String filePath = nfsPath+batch.getBatchServiceSequencePk();
				String fileName = "preprocessedData_"+batch.getBatchServiceSequencePk()+".zip";
				httpService.httpServiceDownloader(listUrl, filePath, fileName);
				// zip파일 압축 풀고 zip파일 삭제
				unzipAndDelete(filePath, fileName);

				// 모델
				listUrl = ip + "/models/"+batchInfoParam.get("MODEL_SEQUENCE_FK1")+"/download";
				filePath = nfsPath+batch.getBatchServiceSequencePk();
				fileName = "models_"+batch.getBatchServiceSequencePk()+".zip";
				httpService.httpServiceDownloader(listUrl, filePath, fileName);
				// zip파일 압축 풀고 zip파일 삭제
				unzipAndDelete(filePath, fileName);
				logger.info("preprocessedData, model Files save complete...");


				/*************** 배치서버에 전송 *********************/
/*
				if( MakeUtil.isNotNullAndEmpty(batchServerUrl) ) {
					listUrl = batchServerUrl+"/batchStart/"+batch.getBatchServiceSequencePk();
					JSONObject batchServerHttpJson = httpService.httpServiceGET(listUrl, null);
					if( "200".equals(batchServerHttpJson.get("type")) ) {
						logger.info("BatchServer send complete...");
					}else {
						logger.error("Error BatchServer send error..."+batchServerHttpJson.toString());
						throw new RuntimeException("Error send batchModeul BATCH_INFO");
					}
				}
*/
				Map<String, Object> detail = batchRestMapper.batchService(batch.getBatchServiceSequencePk());
				if( MakeUtil.isNotNullAndEmpty(detail) )	resultJson.put("batchService", MakeUtil.nvlJson(new JSONObject().fromObject(detail)));

				resultJson.put("result", "success");
				resultJson.put("type", "2001");

			}else {
				resultJson.put("result", "error");
				resultJson.put("type", "5000");
			}
			return resultJson;
		}
	}

	/**
	 * 배치 수정
	 * @param batch
	 * @return
	 * @throws Exception
	 */
	public JSONObject batchServicesAsPatch(Batch batch) throws Exception {
		JSONObject resultJson = new JSONObject();
		// 템플릿 명 중복 체크
		if( batchRestMapper.checkBatchName(batch) > 0 ) {
			resultJson.put("result", "fail");
			resultJson.put("type", "4100");
			resultJson.put("detail", "duplicateName");
			return resultJson;

		}else {
			// 배치 수정
			batchRestMapper.updateBatchService(batch);
			Map<String, Object> detail = batchRestMapper.batchService(batch.getBatchServiceSequencePk());
			if( MakeUtil.isNotNullAndEmpty(detail) )	resultJson.put("batchService", MakeUtil.nvlJson(new JSONObject().fromObject(detail)));

			/*************** 배치서버에 전송 *********************/
			if( "start".equals(detail.get("BATCH_STATE")) ) {
				String listUrl = batchServerUrl+"/batchStart/"+batch.getBatchServiceSequencePk();
				if( MakeUtil.isNotNullAndEmpty(batchServerUrl) ) {
					JSONObject batchServerHttpJson = httpService.httpServiceGET(listUrl, null);
					if( "200".equals(batchServerHttpJson.get("type")) ) {
						resultJson.put("result", "success");
						resultJson.put("type", "2001");
						logger.info("BatchServer send "+batch.getBatchState()+" complete...");
					}else {
						resultJson.put("result", "fail");
						resultJson.put("type", "5000");
						resultJson.put("detail", batchServerHttpJson.get("data"));
						logger.error("Error BatchServer send "+batch.getBatchState()+" error...");
					}
				}else {
					resultJson.put("result", "success");
					resultJson.put("type", "2001");
					logger.info("BatchServer send complete...");
				}
			}else { // end
				resultJson.put("result", "success");
				resultJson.put("type", "2001");
			}

			return resultJson;
		}
	}

	/**
	 * 배치 삭제
	 * @param batchServiceSequencePk
	 * @return
	 * @throws Exception
	 */
	public JSONObject batchServicesAsDelete(Integer batchServiceSequencePk) throws Exception {
		JSONObject resultJson = new JSONObject();

		/*************** 배치서버에 전송 *********************/
		if( MakeUtil.isNotNullAndEmpty(batchServerUrl) ) {
			String listUrl = batchServerUrl+"/batchStop/"+batchServiceSequencePk;
			JSONObject batchServerHttpJson = httpService.httpServiceGET(listUrl, null);
			if( "200".equals(batchServerHttpJson.get("type")) ) {
				// 배치 수정
				Batch batch = new Batch();
				batch.setBatchServiceSequencePk(batchServiceSequencePk);
				batch.setDeleteFlag(true);
				batchRestMapper.updateBatchService(batch);
				logger.info("BatchServer send complete...");
			}else {
				throw new RuntimeException("Error send batchModeul BATCH_INFO");
			}
		}else {
			// 배치 수정
			Batch batch = new Batch();
			batch.setBatchServiceSequencePk(batchServiceSequencePk);
			batch.setDeleteFlag(true);
			batchRestMapper.updateBatchService(batch);
		}

		resultJson.put("result", "success");
		resultJson.put("type", "2001");
		return resultJson;
	}

	/**
	 * 배치서버 목록 조회
	 * @return
	 * @throws Exception
	 */
	public JSONObject batchServers() throws Exception {
		JSONObject resultJson = new JSONObject();
		JSONArray jsonArr = new JSONArray();

		List<Map<String, Object>> list = batchRestMapper.batchServers();
		for (Map<String, Object> map : list) {
			if( MakeUtil.isNotNullAndEmpty(map) )	jsonArr.add(MakeUtil.nvlJson(new JSONObject().fromObject(map)));
		}

		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		resultJson.put("batchServers", jsonArr);
		return resultJson;
	}

	/**
	 * 배치서버 생성
	 * @param batch
	 * @return
	 * @throws Exception
	 */
	public JSONObject batchServersAsPost(Instance instance) throws Exception {
		JSONObject jsonResult = new JSONObject();

		// 중복체크
		if( sandboxRestMapper.checkInstanceName(instance.getName()) > 0 ) {
			jsonResult.put("result", "fail");
			jsonResult.put("type", "4100");
			jsonResult.put("detail", "duplicateName");
			return jsonResult;

		}else {
			// 인스턴스 생성
			String url = osUrl+":"+apiPort+apiMethod+"/"+projectId+"/servers";
			String jsonMessage = "";
			JSONObject json = new JSONObject();
			JSONObject paramJson = new JSONObject();
			JSONArray jsonTempArr = new JSONArray();
			JSONObject jsonTemp = new JSONObject();

			paramJson.put("name", instance.getName());
			paramJson.put("imageRef", imageRef);  // SNAPSHOT_ID
			paramJson.put("flavorRef", flavorRef); // CloudInstanceServerId
			paramJson.put("key_name", keyName);
			paramJson.put("availability_zone", availabilityZone);
			paramJson.put("OS-DCF:diskConfig", diskConfig);
			paramJson.put("max_count", maxCount);
			paramJson.put("min_count", minCount);

			jsonTemp.put("uuid", networks);
			jsonTempArr.add(jsonTemp);
			paramJson.put("networks", jsonTempArr);

			jsonTempArr = new JSONArray();
			jsonTemp = new JSONObject();
			jsonTemp.put("name", securityGroups);
			jsonTempArr.add(jsonTemp);
			paramJson.put("security_groups", jsonTempArr);

			json.put("server", paramJson);
			jsonMessage = json.toString();
			jsonResult = httpService.httpServicePOST(url, jsonMessage, "openStack");

			// success:{"type":202,"title":"Accepted"}
			if( "202".equals(jsonResult.get("type")) ) {
				logger.info("Server creation completed... ");

				instance.setKeypairName(keyName); // 키페어 이름
				instance.setServerState("create_call"); // 서버상태
				instance.setModuleState("checking");
				instance.setAvailabilityZone(availabilityZone);// 가용구역
				instance.setCloudInstanceServerId(flavorRef);  // 서버아이디
				instance.setTemplateId(999999);
				instance.setAnalysisInstanceServerType("batch"); // 서버타입(sandbox, batch)

				/* 인스턴스 저장 */
				sandboxRestMapper.insertInstance(instance);
				logger.info("Instance insert completed...");

				/* 인스턴스 상세 저장 */
				instance.setSnapshotId(imageRef); // 스냅샷 아이디

				sandboxRestMapper.insertInstanceDetail(instance);
				logger.info("InstanceDetail insert completed...");

				Map<String, Object> detail = sandboxRestMapper.instance(instance.getInstanceSequencePk());

				jsonResult.put("batchServer", new JSONObject().fromObject(detail));
				jsonResult.put("result", "success");
				jsonResult.put("type", "2001");
				logger.info("Server infomation failed...");

			}else if( "400".equals(jsonResult.get("type")) ) { // Bad Request
				JSONObject errorJson = new JSONObject().fromObject(jsonResult.get("data"));
				errorJson = new JSONObject().fromObject(errorJson.get("badRequest"));
				String message = errorJson.get("message")+"";

				if( message.indexOf("disk is smaller than the minimum") > -1 ) { // 디스크가 이미지보다 작다
					jsonResult.put("type", "4000");
					jsonResult.put("detail", "disk is smaller than the minimum");
				}else {
					jsonResult.put("type", "5000");
					jsonResult.put("detail", jsonResult.get("data"));
				}


			}else if( "403".equals(jsonResult.get("type")) ) { // Forbidden
				JSONObject errorJson = new JSONObject().fromObject(jsonResult.get("data"));
				errorJson = new JSONObject().fromObject(errorJson.get("forbidden"));
				String message = errorJson.get("message")+"";

				if( message.indexOf("Quota exceeded for ram:") > -1 ) { // 할당 메모리 초과
					jsonResult.put("type", "4005");
					jsonResult.put("detail", "Quota exceeded for ram:");

				}else if( message.indexOf("Quota exceeded for cores:") > -1 ) { // 할당 코어 초과
					jsonResult.put("type", "4005");
					jsonResult.put("detail", "Quota exceeded for cores:");

				}else {
					jsonResult.put("type", "5000");
					jsonResult.put("detail", jsonResult.get("data"));
				}

			}else {
				jsonResult.put("detail", jsonResult.get("data"));
				jsonResult.put("type", "5000");
				logger.error("Failed to create server creation... ",jsonResult.toString());
			}
		}
		return jsonResult;
	}

	/**
	 * zip파일 압축 풀고 zip파일 삭제
	 * @param filePath
	 * @param fileName
	 * @throws Exception
	 */
	public void unzipAndDelete(String filePath, String fileName) throws Exception {
		File zipFile = new File(filePath+"\\"+fileName);
        File targetDir = new File(filePath);
        logger.info("--- unzipAndDelete unzip zipFile: "+zipFile+", targetDir: "+targetDir);
        ZipUtils.unzip(zipFile, targetDir, false);

        FileUtil.fileDelete(filePath+"\\"+fileName);
        logger.info("--- unzipAndDelete fileDelete: "+filePath+"\\"+fileName);
	}

	/**
	 * 배치 시작/정지
	 * @param batch
	 * @return
	 */
	public JSONObject batchServicesStartAndStop(Batch batch) throws Exception {
		JSONObject resultJson = new JSONObject();

		// 배치 시작/정지 수정
		batchRestMapper.updateBatchServiceUseFlag(batch);
		Map<String, Object> detail = batchRestMapper.batchService(batch.getBatchServiceSequencePk());
		if( MakeUtil.isNotNullAndEmpty(detail) )	resultJson.put("batchService", MakeUtil.nvlJson(new JSONObject().fromObject(detail)));

		/*************** 배치서버에 전송 *********************/
		String listUrl = null;
		if( batch.isUseFlag() ) {
			listUrl = batchServerUrl+"/batchStart/"+batch.getBatchServiceSequencePk();
		}else { // end
			listUrl = batchServerUrl+"/batchStop/"+batch.getBatchServiceSequencePk();
		}

		if( MakeUtil.isNotNullAndEmpty(batchServerUrl) ) {
			JSONObject batchServerHttpJson = httpService.httpServiceGET(listUrl, null);
			if( "200".equals(batchServerHttpJson.get("type")) ) {
				resultJson.put("result", "success");
				resultJson.put("type", "2001");
				logger.info("BatchServer send "+batch.getBatchState()+" complete...");
			}else {
				resultJson.put("result", "fail");
				resultJson.put("type", "5000");
				resultJson.put("detail", batchServerHttpJson.get("data"));
				logger.error("Error BatchServer send "+batch.getBatchState()+" error...");
				throw new RuntimeException("Error BatchServer send "+batch.getBatchState()+" error...");
			}
		}else {
			resultJson.put("result", "success");
			resultJson.put("type", "2001");
			logger.info("BatchServer send complete...");
		}

		return resultJson;
	}

	/**
	 * 배치 로그 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public String batchLogs(SearchData searchData) {
		JSONObject resultJson = new JSONObject();

		// sort 컬럼명 지정
		String[] columns = searchData.getColumns();
		searchData.setSSortCol(columns[searchData.getISortCol_0()]);

		List<Map<String, Object>> list = batchRestMapper.batchLogs(searchData);
		int searchTotalCount = batchRestMapper.batchLogsSearchTotalCount(searchData);
		int totalCount = batchRestMapper.batchLogsTotalCount(searchData);

		resultJson.put("aaData", list);
		resultJson.put("iTotalDisplayRecords", searchTotalCount);
		resultJson.put("iTotalRecords", totalCount);

		return resultJson.toString();
	}

	/**
	 * 배치 개별 로그 조회
	 * @param logBatchSequencePk
	 * @return
	 */
	public JSONObject batchLog(Integer logBatchSequencePk) {
		JSONObject resultJson = new JSONObject();

		Map<String, Object> detail = batchRestMapper.batchLog(logBatchSequencePk);
		if( MakeUtil.isNotNullAndEmpty(detail) )	resultJson.put("batchLog", MakeUtil.nvlJson(new JSONObject().fromObject(detail)));

		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		return resultJson;
	}

}
