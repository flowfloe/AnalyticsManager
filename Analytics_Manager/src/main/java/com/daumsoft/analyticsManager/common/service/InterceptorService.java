package com.daumsoft.analyticsManager.common.service;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.daumsoft.analyticsManager.common.utils.MakeUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component
public class InterceptorService extends HandlerInterceptorAdapter{

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AuthService authService;

	@Value("${sso.isDevTest}")
	private boolean ssoIsDevTest;

	@Value("${sso.devUserRole}")
	private String ssoDevUserRole;

	@Value("${cityHub.url}")
	private String cityHubUrl;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		try {
			// modules 체크(dynamicrouting)
			String requestUrl = ""+request.getRequestURL();

      logger.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");
			logger.info("requestUrl: "+requestUrl);
      logger.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");
			if( !ssoIsDevTest ) {
				if( "http://13.125.90.2:2020/error".equals(requestUrl))	return true;

				// API Gateway
				String accessToken=null, code=null;
				String authorization = request.getHeader("Authorization");
				if( MakeUtil.isNotNullAndEmpty(authorization) && authorization.contains("bearer") ) {
					logger.info("API Gateway: "+requestUrl);
					logger.info("request.getHeader: "+request.getHeader("Authorization"));
					accessToken = authorization.replace("bearer ", "");
				}else {
					accessToken = authService.getAccessTokenFromCookie(request, requestUrl); // access_token
					code = request.getParameter("code");
					logger.info("InterceptorService - code: "+code+", token : "+accessToken);
				}


				/* 로그인시... code에 값이 들어오면 체크 */
				if( MakeUtil.isNotNullAndEmpty(code) ) {
					String responseToken = authService.getTokenByAuthorizationCode(code, requestUrl); // access_token, refresh_token
					logger.info("InterceptorService - responseToken: "+responseToken);

					if( "connect timed out".equals(responseToken) ) {
						response.setCharacterEncoding("UTF-8");
						response.setContentType("text/html; charset=UTF-8");
						PrintWriter printwriter = response.getWriter();
						printwriter.print("<script>alert('로그인 연결이 끊겼습니다. 잠시후에 시도해주세요.');</script>");
						printwriter.flush();
						printwriter.close();
						return false;
					}

					if( MakeUtil.isNotNullAndEmpty(responseToken) ) {
						JsonObject jsonToken = new JsonParser().parse(responseToken).getAsJsonObject();
						accessToken = jsonToken.get("access_token").getAsString();
						// 사용자 role 체크
						boolean userRoleCheck = authService.userRoleCheck(request, response, "", accessToken, requestUrl);
//						boolean userRoleCheck = true;
						if( !userRoleCheck ) {
							response.setCharacterEncoding("UTF-8");
							response.setContentType("text/html; charset=UTF-8");
							PrintWriter printwriter = response.getWriter();
							printwriter.print("<script>alert('사용 권한이 없습니다. 권한을 신청하세요.');</script>");
							printwriter.print("<script>location.href='"+cityHubUrl+"'</script>");
							printwriter.flush();
							printwriter.close();
							return false;
						}
						authService.cookieAddTokenByJson(request,response,responseToken,requestUrl); // token 쿠키생성
						authService.createTokenSession(responseToken, request); // token 세션생성
						authService.createUserSession(accessToken, request, response, requestUrl); // 유저 정보 세션에 저장
					}
				}

				/* 토큰 체크 */									   // 사용자 role 체크
				if( MakeUtil.isNotNullAndEmpty(accessToken) && authService.userRoleCheck(request, response, "", accessToken, requestUrl)) {
//				if( MakeUtil.isNotNullAndEmpty(accessToken) ) {
					// 토큰 유효기간 체크
					if( authService.ValidateToken(authService.getPublicKey(requestUrl), accessToken, request, response,"", requestUrl) ){

						// 세션생성
						if( request.getSession().getAttribute("accessToken") == null )
							authService.createAccessTokenSession(accessToken, request);

						if( request.getSession().getAttribute("userId") == null
								|| request.getSession().getAttribute("userRole") == null
								|| request.getSession().getAttribute("cityHubUrl") == null )
							authService.createUserSession(accessToken, request, response, requestUrl); // 유저 정보 세션에 저장

						logger.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");
						logger.info(" %%%%%%%%%%% InterceptorService - True %%%%%%%%%%% ");
						logger.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");
						return true;

					}else {
						authService.removeCookie(request, response, requestUrl);
						authService.removeSession(request);
						String integratedPortal = authService.getAuthCode(request, requestUrl);
						logger.info("InterceptorService - sendRedirect integratedPortal: "+integratedPortal);
						response.sendRedirect(integratedPortal); // 통합포탈 로그인페이지로 이동
					}

				}else {
					authService.removeCookie(request, response, requestUrl);
					authService.removeSession(request);
					String integratedPortal = authService.getAuthCode(request, requestUrl);
					logger.info("InterceptorService - sendRedirect integratedPortal: "+integratedPortal);
					response.sendRedirect(integratedPortal); // 통합포탈 로그인페이지로 이동
				}
			}else {
				if( !ssoDevUserRole.equals(request.getSession().getAttribute("userRole")) ) {
					HttpSession session = request.getSession();
					if( "Analytics_Admin".equals(ssoDevUserRole) ) {
						session.setAttribute("userId", "cityhub04");
						session.setAttribute("userRole", "Analytics_Admin");
						session.setAttribute("userNickname", "다음소프트");
						session.setAttribute("userName", "다음소프트");
					}else {
						session.setAttribute("userId", "skdisk");
						session.setAttribute("userRole", "Analytics_User");
						session.setAttribute("userNickname", "muna");
						session.setAttribute("userName", "안정현");
					}
				}
				return true;
			}


		} catch (Exception e) {
			MakeUtil.printErrorLogger(e, "preHandle Error");
			logger.error("Error : ",e);
		}
	return false;
	}

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        logger.info("Interceptor > postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception arg3) throws Exception {
//        logger.info("Interceptor > afterCompletion" );
    }
}
