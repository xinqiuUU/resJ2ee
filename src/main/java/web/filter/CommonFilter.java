package web.filter;

import com.google.gson.Gson;
import model.JsonModel;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class CommonFilter implements Filter {  //适配类
    //*** 后端传数据到前端  关键地方   ***    以json格式传数据到前端
    protected void writeJson(JsonModel jm , HttpServletResponse resp) throws IOException {
        resp.setContentType("text/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        Gson g = new Gson();
        out.println(  g.toJson(  jm  )); ///后端 把 运行情况 以json类型传出到前端
        out.flush();
        out.close();
    }
}
