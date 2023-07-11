package com.cello.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.cello.reggie.common.BaseContext;
import com.cello.reggie.common.R;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.util.AntPathMatcher;

/**
 * 检查用户是否完成登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();

        //log.info("拦截到请求：{}",requestURI);

        //不需要处理的请求路径
        String[] urls = new String []{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/frontend/**",
                "/common/**"
        };

        boolean check = check(urls, requestURI);

        //如果不需要处理，则直接放行

        if(check){
            //log.info("本次请求不需要处理:{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){

            //log.info("用户已登录，用户Id为{}",request.getSession().getAttribute("employee"));

            //设置当前登录用户的id
            Long empId = (Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        //如果未登录，返回失败相应数据
        log.info("用户未登录！");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String[] urls, String RequestURI){
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String url : urls) {
            boolean match = antPathMatcher.match(url, RequestURI);
            if (match) {
                //匹配
                return true;
            }
        }
        //不匹配
        return false;
    }
}
