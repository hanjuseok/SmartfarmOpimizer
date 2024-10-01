// 필요한 패키지 불러오기
const express = require('express');
const cors = require('cors'); // CORS 패키지
const mongoose = require('mongoose'); // MongoDB 연결을 위한 패키지

const app = express();
const PORT = 3000;

// CORS 설정
app.use(cors());

// JSON 요청을 처리할 수 있도록 설정
app.use(express.json());

// MongoDB 연결 설정
mongoose.connect('mongodb://localhost:27017/smartfarm', {
  useNewUrlParser: true,
  useUnifiedTopology: true
})
.then(() => console.log('MongoDB에 연결되었습니다.'))
.catch(err => console.error('MongoDB 연결 오류:', err));

// 데이터 모델 정의
const DataSchema = new mongoose.Schema({
  temperature: Number,
  humidity: Number,
  timestamp: { type: Date, default: Date.now }
});

const Data = mongoose.model('Data', DataSchema);

// GET 요청: 모든 데이터 가져오기
app.get('/api/data', async (req, res) => {
  try {
    const data = await Data.find();
    res.json(data);
  } catch (error) {
    res.status(500).json({ message: '데이터를 가져오는 중 오류 발생', error });
  }
});

// POST 요청: 새로운 데이터 저장하기
app.post('/api/data', async (req, res) => {
  const { temperature, humidity } = req.body;

  const data = new Data({ temperature, humidity });

  try {
    await data.save();
    res.json({ message: '데이터가 성공적으로 저장되었습니다.' });
  } catch (error) {
    res.status(500).json({ message: '데이터 저장 중 오류 발생', error });
  }
});

// 에러 핸들링 미들웨어
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).send('무언가 잘못되었습니다!');
});

// 서버 시작
app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});
