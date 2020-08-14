package minesweeper

import java.util.*

val input = Scanner(System.`in`)

enum class CellValue(val symbol: Char) {
    MARKED('*'),
    UNEXPOSED('.'),
    CLEAR('/'),
    MINE('X');
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
