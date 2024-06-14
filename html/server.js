
const http = require("http");
const url = require("url");
const express = require("express");
const cors = require("cors");
const bodyParser = require("body-parser");
const mysql = require("mysql2/promise");
const fs = require("fs");

const app = express();
app.use(cors());
app.use(bodyParser.json()); // 用于解析JSON格式的请求体
app.use(express.static("db"));

// 设置HTTP服务器
const server = http.createServer(app);

let mapdata = fs.promises.readFile("./db/mapdata.json", "utf-8");
var point = [];
var line = [];
var area = [];
mapdata.then((data) => {
  var jsonMapData = JSON.parse(data);
  point = jsonMapData.point;
  line = jsonMapData.line;
  area = jsonMapData.area;
});

// async function createConnection() {
//   return mysql.createConnection(dbConfig);
// }

app.get("/point/list/search/:type/:key", async (req, res) => {
  // 使用body-parser时，可以直接从req.body获取解析后的JSON对象
  let type = req.params.type;
  let key = req.params.key;
  if (type && key) {
    try {
      let searchList = await serachPointByName(key);
      res.status(200).json({ message: "OK", data: searchList });
    } catch (error) {
      res.status(500).json({ error: "Failed" });
    }
  } else {
    res.status(400).json({ error: "Invalid" });
  }
});

app.post("/user/login", async (req, res) => {
  if (req.body) {
    try {
      let name = req.body.name;
      let passwd = req.body.passwd;
      if (name && passwd) {
        res.status(200).json({ message: "OK", id : 100001 });
      } else {
        res.status(400).json({ error: "Invalid" });
      }
    } catch(error) {

    }
  }
})


app.post("/point/route", async (req, res) => {
  if (req.body) {
    try {
      let p = req.body.point;
      for (let i = 0; i < line.length; i++) {
        let l = line[i].point.length;
        if (line[i].point[0].id == p[0] && line[i].point[l - 1].id == p[1]) {
          res.status(200).json({ message: "OK", data: line[i] });
          break;
        } else if (
          line[i].point[0].id == p[1] &&
          line[i].point[l - 1].id == p[0]
        ) {
          res.status(200).json({ message: "OK", data: line[i] });
          break;
        }
      }
    } catch (error) {
      res.status(500).json({ error: "Failed" });
    }
  } else {
    res.status(400).json({ error: "Invalid" });
  }
});

app.get("/point/list/all", async (req, res) => {
  res.setHeader("Content-Type", "application/json");
  res.send(point);
});

app.get("/point/get/info/:id", async (req, res) => {
  let id = req.params.id;
  res.setHeader("Content-Type", "application/json");
  res.send(point[id - 1]);
});

async function serachPointByName(name) {
  let res = [];
  for (let i = 0; i < point.length; i++) {
    if (point[i].name.includes(name)) {
      res.push(point[i]);
    }
  }
  return res;
}

// 启动服务器
server.listen(3000, () => {
  console.log(`Server running at http://localhost:3000/`);
});

/*
async function savePoint(point) {
  const connection = await createConnection();

  try {
    const [result] = await connection.query(
      "INSERT INTO point(lat, lng, type) VALUES (?, ?, ?)",
      [point.lat, point.lng, point.type]
    );
    console.log("Point saved with ID:", result.insertId);
    return result.insertId; // 返回新插入文章的ID
  } catch (error) {
    console.error("Error saving article:", error);
    throw error;
  } finally {
    // 确保关闭连接
    connection.end();
  }
}
*/
