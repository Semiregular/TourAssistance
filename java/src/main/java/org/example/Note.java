package org.example;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/*id从1开始*/

@RestController
@RequestMapping("/note")
public class Note {
    public NoteMessage[] saved;
    public int type=0;
    public int idRem=-1;
    public String NoteData="/home/pro/tour/data/NoteMessage/";
    @PostMapping("/get/read")
    public JSONObject Read_note(@RequestBody String input)
    {
        JSONObject inputJson= JSONObject.parseObject(input);
        JSONObject output=new JSONObject();
        JSONArray array=new JSONArray();
        int note_id=inputJson.getInteger("id");

        NoteMessage note=Read_noteMessage(note_id);
        note.view++;
        Write(note);
        output.put("id",note_id);
        output.put("name",note.name);
        output.put("title",note.title);
        output.put("content",note.content);
        output.put("timeCreated",note.timeCreated);
        output.put("timeModified",note.timeModified);
        output.put("view",note.view);
        for (int i = 0; i < note.tag.length; i++) {
            array.add(i,note.tag[i]);
        }
        output.put("tag",array);
        return output;
    }

    @PostMapping("/insert")
    public JSONObject insert(@RequestBody String input)
    {
        JSONObject inputJson= JSONObject.parseObject(input);
        JSONArray array;
        int id;
        NoteMessage ins=new NoteMessage();

        Path path = Paths.get(NoteData+"num.txt");
        List<String> lines;
        try {
            JSONObject json;
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            String content = String.join(System.lineSeparator(), lines);
            json= JSONObject.parseObject(content);
            id=json.getInteger("num")+1;
            json=new JSONObject();
            json.put("num",id);
            Files.write(Paths.get(NoteData+"num.txt"),json.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ins.id=id;
        ins.name=inputJson.getString("name");
        ins.uid=inputJson.getInteger("uid");
        ins.pid=inputJson.getInteger("pid");
        ins.title=inputJson.getString("title");
        ins.content=inputJson.getString("content");
        ins.timeCreated= ins.timeModified=inputJson.getString("time");
        array=inputJson.getJSONArray("tag");
        ins.tag=new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            ins.tag[i]=array.getString(i);
        }
        ins.view=0;
        Write(ins);

        Point.all_place[ins.pid].note_num++;
        Point.all_place[ins.pid].noteId=Arrays.copyOf(Point.all_place[ins.pid].noteId,Point.all_place[ins.pid].noteId.length+1);
        Point.all_place[ins.pid].noteId[Point.all_place[ins.pid].noteId.length-1]=id;
        Point.point_save();

        User.all_user[ins.uid-100000].noteId=Arrays.copyOf(User.all_user[ins.uid-100000].noteId,User.all_user[ins.uid-100000].noteId.length+1);
        User.all_user[ins.uid-100000].noteId[User.all_user[ins.uid-100000].noteId.length-1]=id;
        User.user_save();

        type=0;
        idRem=-1;
        JSONObject json=new JSONObject();
        json.put("id",id);
        return json;
    }

    @PostMapping("/list/u")
    public JSONObject list_u(@RequestBody String input){
        JSONObject inputJson= JSONObject.parseObject(input);
        JSONObject json;
        JSONArray array=new JSONArray();
        int id = inputJson.getInteger("id");
        int tolPage;

        if(type!=1||idRem!=id)
        {
            type=1;
            idRem=id;
            saved=new NoteMessage[User.all_user[id-100000].noteId.length];
            for (int i = 0; i < User.all_user[id-100000].noteId.length; i++) {
                saved[i] = Read_noteMessage(User.all_user[id-100000].noteId[i]);
            }
            switch (inputJson.getInteger("type")) {
                case 1: ByView();
                    break;
                case 2: ByDate();
            }
        }

        tolPage=saved.length/ inputJson.getInteger("limit")+1;
        for (int i = 0; i < inputJson.getInteger("limit")&&(inputJson.getInteger("offset")+i< saved.length); i++) {
            json=new JSONObject();
            json.put("id",saved[inputJson.getInteger("offset")+i].id);
            json.put("name",saved[inputJson.getInteger("offset")+i].name);
            json.put("title",saved[inputJson.getInteger("offset")+i].title);
            if (saved[inputJson.getInteger("offset")+i].content.length()<20)
            {
                json.put("summary",saved[inputJson.getInteger("offset")+i].content);
            }
            else
            {
                json.put("summary", saved[inputJson.getInteger("offset") + i].content.substring(0, 20));
            }
            json.put("timeCreated",saved[inputJson.getInteger("offset")+i].timeCreated);
            array.add(i,json);
        }
        json=new JSONObject();
        json.put("data",array);
        json.put("tolPage",tolPage);
        return json;
    }

    @PostMapping("/list/p")
    public JSONObject list_p(@RequestBody String input){
        JSONObject inputJson= JSONObject.parseObject(input);
        JSONObject json;
        JSONArray array=new JSONArray();
        int id = inputJson.getInteger("id");
        int tolPage;

        if(type!=2||idRem!=id)
        {
            type=2;
            idRem=id;
            saved=new NoteMessage[Point.all_place[id].noteId.length];
            for (int i = 0; i < Point.all_place[id].noteId.length; i++) {
                saved[i] = Read_noteMessage(Point.all_place[id].noteId[i]);
            }
            switch (inputJson.getInteger("type")) {
                case 1: ByView();
                    break;
                case 2: ByDate();
            }
        }

        tolPage=saved.length/ inputJson.getInteger("limit")+1;
        for (int i = 0; i < inputJson.getInteger("limit")&&(inputJson.getInteger("offset")+i< saved.length); i++) {
            json=new JSONObject();
            json.put("id",saved[inputJson.getInteger("offset")+i].id);
            json.put("name",saved[inputJson.getInteger("offset")+i].name);
            json.put("title",saved[inputJson.getInteger("offset")+i].title);
            if (saved[inputJson.getInteger("offset")+i].content.length()<20)
            {
                json.put("summary",saved[inputJson.getInteger("offset")+i].content);
            }
            else
            {
                json.put("summary", saved[inputJson.getInteger("offset") + i].content.substring(0, 20));
            }
            json.put("timeCreated",saved[inputJson.getInteger("offset")+i].timeCreated);
            array.add(i,json);
        }
        json=new JSONObject();
        json.put("data",array);
        json.put("tolPage",tolPage);
        return json;
    }

    public NoteMessage Read_noteMessage(int note_id)
    {
        NoteMessage ret=new NoteMessage();
        JSONArray array;
        JSONObject json;
        Path path = Paths.get(NoteData+note_id+".txt");
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String content = String.join(System.lineSeparator(), lines);
        content=DeflaterUtils.unzip(content);//调用解压函数
        json= JSONObject.parseObject(content);

        ret.id=note_id;
        ret.name=json.getString("name");
        ret.title=json.getString("title");
        ret.content=json.getString("content");
        ret.view=json.getInteger("view");
        ret.timeCreated=json.getString("timeCreated");
        ret.timeModified=json.getString("timeModified");
        ret.uid=json.getInteger("uid");
        ret.pid=json.getInteger("pid");
        array=json.getJSONArray("tag");
        ret.tag=new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            ret.tag[i]=array.getString(i);
        }
        return ret;
    }

