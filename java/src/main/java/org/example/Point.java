package org.example;

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

/**
 * 用于对于大地图上的所有景点进行操作
 * 获取所有点的id,name,lag,lng,detail
 *
 */
@RestController
@RequestMapping("/point")
public class Point {
    public static int PLACE_NUM;//景点总数'
    public static JSONArray array_line;
    public static PlaceMessage[] all_place;
    public static String all_place_data="/home/pro/tour/data/all_place.txt";
    private int[] out;
    @GetMapping("/list/all")
    public JSONArray cas(){
        JSONObject json;
        JSONArray array;
        array=new JSONArray();
        for (int i = 1; i <= PLACE_NUM; i++) {
            json=new JSONObject();
            json.put("id",all_place[i].id);
            json.put("lat",all_place[i].lat);
            json.put("lng",all_place[i].lng);
            json.put("name",all_place[i].name);
            json.put("detail",all_place[i].message);
            json.put("type",all_place[i].type);
            array.add(json);
        }
        return array;
    }
    @GetMapping("/get/info/{id}")
    public JSONObject sa(@PathVariable int id){
        JSONObject json=new JSONObject();
        json.put("id",all_place[id].id);
        json.put("name",all_place[id].name);
        json.put("lat",all_place[id].lat);
        json.put("lng",all_place[id].lng);
        json.put("eval",all_place[id].evaluation);
        json.put("pop",all_place[id].popularity);
        json.put("type",all_place[id].type);
//        json.put("comment",all_place[id].comment_num);
        json.put("note",all_place[id].note_num);
        json.put("detail",all_place[id].message);
        return json;
    }
    @GetMapping("/list/search/{searchType}/{keyword}")
    public JSONArray sc(@PathVariable(value = "searchType") int searchType,@PathVariable(value = "keyword") String keyword){//0为按热度排列前十，1为按评价排列前十，2为按照关键字进行查询，3为按照偏好查询，4为按照热度排序out中的景点，5为按照评价排序out中的景点,十位往上为type
        int type=searchType/10;
        int mid;
        searchType=searchType%10;
        switch (searchType){
            case 0:
            case 1:{
                out=new int[10];
                int mid1,mid2;
                for (int i = 1; i <= PLACE_NUM; i++) {
                    for (int j = 0; j < 10; j++) {
                        switch (searchType){
                            case 0:if(all_place[out[j]].popularity<all_place[i].popularity) {
                                mid1=out[j];
                                out[j]=all_place[i].id;
                                j++;
                                for(;j<10;j++){
                                    mid2=out[j];
                                    out[j]=mid1;
                                    mid1=mid2;
                                }
                            }
                                break;
                            case 1:if((all_place[out[j]].evaluation[0]+all_place[out[j]].evaluation[1]+all_place[out[j]].evaluation[2]+all_place[out[j]].evaluation[3]+all_place[out[j]].evaluation[4])==0||((all_place[out[j]].evaluation[0]+all_place[out[j]].evaluation[1]*2+all_place[out[j]].evaluation[2]*3+all_place[out[j]].evaluation[3]*4+all_place[out[j]].evaluation[4]*5)/(all_place[out[j]].evaluation[0]+all_place[out[j]].evaluation[1]+all_place[out[j]].evaluation[2]+all_place[out[j]].evaluation[3]+all_place[out[j]].evaluation[4]))<((all_place[i].evaluation[0] +all_place[i].evaluation[1]*2+all_place[i].evaluation[2]*3+all_place[i].evaluation[3]*4+all_place[i].evaluation[4]*5)/(all_place[i].evaluation[0]+all_place[i].evaluation[1]+all_place[i].evaluation[2]+all_place[i].evaluation[3]+all_place[i].evaluation[4]))) {
                                mid1=out[j];
                                out[j]=all_place[i].id;
                                j++;
                                for(;j<10;j++){
                                    mid2=out[j];
                                    out[j]=mid1;
                                    mid1=mid2;
                                }
                            }
                                break;
                        }
                    }
                }
            }
                break;
            case 2:{
                out=new int[0];
                ByKeyword(keyword);
                ByPop();
            }
                break;
            case 3:{
                out =new int[0];
                ByType(type);
                ByEval();
            }
                break;
            case 4:{
                out =new int[0];
                ByType(type);
                ByPop();
            }
                break;
            case 5:{
                out =new int[0];
                ByKeyword(keyword);
                ByEval();
            }
        }
        JSONArray array=new JSONArray();
        JSONObject json;
        for (int i = 0; i < out.length; i++) {
            json=new JSONObject();
            json.put("id",all_place[out[i]].id);
            json.put("lat",all_place[out[i]].lat);
            json.put("lng",all_place[out[i]].lng);
            json.put("name",all_place[out[i]].name);
            json.put("detail",all_place[out[i]].message);
            array.add(json);
        }
        return array;
    }

