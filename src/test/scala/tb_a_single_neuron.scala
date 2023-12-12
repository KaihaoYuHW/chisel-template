import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class tb_a_single_neuron extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "a_single_neuron"
  it should "output spikes" in {
    test(new a_single_neuron(5, 2, 2, 3, 4)) { c =>
      c.io.pixel_1.poke(0.U)
      c.io.pixel_2.poke(1.U)
      c.io.pixel_3.poke(1.U)

      c.clock.step()

      c.io.pixel_1.poke(0.U)
      c.io.pixel_2.poke(0.U)
      c.io.pixel_3.poke(1.U)

      c.clock.step()

      c.io.pixel_1.poke(0.U)
      c.io.pixel_2.poke(1.U)
      c.io.pixel_3.poke(1.U)

      c.clock.step()

      c.io.pixel_1.poke(0.U)
      c.io.pixel_2.poke(0.U)
      c.io.pixel_3.poke(1.U)

      c.clock.step()

      c.io.pixel_1.poke(0.U)
      c.io.pixel_2.poke(1.U)
      c.io.pixel_3.poke(1.U)

      c.clock.step(2)

      c.io.spike_output.expect(1.U)

      println("spike_output: " + c.io.spike_output.peek().litValue)
    }
  }
}
