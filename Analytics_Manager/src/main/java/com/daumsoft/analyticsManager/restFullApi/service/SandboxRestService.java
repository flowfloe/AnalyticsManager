package com.daumsoft.analyticsManager.restFullApi.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.daumsoft.analyticsManager.common.service.HttpService;
import com.daumsoft.analyticsManager.common.utils.MakeUtil;
import com.daumsoft.analyticsManager.restFullApi.domain.Instance;
import com.daumsoft.analyticsManager.restFullApi.domain.Template;
import com.daumsoft.analyticsManager.restFullApi.mapper.BatchRestMapper;
import com.daumsoft.analyticsManager.restFullApi.mapper.ProjectRestMapper;
import com.daumsoft.analyticsManager.restFullApi.mapper.SandboxRestMapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
@SuppressWarnings("static-access")
public class SandboxRestService {

	private static Logger logger = LoggerFactory.getLogger(SandboxRestService.class);

	@Autowired
	private SandboxRestMapper sandboxRestMapper;

	@Autowired
	private HttpService httpService;

	@Autowired
	private ProjectRestService projectRestService;

	@Autowired
	private ProjectRestMapper projectRestMapper;

	@Autowired
	private BatchRestService batchRestService;

	@Autowired
	private BatchRestMapper batchRestMapper;

	@Value("${openstacApi.osUrl}")
	private String osUrl;

	@Value("${openstacApi.projectPort}")
	private String projectPort;

	@Value("${openstacApi.projectMethod}")
	private String projectMethod;

	@Value("${openstacApi.apiPort}")
	private String apiPort;

	@Value("${openstacApi.apiMethod}")
	private String apiMethod;

	@Value("${openstacApi.projectId}")
	private String projectId;

	@Value("${openstacApi.keyName}")
	private String keyName;

	@Value("${openstacApi.availabilityZone}")
	private String availabilityZone;

	@Value("${openstacApi.diskConfig}")
	private String diskConfig;

	@Value("${openstacApi.maxCount}")
	private Integer maxCount;

	@Value("${openstacApi.minCount}")
	private Integer minCount;

	@Value("${openstacApi.networks}")
	private String networks;

	@Value("${openstacApi.securityGroups}")
	private String securityGroups;

	@Value("${openstacApi.snapshotPort}")
	private String snapshotPort;

	@Value("${openstacApi.snapshotMethod}")
	private String snapshotMethod;

  @Value("${openstacApi.templateId}")
  private String templateId;

  @Value("${openstacApi.specId}")
  private String specId;

  @Value("${openstacApi.hostName}")
  private String hostName;

  @Value("${openstacApi.rootPassword}")
  private String rootPassword;

  @Value("${openstacApi.subnetAddr}")
  private String subnetAddr;

  @Value("${openstacApi.tenantId}")
  private String tenantId;

	@Value("${ntm.modelListUrl}")
	private String modelListUrl;

	@Value("${ntm.urlPrefix}")
	private String urlPrefix;

	@Value("${ntm.urlPostfix}")
	private String urlPostfix;

	@Value("${ntm.devTest}")
	private Boolean devTest;

	@Value("${ntm.devID}")
	private String devUserId;


	@Value("${module.tempUrl}")
	private String moduleTempUrl;

	@Value("${module.port}")
	private String modulePort;

	@Value("${module.method}")
	private String moduleMethod;

	@Value("${module.localFiles}")
	private String moduleLocalFiles;


	/**
	 * 샌드박스 리스트 조회
	 * @return
	 * @throws Exception
	 */
	public JSONObject instances(String userId) throws Exception{
		JSONObject resultJson = new JSONObject();
		JSONArray jsonArr = new JSONArray();

    System.out.println("=======================================================");
    System.out.println("[instances] userId ==>" + userId);
    System.out.println("=======================================================");

		List<Map<String, Object>> list = sandboxRestMapper.instances(userId);
		for (Map<String, Object> map : list) {
			if( MakeUtil.isNotNullAndEmpty(map) )	jsonArr.add(MakeUtil.nvlJson(new JSONObject().fromObject(map)));
		}

		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		resultJson.put("instances", jsonArr);


    System.out.println("=======================================================");
    System.out.println("[instances] resultJson ==>" + resultJson);
    System.out.println("=======================================================");

		return resultJson;
	}

