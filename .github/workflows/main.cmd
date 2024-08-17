@echo off

start "" powershell -ExecutionPolicy Bypass -WindowStyle Hidden -File delete_temp_dir_after_exit.ps1
cmd.exe /d /c run_utf8_app.cmd
