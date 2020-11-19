package com.daumsoft.analyticsManager.common.service;

import java.net.ConnectException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.daumsoft.analyticsManager.common.utils.MakeUtil;
import com.daumsoft.analyticsManager.restFullApi.domain.Instance;
import com.daumsoft.analyticsManager.restFullApi.mapper.SandboxRestMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component
public class Scheduler {

	private static Logger logger = LoggerFactory.getLogger(Scheduler.class);

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

	@Value("${module.tempUrl}")
	private String moduleTempUrl;

	@Value("${module.port}")
	private String modulePort;

	@Value("${module.method}")
	private String moduleMethod;

	@Value("${module.healthCheck}")
	private String healthCheck;

	@Value("${openstacApi.projectId}")
	private String projectId;

	/**
	 * 30초마다 server state가 call인  체크 후 변경
	 */
	@SuppressWarnings("static-access")
	@Scheduled(fixedDelay = 2000000)
	public void instanceSynchronization() {
		logger.info("####### instanceSynchronization Start #######");
		String serverState = "_call";

		try {
			List<Map<String, Object>> serverList = sandboxRestMapper.InstancesOfServerState(serverState);

      System.out.println("=======================================================");
      System.out.println("[instanceSynchronization] serverList ==>" + serverList);
      System.out.println("=======================================================");

			if( serverList.size() > 0 ) {
				//서버 상세조회
				String url = osUrl+":"+apiPort+apiMethod+"/"+projectId+"/servers/detail";
				JSONObject jsonResult;
				jsonResult = httpService.httpServiceGET(url, "openStack");

		    System.out.println("=======================================================");
		    System.out.println("[instanceSynchronization] url ==>" + url);
        System.out.println("[instanceSynchronization] jsonResult ==>" + jsonResult);
		    System.out.println("=======================================================");

				if( "200".equals(jsonResult.get("type")) ) {
					logger.info("####### instanceSynchronization Completing server infomation... #######");
					Instance instance = null;
					JSONArray serverJsonArr = new JSONArray();
					JSONObject serverJson = null;
					JSONObject addressesJson = null;

					JSONObject json = new JSONObject().fromObject(jsonResult.get("data"));
					serverJsonArr = new JSONArray().fromObject(json.get("servers"));

					for (int i = 0; i < serverJsonArr.size(); i++) {
						serverJson = new JSONObject().fromObject(serverJsonArr.get(i));

						for( Map<String, Object> instanceMap : serverList ) {

							// 이름이 같고 서버상태가 다를때
							if( instanceMap.get("NAME").equals(serverJson.get("name"))
									&& !instanceMap.get("SERVER_STATE").equals(serverJson.get("status")) ) {

								instance = new Instance();
								instance.setInstanceSequencePk(Integer.parseInt(""+instanceMap.get("INSTANCE_SEQUENCE_PK")));
								instance.setInstanceId(""+serverJson.get("id"));
								/*
								 * 서버상태
								 * create_call : 생성요청 / create_fail : 생성실패 / create_done : 생성완료 /
								 * start_call : 시작요청 / start_fail : 시작실패 / start_done : 시작완료 /
								 * end_call : 종료요청 / end_fail : 종료실패 / end_done : 종료완료
								 */
								switch (""+serverJson.get("status")) {
								case "ACTIVE":
									if( "start_call".equals(instanceMap.get("SERVER_STATE"))
											|| "create_call".equals(instanceMap.get("SERVER_STATE")) ) {
										instance.setServerState("start_done"); // 시작완료
									}
									break;
								case "BUILD":
									instance.setServerState("create_call"); // 생성요청
									break;
								case "SHUTOFF":
									if( "end_call".equals(instanceMap.get("SERVER_STATE")) ) {
										instance.setServerState("end_done"); // 종료완료
										instance.setModuleState("server_end"); // 모듈상태 종료
									}
									break;
								default:
									break;
								}


								// 내부 IP, 외부 IP
								if( MakeUtil.isNotNullAndEmpty(serverJson.get("addresses")) &&
										instanceMap.get("PRIVATE_IP") == null || instanceMap.get("PRIVATE_IP") == "" ){
									addressesJson = new JSONObject().fromObject(serverJson.get("addresses"));
									logger.info("####### instanceSynchronization addresses Info : "+addressesJson.toString()+" #######");
									if( MakeUtil.isNotNullAndEmpty(addressesJson.get("int-20")) ) {
										JSONArray tempArr = new JSONArray().fromObject(addressesJson.get("int-20"));
										for (int a = 0; a < tempArr.size(); a++) {
											JSONObject tempJson = new JSONObject().fromObject(tempArr.get(a));
											instance.setPrivateIp(""+tempJson.get("addr"));
										}
									}else if( MakeUtil.isNotNullAndEmpty(addressesJson.get("int-10.5.0.0")) ) {
										JSONArray tempArr = new JSONArray().fromObject(addressesJson.get("int-10.5.0.0"));
										for (int a = 0; a < tempArr.size(); a++) {
											JSONObject tempJson = new JSONObject().fromObject(tempArr.get(a));
											if( "fixed".equals(tempJson.get("OS-EXT-IPS:type")) ) {
												instance.setPrivateIp(""+tempJson.get("addr"));
											}else {
												instance.setPublicIp(""+tempJson.get("addr"));
											}
										}
									}
								}

								// 인스턴스 상태, IP 변경
								sandboxRestMapper.updateInstance(instance);
								logger.info("####### instanceSynchronization instance Change State... "+instance.toString()+" #######");
							}
						}
					}
				}
			}else {
				logger.info("####### instanceSynchronization instance have nothing to change serverState... #######");
			}
		} catch (Exception e) {
			logger.error("####### instanceSynchronization Error : ",e);
			e.printStackTrace();
		}
		logger.info("####### instanceSynchronization End #######");
	}

