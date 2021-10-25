package jp.krohigewagma.tonegenerator.v1_1

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
class OSCObject {
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

        /**
         * AudioTrackを再利用するかどうかのフラグ
         */
        var reuseAudioTrack = true
            private set
    }

    /**
     * AudioTrack
     */
    private var audioTrack : AudioTrack? = null

    /**
     * カウンタ
     */
    private var cnt = 0

    private var tone = Tone.NONE

    private var level = 0.0f

    private var func = 0

    private val mutex = Mutex()

    /**
     * 初期化
     */
    init {
        initAudioTrack()
        toneOn(this.tone, this.level, this.func)
    }

    private fun initAudioTrack(){
        this.audioTrack = ToneGeneratorConfig.createAudioTrack()
    }

    /**
     * 音データを設定する
     */
    fun toneOn(tone : Tone, level: Float, func : Int){
        this.tone = tone
        this.level = level
        this.func = func
        this.cnt = 0

        if(audioTrack == null){
            initAudioTrack()
        }

        try{
            if(AudioTrack.PLAYSTATE_PLAYING != audioTrack?.playState){
                audioTrack?.play()
            }

            GlobalScope.launch(Dispatchers.Default) {
                mutex.withLock {
                    while(AudioTrack.PLAYSTATE_PLAYING == audioTrack?.playState){
                        playTone()
                    }
                    if(!reuseAudioTrack){
                        audioTrack?.release()
                        audioTrack = null
                    }
                }
            }
        }catch(e:Exception){
            Log.e(ToneGeneratorConfig.APP_NAME, e.message!!)
        }
    }

    /**
     * 停止する
     */
    fun toneOff(){
        try{
            if(audioTrack?.playState == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack?.stop()
            }
        }catch(e :Exception){
            Log.e(ToneGeneratorConfig.APP_NAME, e.message!!)
        }
    }

    /**
     * 開放
     */
    fun release(){
        //android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT)
        if (null != this.audioTrack) {
            audioTrack?.stop()
            audioTrack?.release()
        }
        audioTrack = null
    }

    /**
     * 音を生成
     */
    private fun generate() : Int{
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
        }

        this.cnt++

        // TODO:ここの条件式はこれでいいか確認できていない
        if(this.cnt >= (ToneGeneratorConfig.sampleRate / frequency)){
            this.cnt = 0
        }

        return floor(toneData).toInt()
    }


    /**
     * 再生する
     */
    private fun playTone(){
        if(tone == Tone.NONE
                || null == audioTrack
                || AudioTrack.STATE_UNINITIALIZED == audioTrack?.state
                || AudioTrack.PLAYSTATE_STOPPED == audioTrack?.playState
        ){
            return
        }

        val toneBuff = ByteArrayOutputStream()

        val len = 0
        toneBuff.use { buff ->
            try {
                if(AudioTrack.PLAYSTATE_PLAYING == audioTrack?.playState){
                    for (i in 0..len) {
                        var data = generate()
                        buff.write(data)
                    }
                    val buffer = buff.toByteArray()
                    if(buffer.isNotEmpty()){
                        audioTrack?.setVolume(level)
                        when(audioTrack?.write(buffer, 0, buffer.size)){
                            AudioTrack.ERROR_BAD_VALUE -> Log.i(ToneGeneratorConfig.APP_NAME, "ERROR_BAD_VALUE")
                            AudioTrack.ERROR_DEAD_OBJECT -> Log.i(ToneGeneratorConfig.APP_NAME, "ERROR_DEAD_OBJECT")
                            AudioTrack.ERROR_INVALID_OPERATION -> Log.i(ToneGeneratorConfig.APP_NAME, "ERROR_INVALID_OPERATION")
                        }
                    }
                }
                true
            }catch(e:Exception){
                Log.e(ToneGeneratorConfig.APP_NAME, "Error!!!", e)
            }
        }
    }
}