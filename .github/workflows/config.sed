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
TargetName=timepill-backup-17-windows.exe 
FriendlyName=timepill-backup 
AppLaunched=cmd.exe /d /c "run_utf8.bat" 
PostInstallCmd=<None> 
AdminQuietInstCmd= 
UserQuietInstCmd= 
FILE0="target/native/timepill-backup-17.exe" 
FILE1="run_utf8.bat" 
[SourceFiles] 
SourceFiles0=%~dp0
[SourceFiles0] 
%FILE0%= 
%FILE1%= 