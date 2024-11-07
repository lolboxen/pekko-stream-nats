package com.lolboxen.nats

import com.lolboxen.nats.ConnectionSource.Protocol
import io.nats.client.{Message, MessageHandler}
import org.apache.pekko.stream.{Attributes, FlowShape}

abstract class PushSubscriptionLogic[S](shape: FlowShape[Protocol, Message], inheritedAttributes: Attributes)
  extends SubscriptionLogic[Message, S](shape, inheritedAttributes) with MessageHandler {
  override def onPull(): Unit = ()

  override def onMessage(msg: Message): Unit = emit(shape.out, msg)
}
