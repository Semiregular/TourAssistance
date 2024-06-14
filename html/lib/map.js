// 绘制点
function drawPoint() {
  for (let i = 0; i < point.length; i++) {
    let marker = L.marker([point[i].lat, point[i].lng]);
    if (point[i].type !== 2 && point[i].id < 62) {
      normalPointGroup.push(marker);
    } else if(point[i].id < 62) {
      specialPointGroup.push(marker);
    }
    marker.on("click", function (e) {
      let latlng = e.latlng;
      let p = popPointByLatlng(latlng);
      curPoint = p.id;
      if (point[i].type !== 2) {
        var content = `
      <div class="popup-main">
        <div class="popup-left point-related">
          <img src="./lib/images/user.jpg" class="popup-img" />
        </div>
        <div class="popup-right">
          <div class="popup-name point-related">${p.name}</div>
          <div class="popup-detail point-related">${p.detail}</div> 
          <div class="popup-btn">
            <div class="popup-btn-item point-start">加点</div>
            <div class="popup-btn-item point-end">终点</div>
            <div class="popup-btn-item point-comment">写评论</div>
          </div>
        </div>
      </div>
      `;
      } else {
        var content = `
      <div class="popup-main">
        <div class="popup-left point-related">
          <img src="./lib/images/user.jpg" class="popup-img" />
        </div>
        <div class="popup-right">
          <div class="popup-name point-related">${p.name}</div>
          <div class="popup-detail point-related">${p.detail}</div> 
          <div class="popup-btn">
            <div class="popup-btn-item point-start">加点</div>
            <div class="popup-btn-item point-end">终点</div>
            <div class="popup-btn-item point-comment">景区地图</div>
          </div>
        </div>
      </div>
      `;
      }
      var popup = L.popup({ offset: [1.7, -18] })
        .setLatLng(latlng)
        .setContent(content)
        .openOn(map);
      getPointInfo(p.id);
      getPointNoteList(p.id);
      $(".point-start").on("click", function () {
        //startPoint = p.id;
        routePointList.push(p.id)
        curPoint = p.id;
        $(".left-panel-item").hide();
        $(`.left-panel-item:eq(1)`).show();
        $(`.left-nav-item`).removeClass("left-nav-item-active");
        $(`.left-nav-item:eq(1)`).addClass("left-nav-item-active");
        if(routePointList.length === 1){
          $("#search-travel-start-input").val(p.name);
          routePointListName += p.name;
        }else{
          routePointListName += "->" + p.name;
        }
        if(routePointList.length > 2){
          $("#search-travel-multi-input").val(routePointListName);
        }
      });
      $(".point-end").on("click", function () {
        //endPoint = p.id;
        routePointList.push(p.id)
        curPoint = p.id;
        $(".left-panel-item").hide();
        $(`.left-panel-item:eq(1)`).show();
        $(`.left-nav-item`).removeClass("left-nav-item-active");
        $(`.left-nav-item:eq(1)`).addClass("left-nav-item-active");
        $("#search-travel-end-input").val(p.name);
        routePointListName += "->" + p.name;
        if(routePointList.length > 2){
          $("#search-travel-multi-input").val(routePointListName);
        }
        getRoute();
      });
      $(".point-comment").on("click", function () {
        if (point[i].type === 2) {
          $(".leaflet-control-layers-selector:eq(1)").click();
        } else {
          collapseRight(2);
      
        }
      });
      $(".point-related").on("click", function () {
        collapseRight(0);
        getPointInfo(p.id);
        getPointNoteList(p.id);
        curPoint = p.id;
      });
    });
    if(point[i].id >= 62){
      moreCluster.addLayer(marker);
    }else{
      markerCluster.addLayer(marker);
    }
  }
  map.addLayer(markerCluster);
  normalLayer = L.layerGroup(normalPointGroup);
  specialLayer = L.layerGroup(specialPointGroup);
  overlays = {
    '建筑': normalLayer,
    '景区': specialLayer
  };
  layerControl = L.control.layers(baseLayers, overlays).addTo(map);
  $(".leaflet-control-layers-selector:eq(0)").click(
    () => {
      map.addLayer(markerCluster)
      map.removeLayer(moreCluster)
      map.removeLayer(curRouteLayer)
    }
  );
  $(".leaflet-control-layers-selector:eq(1)").click(
    () => {
      map.removeLayer(markerCluster)
      map.addLayer(moreCluster)
      map.removeLayer(curRouteLayer)
    }
  );
}

