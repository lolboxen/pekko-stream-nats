package com.lolboxen.nats

import com.lolboxen.nats.ConnectionSource.{Connected, Protocol}
import io.nats.client.{Connection, Dispatcher, Message, MessageHandler}
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.{Flow, Keep}
import org.apache.pekko.stream.testkit.scaladsl.{TestSink, TestSource}
import org.apache.pekko.testkit.TestKit
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import util.MockUtils.captureAndReturn

import scala.concurrent.Promise

class CoreSubscriptionSourceTest
  extends TestKit(ActorSystem("CoreSubscriptionSourceTest"))
    with AnyFlatSpecLike
    with Matchers
    with ScalaFutures
    with MockFactory
    with BeforeAndAfterAll {

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  it should "emit messages after connecting" in {
    val handler = Promise[MessageHandler]()
    val dispatcher = mock[Dispatcher]
    val connection = mock[Connection]
    val message = mock[Message]
    (connection.createDispatcher(_: MessageHandler)).expects(*).once().onCall(captureAndReturn(handler, dispatcher))
    (dispatcher.subscribe(_: String)).expects("subject").once().returning(dispatcher)
    (dispatcher.isActive _).expects().once().returning(true)
    (dispatcher.unsubscribe(_: String)).expects("subject").once()

    val (pub, sub) = TestSource[Protocol]()
      .via(Flow.fromGraph(new CoreSubscriptionSource("subject")))
      .toMat(TestSink())(Keep.both)
      .run()

    pub.sendNext(Connected(connection))
    whenReady(handler.future) { messageHandler =>
      messageHandler.onMessage(message)
      messageHandler.onMessage(message)
      sub.request(2).expectNext(message, message)
      pub.sendComplete()
      sub.expectComplete()
    }
  }
}
