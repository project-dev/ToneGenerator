package jp.krohigewagma.tonegenerator

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import java.io.ByteArrayOutputStream

/**
 *
 * @param bitRate ビットレート
 * @param channel チャンネル数
 * @param sampleRate サンプリングレート
 */
class ToneController(private var sampleRate : Int, private var channel : Int, private var bitRate : Int) {

    companion object{
        const val APP_NAME = "ToneGenerator"
    }

    private var trackMap = mutableMapOf<Int, OSCObject>()

    init{
        OSCObject.sampleRate = this.sampleRate
        OSCObject.bitRate = this.bitRate
        OSCObject.channel = this.channel

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)
    }

    /**
     * 音を鳴らす意
     * @param tone 音階(周波数)
     * @param level 音量
     * @return 管理用ID
     */
    fun toneOn(tone : Tone, level : Int, func : Int) : Int{
        //toneBuff.add(OSCObject(tone, level, func))
        if(trackMap.size >= 4){
            // 同時に4津の音は鳴らせない
            return -1
        }
        var osc = OSCObject(tone, level, func)
        trackMap[osc.hashCode()] = osc
        return osc.hashCode()
    }

    /**
     * 音を留める
     */
    fun toneOff(id : Int){
        if(!trackMap.containsKey(id)){
            return
        }
        var ocs = trackMap[id]
        trackMap.remove(id)
        ocs?.release()
    }

}