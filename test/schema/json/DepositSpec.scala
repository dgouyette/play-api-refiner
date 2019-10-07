package schema.json

import org.scalatestplus.play.PlaySpec
import eu.timepit.refined.auto._
import eu.timepit.refined.api._
import eu.timepit.refined.collection._
import eu.timepit.refined.numeric._
class DepositSpec extends PlaySpec {

  case class AmountDeposited(value : BigDecimal)
  def deposit(amount : BigDecimal): Either[String, AmountDeposited] = amount match {
    case value if value > 0 =>  Right(AmountDeposited(value))
    case _ => Left("invalid amount for deposit")
  }
  
  case class AmountDepositedT(value : BigDecimal Refined Positive)
  def depositT(amount : BigDecimal Refined Positive) = AmountDepositedT(amount)


  "deposit a negative amount" must {
    "return an error" in {
      deposit(-100) mustBe Symbol("left")
      deposit(100) mustBe Right(AmountDeposited(100))
    }
  }


}
