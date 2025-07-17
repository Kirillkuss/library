# Улучшенный скрипт записи HEVC с защитой от битых файлов
param(
    [string]$rtspUrl = "rtsp://localhost:8554/first",
    [int]$duration = 20
)
$endpointName = ($rtspUrl -split '/')[-1] 
$tempFile = "temp_$endpointName.hevc"
$finalFile = "$endpointName.mkv"

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

# Ожидание с таймаутом
$ffmpegProcess | Wait-Process -Timeout ($duration + 5) -ErrorAction SilentlyContinue
if (!$ffmpegProcess.HasExited) {
    $ffmpegProcess | Stop-Process -Force
    Write-Host "FFmpeg остановлен по таймауту"
}

# 2. Перепаковка в MKV (если файл не пустой)
if ((Test-Path $tempFile) -and (Get-Item $tempFile).Length -gt 0) {
    ffmpeg -loglevel warning -i $tempFile -c copy -f matroska $finalFile
    
    # Проверка результата
    if (Test-Path $finalFile) {
        $fileDuration = ffprobe -v error -show_entries format=duration -of default=nw=1:nk=1 $finalFile 2>$null
        $codecInfo = ffprobe -v error -select_streams v:0 -show_entries stream=codec_name -of default=nw=1:nk=1 $finalFile 2>$null
        
        Write-Host "Запись успешна: $finalFile"
        Write-Host "Длительность: $([math]::Round([float]$fileDuration, 2)) сек."
        Write-Host "Кодек видео: $codecInfo"
        
        # Удаляем временный файл
        Remove-Item $tempFile -ErrorAction SilentlyContinue
    }
    else {
        Write-Host "Ошибка: не удалось создать финальный файл"
    }
}
#else {
 #   Write-Host "Ошибка: временный файл не был создан или пуст"
#}