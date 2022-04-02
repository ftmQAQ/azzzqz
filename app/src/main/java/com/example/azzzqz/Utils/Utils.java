package com.example.azzzqz.Utils;

import android.util.Log;

import com.example.azzzqz.Javabean.Msg;
import com.example.azzzqz.Javabean.News;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utils {
    public static OkHttpClient client=new OkHttpClient();
    /**
     * 使用okhttp访问网络，返回一个响应(Response)
     * @param url:网络地址
     * @return 响应
     */
    public static Response execute(String url){
        Request request=new Request.Builder().url(url).build();
        try {
            Response response=client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对JSON数据进行解析
     * @param data:JSON数据解析
     * @return ArrayList<News>一批列表项数据
     */
    //登录数据解析
    public static User loginparse(String data){
        User user=new User();
        try {
            JSONObject object=new JSONObject(data);
            String username=object.getString("username");
            String portrait=object.getString("portrait");
            int result=object.getInt("result");
            user.setUsername(username);
            user.setPortrait_img(portrait);
            user.setResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    //注册数据解析
    public static User registerparse(String data){
        User user=new User();
        try {
            JSONObject object=new JSONObject(data);
            String username=object.getString("username");
            int account=object.getInt("account");
            int result=object.getInt("result");
            user.setAccount(account);
            user.setUsername(username);
            user.setResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    //好友数据解析
    public static ArrayList<User> friendparse(String data){
        ArrayList<User> result=new ArrayList<>();
        try {
            JSONObject object=new JSONObject(data);
            int x=object.getInt("result");
            if(x==0){
                return null;
            }
            JSONArray object2=object.getJSONArray("data");
            for(int i=0;i<object2.length();i++){
                JSONObject objectIn=object2.getJSONObject(i);
                String username=objectIn.getString("username");
                int account=objectIn.getInt("account");
                String img=objectIn.getString("portrait");
                User user=new User();
                user.setAccount(account);
                user.setUsername(username);
                user.setPortrait_img(img);
                result.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //添加好友数据解析
    public static User addfriendparse(String data){
        User result=new User() ;
        try {
            JSONObject object=new JSONObject(data);
            int re=object.getInt("result");
            String username=object.getString("username");
            int account=object.getInt("account");
            String img=object.getString("portrait");
            result.setUsername(username);
            result.setResult(re);
            result.setAccount(account);
            result.setPortrait_img(img);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //添加好友请求去服务器数据解析
    public static ArrayList<User> addfriendrequestparse(String data){
        ArrayList<User> result=new ArrayList<>();
        try {
            Log.i("msg",data);
            JSONObject object=new JSONObject(data);
            for(int i=0;i<object.length();i++){
                JSONObject objectIn=object.getJSONObject(String.valueOf(i));
                String username=objectIn.getString("username");
                int account=objectIn.getInt("account");
                String img=objectIn.getString("portrait");
                User user=new User();
                user.setAccount(account);
                user.setUsername(username);
                user.setPortrait_img(img);
                result.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //添加好友数据解析
    public static User allowparse(String data){
        User result=new User() ;
        try {
            JSONObject object=new JSONObject(data);
            int re=object.getInt("result");
            result.setResult(re);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //获取好友用户名数据解析
    public static User getnameparse(String data){
        User result=new User() ;
        try {
            JSONObject object=new JSONObject(data);
            String name=object.getString("username");
            result.setUsername(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //获取信息数据解析
    public static ArrayList<Msg> getmsgparse(String data){
        ArrayList<Msg> result=new ArrayList<>();
        try {
            JSONObject object=new JSONObject(data);
            try{
                JSONArray object2=object.getJSONArray("data");
                for(int i=0;i<object2.length();i++){
                    JSONObject objectIn=object2.getJSONObject(i);
                    int proposer=objectIn.getInt("proposer");
                    int recipient=objectIn.getInt("recipient");
                    String msgfo=objectIn.getString("msg");
                    String date=objectIn.getString("date");
                    Msg msg=new Msg();
                    msg.setRecipient(recipient);
                    msg.setProposer(proposer);
                    msg.setMsg(msgfo);
                    msg.setDate(date);
                    msg.setType(0);
                    Log.i("msgxxx",msg.getMsg());
                    result.add(msg);
                }
            }catch (Exception ex){
                Log.i("Utiles getmsgparse","数据解析失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //发送信息解析
    public static Msg putmsgparse(String data){
        Msg msg=new Msg();
        try {
            JSONObject object=new JSONObject(data);
            int result=object.getInt("result");
            msg.setResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static ArrayList<News> newsparse(String data){
        ArrayList<News> result=new ArrayList<>();
        try {
            JSONObject object=new JSONObject(data);
            JSONObject douga=object.getJSONObject("douga");
            for(int i=0;i<douga.length();i++){
                JSONObject objectIn=douga.getJSONObject(String.valueOf(i));
                String title=objectIn.getString("title");
                String pic=objectIn.getString("pic");
                JSONObject owner=objectIn.getJSONObject("owner");
                String ownername=owner.getString("name");
                String content_url=objectIn.getString("aid");
                String tname=objectIn.getString("tname");

                News news=new News(title,pic,ownername,content_url,tname);
                result.add(news);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //手机号校验
    public static boolean  checkTel(String tel){
        Pattern p = Pattern.compile("^[1][3,4,5,7,8,9][0-9]{9}$");
        Matcher matcher = p.matcher(tel);
        return matcher.matches();
    }

    //更新手机号结果解析
    public static boolean upphone(String data){
        Boolean result=false;
        try {
            JSONObject object=new JSONObject(data);
            int upphone=object.getInt("result");
            if(upphone==1){
                result=true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //更新头像结果解析
    public static boolean upportrait(String data){
        Boolean result=false;
        try {
            JSONObject object=new JSONObject(data);
            int upportrait=object.getInt("result");
            if(upportrait==1){
                result=true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    //删除好友结果解析
    public static User updelfriend(String data){
        User result=new User();
        try {
            JSONObject object=new JSONObject(data);
            int updel=object.getInt("result");
            result.setResult(updel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //个人数据解析
    public static User userinfoparse(String data){
        User user=new User();
        try {
            Log.i("Utils",data);
            JSONObject object=new JSONObject(data);
            String sex=object.getString("sex");
            int age=object.getInt("age");
            String phone=object.getString("phone");
            String img=object.getString("portrait");
            String username=object.getString("username");
            user.setPortrait_img(img);
            user.setAge(age);
            user.setPhone(phone);
            user.setSex(sex);
            user.setUsername(username);
        }catch(JSONException e) {
            e.printStackTrace();
        }
        return user;
    }



    //头像选择
    public static int portraitselect(String text){
        if(text.equals("test")){
            return R.drawable.test;
        }else if(text.equals("test2")){
            return R.drawable.test2;
        }else if(text.equals("test3")){
            return R.drawable.test3;
        }else if(text.equals("test4")){
            return R.drawable.test4;
        }else if(text.equals("test5")){
            return R.drawable.test5;
        }else{
            return R.drawable.test;
        }
    }
}