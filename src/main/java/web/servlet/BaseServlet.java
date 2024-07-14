package web.servlet;

import com.google.gson.Gson;
import com.yc.dao.DBHelper;
import model.JsonModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class BaseServlet extends HttpServlet {
    protected DBHelper db = new DBHelper();

    /** 从c中找出  名字为  methodName的方法。。。**/
    private <T> Method findSetMethod(String methodName, Class<T> c) {
        Method[] ms=c.getDeclaredMethods();
        for(   Method m:ms ){
            if(  methodName.equals( m.getName() )){
                return m;
            }
        }
        return null;
    }
    protected <T> T parseObjectFromRequest(HttpServletRequest request, Class<T> cls) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        //创建T
        T t = cls.newInstance();
        //取出request中所有的参数
        Map<String,String[]> parameterMap = request.getParameterMap();
        for (   Map.Entry<String,String[]  > entry:parameterMap.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue()[0]; //只考虑一个的情况
            //到cls中找  "set"+key
            String methodname = "set"+key.substring(0,1).toUpperCase()+key.substring(1);
            Method setMethod = findSetMethod(  methodname , cls );
            if (  setMethod==null ){
                continue;
            }
            //System.out.println(   "当前属性为:"+ key+",值为:"+value+",对应的方法为:"+ setMethod  );
            //激活此方法, 设置值进去
            // 取出  setMethod中的参数类型，进行判断，看它是哪种
            Class parameterTypeClass=setMethod.getParameterTypes()[0];
            String parameterTypeName=parameterTypeClass.getName();
            if(  "int".equals(parameterTypeName) || "java.lang.Integer".equals(parameterTypeName)){
                int p=Integer.parseInt( value.toString());
                setMethod.invoke(t, p);
            }else  if(  "float".equals(parameterTypeName) || "java.lang.Float".equals(parameterTypeName)){
                float p=Float.parseFloat( value.toString());
                setMethod.invoke(t, p);
            }else  if(  "double".equals(parameterTypeName) || "java.lang.Double".equals(parameterTypeName)){
                double p=Double.parseDouble( value.toString());
                setMethod.invoke(t, p);
            }else  if(  "byte".equals(parameterTypeName) || "java.lang.Byte".equals(parameterTypeName)){
                byte p=Byte.parseByte( value.toString());
                setMethod.invoke(t, p);
            }else  if(  "long".equals(parameterTypeName) || "java.lang.Long".equals(parameterTypeName)){
                long p=Long.parseLong( value.toString());
                setMethod.invoke(t, p);
            }else  if(  "short".equals(parameterTypeName) || "java.lang.Short".equals(parameterTypeName)){
                short p=Short.parseShort( value.toString());
                setMethod.invoke(t, p);
            }else {
                setMethod.invoke(t, value.toString());
            }
        }
        return t;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String op = req.getParameter("op"); //op=regFile
        JsonModel jm = new JsonModel(); //用来保存要运行后的信息  并  返回到前端
        try {
            if (op == null || "".equals(op)) {
                // out.println( "{code:0,error:'没有op参数'}"  );
                jm.setCode(0);
                jm.setError("op参数不能为空..");
                writeJson(jm,resp);
                return;
            }
            ///        反 射
            Method[] methods  = this.getClass().getDeclaredMethods();//取子类中的方法
            for (Method m:methods){
                if (  m.getName().equals(  op  )  ) {  // 判断有没有 regFile方法
                    m.invoke(this, req,  resp);//激活对应函数  regFile
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jm.setCode(0);
            jm.setError(  e.getMessage()  );
            writeJson(jm,resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        req.setCharacterEncoding("utf-8");
//        resp.setCharacterEncoding("utf-8");  // 响应流的编码
//        resp.setContentType("text/html;charset=utf-8");

        super.service(req, resp);
    }

    //*** 后端传数据到前端  关键地方   ***    以json格式传数据到前端
    protected void writeJson(  JsonModel jm , HttpServletResponse resp) throws IOException {
        resp.setContentType("text/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        Gson g = new Gson();
        out.println(  g.toJson(  jm  )); ///后端 把 运行情况 以json类型传出到前端
        out.flush();
        out.close();
    }
}
