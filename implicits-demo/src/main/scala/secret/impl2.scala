package secret

import math.{Calculator, ScientificCalculator}

object ?? {

  implicit def upgradeCalculator(calc: Calculator): ScientificCalculator = ScientificCalculator()

}
