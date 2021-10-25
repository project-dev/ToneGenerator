package jp.krohigewagma.tonegenerator.v1

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
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

        var sampleRate : Int = 0
        var channel : Int = 0
        var bitRate : Int = 0
        var level : Int = 100

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

    val mutex = Mutex()

    /**
     * 初期化
     */
    init {
        initAudioTrack()
        setTone(this.tone, this.level, this.func)
    }

    private fun initAudioTrack(){
        var minBuffSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_8BIT)
        this.audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                        AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                )
                .setAudioFormat(
                        AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                                .setSampleRate(sampleRate)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build()
                )
                .setTransferMode(AudioTrack.MODE_STREAM)
                //.setBufferSizeInBytes(minBuffSize * 2)
                .build()

        this.audioTrack?.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener{
            override fun onMarkerReached(track: AudioTrack?) {
                Log.d(ToneController.APP_NAME, "onMarkerReached")
            }

            override fun onPeriodicNotification(track: AudioTrack?) {
                if(AudioTrack.PLAYSTATE_STOPPED == track?.playState){
                    Log.d(ToneController.APP_NAME, "onPeriodicNotification End")
                }
            }
        })
    }

    /**
     * 音データを設定する
     */
    fun setTone(tone : Tone, level: Int, func : Int){
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
            Log.e(ToneController.APP_NAME, e.message!!)
        }
    }

    /**
     * 停止する
     */
    fun stop(){
        try{
            if(audioTrack?.playState == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack?.stop()
            }
        }catch(e :Exception){
            Log.e(ToneController.APP_NAME, e.message!!)
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
    private fun generate(sampleRate : Int ) : Int{
        var frequency = toneBase[this.tone.key] * this.tone.oct

        // TODO:ここの計算を理解していないのでどこかで理解する
        // https://dev.classmethod.jp/articles/andoid_sound_generator_xmas/
        var r = this.cnt / (sampleRate / frequency) * (Math.PI * 2)
        var toneData = when(func){
            0->sin(r)                             // sin波
            1->if( sin(r) > 0.0) 1.0 else -1.0    // 矩形波
            2->if( sin(r) > 0.5) 1.0 else -1.0    // 矩形波
            3->(0..1000).random() / 10.0        // ノイズ
            else -> sin(r)
        }

        this.cnt++

        // TODO:ここの条件式はこれでいいか確認できていない
        if(this.cnt >= sampleRate * frequency){
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
                        var data = generate(sampleRate)
                        buff.write(data)
                    }
                    val buffer = buff.toByteArray()
                    if(buffer.isNotEmpty()){
                        when(audioTrack?.write(buffer, 0, buffer.size)){
                            AudioTrack.ERROR_BAD_VALUE -> Log.i(ToneController.APP_NAME, "ERROR_BAD_VALUE")
                            AudioTrack.ERROR_DEAD_OBJECT -> Log.i(ToneController.APP_NAME, "ERROR_DEAD_OBJECT")
                            AudioTrack.ERROR_INVALID_OPERATION -> Log.i(ToneController.APP_NAME, "ERROR_INVALID_OPERATION")
                        }
                    }
                }
                true
            }catch(e:Exception){
                Log.e(ToneController.APP_NAME, "Error!!!", e)
            }
        }
    }
}