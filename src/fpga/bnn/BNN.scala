package SudoKu.bnn

import Chisel._
import Array._

class BNN(num_layers: Int, layers_input: List[Int], layers_output: List[Int]) extends Module {
  val io = new Bundle {
    val input = Bool(INPUT)
    val enable = Bool(INPUT)
    val output = Bits(OUTPUT, width=10)
    val done = Bool(OUTPUT)
  }

  var layers:Array[Layer] = ofDim(num_layers)
  var enable_regs = Vec.fill(num_layers){ Reg(init=Bool(false)) }
  var counters = Vec.fill(num_layers){ Reg(init=UInt(0, width=9)) }
  for (layer <- 0 until num_layers) {
    layers(layer) = Module( new Layer(layer, layers_input(layer), layers_output(layer)) )
    if(layer == 0) {
      layers(layer).io.input := io.input
      // The code below is only needed if enable is only true for one cycle
      /*
      val images_started = Reg(init=UInt(0, width=7))
      counters(layer) := counters(layer) + UInt(1)
      when (counters(layer) >= UInt(layers_input(layer))){
          counters(layer) := UInt(0)
          images_started := images_started + UInt(1)
      }
      when (images_started === UInt(81)) {
          enable_regs(layer) = Bool(false)
      }
      */
      layers(layer).io.enable := io.enable
    } else {
      layers(layer).io.input := layers(layer-1).io.output
      when (layers(layer-1).io.layer_done) {
          enable_regs(layer) := Bool(true)
      }
      when (enable_regs(layer)) {
          counters(layer) := counters(layer) + UInt(1)
      }
      when (counters(layer) >= UInt(layers_input(layer))){
          counters(layer) := UInt(0)
          enable_regs(layer) := Bool(false)
      }
      layers(layer).io.enable := enable_regs(layer)
    }
  }
  io.output := layers.last.io.output
}

class BNNTest(bnn: BNN, num_layers: Int) extends Tester(bnn) {
  def printLayerOutput() {
    for (layer <- 0 until num_layers) {
      peek(bnn.layers(layer).io.output)
      peek(bnn.layers(layer).io.enable)
    }
  }
  poke(bnn.io.input, 1)
  poke(bnn.io.enable, true)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  poke(bnn.io.input, 0)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  poke(bnn.io.input, 0)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  poke(bnn.io.input, 0)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  poke(bnn.io.input, 1)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  poke(bnn.io.input, 0)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  poke(bnn.io.input, 1)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  poke(bnn.io.input, 1)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  poke(bnn.io.enable, false)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
  peek(bnn.io.output)
  step(1)
  printLayerOutput()
}

object bnn {
  def main(args: Array[String]): Unit = {
    val gen_args =
      Array("--backend", "c", "--genHarness", "--compile", "--test", "--targetDir", "sim_test")
      //Array("--backend", "v", "--targetDir", "verilog")
      //Array("--backend", "dot", "--targetDir", "dot")
      chiselMainTest(
        gen_args,
                       // num_layers, input_size, output_size
          () => Module(new BNN(3, List(4, 3, 3), List(3, 3, 3))))
          //() => Module(new BNN(4, List(784, 256, 256, 256), List(256, 256, 256, 10))))
            { c => new BNNTest(c, 3) }
  }
}
