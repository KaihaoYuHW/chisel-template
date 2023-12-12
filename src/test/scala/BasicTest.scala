import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class BasicTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Dual_AD_and_Pre_Adder"
  it should "output something right" in {
    test(new Dual_AD_and_Pre_Adder(true.B, false.B, false.B, true.B,
      true.B, true.B, false.B, false.B)) { c =>
      //
      c.io.a.poke(5.U)  //A=5
      c.io.acin.poke(3.U)
      c.io.d.poke(4.U)  //D=4
      c.io.inmode(0).poke(0.U)
      c.io.inmode(2).poke(0.U)
      c.io.inmode(3).poke(1.U)
      c.io.inmode1a.poke(0.U)

      c.clock.step(2)
      //A_MULT = A/ACIN +/- D
      c.io.a_mult.expect(9.U)

      println("acout :" + c.io.acout.peek().litValue)
      println("x_mux :" + c.io.x_mux.peek().litValue)
      println("a_mult :" + c.io.a_mult.peek().litValue)
    }
  }
}