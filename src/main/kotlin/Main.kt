package archives.tater.unbalancedmusket

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.effectiveName
import dev.kord.core.event.interaction.MessageCommandInteractionCreateEvent
import dev.kord.core.on
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.launch

suspend fun main() {
    val dotenv = Dotenv.load()

    with (Kord(dotenv["BOT_TOKEN"])) {
        createGlobalMessageCommand("Pin/Unpin")

        on<MessageCommandInteractionCreateEvent> {
            if (interaction.target.fetchMessage().isPinned) {
                kord.launch {
                    interaction.respondPublic {
                        content = "${interaction.user.effectiveName} unpinned a message"
                    }
                }
                interaction.target.unpin("Pin Bot")
            } else {
                kord.launch {
                    interaction.respondPublic {
                        content = "${interaction.user.effectiveName} pinned a message"
                    }
                }
                interaction.target.pin("Pin Bot")
            }
        }

        login()
    }
}
