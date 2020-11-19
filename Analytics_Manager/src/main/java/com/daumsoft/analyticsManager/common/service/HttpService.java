package com.daumsoft.analyticsManager.common.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.daumsoft.analyticsManager.common.utils.FileUtil;
import com.daumsoft.analyticsManager.common.utils.MakeUtil;

import net.sf.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class HttpService {

	private Logger logger = LoggerFactory.getLogger(HttpService.class);

	@Value("${openstacApi.osUrl}")
	private String osUrl;

	@Value("${openstacApi.tokenPort}")
	private String tokenPort;

	@Value("${openstacApi.tokenMethod}")
	private String tokenMethod;

  @Value("${openstacApi.rootPassword}")
  private String rootPassword;

  @Value("${openstacApi.connUserId}")
  private String connUserId;

  @Value("${openstacApi.connPassword}")
  private String connPassword;

	private String token = "temp";

	private OkHttpClient client;

	public HttpService() {
		try {
			client = new OkHttpClient();
			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            client = builder.build();

            logger.info("--- HttpService : Set client ");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("--- HttpService : "+e.toString());
		}
	}

	/**
	 * HttpService GET
	 * @param connUrl
	 * @return
	 * @throws IOException
	 */
	public JSONObject httpServiceGET(String connUrl, String option) throws Exception{
		logger.info("--- httpServiceGET "+option+" connUrl: "+connUrl+" ---");
		JSONObject result = new JSONObject();
		Request request = null;

    System.out.println("=======================================================");
    System.out.println("[httpServiceGET] token ==>" + token + "|| option ==>" + option);
    System.out.println("=======================================================");

		if( MakeUtil.isNotNullAndEmpty(option) && "openStack".equals(option) ) {
		  System.out.println("=======================================================");
	    System.out.println("[httpServiceGET] option ==>" + option);
	    System.out.println("=======================================================");
			request = new Request.Builder().url(connUrl).get().addHeader("x-auth-token", token).build();
		}else {
			request = new Request.Builder().url(connUrl).build();
		}

		String resMessage = "";

		Response response = client.newCall(request).execute();
		resMessage = response.body().string();


    System.out.println("=======================================================");
    System.out.println("[httpServiceGET] response.code() ==>" + response.code() + "|| response.message() ==>" + response.message());
    System.out.println("=======================================================");

		if( MakeUtil.isNotNullAndEmpty(option) && "openStack".equals(option) ) {
			// 토큰 만료시 재발급
			//if( "401".equals(""+response.code()) && "".equals(response.message())) { // Real
			if( "401".equals(""+response.code()) && "Unauthorized".equals(response.message())) { // Test bed
				result = openstacApiTokenAndRetry(connUrl, "GET", "", option);


		    System.out.println("=======================================================");
		    System.out.println("[httpServiceGET] result ==>" + result);
		    System.out.println("=======================================================");


				return result;
			}
		}

		result.put("type", ""+response.code());
		result.put("title", response.message());
		result.put("data", resMessage);

		response.body().close();
		logger.info("--- httpServiceGET result : "+result.toString());
		return MakeUtil.nvlJson(result);
	}

	/**
	 * httpService POST
	 * @param connUrl
	 * @param jsonMessage
	 * @return
	 * @throws IOException
	 */
	public JSONObject httpServicePOST(String connUrl, String jsonMessage, String option) throws Exception{
		logger.info("--- httpServicePOST "+option+" connUrl: "+connUrl+", jsonMessage: "+jsonMessage+" ---");
		JSONObject result = new JSONObject();
		Request okRequest = null;
		Response response = null;
		String resMessage = "";

		RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),jsonMessage.toString());
		if( MakeUtil.isNotNullAndEmpty(option) && "openStack".equals(option) ) {
			okRequest = new Request.Builder().url(connUrl).get().addHeader("x-auth-token", token).post(requestBody).build();
		}else {
			okRequest = new Request.Builder().url(connUrl).post(requestBody).build();
		}


		response = client.newCall(okRequest).execute();
		resMessage = response.body().string();

		if( MakeUtil.isNotNullAndEmpty(option) && "openStack".equals(option) ) {
			// 토큰 만료시 재발급
			if( "401".equals(""+response.code()) && "Unauthorized".equals(response.message())) {
				result = openstacApiTokenAndRetry(connUrl, "POST", jsonMessage, option);
				return result;
			}
		}


		result.put("type", ""+response.code());
		result.put("title", response.message());
		result.put("data", resMessage);

		response.body().close();
		logger.info("--- httpServicePOST result : "+result.toString());
		return MakeUtil.nvlJson(result);

	}

	/**
	 * httpService PATCH
	 * @param connUrl
	 * @param jsonMessage
	 * @param option
	 * @return
	 * @throws Exception
	 */
	public JSONObject httpServicePATCH(String connUrl, String jsonMessage, String option) throws Exception{
		logger.info("--- httpServicePATCH "+option+" connUrl: "+connUrl+", jsonMessage: "+jsonMessage+" ---");
		JSONObject result = new JSONObject();
		Request okRequest = null;
		Response response = null;
		String resMessage = "";


    System.out.println("====================================================");
    System.out.println("[httpServicePATCH] option ==>" + option);
    System.out.println("====================================================");

		RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),jsonMessage.toString());
		if( MakeUtil.isNotNullAndEmpty(option) && "openStack".equals(option) ) {
			okRequest = new Request.Builder().url(connUrl).get().addHeader("x-auth-token", token).patch(requestBody).build();
		}else {
			okRequest = new Request.Builder().url(connUrl).patch(requestBody).build();
		}


    System.out.println("====================================================");
    System.out.println("[httpServicePATCH] okRequest ==>" + okRequest);
    System.out.println("====================================================");


		response = client.newCall(okRequest).execute();
		resMessage = response.body().string();


		if( MakeUtil.isNotNullAndEmpty(option) && "openStack".equals(option) ) {
			// 토큰 만료시 재발급
			if( "401".equals(""+response.code()) && "Unauthorized".equals(response.message())) {
				result = openstacApiTokenAndRetry(connUrl, "PATCH", jsonMessage, option);
				return result;
			}
		}


		result.put("type", ""+response.code());
		result.put("title", response.message());
		result.put("data", resMessage);

		response.body().close();
		logger.info("--- httpServicePATCH result : "+result.toString());
		return MakeUtil.nvlJson(result);

	}


	/**
	 * httpService DELETE
	 * @param connUrl
	 * @return
	 * @throws Exception
	 */
	public JSONObject httpServiceDELETE(String connUrl, String option) throws Exception{
		logger.info("--- httpServiceDELETE "+option+" connUrl: "+connUrl+" ---");
		JSONObject result = new JSONObject();
		Request request = null;

		if( MakeUtil.isNotNullAndEmpty(option) && "openStack".equals(option) ) {
			request = new Request.Builder().url(connUrl).delete().addHeader("x-auth-token", token).build();
		}else {
			request = new Request.Builder().url(connUrl).delete().build();
		}

		String resMessage = "";

		Response response = client.newCall(request).execute();
		resMessage = response.body().string();

		if( MakeUtil.isNotNullAndEmpty(option) && "openStack".equals(option) ) {
			// 토큰 만료시 재발급
			if( "401".equals(""+response.code()) && "Unauthorized".equals(response.message())) {
				result = openstacApiTokenAndRetry(connUrl, "DELETE", "", option);
				return result;
			}
		}

		result.put("type", ""+response.code());
		result.put("title", response.message());
		result.put("data", resMessage);

		response.body().close();
		logger.info("--- httpServiceDELETE result : "+result.toString());
		return MakeUtil.nvlJson(result);
	}

	/**
	 * 토큰생성
	 * @param tokenUrl
	 * @param connUrl
	 * @param jsonMessage
	 * @return
	 */
	public JSONObject openstacApiTokenAndRetry(String connUrl, String method, String jsonMessage, String option) {
		JSONObject json = new JSONObject();

		String tokenUrl = osUrl+":"+tokenPort+tokenMethod;

    System.out.println("=======================================================");
    System.out.println("[openstacApiTokenAndRetry]openstacApiTokenAndRetry tokenUrl: "+tokenUrl);
    System.out.println("=======================================================");

    // Test
		String tokenParam = "{\"auth\": {\"identity\": {\"methods\": [\"password\"],\"password\": {\"user\": {\"domain\": {\"name\": \"Default\"},\"name\": \"pine\",\"password\": \"pine1234!@#$\"}}},\"scope\": {\"project\": {\"domain\": {\"name\": \"Default\"},\"name\": \"Development\"}}}}";
    // Real
    //String tokenParam = "{\"id\":\""+connUserId+"\", \"password\":\""+connPassword+"\"}";
		logger.info("openstacApiTokenAndRetry tokenUrl: "+tokenUrl);
		logger.info("openstacApiTokenAndRetry tokenParam: "+tokenParam);

    System.out.println("=======================================================");
		System.out.println("[openstacApiTokenAndRetry]openstacApiTokenAndRetry tokenUrl: "+tokenUrl);
		System.out.println("[openstacApiTokenAndRetry]openstacApiTokenAndRetry tokenParam: "+tokenParam);
    System.out.println("=======================================================");

		Request okRequest = null;
		Response response = null;
		String resMessage = "";

		try {
			RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),tokenParam.toString());

	    System.out.println("=======================================================");
	    System.out.println("[openstacApiTokenAndRetry] tokenParam.toString() ==>" + tokenParam.toString());
	    System.out.println("=======================================================");

			okRequest = new Request.Builder().url(tokenUrl).post(requestBody).build();
			response = client.newCall(okRequest).execute();
			resMessage = response.body().string();


			//JSONObject result = JSONObject.fromObject(JSONSerializer.toJSON(resMessage)); //Real

      System.out.println("=======================================================");
      System.out.println("[openstacApiTokenAndRetry] response ==>" + response);
      System.out.println("[openstacApiTokenAndRetry] resMessage ==>" + resMessage);
      System.out.println("[openstacApiTokenAndRetry] response.code() ==>" + response.code());
      System.out.println("=======================================================");

			//if( "201".equals(""+response.code()) || "202".equals(""+response.code()) ) {// Real
      if( "201".equals(""+response.code()) ) {//Test
				token = response.header("X-Subject-Token"); //Test
			  //token = (String)result.get("accessToken"); //Real
				logger.info("openstacApiTokenAndRetry token: "+token);

				if( "GET".equals(method) ) {
					json = httpServiceGET(connUrl, "openStack");

				}else if( "POST".equals(method) ) {
					json = httpServicePOST(connUrl, jsonMessage, "openStack");

				}else if( "PATCH".equals(method) ) {
					json = httpServicePATCH(connUrl, jsonMessage, "openStack");

				}else if( "DELETE".equals(method) ) {
					json = httpServiceDELETE(connUrl, "openStack");
				}

			}else {
				json.put("detail", resMessage);
			}

		} catch (MalformedURLException me) {
			me.printStackTrace(System.err);
		} catch (IOException ioe) {
			ioe.printStackTrace(System.err);
		} catch(Exception e) {
			e.printStackTrace(System.err);
		} finally {
			response.body().close();
		}

		return MakeUtil.nvlJson(json);
	}


	/**
	 * 파일 다운로드
	 * @param connUrl
	 * @param filePath
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public void httpServiceDownloader(String connUrl, String filePath, String fileName) throws Exception{
		logger.info("--- httpServiceDownloader connUrl: "+connUrl+", filePath: "+filePath+", fileName: "+fileName+" ---");
        FileOutputStream fos = null;
        InputStream is = null;
        try {
        	FileUtil.mkdir(filePath);
            fos = new FileOutputStream(filePath + "\\" + fileName);

            URL url = new URL(connUrl);
            URLConnection urlConnection = url.openConnection();
            is = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int readBytes;
            while ((readBytes = is.read(buffer)) != -1) {
                fos.write(buffer, 0, readBytes);
            }
        } finally {
            if (fos != null)	fos.close();
            if (is != null)		is.close();
        }
        logger.info("--- httpServiceDownloader compleate download!! ---");
	}


}
