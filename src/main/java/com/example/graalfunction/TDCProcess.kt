package com.gomezrondon.cloudruntest

class TDCProcess {

    companion object{

        var crcExchange = 0.0
        var money = 0.0
        val percentage = 100.0


        fun calculatePayment(crcExchange:String,money:String, text:String): String {
            return calculatePayment(crcExchange.toDouble() ,money.toDouble() , text )
        }

        fun calculatePayment(crcExchange:Double = 640.0,money:Double = 500.00, text:String): String {
            this.crcExchange = crcExchange
            this.money = money
            val loadData = loadData(text)
            val processData = processData(loadData)
            val outPutString = printResults(processData, crcExchange, percentage, money)

            return outPutString
        }



    fun loadData(text: String): List<Tdc> {
        val lines = text.split("\n").filter { it.isNotEmpty() }.map { it.trim() }
        val totalLines = lines.count()
//        val defaultTasa: Double = 100.0/totalLines
        val defaultTasa: Double = 100.0/totalLines

        val listOfTdc = lines.map { it.replace(",", "") }
            .map { it.replace("""[\s]+""".toRegex(), " ") }
            .map {
                var percentage = defaultTasa
                val typeTdc = it.split(" ")[0]
                if (it.split(" ").size > 2) {
                    percentage = it.split(" ")[2].toDouble()
                }

                if (it.contains("CRC")) {
                    val colones = it.split(" ")[1].toDouble()
                    var converted = colones / crcExchange
                    if (colones < 0.0) {
                        converted = 0.0
                    }

                    Tdc(typeTdc, round(converted), percentage)
                } else {
                    var dolars = it.split(" ")[1].toDouble()
                    if (dolars < 0.0) {
                        dolars = 0.0
                    }
                    Tdc(typeTdc, round(dolars), percentage)
                }
            }

        return listOfTdc
    }

    private fun processData(listOfTdc: List<Tdc>): List<Tdc> {
        val totalLines = listOfTdc.size
        val avgTasa = listOfTdc.map { it.percentage }.average()
        var defaultTasa: Double = avgTasa

        var sumaTotal = 0.0
        do {
            var sumaTemp = sumaTotal
            defaultTasa = recalDefaultTasa(listOfTdc as MutableList<Tdc>, money, defaultTasa, percentage)
            listOfTdc.filter { !it.isDone && it.percentage < defaultTasa  }.map { it.percentage = defaultTasa }
            sumaTotal=  listOfTdc.filter { it.isDone }.map { it.percentage }.sum()

        }while (sumaTemp != sumaTotal)

        val resultList = process(listOfTdc, percentage, money)

        return resultList
    }

        private fun recalDefaultTasa(
            listOfTdc: MutableList<Tdc>,
            money: Double,
            defaultTasa: Double,
            percentage: Double
        ): Double {
            var defaultTasa1 = defaultTasa
            listOfTdc.map {
                if (!it.isDone) {
                    val newTasa = canIncrease(it.percentage, it.amount, money)
                    val canIncrease = canIncrease(it.percentage + 1, it.amount, money)
                    it.calculateisDone(newTasa, canIncrease)
                    it.percentage = newTasa
                }
            }

            val tasaFija = listOfTdc.filter { it.isDone }.map { it.percentage }.sum().let { round(percentage - it) }

            if (tasaFija < 1) {
                return 0.0
            }

            val PaierList = listOfTdc.partition { !it.isDone && it.percentage > defaultTasa }
            val conPorcentListOwner = 100 - PaierList.first.map { it.percentage }.sum()
            val restantes = PaierList.second.filter { !it.isDone }.count()
            defaultTasa1 = conPorcentListOwner.div(restantes) // hay que corregir
            return defaultTasa1
        }


        private fun printResults(
            resultList: List<Tdc>,
            crcExchange: Double,
            percentage: Double,
            money: Double
        ): String {

            val toList = resultList.map { tdc ->
                """${tdc.typeTdc}|${tdc.amount}|${tdc.percentage}|${tdc.payment}|${round(tdc.payment * crcExchange)}"""
            }.toMutableList()

            toList.add(0, "Moneda|Deuda en USD|Nuevo % USD|Pago en USD|Pago en CRC")

            val stringBuild = StringBuilder("")
            val maxs = getMaxList(toList, "|")
//    println(maxs)
            val borde = 4
            val sum1 = maxs.map { it + borde }.sum()
//            println( "-".repeat(sum1))
            stringBuild.append("-".repeat(sum1)).append("\n")
            toList.mapIndexed { index2, line ->
                var pepe = " ".repeat(sum1)
                var size = 1
                pepe = insertInStr(pepe, "|", 0)
                line.split("|").mapIndexed { index, it ->
                    pepe = insertInStr(pepe, "|", size)
                    size += borde/2
                    pepe = insertInStr(pepe, it, size)
                    size += maxs[index] + borde/2
                }
                pepe = insertInStr(pepe, "|", size)
                if (index2 == 0) {
                    pepe=  pepe + "\n" + "-".repeat(sum1)
                }
                pepe
            }.forEach {
//                println(it)
                stringBuild.append(it).append("\n")
            }


            val sum = resultList.map { it.payment }.sum()

//            println( "-".repeat(sum1))
//            println("suma payment: ${round(sum)}")
            stringBuild.append("-".repeat(sum1)).append("\n")
            stringBuild.append("suma payment: ${round(sum)}").append(" CRCEx: ${crcExchange}").append("\n")


            val residue = percentage - (sum * percentage / money)
//            println("-------------------------")
//            println("% residuo: ${round(residue)}")
            stringBuild.append("-------------------------").append("\n")
            stringBuild.append("% residuo: ${round(residue)}").append("\n")
            return stringBuild.toString()
        }

        // create a method that replace the last n characters of a string with a given string
// example: "FXG0000" , "10" , 6 -> "FXG0010"
        fun insertInStr(str: String, value: String, position: Int): String {
            val posi = getPosition(position, str.length, value.length)
            val sb = StringBuilder(str)
            sb.replace(posi, posi + value.length, value)
            return sb.substring(0, str.length)
        }

        private fun getPosition(position: Int, length: Int, length1: Int): Int {
            var posi = position - 1
            val algo: Int = length - length1
            if (posi < 0) {
                posi = 0
            }
            if (posi > algo) {
                posi = algo
            }
            return posi
        }


        private fun getMaxList(toList: List<String>, delimiter: String): MutableList<Int> {

            val colNumber = toList.get(0).split(delimiter).size

            val maxs = mutableListOf<Int>()
            (0 until colNumber).forEach { maxs.add(0) }

            toList.map {
                it.split("|").mapIndexed { index, col ->
                    if (maxs[index] < col.length) {
                        maxs[index] = col.length
                    }
                }
            }
            return maxs
        }

        private fun process(
        listOfTdc: List<Tdc>,
        percentage: Double,
        money: Double
    ) = listOfTdc.map { tdc ->
        var result: Double = calPayment(tdc.percentage, tdc.amount, money)

        val d = result * percentage / money
//        println("Amount: ${tdc.amount}, new %: ${round(d)},  Payment: ${round(result)}       ${round(result * crcExchange)}")
        tdc.percentage = round(d)
        tdc.payment = round(result)

        tdc
    }

    private fun recalTasa(copy: MutableList<Double>, tasaList: MutableList<Double>, amountList: List<Double>, tasaSum: Double, amount: Double): Double {
        var suma1 = tasaSum
        copy.forEachIndexed { index, _ ->
            val newTasa = tasaList[index]
            val increase = canIncrease( newTasa + 0.1, amountList[index], amount )
            if (increase > newTasa && newTasa > 0.00) {
                val diff = increase - newTasa
                suma1 += diff
                if (suma1 < 100) {
                    tasaList[index] += diff
                }
            }

        }
        return suma1
    }

    private fun canIncrease(posiblePerc: Double, tdcAmount: Double, amount: Double): Double {
        val result: Double = calPayment(posiblePerc, tdcAmount, amount)

        val newPercentage = result * 100 / amount
        return newPercentage
    }

    private fun calPayment(posiblePerc: Double, tdcAmount: Double, amount: Double): Double {

        var result: Double = (amount * posiblePerc) / 100

        if (tdcAmount < result) {
            result = tdcAmount
        }
        return result
    }

    }// companion object


}//fin de clase

private fun round(result: Double) = "%.2f".format(result).toDouble()

data class Tdc(val typeTdc:String,
               val amount: Double,
               var percentage: Double = 0.0,
               var payment: Double = 0.0
               , var isDone:Boolean = false){

    fun calculateisDone(newTasa:Double, defaultTasa: Double) {
        val diff = defaultTasa - newTasa
        if (diff < 0.5) {
            this.isDone = true
        }
    }


}