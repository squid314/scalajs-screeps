package com.squid314.screeps.upload

import java.nio.file.{Files, Path, Paths}
import java.util
import java.util.stream.Collectors

import scalaj.http.Http
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.io.Source
import scala.util.Using.resource
import scala.jdk.CollectionConverters._

case class Creds(accountAlias: String, token: String, ptr: Boolean)

/** main class to upload the generated file */
object Upload extends App {
    implicit val credsFormat: JsonFormat[Creds] = jsonFormat3(Creds)

    // hacky git branch name extraction, but whatever
    for (githead <- resource(Source.fromFile(".git/HEAD")) {_.getLines.toList}
        .find(_.startsWith("ref: refs/heads/"))
        .map(_.substring("ref: refs/heads/".length))) {

        for (compiledjs <-
                 List("./screeps/target/scala-2.13/screeps-opt.js",
                     "./screeps/target/scala-2.13/screeps-fastopt.js")
                     .find(f => Files.isRegularFile(Paths.get(f)))) {

            println(s"uploading file: $compiledjs")
            val otherFiles: List[(String, String)] =
                Files.list(Paths.get("./screeps/src/main/resources/")).collect(Collectors.toList[Path]).asScala.toList
                    .map(f => {
                        val fileName = f.getFileName.toString
                        fileName.substring(0, fileName.length - 3) -> resource(Source.fromFile(f.toFile)) {_.mkString}
                    })
            val upload = Map(
                "branch" -> Left(githead),
                "modules" -> Right(Map(
                    // TODO multiple modules?
                    LazyList("main" -> resource(Source.fromFile(compiledjs)) {_.mkString})
                        .appendedAll(otherFiles): _*)
                )
            )
            val creds = resource(Source.fromFile(".screeps.json")) {_.mkString}.parseJson.convertTo[Creds]
            println(s"uploading for branch: $githead")
            val resp = Http("https://screeps.com/api/user/code")
                .timeout(2500, 5000)
                .postData(upload.toJson.compactPrint)
                .auth(creds.accountAlias, creds.token)
                .header("content-type", "application/json")
                .asString
            println(s"Response Code: ${resp.code}")
        }
    }
}
