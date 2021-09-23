package jp.krohigewagma.tonegenerator

import android.util.Log
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

class ToneGenerator(val tone : Tone, val level: Int, val func : Int) {
    companion object{
        /**
         * 音階基礎定義
         */
        private val toneBase = arrayListOf(
                0.0,
                130.813, //C
                138.591, // C# | Db
                146.832, // D
                155.563, // D# | Eb
                164.814, // E
                174.614, // F
                184.995, // F# | Gb
                195.996, // G
                207.652, // G# | Ab
                220.000, // A
                233.082, // A# | Bb
                246.942  // B
        )
    }

    /**
     * カウンタ
     */
    var cnt = 0

    /**
     * 音を生成
     */
    fun generate(sampleRate : Int ) : Int{
        var frequency = toneBase[this.tone.key] * this.tone.oct

        // TODO:ここの計算を理解していないのでどこかで理解する
        // https://dev.classmethod.jp/articles/andoid_sound_generator_xmas/
//        var r = this.cnt / (sampleRate / frequency) * Math.PI * 2
        var r = this.cnt / (sampleRate / frequency) * (Math.PI * 2)
        var toneData = when(func){
            0->sin(r)                             // SIN波
            1->if( sin(r) > 0.0) 1.0 else -1.0    // 矩形波
            2->if( sin(r) > 0.5) 1.0 else -1.0    // 矩形波
            else -> sin(r)
        } * this.level

        this.cnt++

        // TODO:ここの条件式おかしい気がする
        if(this.cnt >= sampleRate * frequency){
            this.cnt = 0
        }

        return floor(toneData).toInt()
    }
}