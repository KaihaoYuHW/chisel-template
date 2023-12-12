import chisel3._
import chisel3.util._

//sel_input: choose input A or ACIN
//sel_a1: choose no delay or 1 clock delay
//sel_a2: no delay or 2 clocks delay
//sel_d: choose input D no delay or 1 clock delay
//sel_ad: no delay or dalay
//amultsel: output only A/ACIN or A/ACIN+D
class Dual_AD_and_Pre_Adder(sel_input: Bool, sel_a1: Bool, sel_a2: Bool, sel_acout: Bool,
                            sel_d: Bool, preaddinsel: Bool, sel_ad: Bool, amultsel: Bool) extends Module{
  val io = IO(new Bundle {
    val a = Input(UInt(30.W))
    val acin = Input(UInt(30.W))
    val d = Input(UInt(27.W))
    val inmode = Input(Vec(4, UInt(27.W)))
    val inmode1a = Input(UInt(30.W))
    val acout = Output(UInt(30.W))
    val x_mux = Output(UInt(30.W))
    val a_mult = Output(UInt(27.W))
  })

  val a2a1 = Wire(UInt(27.W))
  val b2b1 = 7.U(18.W)

  //register A1
  val a1In = Mux(sel_input, io.a, io.acin)
  val a1Out = RegNext(a1In)

  //register A2
  val a2Out = RegNext(Mux(sel_a1, a1In, a1Out))

  //register A2->MUX
  val a2OutMux = Mux(sel_a2, a1In, a2Out)

  //ACOUT output
  io.acout := Mux(sel_acout, a1Out, a2OutMux)

  //X MUX output
  io.x_mux := a2OutMux

  //A2A1
  a2a1 := Mux(io.inmode(0) === 1.U, a2OutMux, a1Out) & (~io.inmode1a).asUInt

  //register D
  val dOut = RegNext(io.d)

  //+- input
  val preadd_d = Mux(sel_d, io.d, dOut) & (~io.inmode(2)).asUInt
  val preadd_ab = Mux(preaddinsel, a2a1, b2b1)

  //ALU
  val preadd_out = Wire(UInt(27.W))
  preadd_out := Mux(io.inmode(3) === 1.U, preadd_ab + preadd_d, preadd_ab - preadd_d)
//  switch(io.inmode(3)){
//    is(1.U) {preadd_out := preadd_ab + preadd_d}
//    is(0.U) {preadd_out := preadd_ab - preadd_d}
//  }

  //register AD
  val adout = RegNext(preadd_out)

  //A MULT
  val ad_data = Mux(sel_ad, preadd_out, adout)
  io.a_mult := Mux(amultsel, a2a1, ad_data)
}
