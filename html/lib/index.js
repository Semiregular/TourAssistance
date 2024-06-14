// 改变左侧面板

function changeLeft(id) {
  $(".left-panel-item").hide();
  $(`.left-panel-item:eq(${id})`).show();
  $(`.left-nav-item`).removeClass("left-nav-item-active");
  $(`.left-nav-item:eq(${id})`).addClass("left-nav-item-active");
  if(id === 2 ){
    if(idxUser !== 0){
      getUserNoteList()
    }else{
      renderUserEmpty()
    }
  }
  collapseLeft();
}

function changeTravelType(id) {
  $(".travel-type-item").removeClass("travel-type-item-active");
  $(`.travel-type-item:eq(${id})`).addClass("travel-type-item-active");
  travelTypeF = 4 - id;
  travelType = travelTypeS * 10 + travelTypeF;
}

function changeTravelTypes(id) {
  $(".travel-types-item").removeClass("travel-types-item-active");
  $(`.travel-types-item:eq(${id})`).addClass("travel-types-item-active");
  travelTypeS = id + 1;
  travelType = travelTypeS * 10 + travelTypeF;
}
// 折叠左侧导航
function collapseLeft() {
  if (!isLeftCollapsed) {
    for (let i = 0; i < 3; i++) {
      $(`.line:eq(${i})`).removeClass(`line-${i}`);
    }
    $(".left-nav").addClass("left-nav-hide");
    isLeftCollapsed = true;
  } else {
    for (let i = 0; i < 3; i++) {
      $(`.line:eq(${i})`).addClass(`line-${i}`);
    }
    $(".left-nav").removeClass("left-nav-hide");
    isLeftCollapsed = false;
  }
}

// 折叠右侧面板
function collapseRight(type) {
  if (rightToolType === type && !isRightCollapsed) {
    $(".right-sidebar").addClass("right-sidebar-hide");
    $(".right-panel").addClass("right-panel-hide");
    $(".middle-container").removeClass("middle-container-hide");
    $(`.right-tool-item:eq(${type})`).removeClass("right-tool-item-active");
    isRightCollapsed = true;
  } else {
    $(".right-sidebar").removeClass("right-sidebar-hide");
    $(".right-panel").removeClass("right-panel-hide");
    
    $(".middle-container").addClass("middle-container-hide");
    $(".right-tool-item").removeClass("right-tool-item-active");
    $(`.right-tool-item:eq(${type})`).addClass("right-tool-item-active");
    $(`.right-panel-item`).removeClass("right-panel-item-active");
    $(`.right-panel-item:eq(${type})`).addClass("right-panel-item-active");
    isRightCollapsed = false;
    rightToolType = type;
    if(type === 1 && curNote === 0){
      renderReadEmpty()
    }
    if(type === 0 && curPoint === 0){
      renderPointEmpty()
    }
  }
}
function changeSearchType(cur){
  if(cur + 1 === searchTypeS){
    return
  }
  $(`.type-list-item:eq(${searchTypeS - 1})`).removeClass("type-list-item-active")
  $(`.type-list-item:eq(${cur})`).addClass("type-list-item-active")
  searchTypeS = cur + 1
}

function changeSearchTypes(cur){
  if(cur === searchTypeF){
    return
  }
  $(`.types-list-item:eq(${searchTypeF})`).removeClass("types-list-item-active")
  $(`.types-list-item:eq(${cur})`).addClass("types-list-item-active")
  searchTypeF = cur;
}