    private void ByType(int type) {
        for (int i = 1; i <= PLACE_NUM; i++) {
            if (all_place[i].type== type){
                out= Arrays.copyOf(out,out.length+1);//数组扩容函数
                out[out.length-1]=i;
            }
        }
    }

    private void ByKeyword(String keyword) {
        for (int i = 1; i <= PLACE_NUM; i++) {
            if(all_place[i].name.contains(keyword))//字符串匹配函数
            {
                out= Arrays.copyOf(out,out.length+1);//数组扩容函数
                out[out.length-1]=i;
            }
        }
    }

    private void ByPop() {
        int mid;
        for (int i = 0; i <out.length-1; i++) {
            for (int j = i+1; j < out.length; j++) {
                if(all_place[out[i]].popularity<all_place[out[j]].popularity) {
                    mid=out[j];
                    out[j]=out[i];
                    out[i]=mid;
                }
            }
        }
    }

    private void ByEval() {
        int mid;
        for (int i = 0; i <out.length-1; i++) {
            for (int j = i+1; j < out.length; j++) {
                if((all_place[out[i]].evaluation[0]+all_place[out[i]].evaluation[1]*2+all_place[out[i]].evaluation[2]*3+all_place[out[i]].evaluation[3]*4+all_place[out[i]].evaluation[4]*5)<(all_place[out[j]].evaluation[0] +all_place[out[j]].evaluation[1]*2+all_place[out[j]].evaluation[2]*3+all_place[out[j]].evaluation[3]*4+all_place[out[j]].evaluation[4]*5)) {
                    mid=out[j];
                    out[j]=out[i];
                    out[i]=mid;
                }
            }
        }
    }