function popPointById(id) {
  for (let i = 0; i < point.length; i++) {
    if (point[i].id == id) {
      var pointData = point[i];
      var pointLat = pointData.lat;
      var pointLng = pointData.lng;
      break;
    }
  }
  let pointDetail = pointData.detail;
  let pointType = pointData.type;
  let pointName = pointData.name;
  let latlng = { lat: pointLat, lng: pointLng };
  curPoint = id;
  if (pointType !== 2) {
    var content = `
  <div class="popup-main">
    <div class="popup-left point-related">
      <img src="./lib/images/user.jpg" class="popup-img" />
    </div>
    <div class="popup-right">
      <div class="popup-name point-related">${pointName}</div>
      <div class="popup-detail point-related">${pointDetail}</div> 
      <div class="popup-btn">
        <div class="popup-btn-item point-start">起点</div>
        <div class="popup-btn-item point-end">终点</div>
        <div class="popup-btn-item point-comment">写评论</div>
      </div>
    </div>
  </div>
  `;
  } else {
    var content = `
  <div class="popup-main">
    <div class="popup-left point-related">
      <img src="./lib/images/user.jpg" class="popup-img" />
    </div>
    <div class="popup-right">
      <div class="popup-name point-related">${pointName}</div>
      <div class="popup-detail point-related">${pointDetail}</div> 
      <div class="popup-btn">
        <div class="popup-btn-item point-start">起点</div>
        <div class="popup-btn-item point-end">终点</div>
        <div class="popup-btn-item point-comment">景区地图</div>
      </div>
    </div>
  </div>
  `;
  }
  L.popup({ offset: [1.7, -18], autoPanPadding: [300, 300] })
    .setLatLng(latlng)
    .setContent(content)
    .openOn(map);
  getPointInfo(id);
  getPointNoteList(id);
  $(".point-start").on("click", function () {
    //startPoint = id;
    routePointList.push(id)
    $(".left-panel-item").hide();
    $(`.left-panel-item:eq(1)`).show();
    $(`.left-nav-item`).removeClass("left-nav-item-active");
    $(`.left-nav-item:eq(1)`).addClass("left-nav-item-active");
    if(routePointList.length == 1){
      $("#search-travel-start-input").val(pointName);
      routePointListName +=  pointName;
    }else{
      routePointListName += "->" + pointName;
    }
    if(routePointList.length > 2){
      $("#search-travel-multi-input").val(routePointListName);
    }
    
  });
  $(".point-end").on("click", function () {
    //endPoint = id;
    routePointList.push(id)
    $(".left-panel-item").hide();
    $(`.left-panel-item:eq(1)`).show();
    $(`.left-nav-item`).removeClass("left-nav-item-active");
    $(`.left-nav-item:eq(1)`).addClass("left-nav-item-active");
    $("#search-travel-end-input").val(pointName);
    routePointListName += "->" + pointName;
    if(routePointList.length > 2){
      $("#search-travel-multi-input").val(routePointListName);
    }
    
    getRoute();
  });
  $(".point-comment").on("click", function () {
    if (pointType === 2) {
      $(".leaflet-control-layers-selector:eq(1)").click();
    } else {
      collapseRight(2);
    }
  });
  $(".point-related").on("click", function () {
    collapseRight(0);
    getPointInfo(id);
    getPointNoteList(id);
  });
}

function popPointByLatlng(latlng) {
  for (let i = 0; i < point.length; i++) {
    if (point[i].lat === latlng.lat && point[i].lng === latlng.lng) {
      let p = point[i];
      return p;
    }
  }
}

function getRoute() {
  if (startPoint && endPoint) {
    if (startPoint === endPoint) {
      alert("起点和终点不能相同");
      return;
    } else {
      //let tolPoint = [startPoint, endPoint];
      $.ajax({
        url: remoteAddr + "/point/route",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({ point: routePointList, type: travelType }),
        success: function (res) {
          routePointList = [];
          routePointListName = "";
          if (curRouteLayer.length > 0) {
            for (let i = 0; i < curRouteLayer.length; i++) {
              map.removeLayer(curRouteLayer[i]);
            }
            curRouteLayer = [];
          }
          let polyline = L.polyline(res.point, {
            color: "#c00808",
            weight: 8,
          });
          curRouteLayer.push(polyline);
          for (let i = 0; i < curRouteLayer.length; i++) {
            map.addLayer(curRouteLayer[i]);
          }
          renderRouteList(res);
        },
        error: function (err) {
          console.log(err);
        },
      });
    }
  }
}

