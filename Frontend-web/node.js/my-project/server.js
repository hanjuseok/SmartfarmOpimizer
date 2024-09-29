const express = require('express');
const cors = require('cors'); // CORS 패키지 불러오기
const app = express();
const PORT = 3000;

// CORS 설정
app.use(cors());

// JSON 요청을 처리할 수 있도록 설정
app.use(express.json());

// API 엔드포인트 설정
app.get('/api/data', (req, res) => {
  res.json({ message: 'Hello, world!' });
});

// 서버 시작
app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});