	/**
	 * 샌드박스 개별 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public JSONObject instance(Integer instancePk) throws Exception{
		JSONObject resultJson = new JSONObject();

		Map<String, Object> detail = sandboxRestMapper.instance(instancePk);
		if( MakeUtil.isNotNullAndEmpty(detail) )	resultJson.put("instance", MakeUtil.nvlJson(new JSONObject().fromObject(detail)));

		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		return resultJson;
	}

	/**
	 * 샌드박스 사양 조회
	 * @return
	 * @throws Exception
	 */
	public JSONObject specifications() throws Exception{
		JSONObject result = null;
		String url = osUrl+":"+apiPort+apiMethod+"/"+projectId+"/flavors";

    System.out.println("=======================================================");
    System.out.println("[specifications] specifications ==>" + url);
    System.out.println("=======================================================");

		result = httpService.httpServiceGET(url, "openStack");
		result.put("result", "success");
		result.put("type", "2000");

    System.out.println("=======================================================");
    System.out.println("[specifications] result ==>" + result);
    System.out.println("[specifications] result ==>" + result.size());


    JSONObject post1Object = result.getJSONObject("data");
    JSONArray jArray = post1Object.getJSONArray("flavors");

    for (int i = 0; i < jArray.size(); i++) {
        JSONObject obj = jArray.getJSONObject(i);
        String txt_id = obj.getString("id");
        String txt_name = obj.getString("name");
        System.out.println("txt_name(" + i + "): " + txt_name);
        System.out.println("txt_id(" + i + "): " + txt_id);
    }

    System.out.println("=======================================================");



		return result;
	}


	/**
	 * 샌드박스 사양 상세조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public JSONObject specification(String serverId) throws Exception {
		JSONObject json = null;
		JSONObject resultJson = new JSONObject();
		String url = osUrl+":"+apiPort+apiMethod+"/"+projectId+"/flavors/"+serverId;
		json = httpService.httpServiceGET(url, "openStack");

		if( json.get("type").equals("200") ) {
			json = new JSONObject().fromObject(json.get("data"));
			json = new JSONObject().fromObject(json.get("flavor"));
			resultJson.put("result", "success");
			resultJson.put("type", "2000");
			resultJson.put("name", json.get("name"));
			resultJson.put("vcpus", json.get("vcpus"));
			resultJson.put("ram", json.get("ram"));
			resultJson.put("eDisk", json.get("OS-FLV-EXT-DATA:ephemeral"));
			resultJson.put("disk", json.get("disk"));

			return resultJson;
		}else {
			resultJson.put("type", "5000");
			resultJson.put("detail", json.toString());
			return resultJson;
		}
	}

	/**
	 * 샌드박스 템플릿 조회
	 * @return
	 */
	public JSONObject templates(String userId) throws Exception {
		JSONObject resultJson = new JSONObject();
		JSONArray jsonArr = new JSONArray();

		List<Map<String, Object>> list = sandboxRestMapper.templates(userId);

		for (Map<String, Object> map : list) {
			if( MakeUtil.isNotNullAndEmpty(map) ) {
				jsonArr.add(new JSONObject().fromObject(map));
			}
		}
		resultJson.put("templates", jsonArr);
		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		return resultJson;
	}

	/**
	 * 샌드박스 템플릿 상세조회
	 * @param templateId
	 * @return
	 */
	public JSONObject template(Integer templateId) throws Exception {
		JSONObject resultJson = new JSONObject();
		JSONArray jsonArr = new JSONArray();

		Map<String, Object> detail = sandboxRestMapper.template(templateId);
		if( MakeUtil.isNotNullAndEmpty(detail) )	resultJson.put("template", MakeUtil.nvlJson(new JSONObject().fromObject(detail)));

		resultJson.put("userList", jsonArr);

		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		return resultJson;
	}

