package com.lolboxen.nats

import io.nats.client.{Message, MessageHandler}
import org.apache.pekko.stream.stage.GraphStageLogic

class MessageHandlerAsync(target: GraphStageLogic with MessageHandler) extends MessageHandler {
  private val onMessageCallback = target.getAsyncCallback[Message](target.onMessage)
  override def onMessage(msg: Message): Unit = onMessageCallback.invoke(msg)
}
