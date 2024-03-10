package cs.hse.hw

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class HwApplication

fun main(args: Array<String>) {
    runApplication<HwApplication>(*args)
}
