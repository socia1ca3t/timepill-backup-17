[Version] 
Class=IEXPRESS 
SEDVersion=3 
[Options] 
PackagePurpose=InstallApp 
ShowInstallProgramWindow=0 
HideExtractAnimation=1 
UseLongFileName=1 
InsideCompressed=0 
CAB_FixedSize=0 
CAB_ResvCodeSigning=0 
RebootMode=N 
InstallPrompt=%InstallPrompt% 
DisplayLicense=%DisplayLicense% 
FinishMessage=%FinishMessage% 
TargetName=%TargetName% 
FriendlyName=%FriendlyName% 
AppLaunched=%AppLaunched% 
PostInstallCmd=%PostInstallCmd% 
AdminQuietInstCmd=%AdminQuietInstCmd% 
UserQuietInstCmd=%UserQuietInstCmd% 
SourceFiles=SourceFiles 
[Strings] 
InstallPrompt= 
DisplayLicense= 
FinishMessage= 
TargetName=timepill-backup-17-windows-auto-delete-temp-files.exe
FriendlyName=timepill-backup 
AppLaunched=cmd.exe /d /c "main.cmd"
PostInstallCmd=<None> 
AdminQuietInstCmd= 
UserQuietInstCmd= 
FILE0="target\native\timepill-backup-17.exe" 
FILE1=".github\workflows\main.cmd"
FILE2=".github\workflows\delete_temp_dir_after_exit.ps1"
FILE3=".github\workflows\run_utf8_app.cmd"
[SourceFiles] 
SourceFiles0="D:\a\timepill-backup-17\timepill-backup-17\" 
[SourceFiles0] 
%FILE0%=
%FILE1%=
%FILE2%=
%FILE3%=