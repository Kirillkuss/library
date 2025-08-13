        let currentVideoSize = 1000;
        const videoElement = document.getElementById('myVideo');
        const videoSource = document.getElementById('videoSource');
        const videoContainer = document.getElementById('videoContainer');
        

        function getVideoUrl(videoFile) {
            const protocol = window.location.protocol;
            const host = window.location.hostname;
            const port = window.location.port;
            const basePath = '/library/videos/';
            return `${protocol}//${host}${port ? ':' + port : ''}${basePath}${videoFile}`;
        }

        function changeCamera(videoUrl, button) {
            document.querySelectorAll('.control-section:first-child .btn').forEach(btn => {
                btn.classList.remove('active');
            });
            button.classList.add('active');
            videoSource.src = videoUrl;
            videoElement.load();
            videoElement.play().catch(e => console.log("Autoplay prevented:", e));
        }
        
        function setVideoSize(width, button) {
            currentVideoSize = width;
            videoContainer.style.maxWidth = width + 'px';
            
            document.querySelectorAll('.control-section:last-child .btn').forEach(btn => {
                if (btn.textContent.includes('Fullscreen')) return;
                btn.classList.remove('active');
            });
            button.classList.add('active');
        }

        function toggleFullscreen() {
            if (!document.fullscreenElement) {
                videoElement.requestFullscreen().catch(err => {
                    console.error(`Ошибка при переходе в полноэкранный режим: ${err.message}`);
                });
            } else {
                document.exitFullscreen();
            }
        }
         window.onload = function() {
            const defaultVideoUrl = getVideoUrl('second.mp4');
            document.getElementById('videoSource').src = defaultVideoUrl;
            document.getElementById('myVideo').load();
            document.getElementById('myVideo').play().catch(e => {
                console.log("Autoplay prevented, пользователь должен начать воспроизведение вручную");
            });
        };