package jp.krohigewagma.tonegenerator

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import java.io.ByteArrayOutputStream
import kotlin.math.sin

/**
 *
 * @param bitRate ビットレート
 * @param channel チャンネル数
 * @param sampleRate サンプリングレート
 */
class ToneController(private var sampleRate : Int, private var channel : Int, private var bitRate : Int) : AudioTrack.OnPlaybackPositionUpdateListener, Runnable {

    companion object{
        const val APP_NAME = "ToneGenerator"
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
     * スレッド
     */
    private var thread : Thread? = null

    private var toneBuff  = mutableListOf<ToneGenerator>()

    /**
     * カウンター
     */
    private var cnt = 0

    init{
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)

//        this.buffSize = this.sampleRate * this.channel + this.bitRate
        this.buffSize = this.sampleRate * this.channel + this.bitRate

        //val minSize = AudioTrack.getMinBufferSize(sampleRate,AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_8BIT)

        //if(this.buffSize < minSize){
        //    this.buffSize = minSize
        //}

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
                Log.d(APP_NAME, "onMarkerReached")
            }

            override fun onPeriodicNotification(track: AudioTrack?) {
                if(track?.playState == AudioTrack.PLAYSTATE_STOPPED){
                    Log.d(APP_NAME, "onPeriodicNotification End")
                }
            }
        })

        this.audioTrack?.play()
        this.thread = Thread(this)
        this.thread?.start()
    }

    /**
     * ToneGeneratorを使わなくなったら呼び出す
     */
    fun release(){
        this.thread = null
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT)
        if(this.audioTrack != null){
            if(this.audioTrack?.playState == AudioTrack.PLAYSTATE_PLAYING){
                audioTrack?.stop()
            }
            audioTrack?.release()
        }
        audioTrack = null
    }

    override fun run() {
        while(thread != null){
            playTone()
        }
    }

    /**
     * 音を鳴らす意
     * @param tone 音階(周波数)
     * @param level 音量
     */
    fun toneOn(tone : Tone, level : Int, func : Int){
        toneBuff.clear()
        toneBuff.add(ToneGenerator(tone, level, func))
    }

    /**
     * 音を鳴らす
     * @param tone 音階データ。複数指定することで和音も鳴らせる
     */
    fun toneOn2(tone : List<ToneGenerator>){
        toneBuff.clear()
        toneBuff.addAll(tone)
    }

    /**
     * 音を留める
     */
    fun toneOff(){
        toneBuff.clear()
        this.cnt = 0
    }

    /**
     * 再生する
     */
    private fun playTone(){
        if(toneBuff.size == 0){
            return
        }
        val baos = ByteArrayOutputStream()

        // このサイズで遅延が決まる
        val len = 2
        for(i  in  0..len){
            var data = 0
            toneBuff.forEach{
                data = data or it.generate(sampleRate)
            }
            baos.write(data)
        }
        audioTrack?.write(baos.toByteArray(), 0, baos.size())
    }

    /**
     *
     */
    override fun onMarkerReached(track: AudioTrack?) {

    }

    /**
     *
     */
    override fun onPeriodicNotification(track: AudioTrack?) {
        //playTone()
    }

}