    @PostMapping("/route")
    public JSONObject rot(@RequestBody String input){
        JSONObject inputJson= JSONObject.parseObject(input);
        int type_by=inputJson.getInteger("type");
        JSONArray inputArray=inputJson.getJSONArray("point");
        JSONArray point=new JSONArray();
        JSONObject mid=new JSONObject();
        int[] way=new int[inputArray.size()];
        for (int i = 0; i < way.length; i++) {
            way[i]=inputArray.getInteger(i);
        }
        Min_route[] min_way=new Min_route[way.length];//用于记录最短路线，仅使用id和route[]
        for (int i = 0; i < way.length; i++) {
            min_way[i]=new Min_route();
        }

        int[] traverse=new int[PLACE_NUM +1];//标记未遍历的地点,记录第几遍被遍历到
        double[] mem=new double[PLACE_NUM +1];//用于记录从出发点至此的时间（或者距离）
        int[] before=new int[PLACE_NUM +1];//用于记录前一节点的id

        for (int ven_rem=0;ven_rem<way.length;ven_rem++){

            for (int i = 0; i <= PLACE_NUM; i++) {
                traverse[i]=0;
                mem[i]=0;
                before[i]=0;
            }

            traverse[way[ven_rem]]=1;//将当前的地点序列设置为1
            before[way[ven_rem]]=way[ven_rem];//设置起点的before数组等于自身id
            boolean flag=true;
            int passType;
            int times=1;

            while (flag) {
                flag=false;
                for(int i=1;i<traverse.length;i++) {
                    if (traverse[i]==times){
                        for (int j = 0; j<all_place[i].routeMessage.length; j++){
                            if (traverse[all_place[i].routeMessage[j].id]==0||traverse[all_place[i].routeMessage[j].id]==times+1){

                                all_place[i].routeMessage[j].time=0;
                                passType=0;//passType为0代表不可通过
                                switch (type_by%10){
                                    case 1:
                                        if (all_place[i].routeMessage[j].workCrowding!=0) {
                                            all_place[i].routeMessage[j].time = all_place[i].routeMessage[j].length / all_place[i].routeMessage[j].workCrowding / all_place[i].routeMessage[j].idealSpeed;
                                            passType=1;
                                        }
                                        break;
                                    case 2:
                                        if (all_place[i].routeMessage[j].cycleCrowding!=0) {
                                            all_place[i].routeMessage[j].time = all_place[i].routeMessage[j].length / all_place[i].routeMessage[j].cycleCrowding / all_place[i].routeMessage[j].idealSpeed;
                                            passType=2;
                                        }
                                        break;
                                    case 3:
                                        if (all_place[i].routeMessage[j].driveCrowding!=0) {
                                            all_place[i].routeMessage[j].time = all_place[i].routeMessage[j].length / all_place[i].routeMessage[j].driveCrowding / all_place[i].routeMessage[j].idealSpeed;
                                            passType=3;
                                        }
                                        break;
                                    case 4:
                                        if (all_place[i].routeMessage[j].workCrowding>all_place[i].routeMessage[j].cycleCrowding&&all_place[i].routeMessage[j].workCrowding>all_place[i].routeMessage[j].driveCrowding) {
                                            all_place[i].routeMessage[j].time = all_place[i].routeMessage[j].length / all_place[i].routeMessage[j].workCrowding / all_place[i].routeMessage[j].idealSpeed;
                                            passType=1;
                                        }
                                        else if (all_place[i].routeMessage[j].cycleCrowding>all_place[i].routeMessage[j].driveCrowding) {
                                            all_place[i].routeMessage[j].time = all_place[i].routeMessage[j].length / all_place[i].routeMessage[j].cycleCrowding / all_place[i].routeMessage[j].idealSpeed;
                                            passType=2;
                                        }
                                        else {
                                            all_place[i].routeMessage[j].time = all_place[i].routeMessage[j].length / all_place[i].routeMessage[j].driveCrowding / all_place[i].routeMessage[j].idealSpeed;
                                            passType=3;
                                        }
                                }//以相对严谨的方式计算通过道路的时间
                                all_place[i].routeMessage[j].passType = passType;
                                for (int k = 0; k < all_place[j].routeMessage.length; k++) {
                                    if (all_place[j].routeMessage[k].id == i)
                                        all_place[j].routeMessage[k].passType = passType;
                                }

                                switch (type_by/10){
                                    case 1:
                                        if (passType==0) break;
                                        else if (mem[all_place[i].routeMessage[j].id]==0||mem[all_place[i].routeMessage[j].id]>mem[i]+all_place[i].routeMessage[j].length){
                                            mem[all_place[i].routeMessage[j].id]=mem[i]+all_place[i].routeMessage[j].length;
                                            traverse[all_place[i].routeMessage[j].id]=times+1;
                                            before[all_place[i].routeMessage[j].id]=i;
                                            flag=true;
                                        }
                                        break;
                                    case 2:
                                        if (passType==0) break;
                                        else if(mem[all_place[i].routeMessage[j].id]==0||mem[all_place[i].routeMessage[j].id]>mem[i]+all_place[i].routeMessage[j].time){
                                            mem[all_place[i].routeMessage[j].id]=mem[i]+all_place[i].routeMessage[j].time;
                                            traverse[all_place[i].routeMessage[j].id]=times+1;
                                            before[all_place[i].routeMessage[j].id]=i;
                                            flag=true;
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }
                times++;
            }//计算venues[ven_rem]指向的地点从出发点到其他地点的最短时间

            min_way[ven_rem].id=way[ven_rem];
            min_way[ven_rem].shortest_way =new Way[way.length];
            for (int i = 0; i < way.length; i++) {
                min_way[ven_rem].shortest_way[i]=new Way();
                min_way[ven_rem].shortest_way[i].id=way[i];
                min_way[ven_rem].shortest_way[i].mem =mem[way[i]];
                if (traverse[way[i]]==0)
                {
                    point=new JSONArray();
                    mid=new JSONObject();
                    mid.put("point",point);
                    return mid;
                }
                min_way[ven_rem].shortest_way[i].way=new int[traverse[way[i]]-1];
                int rem_id=way[i];//暂时记录id
                for(int j=0;min_way[ven_rem].id!=rem_id;j++){
                    min_way[ven_rem].shortest_way[i].way[j]=rem_id;
                    rem_id=before[rem_id];
                }//经过地点以倒序顺序存储到way数组中
            }//将数据存入min_way中
        }//依次计算每一个id地点到后续地点的最短距离,并将数据存入min_way中
        //计算出所有场所之间的最短距离

        int[] venue_path=new int[way.length];//用于记录景点的先后顺序
        boolean[] statement=new boolean[way.length];//用于记录景点是否被加入到了路径中
        int[] path_id=new int[0];//用于输出路径
        for (int i = 0; i < way.length; i++) {
            statement[i]=false;
        }//初始化布尔数组
        int rem_min_in_venue_path;//用于记录端点
        int rem_min_venue=0;//用于记录道路
        double rem_min_length;
        venue_path[0]=way[0];
        statement[0]=true;
        venue_path[1]=way[1];
        statement[1]=true;
        if (way.length==2){
            path_id=Arrays.copyOf(path_id,min_way[1].shortest_way[0].way.length+1);
            System.arraycopy(min_way[1].shortest_way[0].way,0,path_id,0,min_way[1].shortest_way[0].way.length);
            path_id[path_id.length-1]=way[1];
        }//单项的情况
        else {
            rem_min_in_venue_path = 2;
            rem_min_length = min_way[2].shortest_way[0].mem + min_way[2].shortest_way[1].mem;
            for (int i = 3; i < way.length; i++) {
                if (rem_min_length > min_way[i].shortest_way[0].mem + min_way[i].shortest_way[1].mem) {
                    rem_min_in_venue_path = i;
                    rem_min_length = min_way[i].shortest_way[0].mem + min_way[i].shortest_way[1].mem;
                }
            }
            venue_path[2] = way[rem_min_in_venue_path];
            statement[rem_min_in_venue_path] = true;

            for (int i = 3; i < way.length; i++) {
                int rem_id_pre = 0;
                int rem_id_aft = 1;
                boolean flag = true;
                while (rem_id_pre < i) {
                    for (int j = 2; j < way.length; j++) {
                        if (!statement[j] && (rem_min_length > min_way[j].shortest_way[rem_id_pre].mem + min_way[j].shortest_way[rem_id_aft].mem || flag)) {
                            flag = false;
                            rem_min_in_venue_path = j;
                            rem_min_venue = rem_id_pre;
                            rem_min_length = min_way[j].shortest_way[rem_id_pre].mem + min_way[j].shortest_way[rem_id_aft].mem;
                        }
                    }//每次循环计算一个节点
                    rem_id_pre++;
                    rem_id_aft++;
                    if (rem_id_aft == i) {
                        rem_id_aft = 0;
                    }
                }//每次循环计算venue_path的一条道路,最终计算出加入后增量最少的点
                rem_id_pre = venue_path[rem_min_venue + 1];
                venue_path[rem_min_venue + 1] = way[rem_min_in_venue_path];
                statement[rem_min_in_venue_path] = true;
                for (int j = rem_min_venue + 2; j <= i; j++) {
                    rem_id_aft = venue_path[j];
                    venue_path[j] = rem_id_pre;
                    rem_id_pre = rem_id_aft;
                }//插入节点
            }//每次循环插入一个节点到环路中
            path_id = Arrays.copyOf(path_id, min_way[way.length - 1].shortest_way[0].way.length);
            System.arraycopy(min_way[way.length - 1].shortest_way[0].way, 0, path_id, 0, min_way[way.length - 1].shortest_way[0].way.length);
            for (int i = way.length - 1; i > 0; i--) {
                path_id = Arrays.copyOf(path_id, path_id.length + min_way[i - 1].shortest_way[i].way.length);
                System.arraycopy(min_way[i - 1].shortest_way[i].way, 0, path_id, path_id.length-min_way[i - 1].shortest_way[i].way.length, min_way[i - 1].shortest_way[i].way.length);
            }
            path_id=Arrays.copyOf(path_id,path_id.length+1);
            path_id[path_id.length-1]=way[0];
        }
        //输出最短路径
        //讲path_id转换为所需数据
        int idTest=0;
        double tolTime=0;
        JSONArray line=new JSONArray();
        JSONArray array_mid;
        mid.put("id",all_place[path_id[0]].id);
        mid.put("name",all_place[path_id[0]].name);
        mid.put("lat",all_place[path_id[0]].lat);
        mid.put("lng",all_place[path_id[0]].lng);
        point.add(mid);
        for (int i = 1; i < path_id.length; i++) {
            for (int j = 0; j < all_place[path_id[i-1]].routeMessage.length; j++) {
                if (all_place[path_id[i-1]].routeMessage[j].id==path_id[i]){
                    idTest=j;
                }
            }
            mid=new JSONObject();
            mid.put("routeId",all_place[path_id[i-1]].routeMessage[idTest].routeId);
            mid.put("name",all_place[path_id[i-1]].routeMessage[idTest].name);
            mid.put("length",all_place[path_id[i-1]].routeMessage[idTest].length);
            mid.put("time",all_place[path_id[i-1]].routeMessage[idTest].time);
            mid.put("passType",all_place[path_id[i-1]].routeMessage[idTest].passType);
            tolTime+=all_place[path_id[i-1]].routeMessage[idTest].time;
            line.add(mid);
            array_mid=all_place[path_id[i-1]].routeMessage[idTest].way;
            for (int j = 1; j < array_mid.size(); j++) {
                point.add(array_mid.getJSONObject(j));
            }
        }
        mid=new JSONObject();
        mid.put("tolTime",tolTime);
        mid.put("point",point);
        mid.put("line",line);
        return mid;
    }
    //返回最短路径地点的id排序，输入地点id，第一个id为起始地点，type中为规划策略，1为最短路径，2为最短时间，3为自行车的最短时间，4为电瓶车的最短时间
    //!!!景点必须要同时被自行车道和电瓶车道连接在一起!!!
    public static void point_save(){
        JSONObject json;
        JSONArray array=new JSONArray();
        JSONArray array_mid;
        for (int i = 1; i <= PLACE_NUM; i++) {
            json=new JSONObject();
            json.put("id",all_place[i].id);
            json.put("name",all_place[i].name);
            json.put("lat",all_place[i].lat);
            json.put("lng",all_place[i].lng);
            array_mid=new JSONArray();
            for (int j = 0; j < all_place[i].evaluation.length; j++) {
                array_mid.add(j,all_place[i].evaluation[j]);
            }
            json.put("eval",array_mid);
            array_mid=new JSONArray();
            for (int j = 0; j < all_place[i].noteId.length; j++) {
                array_mid.add(j,all_place[i].noteId[j]);
            }
            json.put("noteId",array_mid);
            json.put("pop",all_place[i].popularity);
            json.put("type",all_place[i].type);
            json.put("note",all_place[i].note_num);
            json.put("detail",all_place[i].message);
            array.add(json);
        }
        json=new JSONObject();
        json.put("point",array);
        json.put("line",array_line);
        try {
            Files.write(Paths.get(all_place_data),json.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void getPointMessage() {
        JSONObject json;
        JSONObject mid;
        JSONArray array;
        JSONArray array_mid;
        try {
            Path path = Paths.get(all_place_data);
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            String content = String.join(System.lineSeparator(), lines);
            json=JSONObject.parseObject(content);
            array=json.getJSONArray("point");
            PLACE_NUM =array.size();
            all_place=new PlaceMessage[PLACE_NUM +1];
            for (int i = 1; i <= PLACE_NUM; i++) {
                all_place[i]=new PlaceMessage();
            }
            all_place[0]=new PlaceMessage();
            all_place[0].id=0;
            all_place[0].evaluation= new int[]{0,0,0,0,0};
            all_place[0].popularity=0;
            int rem_id;
            for (int i = 0; i < array.size(); i++) {
                mid=array.getJSONObject(i);
                rem_id=mid.getInteger("id");
                all_place[rem_id].setId(rem_id);
                all_place[rem_id].setType(mid.getInteger("type"));
                all_place[rem_id].setLat(mid.getDouble("lat"));
                all_place[rem_id].setLng(mid.getDouble("lng"));
                all_place[rem_id].setName(mid.getString("name"));
                all_place[rem_id].setMessage(mid.getString("detail"));
                all_place[rem_id].setPopularity(mid.getInteger("pop"));
                array_mid=mid.getJSONArray("noteId");
                all_place[rem_id].setNoteId(array_mid);
                array_mid=mid.getJSONArray("eval");
                all_place[rem_id].setEvaluation(array_mid.getInteger(0),array_mid.getInteger(1),array_mid.getInteger(2),array_mid.getInteger(3),array_mid.getInteger(4));
//                all_place[rem_id].setComment_num(mid.getInteger("comment"));
                all_place[rem_id].setNote_num(mid.getInteger("note"));
            }
            array_line=json.getJSONArray("line");//取出line
            double rem_length=0;
            int array_length;
            int rem_id_end;
            JSONArray exchange;
            JSONObject array_json;
            JSONArray crowd;
            for (int i = 0; i < array_line.size(); i++) {
                mid=array_line.getJSONObject(i);//取出单条线路
                array_mid=mid.getJSONArray("point");//取出point
                array_length=array_mid.size();
                array_json=array_mid.getJSONObject(0);
                rem_id=array_json.getInteger("id");
                array_json=array_mid.getJSONObject(array_length-1);
                rem_id_end=array_json.getInteger("id");
                all_place[rem_id].routeMessage=Arrays.copyOf(all_place[rem_id].routeMessage,all_place[rem_id].routeMessage.length+1);
                all_place[rem_id].routeMessage[all_place[rem_id].routeMessage.length-1]=new RouteMessage();
                all_place[rem_id].routeMessage[all_place[rem_id].routeMessage.length-1].id=rem_id_end;
                all_place[rem_id].routeMessage[all_place[rem_id].routeMessage.length-1].routeId=mid.getInteger("id");
                all_place[rem_id].routeMessage[all_place[rem_id].routeMessage.length-1].name=mid.getString("name");
                all_place[rem_id].routeMessage[all_place[rem_id].routeMessage.length-1].way=array_mid;
                all_place[rem_id].routeMessage[all_place[rem_id].routeMessage.length-1].idealSpeed=mid.getDouble("speed");
                crowd=mid.getJSONArray("crowd");
                all_place[rem_id].routeMessage[all_place[rem_id].routeMessage.length-1].workCrowding=crowd.getDouble(0);
                all_place[rem_id].routeMessage[all_place[rem_id].routeMessage.length-1].cycleCrowding=crowd.getDouble(1);
                all_place[rem_id].routeMessage[all_place[rem_id].routeMessage.length-1].driveCrowding=crowd.getDouble(2);
                exchange=new JSONArray();
                exchange.add(array_json);
                json=array_json;
                rem_length=0;
                for (int j = 2; j <= array_length; j++) {
                    array_json=array_mid.getJSONObject(array_length-j);
                    rem_length+=Math.sqrt(Math.pow(array_json.getDouble("lat")-json.getDouble("lat"),2)+Math.pow(array_json.getDouble("lng")-json.getDouble("lng"),2));
                    exchange.add(array_json);
                    json=array_json;
                }
                all_place[rem_id].routeMessage[all_place[rem_id].routeMessage.length-1].length=rem_length;

                all_place[rem_id_end].routeMessage=Arrays.copyOf(all_place[rem_id_end].routeMessage,all_place[rem_id_end].routeMessage.length+1);
                all_place[rem_id_end].routeMessage[all_place[rem_id_end].routeMessage.length-1]=new RouteMessage();
                all_place[rem_id_end].routeMessage[all_place[rem_id_end].routeMessage.length-1].id=rem_id;
                all_place[rem_id_end].routeMessage[all_place[rem_id_end].routeMessage.length-1].routeId=mid.getInteger("id");
                all_place[rem_id_end].routeMessage[all_place[rem_id_end].routeMessage.length-1].name=mid.getString("name");
                all_place[rem_id_end].routeMessage[all_place[rem_id_end].routeMessage.length-1].way=exchange;
                all_place[rem_id_end].routeMessage[all_place[rem_id_end].routeMessage.length-1].idealSpeed=mid.getDouble("speed");
                all_place[rem_id_end].routeMessage[all_place[rem_id_end].routeMessage.length-1].workCrowding=crowd.getDouble(0);
                all_place[rem_id_end].routeMessage[all_place[rem_id_end].routeMessage.length-1].cycleCrowding=crowd.getDouble(1);
                all_place[rem_id_end].routeMessage[all_place[rem_id_end].routeMessage.length-1].driveCrowding=crowd.getDouble(2);
                all_place[rem_id_end].routeMessage[all_place[rem_id_end].routeMessage.length-1].length=rem_length;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    class Min_route {
        int id;
        Way[] shortest_way;
    }
    class Way {
        int id;
        double mem;
        int[] way;//其中为倒序存储
    }
}
