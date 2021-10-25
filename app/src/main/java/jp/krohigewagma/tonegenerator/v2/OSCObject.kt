package jp.krohigewagma.tonegenerator.v2

import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.ByteArrayOutputStream
import kotlin.math.floor
import kotlin.math.sin

/**
 * 音を作って鳴らすメインのクラス
 */
class OSCObject(private var tone : Tone, var level: Int, private var func : Int) {
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

    private var cnt = 0

    /**
     * 音データを設定する
     */
    internal fun toneOn(tone : Tone, level: Int, func : Int){
        if(!ToneGeneratorConfig.isInitialize) {
            Log.w(ToneGeneratorConfig.APP_NAME, "not initialize ToneGenerator")
            return
        }

        this.cnt = 0
        this.tone = tone
        this.level = level
        this.func = func
    }

    /**
     * 停止する
     */
    internal fun toneOff(){
        if(!ToneGeneratorConfig.isInitialize) {
            Log.w(ToneGeneratorConfig.APP_NAME, "not initialize ToneGenerator")
            return
        }

        this.cnt = 0
        this.tone = Tone.NONE
        this.level = 0
    }

    /**
     * 音を生成
     */
    fun generate() : Int{

        if(!ToneGeneratorConfig.isInitialize) {
            Log.w(ToneGeneratorConfig.APP_NAME, "not initialize ToneGenerator")
            return 0
        }

        var mix : Double = 0.0
        var frequency = toneBase[this.tone.key] * this.tone.oct

        // TODO:ここの計算を理解していないのでどこかで理解する
        // https://dev.classmethod.jp/articles/andoid_sound_generator_xmas/
        var r = this.cnt / (ToneGeneratorConfig.sampleRate / frequency) * (Math.PI * 2)
        var toneData = when(func){
            0->sin(r)                             // sin波
            1->if( sin(r) > 0.0) 1.0 else -1.0    // 矩形波
            2->if( sin(r) > 0.5) 1.0 else -1.0    // 矩形波
            3->(0..1000).random() / 10.0        // ノイズ
            else -> sin(r)
        } * level

        var retData = 0
        var mode = false
        if(mode){
            retData = floor(toneData).toInt()
        }else{
            // ここで音声の合成処理を行う
            // http://mobilengineering.blogspot.com/2012/06/audio-mix-and-record-in-android.html?m=1
            // 32768は16bitの最大値
            mix = toneData / 32768.0f

            // ここはおそらくボリューム調整
            mix *= 0.8
            if(mix > 1.0f ) mix = 1.0
            if(mix < -1.0f ) mix = -1.0
            var outData = mix * 32768.0
            retData = floor(outData).toInt()
        }

        this.cnt++
        //if(this.cnt >= ToneGeneratorConfig.sampleRate * frequency) {
        if(this.cnt >= (ToneGeneratorConfig.sampleRate / frequency) * (Math.PI * 2)) {
            this.cnt = 0
        }

        Log.i(ToneGeneratorConfig.APP_NAME, "data $retData")

        return retData
    }

}