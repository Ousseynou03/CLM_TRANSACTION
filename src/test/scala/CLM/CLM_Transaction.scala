package CLM


import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._


import scala.language.postfixOps

class CLM_Transaction  extends  Simulation{

  private val host: String = System.getProperty("urlCible", "http://esbpprd1:1608")
  private val VersionAppli: String = System.getProperty("VersionApp", "Vxx.xx.xx")
  private val TpsMonteEnCharge: Int = System.getProperty("tpsMonte", "4").toInt
  private val TpsPalier: Int = System.getProperty("tpsPalier", (2 * TpsMonteEnCharge).toString).toInt
  private val TpsPause: Int = System.getProperty("tpsPause", "60").toInt
  private val DureeMax: Int = System.getProperty("dureeMax", "1").toInt + 5 * (TpsMonteEnCharge + TpsPalier)

  private val LeCoeff: Int = System.getProperty("coeff", "10").toInt
  private val  nbVu : Int =  LeCoeff * 1


  val httpProtocol =   http
    .baseUrl(host)
    .acceptHeader("application/json")
    .authorizationHeader("Basic ZWNvbWdsOng0SGclUzc=")


  before {

    println("----------------------------------------------" )
    println("host :"+ host   )
    println("VersionAppli :"+ VersionAppli   )
    println("TpsPause : " + TpsPause  )
    println("LeCoeff : " + LeCoeff  )
    println("nbVu : " + nbVu  )
    println("tpsMonte : " + TpsMonteEnCharge )
    println("----------------------------------------------" )
  }

  after  {
    println("----------------------------------------------" )
    println("--------     Rappel - Rappel - Rappel    -----" )
    println("VersionAppli :"+ VersionAppli   )
    println("host :"+ host   )
    println("TpsPause : " + TpsPause  )
    println("LeCoeff : " + LeCoeff  )
    println("nbVu : " + nbVu  )
    println("DureeMax : " + DureeMax )
    println("tpsMonte : " + TpsMonteEnCharge )
    println("--------     Rappel - Rappel - Rappel    -----" )
    println("----------------------------------------------" )
    println(" " )
  }


  val TransacAchat1 = scenario("CLM TRANSACTION 1").exec(ObjectTransaction.scnTransactionAchat1)
  val TransacAchat2 = scenario("CLM TRANSACTION 2").exec(ObjectTransaction.scnTransactionAchat2)
  val TransacAchat3 = scenario("CLM TRANSACTION 3").exec(ObjectTransaction.scnTransactionAchat3)


  setUp(
    TransacAchat1.inject(rampUsers(nbVu * 10) during ( TpsMonteEnCharge  minutes) , nothingFor(  TpsPalier  minutes)),
    TransacAchat2.inject(rampUsers(nbVu * 10) during ( TpsMonteEnCharge  minutes) , nothingFor(  TpsPalier  minutes)),
    TransacAchat3.inject(rampUsers(nbVu * 10) during ( TpsMonteEnCharge  minutes) , nothingFor(  TpsPalier  minutes))
  ).protocols(httpProtocol)
    .maxDuration( DureeMax minutes)

}
