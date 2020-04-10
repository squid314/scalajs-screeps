package com.squid314.screeps.upload

import scalaj.http.Http
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.io.Source


case class Creds(accountAlias: String, token: String, ptr: Boolean)

/** main class to upload the generated file */
object Main extends App {
    implicit val credsFormat: JsonFormat[Creds] = jsonFormat3(Creds)

    // hacky git branch name extraction, but whatever
    for (githead <- Source.fromFile(".git/HEAD").getLines
        .toList
        .headOption
        .filter(_.startsWith("ref: refs/heads/"))
        .map(_.substring("ref: refs/heads/".length))) {

        val upload = Map(
            "branch" -> Left(githead),
            "modules" -> Right(Map(
                "main" -> Source.fromFile("./screeps/target/scala-2.13/screeps-fastopt.js").getLines.mkString("\n")
            ))
        )
        val creds = Source.fromFile(".screeps.json").getLines().mkString("\n").parseJson.convertTo[Creds]
        println(s"uploading for branch: $githead")
        val resp = Http("https://screeps.com/api/user/code")
            .postData(upload.toJson.compactPrint)
            .auth(creds.accountAlias, creds.token)
            .header("content-type", "application/json")
            .asString
        println(s"Response Code: ${resp.code}")
    }
}
