package org.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/user")
public class User {
    public static int USER_NUM;//用户总数
    public static UserMessage[] all_user;
    public static String UserData="/home/pro/tour/data/all_user.txt";
    @PostMapping("/login")
    public JSONObject li(@RequestBody String Body){
        JSONObject json= JSON.parseObject(Body);
        JSONObject back=new JSONObject();
        String name=json.getString("name");
        String password=json.getString("passwd");
        for (int i = 0; i < USER_NUM; i++) {
            if (all_user[i].name.contains(name)&&all_user[i].name.length()==name.length()){
                if (all_user[i].password.contains(password)&&all_user[i].password.length()==password.length()){
                    back.put("id",all_user[i].id);
                }else{
                    back.put("Failure","WrongPassword");
                }
                return back;
            }
        }
        back.put("Failure","NoUserName");
        return back;
    }

    @PostMapping("/logout")
    public void lo(){

    }

    @PostMapping("/insert")
    public JSONObject in(@RequestBody String Body){
        JSONObject json= JSON.parseObject(Body);
        String name=json.getString("name");
        String password=json.getString("passwd");
        all_user= Arrays.copyOf(all_user,all_user.length+1);
        all_user[all_user.length-1]=new UserMessage();
        all_user[all_user.length-1].name=name;
        all_user[all_user.length-1].password=password;
        all_user[all_user.length-1].id=all_user.length+99999;
        all_user[all_user.length-1].noteId=new int[0];
        USER_NUM++;
        json=new JSONObject();
        json.put("id",all_user.length+99999);
        user_save();
        return json;
    }
    public static void user_save(){
        JSONObject json;
        JSONArray write=new JSONArray();
        JSONArray array=new JSONArray();
        for (int i = 0; i < USER_NUM; i++) {
            json=new JSONObject();
            json.put("id",all_user[i].id);
            json.put("name",all_user[i].name);
            json.put("passwd",all_user[i].password);
            for (int j = 0; j < all_user[i].noteId.length; j++) {
                write.add(j,all_user[i].noteId[j]);
            }
            json.put("user_note",write);
            array.add(json);
        }
        try {
            Files.write(Paths.get(UserData),array.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void getUserMessage() {
        JSONArray array;
        JSONArray read;
        JSONObject json;
        try {
            Path path = Paths.get(UserData);
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            String content = String.join(System.lineSeparator(), lines);
            array = JSONArray.parseArray(content);
            USER_NUM = array.size();
            all_user = new UserMessage[USER_NUM];
            for (int i = 0; i < USER_NUM; i++) {
                all_user[i] = new UserMessage();
                json = array.getJSONObject(i);
                all_user[i].id = json.getInteger("id");
                all_user[i].name = json.getString("name");
                all_user[i].password = json.getString("passwd");
                read = json.getJSONArray("user_note");
                if (read == null) {
                    all_user[i].noteId = new int[0];
                } else {
                    all_user[i].noteId = new int[read.size()];
                    for (int j = 0; j < read.size(); j++) {
                        all_user[i].noteId[j] = read.getInteger(j);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
