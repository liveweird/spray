package com.example

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class MyServiceSpec extends Specification with Specs2RouteTest with MyService {
  def actorRefFactory = system

  "MyService" should {

    "return simple GET JSON answer" in {
      Get("/api/anybody/home") ~> myRoute ~> check {
        responseAs[String] must contain("\"home\": true")
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

    "return simple POST JSON answer for a request with arguments" in {
      Post("/api/anybody/home/1") ~> myRoute ~> check {
        responseAs[String] must contain("\"home\": false")
      }
    }
  }
}
