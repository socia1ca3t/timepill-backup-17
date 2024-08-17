# 定义常量
$processName = "timepill-backup-17"
$tempDir = "$env:TEMP\timepill-backup\"

# 检查执行策略
#Set-ExecutionPolicy -ExecutionPolicy Unrestricted -Scope CurrentUser

# 启动程序
#Start-Process -FilePath "timepill-backup-17.exe"

# 创建临时文件夹
New-Item -ItemType Directory -Force -Path $tempDir

$elapsedTime = 0
$processRun = $false
# 持续监控进程状态
while (!$processRun) {

    $process = Get-Process -Name $processName -ErrorAction SilentlyContinue

    if ($process) {

        $processRun = $true

		while (Get-Process -Name $processName -ErrorAction SilentlyContinue) {
			Start-Sleep -Seconds 2
		}
		# 进程关闭，清除临时文件夹后结束
		Remove-Item -Recurse -Force $tempDir

    } else {

        Start-Sleep -Seconds 2
		# 超时未检测到进程，设置$processRun为$false，清除临时文件夹后结束
        if ($elapsedTime -ge 60) {
            $processRun = $true
            Remove-Item -Recurse -Force $tempDir
        }
    }
}