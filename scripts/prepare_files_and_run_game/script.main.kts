import java.io.*
import java.net.URL
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.nio.file.Files

fun download(urlString: String, dest: File) {
    URL(urlString).openStream().use { input ->
        FileOutputStream(dest).use { output ->
            input.copyTo(output)
        }
    }
}

fun unzip(zipFile: File, destDir: File) {
    destDir.mkdirs()
    ZipInputStream(FileInputStream(zipFile)).use { zis ->
        var entry: ZipEntry? = zis.nextEntry
        while (entry != null) {
            val newFile = File(destDir, entry.name)
            if (entry.isDirectory) {
                newFile.mkdirs()
            } else {
                newFile.parentFile.mkdirs()
                FileOutputStream(newFile).use { fos ->
                    zis.copyTo(fos)
                }
            }
            entry = zis.nextEntry
        }
    }
}

fun main() {
    val currentDir = System.getProperty("user.dir")
    val tempFolder = File(currentDir, "Temporary")
    tempFolder.mkdir()
    val brewBinaryFolder = File(tempFolder, "Brew Binary")
    brewBinaryFolder.mkdir()
    val desktopBinaryFolder = File(tempFolder, "Desktop Binary")
    desktopBinaryFolder.mkdir()
    val filesFolder = File(tempFolder, "Files")
    filesFolder.mkdir()

    val brewUrl = "https://archive.org/download/doomrpg_brew/doomrpg.zip"
    val brewFile = File(filesFolder, "doomrpg.zip")
    download(brewUrl, brewFile)

    val desktopUrl = "https://github.com/Erick194/DoomRPG-RE/archive/main.zip"
    val desktopFile = File(filesFolder, "DoomRPG-RE.zip")
    download(desktopUrl, desktopFile)

    unzip(brewFile, brewBinaryFolder)
    unzip(desktopFile, desktopBinaryFolder)

    // Handle GitHub zip root folder
    val subFolders = desktopBinaryFolder.listFiles()?.filter { it.isDirectory }
    if (subFolders != null && subFolders.size == 1) {
        val subFolder = subFolders[0]
        subFolder.listFiles()?.forEach { file ->
            file.copyRecursively(File(desktopBinaryFolder, file.name), overwrite = true)
        }
        subFolder.deleteRecursively()
    }

    // Build the project (assuming CMake and dependencies are installed)
    val pbCMake = ProcessBuilder("cmake", ".")
    pbCMake.directory(desktopBinaryFolder)
    pbCMake.start().waitFor()

    val pbBuild = ProcessBuilder("cmake", "--build", ".", "--config", "Release")
    pbBuild.directory(desktopBinaryFolder)
    pbBuild.start().waitFor()

    // Move executables from Release folder to root if present
    val releaseFolder = File(desktopBinaryFolder, "Release")
    if (releaseFolder.exists()) {
        releaseFolder.listFiles()?.filter { it.name.endsWith(".exe") }?.forEach { exe ->
            exe.copyTo(File(desktopBinaryFolder, exe.name), overwrite = true)
        }
    }

    // Copy Desktop Binary to Game
    val gameFolder = File(currentDir, "Game")
    desktopBinaryFolder.copyRecursively(gameFolder, overwrite = true)

    // Copy doomrpg.bar to Game
    val barFile = File(brewBinaryFolder, "doomrpg.bar")
    if (barFile.exists()) {
        barFile.copyTo(File(gameFolder, "doomrpg.bar"), overwrite = true)
    } else {
        println("Warning: doomrpg.bar not found in Brew Binary folder.")
    }

    // Run BarToZip.exe in Game folder
    val pbBarToZip = ProcessBuilder("BarToZip.exe")
    pbBarToZip.directory(gameFolder)
    pbBarToZip.start().waitFor()

    // Run DoomRPG.exe in Game folder to check
    val pbDoomRPG = ProcessBuilder("DoomRPG.exe")
    pbDoomRPG.directory(gameFolder)
    pbDoomRPG.start().waitFor()
}