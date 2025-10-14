import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class SwarmConfigurable : Configurable {

    private var swarmPathField: TextFieldWithBrowseButton? = null

    override fun createComponent(): JComponent {
        swarmPathField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                "Select swarm Executable",
                "Choose the location of swarm.exe",
                null,
                FileChooserDescriptorFactory.createSingleFileDescriptor("exe")
            )
        }

        return FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("swarm executable path:"), swarmPathField!!, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun isModified(): Boolean {
        val settings = SwarmSettings.getInstance()
        return swarmPathField?.text != settings.state.swarmExecutablePath
    }

    override fun apply() {
        val settings = SwarmSettings.getInstance()
        settings.state.swarmExecutablePath = swarmPathField?.text ?: ""
    }

    override fun reset() {
        val settings = SwarmSettings.getInstance()
        swarmPathField?.text = settings.state.swarmExecutablePath
    }

    override fun getDisplayName(): String = "swarm"
}