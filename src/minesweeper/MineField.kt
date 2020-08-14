package minesweeper

class MineField(private val numOfMines: Int, private val sizeX: Int, private val sizeY: Int) {
    val minefield = Array(sizeY) { Array(sizeY) { Cell() } }
    var excludeX = -1
    var excludeY = -1

    private fun randomMinesGenerator(): Array<Array<Int>> {
        val excludeIndex = excludeY * sizeX + excludeX
        var minesCoordinates = arrayOf<Array<Int>>()
        val rangeBasedList = ((0 until (sizeX * sizeY)).toList() - excludeIndex).shuffled()
        val arrayOfMines = rangeBasedList.take(numOfMines).toTypedArray()
        for (i in arrayOfMines) {
            val x = i % sizeX
            val y = i / sizeX
            minesCoordinates += arrayOf(x, y)
        }
        return minesCoordinates
    }

    fun placeMines() {
        for (coordinate in randomMinesGenerator()) {
            minefield[coordinate[1]][coordinate[0]].clear = false
        }
    }

    private fun neighboursCoordinates(x: Int, y: Int): Array<Array<Int>> {
        val xArguments = arrayOf(x - 1, x, x + 1).filter { i: Int -> i in 0 until sizeX }
        val yArguments = arrayOf(y - 1, y, y + 1).filter { i: Int -> i in 0 until sizeY }
        var neighboursCoordinates = arrayOf<Array<Int>>()
        for (newY in yArguments) {
            for (newX in xArguments) {
                if (newX != x || newY != y) {
                    neighboursCoordinates += arrayOf(newY, newX)
                }
            }
        }
        return neighboursCoordinates
    }

    private fun minesAround(x: Int, y: Int): Int {
        val neighbors = neighboursCoordinates(x, y)
        var nearbyMines = 0
        for (cell in neighbors) {
            if (!minefield[cell[0]][cell[1]].clear) nearbyMines++
        }
        return nearbyMines
    }

    /** Place in any empty cell a number of mines around them (if any)*/
    fun placeHints() {
        for (y in 0 until sizeY) {
            for (x in 0 until sizeX) {
                if (minefield[y][x].clear) {
                    minefield[y][x].neighbors = minesAround(x, y)
                }
            }
        }
    }

    /** Recursively expose any empty neighbouring cells*/
    fun exposeNeighbours(x: Int, y: Int) {
        for (coordinate in neighboursCoordinates(x, y)) {
            val cell = minefield[coordinate[0]][coordinate[1]]
            if (!cell.exposed && cell.clear) {
                cell.expose()
                if (!cell.hasNeighbors) exposeNeighbours(coordinate[1], coordinate[0])
            }
        }
    }

    /** Expose cell and all empty cells around*/
    fun exposeCell(x: Int, y: Int): Boolean {
        val successfullyExposed = minefield[y][x].expose()
        if (successfullyExposed && !minefield[y][x].hasNeighbors) {
            exposeNeighbours(x, y)
        }
        return successfullyExposed
    }

    fun printField() {
        val columns = (1..sizeX).toList().toTypedArray()
        val indexes = (1..sizeY).toList().toTypedArray()
        println(" |" + columns.joinToString(separator = "") + "|")
        println("-|" + "-".repeat(sizeX) + "|")
        for (y in 0 until sizeY) {
            print("${indexes[y]}|")
            for (x in 0 until sizeX) {
                print(minefield[y][x].value)
            }
            println("|")
        }
        println("-|" + "-".repeat(sizeX) + "|")
    }

    /** Returns true if the game can be continued.
     * If not provides information of the way the game ended*/
    fun gamesStatus(): Boolean {
        if (Cell.boom) {
            printField()
            println("You stepped on a mine and failed!")
            return false
        }
        if (Cell.badMarkers == 0 && Cell.goodMarkers == numOfMines) {
            println("Congratulations! You found all the mines!")
            return false
        }
        if (sizeX * sizeY - numOfMines == Cell.numOfExposed) {
            println("Congratulations! You found all the mines!")
            return false
        }
        return true
    }
}