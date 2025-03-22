package dev.gamerzero.rokasthelper

import com.aallam.openai.api.audio.SpeechRequest
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import dev.gamerzero.rokasthelper.ai.Assistant
import dev.gamerzero.rokasthelper.configuration.ApplicationProperties
import dev.gamerzero.rokasthelper.service.AppUserService
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSource
import org.springframework.stereotype.Component
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.methods.send.SendVoice
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Voice

@Component
class RokastHelperBot(
    private val applicationProperties: ApplicationProperties,
    private val aiAssistant: Assistant,
    private val appUserService: AppUserService
) : SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private val telegramClient = OkHttpTelegramClient(botToken)
    private val openai = OpenAI(token = applicationProperties.openAiKey)
    override fun getBotToken(): String = applicationProperties.telegramBotToken
    override fun getUpdatesConsumer(): LongPollingUpdateConsumer = this

    override fun consume(update: Update) {
        if (!update.hasMessage()) return
        val appUser = appUserService.findOrCreateUser(
            update.message.from.id,
            update.message.from.userName
        )
        val typingAction = SendChatAction(
            update.message.chat.id.toString(),
            "typing"
        )
        telegramClient.execute(typingAction)

        val messageText = when {
            update.message.hasText() -> update.message.text
            update.message.hasVoice() -> runBlocking { transcription(update.message.voice) }
            else -> "This message contains unsupported formats such as photo or video (only text and voice messages are supported)"
        }
        telegramClient.execute(typingAction)

        val llmResponse =
            aiAssistant.chat(appUser.id, appUser.username, messageText, appUser.excludedIngredients)

        val voiceFile = runBlocking {
            openai.speech(
                SpeechRequest(
                    model = ModelId("tts-1"),
                    input = llmResponse,
                    voice = com.aallam.openai.api.audio.Voice.Alloy
                )
            )
        }

        val textResponse = SendMessage
            .builder()
            .parseMode(ParseMode.MARKDOWN)
            .chatId(update.message.chatId)
            .text(llmResponse)
            .build()
        val response = SendVoice
            .builder()
            .chatId(update.message.chatId)
            .voice(InputFile(voiceFile.inputStream(), "Voice response"))
            .build()
        telegramClient.execute(textResponse)
        telegramClient.execute(response)
    }

    private suspend fun transcription(voice: Voice): String {
        val fileInfo = telegramClient.execute(GetFile(voice.fileId))
        val file = telegramClient.downloadFile(fileInfo.filePath)

        val request = TranscriptionRequest(
            audio = FileSource(
                name = "${file.name}.${voice.mimeType.removePrefix("audio/")}",
                source = file.inputStream().asSource()
            ),
            model = ModelId("whisper-1"),
        )

        val transcription = openai.transcription(request)
        return transcription.text
    }
}