function searchPoint(type, key) {
  $.ajax({
    url: `${remoteAddr}/point/list/search/${type}/${key}`,
    type: "GET",
    contentType: "application/json",
    dataType: "json",
    data: "",
    success: function (res) {
      let pointList = res;
      if (pointList.length > 0) {
        renderPointList(pointList);
      }
    },
    error: function (err) {
      console.log(err);
    },
  });
}

function renderPointList(data) {
  let pointList = $(".point-list");
  pointList.empty();
  for (var i = 0; i < data.length; i++) {
    var pointData = data[i];
    var pointItem = $("<div class='point-list-item'></div>");
    var pointLeft = $("<div class='point-left'></div>");
    var pointRight = $("<div class='point-right'></div>");
    var pointImg = $(
      "<img class='point-img' src='" + "./lib/images/user.jpg" + "'/>"
    );
    var name = $("<div class='point-name'></div>").text(pointData.name);
    var detail = $("<div class='point-detail'></div>").text(pointData.detail);
    pointLeft.append(pointImg);
    pointRight.append(name);
    pointRight.append(detail);
    pointItem.append(pointLeft);
    pointItem.append(pointRight);
    pointItem.attr("point-id", pointData.id);
    pointItem.click(function () {
      let id = $(this).attr("point-id");
      popPointById(id);
    });
    pointList.append(pointItem);
  }
  renderPointAnimation();
}

function renderRouteList(res) {
  let routeList = $(".route-list");
  routeList.empty();
  let p = res.point;
  let l = res.line;
  let tol = 0;
  for (var i = 0; i < p.length; i++) {
    if (p[i].name === undefined) {
      continue;
    }
    var routeItem = $("<div class='route-list-item'></div>");
    var routeTop = $("<div class='route-top'></div>");
    var routeBtm = $("<div class='route-btm'></div>");
    var routeTopLeft = $("<div class='route-top-left'></div>");
    var routeTopRight = $("<div class='route-top-right'></div>").text(
      p[i].name
    );
    var routeBtmLeft = $("<div class='route-btm-left'></div>");
    var routeBtmRight = $("<div class='route-btm-right'></div>");
    if (i !== p.length - 1) {
      var roadName = $("<div class='route-road-name'></div>").text(l[tol].name);
      var roadLength = $("<div class='route-road-length'></div>").html(
        "全长<span> " + Math.floor(l[tol].length * 100000) + " </span>米"
      );
      var roadTime = $("<div class='route-road-time'></div>").html(
        "，大约需要<span> " + Math.floor(l[tol].time * 30 + 2) + " </span>分钟"
      );
      var roadType = $("<div class='route-road-type'></div>");
      if (l[tol].passType === 1) {
        roadType.text("步行");
      } else if (l[tol].passType === 2) {
        roadType.text("骑行");
      } else if (l[tol].passType === 3) {
        roadType.text("公交");
      }
      tol++;
      var roadTop = $("<div class='route-road-top'></div>");
      var roadBtm = $("<div class='route-road-btm'></div>");
      roadTop.append(roadName);
      roadTop.append(roadType);
      roadBtm.append(roadLength);
      roadBtm.append(roadTime);
      routeBtmRight.append(roadTop);
      routeBtmRight.append(roadBtm);
    }
    if (i === p.length - 1) {
      routeBtm.css("display", "none");
    }
    routeTop.append(routeTopLeft);
    routeTop.append(routeTopRight);
    routeBtm.append(routeBtmLeft);
    routeBtm.append(routeBtmRight);
    routeItem.append(routeTop);
    routeItem.append(routeBtm);
    routeList.append(routeItem);
  }
}

function getPointInfo(id) {
  $.ajax({
    url: `${remoteAddr}/point/get/info/${id}`,
    type: "GET",
    data: "",
    dataType: "text",
    success: function (res) {
      let p = JSON.parse(res);
      renderPointInfo(p);
    },
    error: function (res) {
      console.log(res);
    },
  });
}

function renderPointInfo(p) {
  $(".info-point-name").text(p.name);
  $(".info-point-detail").text(p.detail);
  renderPointEval(p.eval)
}

function renderPointAnimation() {
  $(".point-list-item").each(function (index, item) {
    var delay = index * 200;
    $(item).delay(delay).animate(
      {
        opacity: 1,
        "margin-left": "20px",
      },
      400
    );
  });
}

function renderPointEval(eval) {
  for(let i = 0; i < eval.length; i++){
    $(`.info-point-eval-length:eq(${i})`).css("width", eval[i]/150 + "%");
  }
}