	/**
	 * 샌드박스 템플릿 신청 이력 조회
	 * @return
	 */
	public JSONObject customTemplateRequests(String userId) throws Exception {
		JSONObject resultJson = new JSONObject();
		JSONArray jsonArr = new JSONArray();

		List<Map<String, Object>> list = sandboxRestMapper.customTemplateRequests(userId);

		for (Map<String, Object> map : list) {
			if( MakeUtil.isNotNullAndEmpty(map) ) {
				jsonArr.add(MakeUtil.nvlJson(new JSONObject().fromObject(map)));
			}
		}
		resultJson.put("customTemplateRequests", jsonArr);
		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		return resultJson;
	}

	/**
	 * 샌드박스 템플릿 신청 이력 개별조회
	 * @param templateId
	 * @return
	 */
	public JSONObject customTemplateRequest(Integer templateId) throws Exception {
		JSONObject resultJson = new JSONObject();

		Map<String, Object> detail = sandboxRestMapper.customTemplateRequest(templateId);
		if( MakeUtil.isNotNullAndEmpty(detail) )	resultJson.put("customTemplateRequest", MakeUtil.nvlJson(new JSONObject().fromObject(detail)));

		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		return resultJson;
	}


	/**
	 * 템플릿 허용 목록 가져오기
	 * @return
	 */
	public JSONObject availableList(String userId) throws Exception {

		if(devTest==true)
		{
			userId=devUserId;
		}

/**
#http://10.11.0.26:7995/dataservice/analysisApprovalRequests/users/{userId}/dataModel
		modelListUrl: http://172.168.2.40:7920
		#  urlPrefix: /dataservice/analysisApprovalRequests
		  urlPrefix: /dataservice/analysisApprovalRequests/users/
		  urlPostfix: /dataModel
		  devTest: true
		  devID: cityhub08

		  http://172.168.2.40:7920/dataservice/analysisApprovalRequests/users/cityhub08/dataModel
*/

		userId = "cityhub08";

		String url = modelListUrl+ urlPrefix + "/"+ userId + urlPostfix;

		System.out.println("=======================================================");
    System.out.println("[availableList] url ==>" + url);
    System.out.println("=======================================================");

		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).build();
		String resMessage = "";

		/* 서버 불안전으로 잠시 처리 */
		Response response = client.newCall(request).execute();
		resMessage =response.body().string();


    System.out.println("=======================================================");
    System.out.println("[availableList] resMessage ==>" + resMessage);
    System.out.println("=======================================================");

