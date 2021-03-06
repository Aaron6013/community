package com.nowcoder.community.config;

import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author wensheng
 * @create 2022-03-08 10:36 δΈε
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //ζζ
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                ).hasAnyAuthority(
                        AUTHORITY_USER,AUTHORITY_ADMIN,AUTHORITY_MODERATOR
        )
                .antMatchers("/discuss/top",
                        "/discuss/wonderful"
                        ).hasAnyAuthority(
                                AUTHORITY_MODERATOR)
                .antMatchers("/discuss/delete",
                        "/data/**")
                .hasAnyAuthority(AUTHORITY_ADMIN)
                .anyRequest().permitAll().and().csrf().disable();

        //ζιδΈε€ζΆ
        http.exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                //ζ²‘η»ιοΌιθ¦θ?€θ―
                String header = request.getHeader("x-requested-with");
                if("XMLHttpRequest".equals(header)){
                    //εΌζ­₯θ―·ζ±
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    writer.write(CommunityUtil.getJSONString(403,"δ½ θΏζ²‘ζη»ι"));
                }else {
                    response.sendRedirect(request.getContextPath() + "/login");
                }
            }
        }).accessDeniedHandler(new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                //ζιδΈε€ε€η
                String header = request.getHeader("x-requested-with");
                if("XMLHttpRequest".equals(header)){
                    //εΌζ­₯θ―·ζ±
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    writer.write(CommunityUtil.getJSONString(403,"δ½ ζ²‘ζθ?Ώι?ζ­€εθ½ηζι"));
                }else {
                    response.sendRedirect(request.getContextPath() + "/denied");
                }
            }
        });

        //securityεΊε±δΌζ¦ζͺ/logoutθ―·ζ±οΌδ½ΏεΎθͺε·±εηζ ζ³δ½Ώη¨οΌsecurityι»θ?€δΈΊfilterοΌ
        http.logout().logoutUrl("/securityLogout");
    }
}
