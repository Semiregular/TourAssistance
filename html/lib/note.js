function getUserNoteList() {
  $.ajax({
    url: remoteAddr + "/note/list/u",
    type: "POST",
    contentType: "application/json",
    dataType: "json",
    data: JSON.stringify({
      id: Number(idxUser),
      type: 0,
      offset: 0,
      limit: 10,
    }),
    success: function (res) {
      if (res.data.length !== 0) {
        renderUserNoteList(res.data);
      } else {
        renderUserEmpty();
      }

      tolUserPage = res.tolPage;
    },
    error: function (err) {
      console.log(err);
    },
  });
}

function renderUserNoteList(res) {
  var noteList = res;
  var noteListHtml = "";
  var divider = `<div class="note-divider"></div>`;
  for (var i = 0; i < noteList.length; i++) {
    var note = noteList[i];
    var noteHtml = `
      <div class="note-item" onclick="readNote(${note.id})">
        <div class="note-left">
          <div class="note-avatar"></div>
        </div>
        <div class="note-right">
          <div class="note-name">${note.name}</div>
          <div class="note-summary">${note.summary}</div>
          <div class="note-time">${note.timeCreated}</div>
        </div>
      </div>
        `;
    noteListHtml += noteHtml;
    if (i != noteList.length - 1) {
      noteListHtml += divider;
    }
  }
  $(".user-note-list").html(noteListHtml);
}

function renderUserEmpty() {
  var noteListHtml = `
    <div class="note-user-empty">
        快去投稿吧(*^▽^*)
    </div>
    `;
  $(".user-note-list").html(noteListHtml);
}

function getPointNoteList(pid) {
  $.ajax({
    url: remoteAddr + "/note/list/p",
    type: "POST",
    contentType: "application/json",
    dataType: "json",
    data: JSON.stringify({
      id: pid,
      type: 0,
      offset: 0,
      limit: 10,
    }),
    success: function (res) {
      if (res.data.length !== 0) {
        renderPointNoteList(res.data);
      } else {
        renderPointEmpty();
      }

      tolPointPage = res.tolPage;
    },
    error: function (err) {
      console.log(err);
    },
  });
}

function renderPointNoteList(res) {
  var noteList = res;
  var noteListHtml = "";
  var divider = `<div class="note-divider"></div>`;
  for (var i = 0; i < noteList.length; i++) {
    var note = noteList[i];
    var noteHtml = `
        <div class="note-item" onclick="readNote(${note.id})">
          <div class="note-left">
            <div class="note-avatar"></div>
          </div>
          <div class="note-right">
            <div class="note-name">${note.name}</div>
            <div class="note-summary">${note.summary}</div>
            <div class="note-time">${note.timeCreated}</div>
          </div>
        </div>
          `;
    noteListHtml += noteHtml;
    if (i != noteList.length - 1) {
      noteListHtml += divider;
    }
  }
  $(".point-note-list").html(noteListHtml);
}

function renderPointEmpty() {
  var noteListHtml = `
    <div class="note-point-empty">
        快去投稿吧(*^▽^*)
    </div>
    `;
  $(".point-note-list").html(noteListHtml);
}

function readNote(noteId) {
  curNote = noteId;
  $.ajax({
    url: remoteAddr + "/note/get/read",
    type: "POST",
    contentType: "application/json",
    dataType: "json",
    data: JSON.stringify({
      id: noteId,
    }),
    success: function (res) {
      renderReadNote(res);
      collapseRight(1);
    },
    error: function (err) {
      console.log(err);
    },
  });
}

function insertNote() {
  var str = $(".write-note-tag-input").val();
  $.ajax({
    url: remoteAddr + "/note/insert",
    type: "POST",
    contentType: "application/json",
    dataType: "json",
    data: JSON.stringify({
      uid: Number(idxUser),
      pid: Number(curPoint),
      name: userName,
      title: $(".write-note-title-input").val(),
      content: $(".write-note-content-input").val(),
      tag: str.split(";").slice(0, 3),
      time: getCurrentDateTime(),
    }),
    success: function (res) {
      renderUserNoteList(res);
      $(".write-note-title-input").val("");
      $(".write-note-content-input").val("");
      $(".write-note-tag-input").val("");
    },
    error: function (err) {
      console.log(err);
    },
  });
}

function updateNote() {
  var str = $(".write-note-tag-input").val();
  $.ajax({
    url: remoteAddr + "/note/update",
    type: "POST",
    contentType: "application/json",
    dataType: "json",
    data: JSON.stringify({
      id: curNote,
      title: $(".write-note-title-input").val(),
      content: $(".write-note-content-input").val(),
      tag: str.split(";").slice(0, 3),
      time: getCurrentDateTime(),
    }),
    success: function (res) {
      renderUserNoteList(res);
      curNote = 0;
      $(".write-note-title-input").val("");
      $(".write-note-content-input").val("");
      $(".write-note-tag-input").val("");
    },
    error: function (err) {
      console.log(err);
    },
  });
}

function getCurrentDateTime() {
  var now = new Date();
  var year = now.getFullYear();
  var month = ("0" + (now.getMonth() + 1)).slice(-2); // 月份从0开始，所以加1
  var day = ("0" + now.getDate()).slice(-2);
  var hours = ("0" + now.getHours()).slice(-2);
  var minutes = ("0" + now.getMinutes()).slice(-2);
  var seconds = ("0" + now.getSeconds()).slice(-2);
  return (
    year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds
  );
}

function renderReadNote(res) {
  var note = res;
  var noteHtml = `
        <div class="read-title">${note.title}</div>
        <div class="read-user">
          <div class="read-avatar"></div>
          <div class="read-name">${note.name}</div>
        </div>
        <div class="read-content">${note.content}</div>
        <div class="read-btm">
          <div class="read-tag">${note.tag}</div>
          <div class="read-time">${note.timeCreated}</div>
        </div>
        
        `;
  $(".read-note").html(noteHtml);
}

function renderReadEmpty() {
  var noteHtml = `
    <div class="note-read-empty">
        快去投稿吧(*^▽^*)
    </div>
    `;
  $(".read-note").html(noteHtml);
}

function mark(str, pattern, className) {
  const regex = new RegExp(pattern, "g");
  const replacementTemplate = '<span class="' + className + '">$&</span>';
  return str.replace(regex, replacementTemplate);
}

function searchMark() {
  var dst = $(".search-note-input").val();
  $(".mark").removeClass("mark");
  if (dst) {
    var raw = $(".read-note").html();
    var newHTML = mark(raw, dst, "mark");
    $(".read-note").html(newHTML);
  }
}
