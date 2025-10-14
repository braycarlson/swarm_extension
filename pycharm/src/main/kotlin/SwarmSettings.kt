import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "SwarmSettings",
    storages = [Storage("swarm.xml")]
)
@Service
class SwarmSettings : PersistentStateComponent<SwarmSettings.State> {

    data class State(
        var swarmExecutablePath: String = ""
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(): SwarmSettings {
            return ApplicationManager.getApplication().getService(SwarmSettings::class.java)
        }
    }
}