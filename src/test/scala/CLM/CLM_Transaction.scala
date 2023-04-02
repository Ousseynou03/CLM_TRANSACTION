package CLM
import com.redis.serialization.Parse

import scala.concurrent.duration._
import sys.process._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

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


  val TransacAchat = scenario("CLM TRANSACTION").exec(ObjectTransaction.scnTransactionAchat)


  setUp(
    TransacAchat.inject(rampUsers(nbVu * 2) during ( TpsMonteEnCharge  minutes) , nothingFor(  TpsPalier  minutes))
  ).protocols(httpProtocol)
    .maxDuration( DureeMax minutes)

}
