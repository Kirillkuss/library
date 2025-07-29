param(
    [string]$rtspUrl,
    [int]$duration,
    [string]$outputDir = "."
)

$endpointName = ($rtspUrl -split '/')[-1] 
$tempFile = Join-Path $outputDir "temp_$endpointName.hevc"
$mkvFile = Join-Path $outputDir "$endpointName.mkv"
$mp4File = Join-Path $outputDir "$endpointName.mp4"

Remove-Item $tempFile -ErrorAction SilentlyContinue
Remove-Item $mkvFile -ErrorAction SilentlyContinue
Remove-Item $mp4File -ErrorAction SilentlyContinue

if (!(Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
}

$ffmpegProcess = Start-Process -NoNewWindow -PassThru -FilePath "ffmpeg" -ArgumentList @(
    "-y", 
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
    throw "Таймаут записи HEVC"
}

if ((Test-Path $tempFile) -and (Get-Item $tempFile).Length -gt 0) {
    $mkvResult = & ffmpeg -y -loglevel warning -i $tempFile -c copy -f matroska $mkvFile 2>&1
    if (Test-Path $mkvFile) {
        $fileInfo = ffprobe -v error -show_entries format=duration -of default=nw=1:nk=1 $mkvFile 2>$null
        $mp4Result = & ffmpeg -y -i $mkvFile -vf "scale=1920:1080" -c:v libx264 -preset fast -crf 23 -c:a aac -b:a 128k -f mp4 $mp4File 2>&1
        if (Test-Path $mp4File) {
            $mp4Info = ffprobe -v error -show_entries stream=codec_name,width,height -of default=nw=1:nk=1 $mp4File 2>$null
            Remove-Item $tempFile -ErrorAction SilentlyContinue
            Remove-Item $mkvFile -ErrorAction SilentlyContinue
        }
    }
}