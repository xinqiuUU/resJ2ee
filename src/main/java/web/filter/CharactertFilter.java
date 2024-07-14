package web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(value = "*.action")
public class CharactertFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //设置编码集
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");  // 响应流的编码
        response.setContentType("text/html;charset=utf-8");

        chain.doFilter(request,response);
    }
}
