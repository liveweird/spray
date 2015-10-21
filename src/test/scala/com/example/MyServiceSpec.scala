package com.example

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class MyServiceSpec extends Specification with Specs2RouteTest with MyService {
  def actorRefFactory = system

  import Anybody._

  "MyService" should {

    "return simple GET JSON answer" in {
      Get("/api/anybody/home") ~> myRoute ~> check {
        responseAs[String] must contain("\"home\": true")
      }
    }

    "return simple GET JSON answer is proper" in {
      Get("/api/anybody/home") ~> myRoute ~> check {
        responseAs[Anybody].home === true
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
  }
}
