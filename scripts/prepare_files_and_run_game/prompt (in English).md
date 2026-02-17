I want you to write a script that downloads and unpacks the files for using them for gaming directory

Script is made for Windows, should be written in Kotlin and uses Kotlin Notebook as a main platform to be launched

Script has 5 steps, additionally add logging each time step starts/ends. Also log each warning/error if they're thrown in code

Each step should also contain measuring time how much did it took to complete this step and the last step should also contain total time it took for script to complete

Script's code should be highly readable, using all or most Kotlin features (may use carrying for logging) and should use coroutines



### 1. Create folders:
In script's directory create DoomRPG folder, it contains 2 folders - Game and Temporary
Temporary folder has 3 subfolders - Brew Binary, Desktop Binary and Files

to avoid saying same thing, lets introduce these terms for directories:

DoomRPG 
: folder in script's directory, will contain all the folders and files used next

Game
: DoomRPG/Game

Temporary
: DoomRPG/Temporary 

Brew Binary
: DoomRPG/Temporary/Brew Binary 

Desktop Binary
: DoomRPG/Temporary/Desktop Binary

Files
: DoomRPG/Temporary/Files


Folder structure created:\
DoomRPG\
  ├── Game\
  └── Temporary\
      ├── Brew Binary\
      ├── Desktop Binary\
      └── Files

### 2. Download necessary files:

Download these files

https://archive.org/download/doomrpg_brew/doomrpg.zip

https://github.com/Erick194/DoomRPG-RE/releases/latest

to Files

When you will generate direct link for latest release from link for the second file, you should not use any hardcoded variables for accessing exact release, because the way release named may change (other than user and repository name, for now we assume they won't change)

Save paths to downloaded files

first one as brewBinary variable

second one as desktopBinary variable


### 3. Extract archives:
Extract brewBinary to Brew Binary

Inside desktopBinary archive there is a DoomRPG folder with content
Extract only folder's content to Desktop Binary, do not extract the folder itself


### 4. Final preparations:
Copy content of Desktop Binary into Game

Copy file doomrpg.bar from Brew Binary into Game

Run BarToZip.exe in Game
(
note:

When we try to run BarToZip manually, not by code call, it shows command line and lists its progress in it, until it stops and requires user to press any key for closing window.

Do the same, but instead - run it silently (do not write its progress to kotlin's notebook console) and do not force user to press a key in commandline after its being done, instead do it programmatically yourself

After completing this step BarToZip should not be active as a process 
)


### 5. Run the game to check everything is okay:
Open DoomRPG folder in explorer on background 
(this window should be behind DoomRPG.exe window)

Run DoomRPG.exe on foreground 
(if its possible, force it to take whole window size to run/run it in fullscreen)