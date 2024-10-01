const ctx = document.getElementById('myChart').getContext('2d');
const myChart = new Chart(ctx, {
    type: 'line',
    data: {
        labels: [], // 시간 라벨
        datasets: [{
            label: '온도',
            data: [], // 온도 데이터
            borderColor: 'rgba(255, 99, 132, 1)',
            backgroundColor: 'rgba(255, 99, 132, 0.2)',
            borderWidth: 1
        }, {
            label: '습도',
            data: [], // 습도 데이터
            borderColor: 'rgba(54, 162, 235, 1)',
            backgroundColor: 'rgba(54, 162, 235, 0.2)',
            borderWidth: 1
        }, {
            label: '토양 수분',
            data: [], // 토양 수분 데이터
            borderColor: 'rgba(75, 192, 192, 1)',
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            borderWidth: 1
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            x: {
                beginAtZero: true
            },
            y: {
                beginAtZero: true
            }
        }
    }
});

const analysisCtx = document.getElementById('analysisChart').getContext('2d');
const analysisChart = new Chart(analysisCtx, {
    type: 'line',
    data: {
        labels: [], // 기간별 라벨
        datasets: [{
            label: '온도',
            data: [], // 기간별 온도 데이터
            borderColor: 'rgba(255, 99, 132, 1)',
            backgroundColor: 'rgba(255, 99, 132, 0.2)',
            borderWidth: 1
        }, {
            label: '습도',
            data: [], // 기간별 습도 데이터
            borderColor: 'rgba(54, 162, 235, 1)',
            backgroundColor: 'rgba(54, 162, 235, 0.2)',
            borderWidth: 1
        }, {
            label: '토양 수분',
            data: [], // 기간별 토양 수분 데이터
            borderColor: 'rgba(75, 192, 192, 1)',
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            borderWidth: 1
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            x: {
                beginAtZero: true
            },
            y: {
                beginAtZero: true
            }
        }
    }
});

let updateInterval = 1000; // 기본 업데이트 간격 (1초)
let chartInterval;

// 실시간 시계 업데이트
function updateClock() {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    document.getElementById('clock').innerText = `${hours}:${minutes}:${seconds}`;
}

// 차트 데이터 업데이트
function updateChart() {
    const now = new Date();
    const timeLabel = `${now.getHours()}:${now.getMinutes()}:${now.getSeconds()}`;

    if (myChart.data.labels.length >= 20) {
        myChart.data.labels.shift(); // 라벨이 20개를 초과하면 가장 오래된 라벨 제거
        myChart.data.datasets.forEach(dataset => dataset.data.shift()); // 데이터도 제거
    }

    myChart.data.labels.push(timeLabel);
    myChart.data.datasets[0].data.push(Math.random() * 30 + 20); // 예시 데이터: 온도
    myChart.data.datasets[1].data.push(Math.random() * 50 + 30); // 예시 데이터: 습도
    myChart.data.datasets[2].data.push(Math.random() * 100); // 예시 데이터: 토양 수분

    myChart.update();
}

// 주기적으로 시계와 차트 업데이트
function startUpdating() {
    chartInterval = setInterval(() => {
        updateClock();
        updateChart();
    }, updateInterval);
}

// 시간 범위 설정 함수
function setTimeRange() {
    const input = document.getElementById('timeInput').value;
    const unit = document.getElementById('timeUnit').value;

    if (input) {
        let intervalInSeconds = parseInt(input);

        switch (unit) {
            case 'minutes':
                intervalInSeconds *= 60;
                break;
            case 'hours':
                intervalInSeconds *= 3600;
                break;
            case 'seconds':
            default:
                break;
        }

        updateInterval = intervalInSeconds * 1000; // 밀리초로 변환
        clearInterval(chartInterval); // 기존의 인터벌을 클리어
        startUpdating(); // 새로운 인터벌 시작
    }
}

// 데이터 조회 함수
function fetchData() {
    const period = document.getElementById('periodSelect').value;
    const url = `http://localhost:3000/api/data?period=${period}`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            // 여기에 데이터 가공 및 차트 업데이트 로직 추가
            // 예: analysisChart.data.datasets[0].data = data.temperature;
            // analysisChart.update();
        })
        .catch(error => console.error('데이터 로드 오류:', error));
}

// 온도 조절 함수
function setTemperature() {
    const tempValue = prompt("설정할 온도를 입력하세요:");
    if (tempValue) {
        // 서버에 온도 설정 요청
    }
}

// 습도 조절 함수
function setHumidity() {
    const humidityValue = prompt("설정할 습도를 입력하세요:");
    if (humidityValue) {
        // 서버에 습도 설정 요청
    }
}

// 토양 수분 조절 함수
function setSoilMoisture() {
    const moistureValue = prompt("설정할 토양 수분을 입력하세요:");
    if (moistureValue) {
        // 서버에 토양 수분 설정 요청
    }
}

// 자동화 규칙 설정 함수
function setAutomationRules() {
    const humidityThreshold = document.getElementById('humidityThreshold').value;
    const temperatureThreshold = document.getElementById('temperatureThreshold').value;

    // 설정된 규칙을 서버에 요청
}

// 페이지 로드 후 업데이트 시작
document.addEventListener('DOMContentLoaded', () => {
    startUpdating();
});

window.onload = function() {
    document.getElementById('loginModal').style.display = 'flex'; // 모달을 flex로 표시
};

// 모달 닫기 버튼 클릭 시 모달 숨기기
document.getElementById('closeModal').onclick = function() {
    document.getElementById('loginModal').style.display = 'none'; // 모달을 숨김
};

// 모달 외부 클릭 시 모달 닫기
window.onclick = function(event) {
    const modal = document.getElementById('loginModal');
    if (event.target === modal) {
        modal.style.display = 'none'; // 모달을 숨김
    }
};