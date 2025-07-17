$rtspUrl = "rtsp://localhost:8554/four"
$duration = 20
$endpointName = ($rtspUrl -split '/')[-1] 
$tempFile = "temp_$endpointName.hevc"
$mkvFile = "$endpointName.mkv"
$mp4File = "$endpointName.mp4"

function Log-Info {
    param([string]$message)
    Write-Host ("[INFO] $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') - $message")
}

function Log-Error {
    param([string]$message)
    Write-Host ("[ERROR] $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') - $message") -ForegroundColor Red
}

try {
    Log-Info "Начало записи RTSP потока в HEVC..."
    $ffmpegProcess = Start-Process -NoNewWindow -PassThru -FilePath "ffmpeg" -ArgumentList @(
        "-loglevel", "warning",
        "-fflags", "+genpts+igndts",
        "-use_wallclock_as_timestamps", "1",
        "-i", $rtspUrl,
        "-t", $duration.ToString(),
        "-c:v", "copy",
        "-an",
        "-f", "hevc",
        $tempFile
    )

    $ffmpegProcess | Wait-Process -Timeout ($duration + 10) -ErrorAction SilentlyContinue
    if (!$ffmpegProcess.HasExited) {
        $ffmpegProcess | Stop-Process -Force
        Log-Error "FFmpeg остановлен по таймауту"
        throw "Таймаут записи HEVC"
    }

    if ((Test-Path $tempFile) -and (Get-Item $tempFile).Length -gt 0) {
        Log-Info "Конвертация HEVC в MKV..."
        $mkvResult = & ffmpeg -loglevel warning -i $tempFile -c copy -f matroska $mkvFile 2>&1
        
        if ($LASTEXITCODE -ne 0) {
            Log-Error "Ошибка конвертации в MKV: $mkvResult"
            throw "Не удалось создать MKV файл"
        }

        if (Test-Path $mkvFile) {
            $fileInfo = ffprobe -v error -show_entries format=duration -of default=nw=1:nk=1 $mkvFile 2>$null
            Log-Info "MKV успешно создан: $mkvFile (Длительность: $([math]::Round([float]$fileInfo, 2)) сек.)"

            Log-Info "Начало конвертации MKV в MP4..."
            $mp4Result = & ffmpeg -i $mkvFile -vf "scale=1920:1080" -c:v libx264 -preset fast -crf 23 -c:a aac -b:a 128k -f mp4 $mp4File 2>&1
            
            if ($LASTEXITCODE -ne 0) {
                Log-Error "Ошибка конвертации в MP4: $mp4Result"
                throw "Не удалось создать MP4 файл"
            }

            if (Test-Path $mp4File) {
                $mp4Info = ffprobe -v error -show_entries stream=codec_name,width,height -of default=nw=1:nk=1 $mp4File 2>$null
                Log-Info "MP4 успешно создан: $mp4File (Разрешение: $mp4Info)"

                Log-Info "Удаление временных файлов..."
                Remove-Item $tempFile -ErrorAction SilentlyContinue
                Remove-Item $mkvFile -ErrorAction SilentlyContinue
                
                Log-Info "Процесс завершен успешно. Оставлен только файл: $mp4File"
            }
        }
    } else {
        Log-Error "Временный HEVC файл не был создан или пуст"
        throw "Отсутствует HEVC файл"
    }
}
catch {
    Log-Error "Ошибка в процессе выполнения: $_"
    Remove-Item $tempFile -ErrorAction SilentlyContinue
    Remove-Item $mkvFile -ErrorAction SilentlyContinue
    Remove-Item $mp4File -ErrorAction SilentlyContinue
    exit 1
}