    public void Write(NoteMessage input)
    {
        JSONObject json=new JSONObject();
        JSONArray array=new JSONArray();
        json.put("id",input.id);
        json.put("pid",input.pid);
        json.put("uid",input.uid);
        json.put("name",input.name);
        json.put("view",input.view);
        json.put("content",input.content);
        json.put("title",input.title);
        json.put("timeCreated",input.timeCreated);
        json.put("timeModified",input.timeModified);
        for (int i = 0; i < input.tag.length; i++) {
            array.add(input.tag[i]);
        }
        json.put("tag",array);
        try {
            Files.write(Paths.get(NoteData+input.id+".txt"), DeflaterUtils.zip(json.toString()).getBytes());
        } catch (IOException e) {
            File newFile=new File(NoteData+input.id+".txt");
            try {
                newFile.createNewFile();
                Files.write(Paths.get(NoteData+input.id+".txt"),DeflaterUtils.zip(json.toString()).getBytes());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void ByView()
    {
        int rem;
        int rem_view;
        NoteMessage mid=new NoteMessage();
        for (int i = 0; i < saved.length; i++) {
            rem=0;
            rem_view=saved[0].view;
            for (int j = 1; j < saved.length-i; j++) {
                if (rem_view > saved[j].view) {
                    rem=j;
                    rem_view=saved[j].view;
                }
            }
            mid.Copy(saved[rem]);
            saved[rem].Copy(saved[saved.length-i-1]);
            saved[saved.length-i-1].Copy(mid);
        }
    }//view大的在0

    public void ByDate()
    {
        int rem;
        String rem_date;
        NoteMessage mid=new NoteMessage();
        for (int i = 0; i < saved.length; i++) {
            rem=0;
            rem_date=saved[0].timeModified;
            for (int j = 1; j < saved.length-i; j++) {
                if (Integer.parseInt(rem_date.substring(0,4))>Integer.parseInt(saved[j].timeModified.substring(0,4))||Integer.parseInt(rem_date.substring(5,7))>Integer.parseInt(saved[j].timeModified.substring(5,7))||Integer.parseInt(rem_date.substring(8,10))>Integer.parseInt(saved[j].timeModified.substring(8,10))||Integer.parseInt(rem_date.substring(11,13))>Integer.parseInt(saved[j].timeModified.substring(11,13))||Integer.parseInt(rem_date.substring(14,16))>Integer.parseInt(saved[j].timeModified.substring(14,16))||Integer.parseInt(rem_date.substring(17,19))>Integer.parseInt(saved[j].timeModified.substring(17,19))) {
                    rem=j;
                    rem_date=saved[j].timeModified;
                }
            }
            mid.Copy(saved[rem]);
            saved[rem].Copy(saved[saved.length-i-1]);
            saved[saved.length-i-1].Copy(mid);
        }
    }//时间新的在0
    @PostMapping("/update")
    public String update(@RequestBody String input)
    {
        JSONObject inputJson= JSONObject.parseObject(input);
        JSONArray array;
        int id=inputJson.getInteger("id");
        NoteMessage exchange=Read_noteMessage(id);
        exchange.title=inputJson.getString("title");
        exchange.content=inputJson.getString("content");
        exchange.timeModified=inputJson.getString("time");
        array=inputJson.getJSONArray("tag");
        exchange.tag=new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            exchange.tag[i]= array.getString(i);
        }
        Write(exchange);
        return "{1}";
    }
}
