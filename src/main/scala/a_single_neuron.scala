import chisel3._
import chisel3.util._
import fixedpoint._
import dsptools.numbers._

// 3 inputs.
// w1 w2 w3 are weights.
// rate coding factor.
// Vth is a threshold potential
class a_single_neuron(rate_coding_factor: Int, Vth: Float, w1: Float, w2: Float, w3: Float) extends Module {
  val io = IO(new Bundle {
    val pixel_1 = Input(UInt(1.W))
    val pixel_2 = Input(UInt(1.W))
    val pixel_3 = Input(UInt(1.W))
    val spike_output = Output(UInt(1.W))
  })

  // weight/rate coding factor
  val w1_input = RegInit(0.F(8.BP))
  when (io.pixel_1 === 1.U) {
    w1_input := (w1 / rate_coding_factor).F(8.BP)
  } .otherwise {
    w1_input := 0.F(8.BP)
  }
//  w1_input := Mux(io.pixel_1 === 1.U, (2/5).F(8.BP), 0.F(8.BP))

  val w2_input = RegInit(0.F(8.BP))
  when (io.pixel_2 === 1.U) {
    w2_input := (w2 / rate_coding_factor).F(8.BP)
  }.otherwise {
    w2_input := 0.F(8.BP)
  }
//  val w2_input = RegNext(Mux(io.pixel_2 === 1.U, (3/5).U, 0.U), 0.U)

  val w3_input = RegInit(0.F(8.BP))
  when (io.pixel_3 === 1.U) {
    w3_input := (w3 / rate_coding_factor).F(8.BP)
  }.otherwise {
    w3_input := 0.F(8.BP)
  }
//  val w3_input = RegNext(Mux(io.pixel_3 === 1.U, (4/5).U, 0.U), 0.U)


  // counter 0~rate coding factor
  val cnt = RegInit(0.U)
  when (cnt === rate_coding_factor - 1.U) {
    cnt := 0.U
  } .otherwise {
    cnt := cnt + 1.U
  }

  // do Σ, and if Vm>Vth, reset Vm
  val Vm = RegInit(0.F(8.BP))
  when ((cnt === 1.U) && (Vm >= Vth.F(8.BP))) {
    Vm := 0.F(8.BP)
  } .otherwise {
    Vm := Vm + w1_input + w2_input + w3_input
  }

  // if Vm>Vth, output a spike
  val io.spike_output = RegInit(0.U)
  when ((cnt === 1.U) && (Vm >= Vth.F(8.BP))) {
    io.spike_output := 1.U
  } .otherwise {
    io.spike_output := 0.U
  }

}
