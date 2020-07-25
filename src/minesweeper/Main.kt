package minesweeper

import java.util.*

val input = Scanner(System.`in`)

enum class CellValue(val symbol: Char) {
    MARKED('*'),
    UNEXPOSED('.'),
    CLEAR('/'),
    MINE('X');
}

class Cell {
    var clear = true
    var exposed = false
        set(value) {
            when (field) {
                true -> println("This cell is already exposed!")
                else -> {
                    if (marked) marked = false
                    field = value
                    if (!clear) boom = true
                    else numOfExposed++
                }
            }
        }
    private var marked = false
        set(value) {
            if (exposed) {
                println("Attempt to mark an exposed Cell !")
            } else {
                if (field) {
                    if (clear) badMarkers--
                    else goodMarkers--
                } else {
                    if (clear) badMarkers++
                    else goodMarkers++
                }
                field = value
            }
        }
    var hasNeighbors = false
    var neighbors = 0
        set(value) {
            if (value > 0) {
                this.hasNeighbors = true
                field = value
            }
        }
    val value: Char
        get() {
            return if (this.exposed) {
                if (!this.clear) CellValue.MINE.symbol
                else {
                    if (this.hasNeighbors) {
                        this.neighbors.toString().first()
                    } else CellValue.CLEAR.symbol
                }
            } else {
                if (this.marked) CellValue.MARKED.symbol
                else CellValue.UNEXPOSED.symbol
            }
        }

    fun mark(): Boolean {
        val previous = marked
        marked = !marked
        return marked != previous
    }

    fun expose(): Boolean {
        val previous = exposed
        exposed = true
        return exposed != previous && !boom
    }

    companion object Statistics {
        var goodMarkers = 0
        var badMarkers = 0
        var numOfExposed = 0
        var boom = false
    }
}

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
     * If not provies information of the way the game ended*/
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

/** Read and validate users command*/
fun nextAction(sizeX: Int, sizeY: Int): Triple<Int, Int, String> {
    var x: Int
    var y: Int
    var action: String
    do {
        print("Set/unset mines marks or claim a cell as free: > ")
        x = input.nextInt() - 1
        y = input.nextInt() - 1
        action = input.next().toLowerCase()
        if (x in 0 until sizeX &&
                y in 0 until sizeY &&
                action == "mine" ||
                action == "free") {
            break
        } else println("Invalid input")
    } while (true)
    return Triple(x, y, action)
}

fun main() {
    val sizeX = 9
    val sizeY = 9
    print("How many mines do you want on the field? > ")
    val numOfMines = input.nextInt()

    // create a new empty minefield with a given size
    val newMF = MineField(numOfMines, sizeX, sizeY)
    newMF.printField()

    // wait until the first "free" command is provided
    // first exposed cell must not contain a mine
    do {
        val (x, y, action) = nextAction(sizeX, sizeY)
        when (action) {
            "mine" -> {
                newMF.minefield[y][x].mark()
                newMF.printField()
            }
            "free" -> {
                newMF.minefield[y][x].expose()
                newMF.excludeX = x
                newMF.excludeY = y
                newMF.placeMines()
                newMF.placeHints()
                if (!newMF.minefield[y][x].hasNeighbors) newMF.exposeNeighbours(x, y)
                newMF.printField()
            }
        }
    } while (action != "free")


    // repeat until the end
    // gameStatus() provides the final prompt
    while (newMF.gamesStatus()) {
        val (x, y, action) = nextAction(sizeX, sizeY)
        when (action) {
            "mine" -> if (newMF.minefield[y][x].mark()) newMF.printField()
            "free" -> if (newMF.exposeCell(x, y)) newMF.printField()
        }
    }
}
