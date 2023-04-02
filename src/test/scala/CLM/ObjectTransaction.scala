package CLM


import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder.toActionBuilder
import io.gatling.jdbc.Predef._

import scala.language.postfixOps


///////////////////////////////////////////////////
///	object :  HomePage 		            ///
///////////////////////////////////////////////////
object ObjectTransaction {


  private val tpsPaceDefault: Int = System.getProperty("tpsPace", "1000").toInt
  private val tpsPacingProducts: Int = System.getProperty("tpsPaceProducts", tpsPaceDefault.toString).toInt



  private val TpsPause: Int = System.getProperty("tpsPause", "10").toInt
  private val TempoMillisecond: Int = System.getProperty("TempoMillisecond", "10").toInt


  private val NbreIterDefault: Int = System.getProperty("nbIter", "3").toInt
  private val NbreIter: Int = System.getProperty("nbIterProduct", NbreIterDefault.toString).toInt
  private val groupBy: String = System.getProperty("groupBy", "HomePage")

  //private val FichierPath: String = System.getProperty("dataDir", "./src/test/resources/data/")

  private val FichierPath: String = System.getProperty("dataDir", "data/")
  private val FichierDataDate: String = "Date.csv"
  private val FichierDataClient: String = "Liste.csv"

  val jddDataDate = csv(FichierPath + FichierDataDate).circular
  val jddDateClient = csv(FichierPath + FichierDataClient).circular



  val scnTransactionAchat = scenario("CLM TRANSACTION")


    .repeat(NbreIter) { //Répeter la transaction 3 fois
      exec(flushSessionCookies)
        .exec(flushHttpCache)
        .exec(flushCookieJar)
        .pace(tpsPacingProducts milliseconds)
        .feed(jddDataDate)
        .feed(jddDateClient)
        .exec { session => println("Date de l'achat : " + session("Dateachat").as[String])
          session
        }
      .exec { session => println("TicketId :" + session("ID_SOCLE").as[String])
        session}
        .exec(http("Transaction")
          .post("/ws/clmloyalty/tickets?channel=L&user=125353&site=6310")
          .header("Content-Type", "application/json")
          .asJson
          .body(StringBody(
            """{"date":"${Dateachat}",
               |"ticketId":"${ID_SOCLE}",
               |"value":600.0,
               |"clientId":315616892,
               |"couponsToUse":[],
               |"refund":{
               |"ticketId":"${ID_SOCLE}",
               |"couponsToRefund":[]},
               |"lines":[
               |{
               |"article":{
               |"familyCode":"832",
               |"departmentCode":"81",
               |"groupCode":"10",
               |"code":"2069764590378"
               |},
               |"quantity":-1,
               |"paidValue":600.0,
               |"taxValue":-100.0,
               |"originalValue":600.0,
               |"discounts":{}
               |}
               |],
               |"payment":{"3030":600.0}}""".stripMargin))
          .check(status.is(201))
        )
    } // Fin Répétition








}
