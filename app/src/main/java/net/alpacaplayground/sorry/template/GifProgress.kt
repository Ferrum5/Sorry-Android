package net.alpacaplayground.sorry.template

class GifProgress(var progress: Int, var total: Int){
    val isFinsished: Boolean
    get() = progress == total
}