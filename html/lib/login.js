$(document).ready(function () {
  idxUser = sessionStorage.getItem('idxUser') === null ? 0 : sessionStorage.getItem('idxUser');
  if (idxUser === 0) {
    $(".link-login").on("click", function () {
      $(".login-mask").show();
      $(".register-mask").hide();
    });
  } else {
    $(".link-login").on("click", function () {
      $(`.left-panel-item`).hide();
      $(`.left-panel-item:eq2)`).show();
      $(`.left-nav-item`).removeClass("left-nav-item-active");
      $(`.left-nav-item:eq(2)`).addClass("left-nav-item-active");
      collapseLeft();
    });
  }

  $(".login-button:eq(1)").on("click", function () {
    $(".register-mask").show();
    $(".login-mask").hide();
  });

  $(".quxiao").click(function () {
    $(".login-mask").hide();
    $(".register-mask").hide();
  });

  $(".fanhui").click(function () {
    $(".register-mask").hide();
    $(".login-mask").show();
  });

  $(".login-button:eq(0)").on("click", function () {
    if ($(".login-acco").val().length == 0) {
      alert("请输入账号");
      return;
    }
    if ($(".login-pwd").val().length == 0) {
      alert("请输入密码");
      return;
    }
    var userData = {
      name: $(".login-acco").val(),
      passwd: $(".login-pwd").val(),
    };
    $.ajax({
      url: remoteAddr + "/user/login",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify(userData),
      dataType: "json",
      success: function (res) {
        if (res.id) {
          alert("登录成功！");
          sessionStorage.setItem("idxUser", res.id);
          sessionStorage.setItem("userName", $(".login-acco").val());
          idxUser = res.id;
          userName = $(".login-acco").val();
          $(".link-login").text(userName);
          $(".login-mask").hide();
          $(".link-login").off("click");
          $(".link-login").on("click", function () {
            $(`.left-panel-item`).hide();
            $(`.left-panel-item:eq(2)`).show();
            $(`.left-nav-item`).removeClass("left-nav-item-active");
            $(`.left-nav-item:eq(2)`).addClass("left-nav-item-active");
            collapseLeft();
          });
        } else {
          alert("登录失败");
        }
      },
      error: function (error) {
        console.log(error);
      },
    });
  });

  //判断密码与确认密码是否相等
  $(".register-button").on("click", function () {
    if ($(".acco").val().length == 0) {
      alert("请输入账号");
      return 0;
    }
    if ($(".pwd1").val() == "") {
      alert("请输入密码");
      return 0;
    }
    if ($(".pwd2").val() == " ") {
      alert("请输入确认密码");
      return 0;
    }
    if ($(".pwd2").val().length > 16) {
      alert("密码不能超过16位");
      return 0;
    }
    if ($(".pwd1").val() != $(".pwd2").val()) {
      alert("密码与确认密码不一致");
    } else if ($(".pwd1").val() != "" && $(".pwd2").val() != "") {
      var userData = {
        name: $(".acco").val(),
        passwd: $(".pwd1").val(),
      };
      $.ajax({
        url: remoteAddr + "/user/insert",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(userData),
        dataType: "json",
        success: function (res) {
          if (res) {
            alert("注册成功！");
            sessionStorage.setItem("idxUser", res.id);
            sessionStorage.setItem("userName", $(".acco").val());
            idxUser = res.id;
            userName = $(".acco").val();
            $(".link-login").text(userName);
            $(".register-mask").hide();
            $(".link-login").off("click");
            $(".link-login").on("click", function () {
              $(`.left-panel-item`).hide();
              $(`.left-panel-item:eq(2)`).show();
              $(`.left-nav-item`).removeClass("left-nav-item-active");
              $(`.left-nav-item:eq(2)`).addClass("left-nav-item-active");
              collapseLeft();
            });
          } else {
            alert("注册失败");
          }
        },
        error: function (error) {
          console.log(error);
        },
      });
    }
  });
});

function logout(){
  sessionStorage.removeItem('idxUser');
  sessionStorage.removeItem('userName');
}