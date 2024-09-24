const express = require('express');
const cors = require('cors');
const app = express();
const port = 3000;

// CORS 설정
app.use(cors());

// JSON 바디 파서
app.use(express.json());

// 루트 경로에 대한 핸들러 추가
app.get('/', (req, res) => {
  res.send('Hello World! This is the root path.');
});

// POST 요청 처리
app.post('/data', (req, res) => {
  const { temperature, humidity } = req.body;
  console.log(`Temperature: ${temperature}, Humidity: ${humidity}`);
  res.send('Data received');
});

// 서버 시작
app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});
