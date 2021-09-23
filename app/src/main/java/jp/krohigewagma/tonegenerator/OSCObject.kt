package jp.krohigewagma.tonegenerator

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.math.floor
import kotlin.math.sin

/**
 * 音を作って鳴らすメインのクラス
 */
class OSCObject(private val tone : Tone, val level: Int, private val func : Int) {
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
    }

    /**
     * AudioTrack
     */
    private var audioTrack : AudioTrack? = null

    /**
     * バッファサイズ
     */
    private var buffSize = 0

    /**
     * 実行中フラグ
     */
    private var isPlay = false


    /**
     * カウンタ
     */
    private var cnt = 0



    /**
     * 初期化
     */
    init {
        this.buffSize = sampleRate * channel + bitRate
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
                .build()

        this.audioTrack?.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener{
            override fun onMarkerReached(track: AudioTrack?) {
                Log.d(ToneController.APP_NAME, "onMarkerReached")
            }

            override fun onPeriodicNotification(track: AudioTrack?) {
                if(track?.playState == AudioTrack.PLAYSTATE_STOPPED){
                    Log.d(ToneController.APP_NAME, "onPeriodicNotification End")
                }
            }
        })

        this.audioTrack?.play()
        isPlay = true
        GlobalScope.launch(Dispatchers.Default) {
           while(isPlay){
               playTone()
           }
        }
    }

    /**
     * 開放
     */
    fun release(){
        isPlay = false
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT)
        if(this.audioTrack != null){
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
        if(audioTrack?.state == AudioTrack.STATE_UNINITIALIZED){
            return
        }

        if(tone == Tone.NONE){
            return
        }

        val toneBuff = ByteArrayOutputStream()
        val len = 0
        toneBuff.use { buff ->
            try {
                for (i in 0..len) {
                    var data = generate(sampleRate)
                    buff.write(data)
                }
                if(0 < buff.size()){
                    audioTrack?.write(buff.toByteArray(), 0, buff.size())
                }
                true
            }catch(e:Exception){
                Log.e("tonegenerator", "Error!!!", e)
            }
        }
    }
}