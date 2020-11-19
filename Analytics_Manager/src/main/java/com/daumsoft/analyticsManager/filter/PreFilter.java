package com.daumsoft.analyticsManager.filter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.daumsoft.analyticsManager.restFullApi.service.SandboxRestService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
//@ConfigurationProperties(prefix="zuul")
public class PreFilter extends ZuulFilter {


    @Value("${zuul.isLocalTest}")
    private Boolean localTestSetting;
    @Value("${zuul.localTestServer}")
    private String localTestServer;
    @Value("${zuul.contextPortMap}")
    private String contextPortString;
    @Value("${zuul.contextSubpathMap}")
    private String contextSubpathString;

    private Map<String, String> contextPortMap;
    private Map<String, String> contextSubpathMap;

    @Autowired
    private SandboxRestService sandboxRestService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @PostConstruct
    public void routeMapInit (){

        contextPortMap =new HashMap<String, String>();
        contextSubpathMap =new HashMap<String, String>();


        String[] contextPorts  = contextPortString.split(";");
        for(String contextPort:contextPorts)
        {
            String[] contextAndPort=contextPort.split(":");
            contextPortMap.put(contextAndPort[0],contextAndPort[1]);
        }

        String[] contextSubpaths = contextSubpathString.split(";");
        for(String contextSubpath:contextSubpaths)
        {
            String[] contextAndSubpath=contextSubpath.split(":");
            contextSubpathMap.put(contextAndSubpath[0],contextAndSubpath[1]);
        }


        log.debug("[PreFilter] routeMapInit contextPortMap : " + contextPortMap);
        log.debug("[PreFilter] routeMapInit contextSubpathMap : " + contextSubpathMap);

    }

    @Override
    public Object run() throws ZuulException {

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();


        log.debug("[PreFilter] run request : " + request);

        HttpSession session = request.getSession();
        Map<String, String> params=ctx.getZuulRequestHeaders();

        String uri=request.getRequestURI();
        String proxyPrefixPath=uri.substring(0,ordinalIndexOf(uri,"/", 2));
        int requestPort=request.getServerPort();


        log.debug("[PreFilter] run uri : " + uri);
        log.debug("[PreFilter] run proxyPrefixPath : " + proxyPrefixPath);
        log.debug("[PreFilter] run requestPort : " + requestPort);

        String[] uris=request.getRequestURI().split("\\/");
        String module=uris[2];
        log.debug("[PreFilter] run module : " + module);

        //1. 전달 받은 정보를  출력
        log.debug("Request Param Received  Information---------------------------");
//        for(String key:params.keySet()){
//            log.debug(key+" : "+params.get(key));
//         }
//
//        Enumeration enumForParam = request.getParameterNames();
//
//        while(enumForParam.hasMoreElements())
//        {
//            Object obj = enumForParam.nextElement();
//            String fieldName = (String) obj;
//            String fieldValue = request.getParameter(fieldName);
//            log.debug(fieldName + " : " + fieldValue + "<br>");
//        }
//

        log.debug("scheme : "+request.getScheme());
        log.debug("requestURI : "+uri);
        log.debug("module : "+module);


//        Cookie[] cookies = request.getCookies();
//
//        for(int i=0; i<cookies.length;i++){
//            log.debug("Cookies : "+cookies[i].getName()+" : "+cookies[i].getValue());
//        }



        log.debug("----------------------------");

        // 2. Forward 할 정보를 Context에 추가함
        // 2.1. Parameter에 x-forwarded-prefix 값을 추가
        params.put("x-forwarded-prefix",proxyPrefixPath);


        // 2.2. Forward 정보를 채움
        // 2.2.1. Session에서 정보를 가져옴
        String instanceId= (String)session.getAttribute("instancePk");
        String userId=(String)session.getAttribute("userId");
        String userRole=(String)session.getAttribute("userRole");


        // 2.2.2. Forward 할 IP를 디비에서 가져옴
        Integer instanceIdNum=-1;
        String forwardIp="";

        if(localTestSetting!=null && localTestSetting){
            forwardIp=localTestServer;
        }

        if(instanceId!=null&&instanceId.trim().length()>0) {
            instanceIdNum=Integer.valueOf(instanceId.trim());
        }else{
            log.info("instancePk가"+instanceId+" Session에 정상적으로 입력되지 않았습니다.");
            return null;
        }

        if(!forwardIp.equals("")){
            log.info("localTest Set : "+localTestSetting+"/"+localTestServer);
        }else if(instanceIdNum!=-1&&userId!=null&&userRole.equals("general")||userRole.equals("Analytics_User")){
            forwardIp=sandboxRestService.getPrivateIpaddressWithUserIdAndInstancetId(userId,instanceIdNum);
        }else if(instanceIdNum!=-1&&userId!=null&&(userRole.equals("admin")||userRole.equals("Analytics_Admin"))) {
            forwardIp=sandboxRestService.getPrivateIpaddressWithInstanceId(instanceIdNum);
        }else{
            log.info("instanceId:"+instanceIdNum+", userId : "+userId+", userRole : "+userRole+"세션 정보가 비정상적인 값입니다.");
            return null;
        }

        if(forwardIp==null){
            log.info("instanceId:"+instanceIdNum+", userId : "+userId+", userRole : "+userRole+"데이터베이스에 해당 정보가 인스턴스 정보가 없습니다.");
            return null;
        }

        //2.2.3. Forward 할 URL을 구성
        String forwardUrl=request.getScheme()+"://";
        if(forwardIp.equals("")){
            forwardUrl+="localhost";
        }else {
            forwardUrl+=forwardIp;
        }

        Boolean isPortSet=false;

        for(String key : contextPortMap.keySet()){
            if(module.equals(key)){
                forwardUrl+=(":"+contextPortMap.get(key));
                isPortSet=true;
                break;
            }
        }
        if(!isPortSet){
            forwardUrl+=":"+requestPort;
        }

        //2.2.4. Forward 할 URL을 설정
        try {
            ctx.setRouteHost(new URL(forwardUrl));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // 3. Forward 할 subPath 를 Context에 추가함
        String subPath=uri.substring(ordinalIndexOf(uri,"/", 2), uri.length());

        for(String key : contextSubpathMap.keySet()){
            if(uri.contains(key)){
                subPath=contextSubpathMap.get(key)+subPath;
                break;
            }
        }

        try {
            ctx.set("requestURI", subPath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //3. 포워딩 정보를 출력 하위위해서 변경된 값을 출력
        log.info("user:" +userId+" / routing : "+forwardUrl);
        log.info("uri:" +request.getRequestURI());
        log.info("subPath:" +subPath);

        return null;
    }

    public int ordinalIndexOf(String str, String substr, int n) {
        int pos = -1;
        do {
            pos = str.indexOf(substr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }

}
