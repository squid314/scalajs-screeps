package com.squid314.screeps.upload

import java.nio.file.{Files, Paths}

import scalaj.http.Http
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.io.Source
import scala.util.Using.resource


case class Creds(accountAlias: String, token: String, ptr: Boolean)

/** main class to upload the generated file */
object Main extends App {
    implicit val credsFormat: JsonFormat[Creds] = jsonFormat3(Creds)

    // hacky git branch name extraction, but whatever
    for (githead <- resource(Source.fromFile(".git/HEAD")) {_.getLines.toList} // toList required or the file closes
        .find(_.startsWith("ref: refs/heads/"))
        .map(_.substring("ref: refs/heads/".length))) {

        for (compiledjs <- List("./screeps/target/scala-2.13/screeps-opt.js",
            "./screeps/target/scala-2.13/screeps-fastopt.js")
            .find(f => Files.isRegularFile(Paths.get(f)))) {

            println(s"uploading file: $compiledjs")
            val upload = Map(
                "branch" -> Left(githead),
                "modules" -> Right(Map(
                    // TODO multiple modules?
                    "main" -> resource(Source.fromFile(compiledjs)) {_.mkString}
                ))
            )
            val creds = resource(Source.fromFile(".screeps.json")) {_.mkString}.parseJson.convertTo[Creds]
            println(s"uploading for branch: $githead")
            val resp = Http("https://screeps.com/api/user/code")
                .postData(upload.toJson.compactPrint)
                .auth(creds.accountAlias, creds.token)
                .header("content-type", "application/json")
                .asString
            println(s"Response Code: ${resp.code}")
        }
    }
}
