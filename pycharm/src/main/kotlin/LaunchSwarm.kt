import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import java.io.File
import java.io.IOException

class LaunchSwarm : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val settings = SwarmSettings.getInstance()
        val executablePath = settings.state.swarmExecutablePath

        if (executablePath.isEmpty()) {
            Messages.showErrorDialog(
                "The swarm executable path not configured. Please go to File → Settings → Tools → Swarm to configure it.",
                "swarm is Not Configured"
            )
            return
        }

        if (!File(executablePath).exists()) {
            Messages.showErrorDialog(
                "The swarm executable was not found at: $executablePath",
                "Executable Not Found"
            )
            return
        }

        try {
            val project = e.project

            val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

            val targetPath = when {
                virtualFile == null -> project?.basePath ?: "."
                virtualFile.isDirectory -> virtualFile.path
                else -> virtualFile.parent?.path ?: virtualFile.path
            }

            ProcessBuilder(executablePath, targetPath)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start()
        } catch (ex: IOException) {
            Messages.showErrorDialog("Failed: ${ex.message}", "Error")
        }
    }
}