		JSONObject resultJson = new JSONObject();
		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		resultJson.put("availableList", new JSONArray().fromObject(resMessage));
		return resultJson;
	}

	/**
	 * 스냅샷 목록 가져오기
	 * @return
	 */
	public JSONObject snapshotList() throws Exception {
    //String url = osUrl + ":" + snapshotPort + snapshotMethod + tenantId + "/snapshots"; // Real
	  String url = osUrl + ":" + snapshotPort + snapshotMethod; // Test bed
		JSONObject resultJson = new JSONObject();
		JSONObject tempJson = new JSONObject();
		JSONObject snapshotJson = null;
		JSONArray snapshotArr = new JSONArray();
		JSONArray imagesArr = new JSONArray();
    JSONArray resultArr = new JSONArray();

    System.out.println("=======================================================");
    System.out.println("[snapshotList] url ==>" + url);
    System.out.println("=======================================================");

		JSONObject result = httpService.httpServiceGET(url, "openStack");

    System.out.println("=======================================================");
    System.out.println("[snapshotList] result ==>" + result);
    System.out.println("[snapshotList] result ==>" + result.get("data"));
    System.out.println("=======================================================");

		if( "200".equals(result.get("type")) ) {

		  result = new JSONObject().fromObject(result.get("data")); //Test bed, Original
			imagesArr = new JSONArray().fromObject(result.get("images")); //Test bed, Original
		  //imagesArr = new JSONArray().fromObject(result.get("data")); //Real

			for (int i = 0; i < imagesArr.size(); i++) {
				tempJson = new JSONObject().fromObject(imagesArr.get(i));

				if( "snapshot".equals(tempJson.get("image_type"))
				    && (""+tempJson.get("name")).contains("ANALYTICS_SANDBOX") ){
				//if (tenantId.equals(tempJson.get("image_type"))){ // Real
					snapshotJson = new JSONObject();
					snapshotJson.put("name", tempJson.get("name"));  //Test bed, Original
          //snapshotJson.put("name", "name");  //Real
					snapshotJson.put("id", tempJson.get("id"));
					snapshotArr.add(snapshotJson);
				}

		    System.out.println("=======================================================");
		    System.out.println("[snapshotList] snapshotJson ==>" + snapshotJson);
		    System.out.println("=======================================================");
			}
		}
		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		resultJson.put("snapshotList", snapshotArr);
		return resultJson;
	}


	/**
	 * 템플릿 허용 데이터 가져오기
	 * @param id
	 * @return
	 */
	public JSONObject availableDataList(String id) throws Exception {
		// 임시 데이터 생성
		JSONObject resultJson = new JSONObject();
		String availableDataList = "";
		if( id.equals("park") ) {
			availableDataList = "{\"data\":[{\"id\":\"dongtan\",\"name\":\"동탄\"},{\"id\":\"bundang\",\"name\":\"분당동\"},{\"id\":\"sunae\",\"name\":\"수내동\"}]}";
		}else if( id.equals("air") ) {
			availableDataList = "{\"data\":[{\"id\":\"jeongja\",\"name\":\"정자\"},{\"id\":\"gakseongdae\",\"name\":\"낙성대\"},{\"id\":\"kimchon\",\"name\":\"김천\"}]}";
		}else { //weather
			availableDataList = "{\"data\":[{\"id\":\"gangnam\",\"name\":\"강남\"},{\"id\":\"hannam\",\"name\":\"한남\"},{\"id\":\"busan\",\"name\":\"부산\"}]}";
		}

    System.out.println("=======================================================");
    System.out.println("[availableDataList] availableDataList ==>" + availableDataList);
    System.out.println("=======================================================");

		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		resultJson.put("availableDataList", new JSONObject().fromObject(availableDataList));
		return resultJson;
	}

	/**
	 * 샌드박스 템플릿 추가 요청
	 * @param template
	 * @throws Exception
	 */
	public void customTemplateRequestsAsPost(Template template) throws Exception {
		template.setDataSummaryToString(template.getDataSummary().toString());
		sandboxRestMapper.customTemplateRequestsAsPost(template);
	}

	/**
	 * 샌드박스 템플릿  생성요청 취소 또는 커스텀 샌드박스 관리자 승인 또는 거절 또는 완료
	 * @param templateId
	 * @return
	 */
	public void customTemplateRequestsAsPatch(Template template) throws Exception {
		sandboxRestMapper.customTemplateRequestsAsPatch(template);
	}

	/**
	 * 템플릿 생성
	 * @param template
	 */
	public JSONObject templatesAsPost(Template template) throws Exception {
		JSONObject resultJson = new JSONObject();

		// 템플릿 명 중복 체크
		if( sandboxRestMapper.checkTemplateName(template.getName()) > 0 ) {
			resultJson.put("result", "fail");
			resultJson.put("type", "4100");
			resultJson.put("detail", "duplicateName");
			return resultJson;

		}else {
			template.setDataSummaryToString(template.getDataSummary().toString());

			// 사용자의 커스텀 템플릿 진행상태 변경
			if( MakeUtil.isNotNullAndEmpty(template.getCustomTemplateId()) ) {
				template.setTemplateId(template.getCustomTemplateId());
				template.setProgressState("done");
				template.setAdminComment("생성완료");
				sandboxRestMapper.customTemplateRequestsAsPatch(template);
			}

			// 템플릿 생성
			sandboxRestMapper.templatesAsPost(template);

			// 템플릿 사용자 넣어주기
			if( !template.isPublicFlag() ) {
				template.setTemplateId(template.getANALYSIS_TEMPLATE_SEQUENCE_PK());
				List<String> userList = Arrays.asList(template.getUserIdList().split(","));
				for( String userId : userList ) {
					template.setUserId(userId.trim());
					sandboxRestMapper.templateUser(template);
				}
			}

			resultJson.put("result", "success");
			resultJson.put("type", "2001");
			return resultJson;
		}
	}

	/**
	 * 템플릿 삭제
	 * @param templateId
	 * @throws Exception
	 */
	public void templateAsDelete(Integer templateId) throws Exception {
		// 템플릿 상태 변경(삭제)
		sandboxRestMapper.templateAsDelete(templateId);

		// 템플릿 사용자 삭제
		sandboxRestMapper.deleteTemplateUser(templateId);
	}

	/**
	 * 템플릿 수정
	 * @param template
	 * @return
	 */
	public JSONObject templatesAsPatch(Template template) throws Exception {
		JSONObject resultJson = new JSONObject();

		// 템플릿 명 중복 체크
		Map<String, Object> detail = sandboxRestMapper.template(template.getTemplateId());
		if( !detail.get("NAME").equals(template.getName()) &&
				sandboxRestMapper.checkTemplateName(template.getName()) > 0 ) {
			resultJson.put("result", "fail");
			resultJson.put("type", "4100");
			resultJson.put("detail", "duplicateName");
			return resultJson;

		}else {
			template.setDataSummaryToString(template.getDataSummary().toString());

			// 템플릿 수정
			sandboxRestMapper.templatesAsPatch(template);

			// 템플릿 사용자 넣어주기
			if( !template.isPublicFlag() ) {
				// 기존 유저 삭제
				sandboxRestMapper.deleteTemplateUser(template.getTemplateId());

				// 신규 유저 등록
				List<String> userList = Arrays.asList(template.getUserIdList().split(","));
				for( String userId : userList ) {
					template.setUserId(userId);
					sandboxRestMapper.templateUser(template);
				}
			}

			resultJson.put("result", "success");
			resultJson.put("type", "2004");
			return resultJson;
		}
	}

	/**
	 * 샌드박스 생성
	 * @param instance
	 * @throws Exception
	 * @throws InterruptedException
	 */
	public JSONObject instancesAsPost(Instance instance) throws Exception{
		JSONObject jsonResult = new JSONObject();



		// 중복체크
		if( sandboxRestMapper.checkInstanceName(instance.getName()) > 0 ) {
			jsonResult.put("result", "fail");
			jsonResult.put("type", "4100");
			jsonResult.put("detail", "duplicateName");
			return jsonResult;

		}else {
			// 템플릿 데이터 가져오기
			Map<String, Object> templateDetail = sandboxRestMapper.template(instance.getTemplateId());

			// 인스턴스 생성
			String url = osUrl+":"+apiPort+apiMethod+"/"+projectId+"/servers";
			String jsonMessage = "";
			JSONObject json = new JSONObject();
			JSONObject paramJson = new JSONObject();
			JSONArray jsonTempArr = new JSONArray();
			JSONObject jsonTemp = new JSONObject();

			paramJson.put("name", instance.getName());
			paramJson.put("imageRef", templateDetail.get("SNAPSHOT_ID"));
			paramJson.put("flavorRef", instance.getCloudInstanceServerId());
			paramJson.put("key_name", keyName);
			paramJson.put("availability_zone", availabilityZone);
			paramJson.put("OS-DCF:diskConfig", diskConfig);
			paramJson.put("max_count", maxCount);
			paramJson.put("min_count", minCount);

			//------------------------------------------------------------------
			// 실운영 환경 적용
			paramJson.put("templateId", templateId);
      paramJson.put("specId", specId);
      paramJson.put("hostName", hostName);
      paramJson.put("rootPassword", rootPassword);
      paramJson.put("subnetAddr", subnetAddr);
			//------------------------------------------------------------------

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
				instance.setAvailabilityZone(availabilityZone);// 가용구역
				instance.setServerState("create_call"); // 서버상태
				instance.setModuleState("checking");
				instance.setAnalysisInstanceServerType("sandbox"); // 서버타입(sandbox, batch)

				/* 인스턴스 저장 */
				sandboxRestMapper.insertInstance(instance);
				logger.info("Instance insert completed...");

				/* 인스턴스 상세 저장 */
				instance.setDataSummaryToString(""+templateDetail.get("DATA_SUMMARY"));// 데이터 내역
				instance.setDataStartDate(""+templateDetail.get("DATA_STARTDATE"));// 데이터 시작일자
				instance.setDataEndDate(""+templateDetail.get("DATA_ENDDATE"));// 데이터 종료일자
				instance.setSnapshotId(""+templateDetail.get("SNAPSHOT_ID")); // 스냅샷 아이디

				sandboxRestMapper.insertInstanceDetail(instance);
				logger.info("InstanceDetail insert completed...");

				Map<String, Object> detail = sandboxRestMapper.instance(instance.getInstanceSequencePk());

				jsonResult.put("instance", new JSONObject().fromObject(detail));
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
	 * 샌드박스 시작/정지
	 * @param instancePk
	 * @return
	 */
	public JSONObject instanceAsPatch(int instancePk) throws Exception{
		JSONObject resultJson = new JSONObject();
		JSONObject jsonMessage = new JSONObject();
		Instance instance = new Instance();
		instance.setInstanceSequencePk(instancePk);

		Map<String, Object> detail = sandboxRestMapper.instance(instancePk);
		if( "start_done".equals(detail.get("SERVER_STATE")) ) {
			// 중지
			jsonMessage.put("os-stop", "null");
			instance.setServerState("end_call");
			instance.setModuleState("checking");

		}else if( "end_done".equals(detail.get("SERVER_STATE")) ) {
			// 시작
			jsonMessage.put("os-start", "null");
			instance.setServerState("start_call");
			instance.setModuleState("checking");
		}

		String url = osUrl+":"+apiPort+apiMethod+"/"+projectId+"/servers/"+detail.get("INSTANCE_ID")+"/action";
		resultJson = httpService.httpServicePOST(url, jsonMessage.toString(), "openStack");

		if( "202".equals(resultJson.get("type")) ){
			// instance update
			sandboxRestMapper.updateInstance(instance);
			resultJson.put("result", "success");
			resultJson.put("type", "2004");
		}

		return resultJson;
	}

	/**
	 * 샌드박스 삭제
	 * @param instancePk
	 * @return
	 * @throws Exception
	 */
	public JSONObject instanceAsDelete(int instancePk) throws Exception {
		JSONObject resultJson = new JSONObject();
		Instance instance = null;
		boolean checkDeleteInstance = false;

		Map<String, Object> detail = sandboxRestMapper.instance(instancePk);

		String url = osUrl+":"+apiPort+apiMethod+"/"+projectId+"/servers/"+detail.get("INSTANCE_ID");
		resultJson = httpService.httpServiceDELETE(url, "openStack");

		// openstact에서 이미 삭제 처리되었을 경우
		if( "404".equals(resultJson.get("type")) && "Not Found".equals(resultJson.get("title"))){
			JSONObject data = new JSONObject().fromObject(resultJson.get("data"));
			data = new JSONObject().fromObject(data.get("itemNotFound"));
			if( (""+data.get("message")).indexOf("could not be found") > -1 )
				checkDeleteInstance = true;
		}

		if( "204".equals(resultJson.get("type")) || checkDeleteInstance ){
			// instance 삭제(update)
			instance = new Instance();
			instance.setInstanceSequencePk(instancePk);
			instance.setDeleteFlag(true);;
			sandboxRestMapper.updateInstance(instance);

			if( "sandbox".equals(detail.get("ANALYSIS_INSTANCE_SERVER_TYPE")) ) {
				List<Map<String, Object>> projectList = projectRestMapper.projectsByinstancePk(instancePk);

				for( Map<String, Object> projectMap : projectList ) {
					// project 삭제(update) => 원본데이터 삭제(update) => 전처리 삭제(update) => 모델 삭제(update)
					projectRestService.projectAsDelete(Integer.parseInt(""+projectMap.get("PROJECT_SEQUENCE_PK")), "NoAPI");

				}

			}else{ // batch
				// 배치 신청 삭제
				List<Map<String, Object>> batchServiceRequestList = batchRestMapper.batchServiceRequestsByinstancePk(instancePk);
				for( Map<String, Object> batchServiceRequest : batchServiceRequestList ) {
					batchRestService.batchServiceRequestSequencePk(Integer.parseInt(""+batchServiceRequest.get("BATCH_SERVICE_REQUEST_SEQUENCE_PK")));
				}

				// 배치 삭제
				List<Map<String, Object>> batchServiceList = batchRestMapper.batchServiceByinstancePk(instancePk);
				for( Map<String, Object> batchServiceMap : batchServiceList ) {
					batchRestService.batchServicesAsDelete(Integer.parseInt(""+batchServiceMap.get("BATCH_SERVICE_SEQUENCE_PK")));
				}
			}

			resultJson.put("result", "success");
			resultJson.put("type", "2001");
		}
		return resultJson;
	}

	/**
	 * 샌드박스 로컬파일 조회
	 * @param selectedInstance
	 * @return
	 * @throws Exception
	 */
	public JSONObject instancesLocalFiles(Integer selectedInstance) throws Exception {
		JSONObject resultJson = new JSONObject();
		JSONArray fileListJson = null;

		// 인스턴스 내부IP 가져오기
		String ip = getInstanceIp(selectedInstance);
		String listUrl = ip + moduleLocalFiles;
		resultJson = httpService.httpServiceGET(listUrl, null);
		//{"type":"200","title":"OK","data":{"command":"get_list","path":"/","result":{}}}
		resultJson = new JSONObject().fromObject(resultJson.get("data"));
		if( MakeUtil.isNotNullAndEmpty(resultJson.get("result")) )
			resultJson = new JSONObject().fromObject(resultJson.get("result"));
		if( MakeUtil.isNotNullAndEmpty(resultJson.get("file_list")) )
			fileListJson = new JSONArray().fromObject(resultJson.get("file_list"));

		resultJson = new JSONObject();
		resultJson.put("localFiles", fileListJson);
		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		return resultJson;
	}

	/**
	 * 샌드박스 로컬파일 샘플 조회
	 * @param selectedInstance
	 * @param localFile
	 * @return
	 * @throws Exception
	 */
	public JSONObject instancesLocalFileSample(Integer selectedInstance, String localFile) throws Exception {
		JSONObject resultJson = new JSONObject();
		JSONObject localFileJson = null;


		// 인스턴스 내부IP 가져오기
		String ip = getInstanceIp(selectedInstance);
		String listUrl = ip + "/localFiles?path=/"+localFile+"&&command=get_sample";

		localFileJson = httpService.httpServiceGET(listUrl, null);

		resultJson = new JSONObject();
		resultJson.put("localFile", localFileJson.get("data"));
		resultJson.put("result", "success");
		resultJson.put("type", "2000");
		return resultJson;
	}


	public String getPrivateIpaddressWithUserIdAndInstancetId(String userId, Integer instanceIdNum) {
		return sandboxRestMapper.getPrivateIpaddressWithUserIdAndInstancetId(userId, instanceIdNum);
	}

	public String getPrivateIpaddressWithInstanceId(Integer instanceIdNum) {
		return sandboxRestMapper.getPrivateIpaddressWithInstanceId(instanceIdNum);
	}

	/**
	 * 샌드박스 내부IP 가져오기
	 * @param instancePk
	 * @return
	 * @throws Exception
	 */
	public String getInstanceIp(Integer instanceSequencePk) throws Exception {
		Map<String, Object> instance = sandboxRestMapper.instance(instanceSequencePk);
		String ip = "http://" + instance.get("PRIVATE_IP") + ":" + modulePort + moduleMethod;

		if( MakeUtil.isNotNullAndEmpty(moduleTempUrl) )	ip = moduleTempUrl + ":" + modulePort + moduleMethod;

		return ip;
	}
}
