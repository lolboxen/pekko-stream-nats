package com.lolboxen.nats

import com.lolboxen.nats.ConnectionSource.Protocol
import io.nats.client.{Connection, Dispatcher, Message}
import org.apache.pekko.stream.stage.{GraphStage, GraphStageLogic}
import org.apache.pekko.stream.{Attributes, FlowShape, Inlet, Outlet}

class CoreSubscriptionSource(subject: String) extends GraphStage[FlowShape[Protocol, Message]] {

  protected val in: Inlet[Protocol] = Inlet("CoreSubscriptionSource.in")
  protected val out: Outlet[Message] = Outlet("CoreSubscriptionSource.out")
  override def shape: FlowShape[Protocol, Message] = FlowShape(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new CoreSubscriptionSourceLogic(subject, inheritedAttributes, shape)
}

class CoreSubscriptionSourceLogic(subject: String, inheritedAttributes: Attributes, shape: FlowShape[Protocol, Message])
  extends PushSubscriptionLogic[Dispatcher](shape, inheritedAttributes) {

  override protected def subscribe(connection: Connection): Dispatcher = {
    logSubscriptionChange(subject, subscribed = true)
    connection.createDispatcher(new MessageHandlerAsync(this)).subscribe(subject)
  }

  override protected def unsubscribe(subscription: Dispatcher): Unit = {
    logSubscriptionChange(subject, subscribed = false)
    if (subscription.isActive) subscription.unsubscribe(subject)
  }
}
