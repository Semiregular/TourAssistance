package org.example;

import com.alibaba.fastjson.JSONArray;

public class RouteMessage {
    int id;//道路另一个节点的id
    int routeId;//道路id
    String name;
    JSONArray way;//记录路线(不算起点)
    int passType;//记录经过的方式
    double length;//长度
    double time;//为0代表不能通过
    double idealSpeed;//理想速度
    double workCrowding;//步行拥挤度（为0代表不能通过）
    double cycleCrowding;//骑行拥挤度
    double driveCrowding;//驾车拥挤度
}