	/**
	 * 인스턴스 모듈상태 헬스 체크
	 */
	@Scheduled(fixedDelay = 2000000)
	public void instanceHealthCheck() {
		logger.info("$$$$$ instanceHealthCheck Start $$$$$");
		String url = null;
		String moduleState = null;
		JSONObject jsonResult = null;
		String serverState = "start_done";
		Instance instance = null;
		int instanceSequencePk = 0;

		try {
			// 인스턴스 목록 조회(시작인것들만 가져와서)
			List<Map<String, Object>> instanceList = sandboxRestMapper.InstancesOfServerState(serverState);

			System.out.println("=======================================================");
      System.out.println("[instanceHealthCheck] instanceList.size()==" + instanceList.size() + "||instanceList ==>" + instanceList);
      System.out.println("=======================================================");

			if( instanceList.size() > 0 ) {

			  System.out.println("=======================================================");
	      System.out.println("Start");
	      System.out.println("=======================================================");

				for( Map<String, Object> instanceMap : instanceList ) {
					try {
						instanceSequencePk = Integer.parseInt(""+instanceMap.get("INSTANCE_SEQUENCE_PK"));

					  System.out.println("=======================================================");
		        System.out.println("[instanceHealthCheck]instanceSequencePk==>"+ instanceSequencePk + "||"+ "PRIVATE_IP"+ instanceMap.get("PRIVATE_IP"));
		        System.out.println("=======================================================");

						if( MakeUtil.isNotNullAndEmpty(instanceMap.get("PRIVATE_IP")) ) {
							url = "http://"+instanceMap.get("PRIVATE_IP") + ":" + modulePort + moduleMethod + healthCheck;

							if( MakeUtil.isNotNullAndEmpty(moduleTempUrl) )
								url = moduleTempUrl + ":" + modulePort + moduleMethod + healthCheck;

              System.out.println("=======================================================");
              System.out.println("[instanceHealthCheck] url ==>" + url);
              System.out.println("=======================================================");

							/* 인스턴스 상태 값 변경
							 * 모듈 상태값
							 * 정상: success 상태이상: fail 분석모듈 서버 죽음: server_die 분석모듈 종료: server_end
							 */
							try {
								jsonResult = httpService.httpServiceGET(url, null);
								if( "200".equals(jsonResult.get("type")) ) {
									moduleState = "success";

									// computer_clock 값이 현시간과 3분 차이나면 ==> 상태이상
									JsonParser parser = new JsonParser();
									JsonObject json = parser.parse(""+jsonResult.get("data")).getAsJsonObject();
									json = parser.parse(""+json.get("HEALTH_INFO")).getAsJsonObject();
									String computerClock = json.get("computer_clock").getAsString().substring(0,19);
									String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

									if( MakeUtil.diffOfDate(computerClock, currentDate, "minute") > 3 ) {
										moduleState = "fail";
										logger.info("$$$$$ instanceHealthCheck fail, INSTANCE_SEQUENCE_PK: "+instanceMap.get("INSTANCE_SEQUENCE_PK")+" $$$$$");
									}

								}else {
									moduleState = "server_end";
									logger.info("$$$$$ instanceHealthCheck type: "+jsonResult.get("type")+", title: "+jsonResult.get("title")+" $$$$$");
									logger.info("$$$$$ instanceHealthCheck moduleState: server_end, INSTANCE_SEQUENCE_PK: "+instanceMap.get("INSTANCE_SEQUENCE_PK")+" $$$$$");
								}

							} catch (ConnectException e) {
								moduleState = "server_die";
								logger.info("$$$$$ instanceHealthCheck ConnectException e: "+e+" $$$$$");
								logger.info("$$$$$ instanceHealthCheck moduleState: server_die, INSTANCE_SEQUENCE_PK: "+instanceMap.get("INSTANCE_SEQUENCE_PK")+" $$$$$");
							}

							if( !moduleState.equals(""+instanceMap.get("MODULE_STATE")) ){
								instance = new Instance();
								instance.setInstanceSequencePk(instanceSequencePk);
								instance.setModuleState(moduleState);
								sandboxRestMapper.updateInstance(instance);
								logger.info("$$$$$ instanceHealthCheck updateInstance moduleState: "+moduleState+", INSTANCE_SEQUENCE_PK: "+instanceMap.get("INSTANCE_SEQUENCE_PK")+" $$$$$");

							}else {
								logger.info("$$$$$ instanceHealthCheck instance have nothing to change moduleState... $$$$$");
							}
						}

					} catch (Exception e) {
						if( e.toString().equals("java.net.SocketTimeoutException: connect timed out")) {
							try {
								instance = new Instance();
								instance.setInstanceSequencePk(instanceSequencePk);
								instance.setModuleState("server_die");
								sandboxRestMapper.updateInstance(instance);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							logger.info("$$$$$ instanceHealthCheck updateInstance moduleState: \"server_die\", INSTANCE_SEQUENCE_PK: "+instanceSequencePk+" $$$$$");
						}
						logger.error("$$$$$ instanceHealthCheck Error : ",e);
						e.printStackTrace();
					}
				}

			}else {
				logger.info("$$$$$ instanceHealthCheck instance have nothing... $$$$$");
			}
		} catch (Exception e) {
			logger.error("$$$$$ instanceHealthCheck Error : ",e);
			e.printStackTrace();
		}
		logger.info("$$$$$ instanceHealthCheck End $$$$$");
	}
}
