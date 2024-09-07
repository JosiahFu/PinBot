@file:JvmName("Main")

package archives.tater.pinbot

import dev.kord.common.entity.MessageType
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.interaction.GlobalMessageCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildMessageCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.launch

fun linkTo(guildId: Snowflake, channelId: Snowflake, messageId: Snowflake) =
    "https://discord.com/channels/$guildId/$channelId/$messageId"

fun linkTo(guild: GuildBehavior, message: MessageBehavior) = linkTo(guild.id, message.channelId, message.id)

suspend fun linkTo(message: MessageBehavior) = linkTo(message.asMessage().getGuild(), message)

suspend fun main() {
    val dotenv = Dotenv.load()

    with (Kord(dotenv["BOT_TOKEN"])) {
        createGlobalMessageCommand("Pin/Unpin")

        on<GuildMessageCommandInteractionCreateEvent> {
            val message = interaction.target.fetchMessage()
            if (message.type == MessageType.ChannelPinnedMessage || message.data.applicationId.value == resources.applicationId) {
                interaction.respondEphemeral {
                    content = "Cannot pin a pin message"
                }
                return@on
            }

            val name = interaction.user.effectiveName
            if (message.isPinned) {
                kord.launch {
                    interaction.respondPublic {
                        content = "**$name** unpinned ${linkTo(message)} from the channel."
                    }
                }
                interaction.target.unpin("Pin Bot")
            } else {
                kord.launch {
                    interaction.respondPublic {
                        content = "**$name** pinned ${linkTo(message)} to the channel."
                    }
                }
                interaction.target.pin("Pin Bot")
            }
        }

        on<GlobalMessageCommandInteractionCreateEvent> {
            interaction.respondPublic {
                content = "Does not support global commands"
            }
        }

        on<MessageCreateEvent> {
            if (message.type == MessageType.ChannelPinnedMessage && message.data.author.id == selfId) {
                message.delete("Redundant pin message")
            }
        }

        on<ReadyEvent> {
            println("Logged in!")
        }

        login()
    }
}
