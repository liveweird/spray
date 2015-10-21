package com.example

import akka.actor.Actor
import spray.httpx.SprayJsonSupport
import spray.routing._
import spray.http._
import spray.json._
import spray.httpx.SprayJsonSupport._
import MediaTypes._

case class Anybody(home: Int)

object Anybody extends DefaultJsonProtocol with SprayJsonSupport {
  implicit def anybodyJsonFormat: RootJsonFormat[Anybody] = jsonFormat1(Anybody.apply)
}

case class Junk1L(junk2La: List[Junk2La], junk2Lb: List[Junk2Lb], junk2Lc: List[Junk2Lc])

object Junk1L extends DefaultJsonProtocol with SprayJsonSupport {
  implicit def junk1LJsonFormat: RootJsonFormat[Junk1L] = jsonFormat3(Junk1L.apply)
}

case class Junk2La(junk3Laa: List[String])

object Junk2La extends DefaultJsonProtocol with SprayJsonSupport {
  implicit def junk2LaJsonFormat: RootJsonFormat[Junk2La] = jsonFormat1(Junk2La.apply)
}

case class Junk2Lb(junk3Lba: List[Int])

object Junk2Lb extends DefaultJsonProtocol with SprayJsonSupport {
  implicit def junk2LbJsonFormat: RootJsonFormat[Junk2Lb] = jsonFormat1(Junk2Lb.apply)
}

case class Junk2Lc(junk3Lca: List[Double])

object Junk2Lc extends DefaultJsonProtocol with SprayJsonSupport {
  implicit def junk2LcJsonFormat: RootJsonFormat[Junk2Lc] = jsonFormat1(Junk2Lc.apply)
}

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  val tonOfJunk = new Junk1L(
      List.fill(100)(new Junk2La(List.fill(100)("Abcdef"))),
      List.fill(100)(new Junk2Lb(List.fill(100)(154538))),
      List.fill(100)(new Junk2Lc(List.fill(100)(123.54534)))
  )

  val myRoute =
    pathPrefix("api") {
      path("anybody" / "home") {
        get {
          respondWithMediaType(`application/json`) {
            complete {
              Anybody(0)
            }
          }
        }
      } ~
      path("anybody" / "home" / Segment) { id =>
        post {
          respondWithMediaType(`application/json`) {
            formFields('id2.as[Int]) { id2 =>
              complete {
                Anybody(id.toInt + id2)
              }
            }
          }
        }
      } ~
      path("anybody" / "junk") {
        get {
          respondWithMediaType(`application/json`) {
            complete {
              tonOfJunk
            }
          }
        }
      }
    }
}