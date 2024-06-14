package org.example;

import com.alibaba.fastjson.JSONArray;

public class PlaceMessage {
    public int id;
    public String name;
    public double lat;
    public double lng;
    public int[] evaluation=new int[5];//评分,自0至4为1到5星
    public int popularity;//游学热度
    public int type;
//    public int comment_num;//评论数
    public int note_num;//笔记数

    public void setNoteId(JSONArray input) {
        if (input==null)
        {
            noteId=new int[0];
        }
        else {
            noteId = new int[input.size()];
            for (int i = 0; i < input.size(); i++) {
                noteId[i] = input.getInteger(i);
            }
        }
    }

    public int[] noteId;
    public  String message;//内容
    public RouteMessage[] routeMessage=new RouteMessage[0];

    //add:


//    public void Write()//用于最终修改文件中数据
//    {
//
//    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setEvaluation(int a,int b,int c,int d,int e) {
        this.evaluation[0] = a;
        this.evaluation[1] = b;
        this.evaluation[2] = c;
        this.evaluation[3] = d;
        this.evaluation[4] = e;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public void setType(int type) {
        this.type = type;
    }

//    public void setComment_num(int comment_num) {
//        this.comment_num = comment_num;
//    }

    public void setNote_num(int note_num) {
        this.note_num = note_num;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRouteMessage(RouteMessage[] routeMessage) {
        this.routeMessage = routeMessage;
    }
}
