package me.zhengjie.utils.sms;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class SmTool {
    private static String url = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";
    private static String account = "C14127691";
    private static String password = "ebfdf4de5569474e281e00e15420187b";

    public static SmResp sendSmMsg(String smContent,String tel){
        SmResp res = new SmResp();
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);
        client.getParams().setContentCharset("GBK");
        method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=GBK");
        //String content = new String("您好！【变量】邀您参加【变量】，时间：【变量】，承办单位：【变量】，地点：【变量】，联系电话：【变量】，请回复1或0[1表示参加，0表示不参加](有效期半小时)");
        NameValuePair[] data = {//提交短信
                new NameValuePair("account", account),
                new NameValuePair("password", password),
                new NameValuePair("mobile", tel),
                new NameValuePair("content", smContent),
        };
        method.setRequestBody(data);
        try {
            client.executeMethod(method);
            String SubmitResult = method.getResponseBodyAsString();
            Document doc = DocumentHelper.parseText(SubmitResult);
            Element root = doc.getRootElement();
            String code = root.elementText("code");
            String msg = root.elementText("msg");
            String smsid = root.elementText("smsid");
            res.setCode(code);
            res.setMsg(msg);
            res.setSmsid(smsid);
        } catch (HttpException e) {
            res.setCode("0");
            res.setMsg("HttpException!");
        } catch (IOException e) {
            res.setCode("0");
            res.setMsg("IOException!");
        } catch (DocumentException e) {
            res.setCode("0");
            res.setMsg("DocumentException!");
        } finally{
            method.releaseConnection();
            client.getHttpConnectionManager().closeIdleConnections(0);
        }
        return res;
    }
}
