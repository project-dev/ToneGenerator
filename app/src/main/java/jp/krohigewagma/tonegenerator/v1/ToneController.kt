package jp.krohigewagma.tonegenerator.v1

import android.util.Log
import java.util.*

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

    private var oscMap = mutableMapOf<Int, OSCObject>()
    private var oscStack = Stack<OSCObject>()

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
/*
        if(trackMap.size >= 4){
            // 同時に4津の音は鳴らせない
            return -1
        }
 */
        if(oscStack.size > 0){
            var osc = oscStack.pop()
            osc.setTone(tone, level, func)
            oscMap[osc.hashCode()] = osc
            return osc.hashCode()
        }else{
            var osc = OSCObject(tone, level, func)
            oscMap[osc.hashCode()] = osc
            return osc.hashCode()
        }

    }

    /**
     * 音を留める
     */
    fun toneOff(id : Int){
        if(!oscMap.containsKey(id)){
            return
        }
        var ocs = oscMap[id]

        oscMap.remove(id)
        oscStack.push(ocs)
        ocs?.stop()
        if(!OSCObject.reuseAudioTrack){
            ocs?.release()
        }
    }

    fun reset(){
        try{
            oscMap.forEach { (id, osc) ->
                oscStack.push(osc)
                osc.stop()
                osc.release()
            }
            oscMap.clear()

            while(oscStack.size > 0){
                var osc = oscStack.pop()
                osc.stop()
                osc.release()
            }


        }catch(e : Exception){
            Log.e(ToneController.APP_NAME, e.message!!)
        }
    }
}