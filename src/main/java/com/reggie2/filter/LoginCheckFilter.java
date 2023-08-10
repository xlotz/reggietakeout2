package com.reggie2.filter;

import com.alibaba.fastjson.JSON;
import com.reggie2.common.BaseContext;
import com.reggie2.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author
 * @date 2023/8/9
 */
@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 获取本次请求的URL
        String requestURI = request.getRequestURI();

        // 定义不需要拦截的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/common/**",
                "/front/**"
        };

        // 判断是否需要处理
        boolean check = check(urls, requestURI);
        // 如果不需要处理，则放行
        if (check){
//            log.info("本次请求不需要处理, 路径:{}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        // 如果是已登录，则放行
        if (request.getSession().getAttribute("employee") !=null){
            log.info("用户已登录, id: {}", request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }
        // 如果未登录，则返回到登录页面
        response.getWriter().write(JSON.toJSONString(Result.error("NotLogin")));
    }

    /**
     * 检查路径
     * @param urls
     * @param requestURL
     * @return
     */
    public boolean check(String[] urls, String requestURL){
        for (String url: urls){
            boolean match = PATH_MATCHER.match(url, requestURL);
            if (match){
                return true;
            }
        }
        return false;
    }
}
