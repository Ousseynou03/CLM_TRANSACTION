package CLM

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.language.postfixOps

object ObjectTransaction {


  private val tpsPaceDefault: Int = System.getProperty("tpsPace", "1000").toInt
  private val tpsPacingProducts: Int = System.getProperty("tpsPaceProducts", tpsPaceDefault.toString).toInt



  private val TpsPause: Int = System.getProperty("tpsPause", "10").toInt
  private val TempoMillisecond: Int = System.getProperty("TempoMillisecond", "10").toInt


  private val NbreIterTransacDefault: Int = System.getProperty("nbIter", "3").toInt
  private val NbreIter: Int = System.getProperty("nbIterTransac", NbreIterTransacDefault.toString).toInt


  private val FichierPath: String = System.getProperty("dataDir", "data/")
  private val FichierDataClient: String = "Liste.csv"
  val jddDateClient = csv(FichierPath + FichierDataClient).circular

  ///////////////////////////////////////////////////////
  //Valeur dynamique de n pour chaque post
  /////////////////////////////////////////////////////////
  val ticketIdSeq1: Iterator[String] = Iterator.from(1).map(i => s"1000000-$i")
  val ticketIdSeq2: Iterator[String] = Iterator.from(1).map(i => s"2000000-$i")
  val ticketIdSeq3: Iterator[String] = Iterator.from(1).map(i => s"3000000-$i")


//SCENARIO 1
  val scnTransactionAchat1 = scenario("CLM TRANSACTION 1")
      .exec(flushSessionCookies)
        .exec(flushHttpCache)
        .exec(flushCookieJar)
        .pace(tpsPacingProducts milliseconds)
        .feed(jddDateClient)
      .exec { session => println("ClientID :" + session("ID_SOCLE").as[String])
        session}
        .exec { session =>
          val nextTicketId = ticketIdSeq1.next()
          session.set("ticketId1", nextTicketId)
        }
        .exec { session =>
          println("TicketID :" + session("ticketId1").as[String])
          session
        }
        .exec(http("CLM_Transaction_1")
          .post("/clm-gl-rest-api/api/tickets/?user=125353&channel=L&site=3050")
          .header("Content-Type", "application/json")
          .asJson
          .body(StringBody(
            """{
               |  "date": "2023-03-15T15:00:06.000+0200",
               |  "ticketId": "${ticketId1}",
               |  "value": 1500,
               |  "clientId":"${ID_SOCLE}",
               |  "couponsToUse": [],
               |  "lines": [
               |    {
               |      "article": {
               |        "familyCode": "9403",
               |        "departmentCode": "116",
               |        "groupCode": "10",
               |        "code": "2069855987582"
               |      },
               |      "quantity": 1,
               |      "paidValue": 1500,
               |      "taxValue": 13.33333,
               |      "originalValue": 1500,
               |      "discounts": {}
               |    }
               |  ],
               |  "payment": {
               |    "3030": 80
               |  }
               |}""".stripMargin)).asJson
          .check(status.is(201)))
        .pause(TpsPause.second)





  //SCENARIO 2
  val scnTransactionAchat2 = scenario("CLM TRANSACTION 2")
      .exec(flushSessionCookies)
        .exec(flushHttpCache)
        .exec(flushCookieJar)
        .pace(tpsPacingProducts milliseconds)
        .feed(jddDateClient)
        .exec { session =>
          println("ClientID :" + session("ID_SOCLE").as[String])
          session
        }
        .exec { session =>
          val nextTicketId = ticketIdSeq2.next()
          session.set("ticketId2", nextTicketId)
        }
        .exec { session =>
          println("TicketID :" + session("ticketId2").as[String])
          session
        }
        .exec(http("CLM_Transaction_2")
          .post("/clm-gl-rest-api/api/tickets/?user=125353&channel=L&site=3047")
          .header("Content-Type", "application/json")
          .asJson
          .body(StringBody(
            """{
              |"date":"2023-03-20T15:00:06.000+0200",
              |"ticketId":"${ticketId2}",
              |"value":400,
              |"clientId":${ID_SOCLE},
              |"couponsToUse":[],
              |"lines":[
              |{
              |"article":
              |{
              |"familyCode":"9403",
              |"departmentCode":"116",
              |"groupCode":"10",
              |"code":"2069855987582"
              |},
              |"quantity":1,
              |"paidValue":400,
              |"taxValue":13.33333,
              |"originalValue":400,
              |"discounts":{}
              |}
              |],
              |"payment":{
              |"3030":80
              |}|}""".stripMargin)).asJson
          .check(status.is(201)))
        .pause(TpsPause.second)




  //SCENARIO 3
  val scnTransactionAchat3 = scenario("CLM TRANSACTION 3")
      .exec(flushSessionCookies)
        .exec(flushHttpCache)
        .exec(flushCookieJar)
        .pace(tpsPacingProducts milliseconds)
        .feed(jddDateClient)
        .exec { session =>
          println("ClientID :" + session("ID_SOCLE").as[String])
          session
        }
        .exec { session =>
          val nextTicketId = ticketIdSeq3.next()
          session.set("ticketId3", nextTicketId)
        }
        .exec { session =>
          println("TicketID :" + session("ticketId3").as[String])
          session
        }
        .exec(http("CLM_Transaction_3")
          .post("/clm-gl-rest-api/api/tickets/?user=125353&channel=L&site=6310")
          .header("Content-Type", "application/json")
          .asJson
          .body(StringBody(
            """{
              |"date":"2023-03-25T15:00:06.000+0200",
              |"ticketId":"${ticketId3}",
              |"value":600,
              |"clientId":${ID_SOCLE},
              |"couponsToUse":[],
              |"lines":[
              |{
              |"article":
              |{
              |"familyCode":"9403",
              |"departmentCode":"116",
              |"groupCode":"10",
              |"code":"2069855987582"
              |},
              |"quantity":1,
              |"paidValue":600,
              |"taxValue":13.33333,
              |"originalValue":600,
              |"discounts":{}
              |}],
              |"payment":{
              |"3030":80
              |}
              |}""".stripMargin)).asJson
          .check(status.is(201)))
        .pause(TpsPause.second)



}
