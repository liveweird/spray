package com.example

import org.specs2.mutable.Specification
import spray.routing.directives.{LoggingMagnet, DebuggingDirectives}
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

class MyServiceSpec extends Specification with Specs2RouteTest with MyService {
  def actorRefFactory = system

  import Anybody._

  def printRequestInfo(req: HttpRequest): Unit = println(req)
  val logRequestPrintln = DebuggingDirectives.logRequest(LoggingMagnet(printRequestInfo))
  implicit val routeTestTimeout = RouteTestTimeout(FiniteDuration(200, MILLISECONDS))

  "MyService" should {

    "return simple GET JSON answer" in {
      Get("/api/anybody/home") ~> myRoute ~> check {
        responseAs[String] must contain("\"home\": 0")
      }
    }

    "return simple GET JSON answer is proper" in {
      Get("/api/anybody/home") ~> myRoute ~> check {
        responseAs[Anybody].home === 0
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the available path" in {
      Put("/api/anybody/home") ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }

    "return simple POST JSON answer" in {
      Post("/api/anybody/home") ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }

    "return simple POST JSON answer for a request with arguments & form params" in {
      Post("/api/anybody/home/1", FormData(Seq("id2" -> "5"))) ~> logRequestPrintln(myRoute) ~> check {
        responseAs[String] must contain("\"home\": 6")
      }
    }

    "return huge GET JSON answer" in {
      Get("/api/anybody/junk") ~> myRoute ~> check {
        val junk1L: Junk1L = responseAs[Junk1L]

        junk1L.junk2La.length === 100
        junk1L.junk2Lb.length === 100
        junk1L.junk2Lc.length === 100
        junk1L.junk2La(0).junk3Laa.length === 100
        junk1L.junk2Lb(0).junk3Lba.length === 100
        junk1L.junk2Lc(0).junk3Lca.length === 100
        junk1L.junk2La(0).junk3Laa(0) === "Abcdef"
        junk1L.junk2Lb(0).junk3Lba(0) === 154538
        junk1L.junk2Lc(0).junk3Lca(0) === 123.54534
      }
    }

    "return long-lasting GET JSON times out" in {
      Get("/api/anybody/slowpoke") ~> myRoute ~> check {
        responseAs[Anybody].home === -1
      }
    }
  }
}
