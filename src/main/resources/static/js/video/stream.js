var protocol = window.location.protocol
var hostname = window.location.hostname;
var port = window.location.port;

        let currentCamera = 'one';
        let isRecording = false;
        let recordingTimer = null;

        document.addEventListener('DOMContentLoaded', function() {
            setStreamSize(640);
            updateButtonStates();
            document.addEventListener('fullscreenchange', updateFullscreenStyles);
            document.addEventListener('webkitfullscreenchange', updateFullscreenStyles);
            document.addEventListener('mozfullscreenchange', updateFullscreenStyles);
            document.addEventListener('MSFullscreenChange', updateFullscreenStyles);
        });

        function updateButtonStates() {
            const cameraButtons = document.querySelectorAll('.control-section:first-child .btn-video-control');
            cameraButtons.forEach(btn => {
                if(btn.textContent.includes('Камера 1')) {
                    btn.classList.add('active');
                } else {
                    btn.classList.remove('active');
                }
            });
            
            const sizeButtons = document.querySelectorAll('.control-section:nth-child(2) .btn-video-control');
            sizeButtons.forEach(btn => {
                if(btn.textContent.includes('640')) {
                    btn.classList.add('active');
                } else {
                    btn.classList.remove('active');
                }
            });
        }

        function switchCamera(cameraName) {
            currentCamera = cameraName;
            const frame = document.getElementById('cameraFrame');
            frame.src = `http://izoo.itrail.by:380/${cameraName}`;
            const buttons = document.querySelectorAll('.control-section:first-child .btn-video-control');
            buttons.forEach(btn => btn.classList.remove('active'));
            event.target.classList.add('active');
        }

        function setStreamSize(height) {
            const frame = document.getElementById('cameraFrame');
            const container = document.getElementById('videoContainer');
            frame.style.height = height + 'px';
            container.style.height = height + 'px';
            const buttons = document.querySelectorAll('.control-section:nth-child(2) .btn-video-control');
            buttons.forEach(btn => {
                if(btn.textContent.includes(height)) {
                    btn.classList.add('active');
                } else {
                    btn.classList.remove('active');
                }
            });
        }

        function toggleFullscreen() {
            const container = document.getElementById('videoContainer');
            if (!document.fullscreenElement) {
                if (container.requestFullscreen) {
                    container.requestFullscreen();
                } else if (container.webkitRequestFullscreen) {
                    container.webkitRequestFullscreen();
                } else if (container.mozRequestFullScreen) {
                    container.mozRequestFullScreen();
                } else if (container.msRequestFullscreen) {
                    container.msRequestFullscreen();
                }
            } else {
                if (document.exitFullscreen) {
                    document.exitFullscreen();
                } else if (document.webkitExitFullscreen) {
                    document.webkitExitFullscreen();
                } else if (document.mozCancelFullScreen) {
                    document.mozCancelFullScreen();
                } else if (document.msExitFullscreen) {
                    document.msExitFullscreen();
                }
            }
        }

        function updateFullscreenStyles() {
            const container = document.getElementById('videoContainer');
            const frame = document.getElementById('cameraFrame');
            
            if (document.fullscreenElement) {
                container.style.maxWidth = 'none';
                container.style.width = '100%';
                container.style.height = '100%';
                frame.style.width = '100%';
                frame.style.height = '100%';
            } else {
                container.style.maxWidth = '1100px';
                frame.style.width = '100%';
                const activeSizeBtn = document.querySelector('.control-section:nth-child(2) .btn-video-control.active');
                if (activeSizeBtn) {
                    const height = parseInt(activeSizeBtn.textContent.match(/\d+/)[0]);
                    setStreamSize(height);
                }
            }
        }

        function startRecording() {

            let path = 'unicast/c1-0/s0/live';
            if (isRecording) {
                alert('Запись уже идет!');
                return;
            }
            if(  currentCamera === 'one' ){
                path = 'unicast/c1-0/s0/live';
            }else{
                if( currentCamera === 'two' ){
                    path = 'unicast/c2-0/s0/live';
                }else{
                    path = 'unicast/c3-0/s0/live';
                }
            }



            const duration = document.getElementById('recordDuration').value;
            const streamPath = `rtsp://itrail:12345Itrail@10.0.50.20:554/${path}`;
            isRecording = true;
            updateRecordingUI('recording', `Запись началась (${duration} сек)`);
            
            const recordData = {
                path: streamPath,
                duration: parseInt(duration)
            };

            fetch(protocol + "//"+ hostname + ':' + port + '/library/videos/record', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(recordData)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при запуске записи');
                }
                return response.json();
            })
            .then(data => {
                isRecording = false;
                if (recordingTimer) {
                    clearTimeout(recordingTimer);
                }
                updateRecordingUI('finished', 'Запись завершена успешно!');

                setTimeout(() => {
                    updateRecordingUI('idle', '');
                }, 3000);
            })
            .catch(error => {
                console.error('Ошибка:', error);
                isRecording = false;
                if (recordingTimer) {
                    clearTimeout(recordingTimer);
                }
                updateRecordingUI('error', 'Ошибка: ' + error.message);
                setTimeout(() => {
                    updateRecordingUI('idle', '');
                }, 3000);
            });

            let timeLeft = parseInt(duration);
            updateCountdown(timeLeft);
            
            recordingTimer = setInterval(() => {
                timeLeft--;
                if (timeLeft <= 0) {
                    clearInterval(recordingTimer);
                    if (isRecording) {
                        updateRecordingUI('processing', 'Обработка записи...');
                    }
                } else {
                    updateCountdown(timeLeft);
                }
            }, 1000);
        }

        function updateCountdown(secondsLeft) {
            const statusText = document.getElementById('statusText');
            if (statusText) {
                statusText.textContent = `Идет запись... Осталось: ${secondsLeft} сек`;
            }
        }

        function updateRecordingUI(status, message = '') {
            const recordButton = document.getElementById('recordButton');
            const recordingStatus = document.getElementById('recordingStatus');
            const statusText = document.getElementById('statusText');
            
            switch(status) {
                case 'recording':
                    recordButton.disabled = true;
                    recordButton.innerHTML = '<i class="fas fa-record-vinyl me-1"></i> Идет запись';
                    recordingStatus.style.display = 'inline-block';
                    statusText.textContent = message;
                    recordingStatus.querySelector('i').className = 'fas fa-circle text-danger blink';
                    break;
                    
                case 'processing':
                    recordButton.disabled = true;
                    recordButton.innerHTML = '<i class="fas fa-cog fa-spin me-1"></i> Обработка';
                    statusText.innerHTML =  message;
                    recordingStatus.querySelector('i').className = 'fas fa-cog fa-spin text-primary';
                    break;
                    
                case 'finished':
                    recordButton.disabled = true;
                    recordButton.innerHTML = '<i class="fas fa-check me-1"></i> Завершено';
                    recordingStatus.style.display = 'inline-block';
                    statusText.innerHTML = '<i class="fas fa-check-circle text-success me-1"></i> ' + message;
                    recordingStatus.querySelector('i').style.display = 'none';
                    break;
                    
                case 'error':
                    recordButton.disabled = false;
                    recordButton.innerHTML = '<i class="fas fa-record-vinyl me-1"></i> Начать запись';
                    recordingStatus.style.display = 'inline-block';
                    statusText.innerHTML = '<i class="fas fa-exclamation-triangle text-warning me-1"></i> ' + message;
                    recordingStatus.querySelector('i').style.display = 'none';
                    break;
                    
                case 'idle':
                default:
                    recordButton.disabled = false;
                    recordButton.innerHTML = '<i class="fas fa-record-vinyl me-1"></i> Начать запись';
                    recordingStatus.style.display = 'none';
                    statusText.textContent = '';
                    break;
            }
        }

        const style = document.createElement('style');
        style.textContent = `
            .blink {
                animation: blink-animation 1s steps(2, start) infinite;
            }
            @keyframes blink-animation {
                to {
                    visibility: hidden;
                }
            }
            .recording-status {
                margin-left: 10px;
                padding: 5px 10px;
                background-color: #f8f9fa;
                border-radius: 4px;
                display: inline-flex;
                align-items: center;
                gap: 5px;
            }
        `;
        document.head.appendChild(style);