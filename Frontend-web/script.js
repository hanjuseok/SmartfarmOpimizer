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
let chartInterval = setInterval(() => {
    updateClock();
    updateChart();
}, updateInterval);

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
        clearInterval(chartInterval); // 이전 인터벌 제거
        chartInterval = setInterval(() => {
            updateClock();
            updateChart();
        }, updateInterval); // 새로운 간격으로 인터벌 설정

        alert(`차트가 ${input} ${unit === 'seconds' ? '초' : unit === 'minutes' ? '분' : '시간'} 간격으로 업데이트됩니다.`);
    }
}

// 온도, 습도 및 토양 수분 조절
function setTemperature() {
    const temperature = prompt('설정할 온도를 입력하세요 (예: 22):');
    if (temperature !== null) {
        alert(`온도가 ${temperature}로 설정되었습니다.`);
    }
}

function setHumidity() {
    const humidity = prompt('설정할 습도를 입력하세요 (예: 60):');
    if (humidity !== null) {
        alert(`습도가 ${humidity}로 설정되었습니다.`);
    }
}

function setSoilMoisture() {
    const moisture = prompt('설정할 토양 수분을 입력하세요 (예: 50):');
    if (moisture !== null) {
        alert(`토양 수분이 ${moisture}로 설정되었습니다.`);
    }
}

// 데이터 요청 및 분석
async function fetchData() {
    const period = document.getElementById('periodSelect').value;

    // 서버로부터 데이터 가져오기
    try {
        const response = await fetch(`/data?period=${period}`);
        const data = await response.json();

        // 분석 차트 업데이트
        updateAnalysisChart(data);
    } catch (error) {
        console.error('데이터를 가져오는 중 오류 발생:', error);
    }
}

function updateAnalysisChart(data) {
    // 분석 차트 데이터 업데이트
    analysisChart.data.labels = data.labels;
    analysisChart.data.datasets[0].data = data.temperature;
    analysisChart.data.datasets[1].data = data.humidity;
    analysisChart.data.datasets[2].data = data.soilMoisture;

    analysisChart.update();
}

// 자동화 규칙 설정
let automationRules = {
    humidityThreshold: null,
    temperatureThreshold: null
};

function setAutomationRules() {
    const humidityThreshold = parseFloat(document.getElementById('humidityThreshold').value);
    const temperatureThreshold = parseFloat(document.getElementById('temperatureThreshold').value);

    automationRules.humidityThreshold = humidityThreshold || null;
    automationRules.temperatureThreshold = temperatureThreshold || null;

    alert('알림이 설정되었습니다.');
}

// 자동화 규칙을 기반으로 조치를 취하는 함수
function checkAutomationRules(data) {
    if (automationRules.humidityThreshold !== null && data.humidity[data.humidity.length - 1] < automationRules.humidityThreshold) {
        // 예: 물 주기 요청
        console.log('토양 수분이 임계값 이하로 떨어졌습니다. 물을 주어야 합니다.');
        // 여기서 서버에 물 주기 요청을 보낼 수 있습니다.
    }

    if (automationRules.temperatureThreshold !== null && data.temperature[data.temperature.length - 1] > automationRules.temperatureThreshold) {
        // 예: 환풍기 켜기 요청
        console.log('온도가 임계값 이상으로 올라갔습니다. 환풍기를 켜야 합니다.');
        // 여기서 서버에 환풍기 켜기 요청을 보낼 수 있습니다.
    }
}

// 주기적으로 데이터 가져오기 및 자동화 규칙 체크
async function fetchData() {
    try {
        const response = await fetch('/data');
        const data = await response.json();
        updateChart(data);
        checkAutomationRules(data); // 자동화 규칙 체크
    } catch (error) {
        console.error('데이터를 가져오는 중 오류 발생:', error);
    }
}
