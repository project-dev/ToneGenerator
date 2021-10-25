package jp.krohigewagma.tonegenerator.v1_1

import android.util.Log
import jp.krohigewagma.tonegenerator.v2.Channel
import java.util.*

/**
 *
 * @param bitRate ビットレート
 * @param channel チャンネル数
 * @param sampleRate サンプリングレート
 */
class ToneGenerator() {

    companion object{
        /**
         * チャンネル
         */
        private val channels = arrayListOf<Channel>()
        private var oscMap = mutableMapOf<Int, OSCObject>()
        private var oscStack = Stack<OSCObject>()

        init{
            // これはここでいいのか・・？コルーチン内でやるべき？
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)
        }

        /**
         * 音を鳴らす意
         * @param tone 音階(周波数)
         * @param level 音量
         * @return 管理用ID
         */
        fun toneOn(tone : Tone, level : Float, func : Int) : Int{
/*
        if(trackMap.size >= 4){
            // 同時に4津の音は鳴らせない
            return -1
        }
 */
            if(oscStack.size > 0){
                var osc = oscStack.pop()
                osc.toneOn(tone, level, func)
                oscMap[osc.hashCode()] = osc
                return osc.hashCode()
            }else{
                var osc = OSCObject()
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
            ocs?.toneOff()
            if(!OSCObject.reuseAudioTrack){
                ocs?.release()
            }
        }

        fun reset(){
            try{
                oscMap.forEach { (id, osc) ->
                    oscStack.push(osc)
                    osc.toneOff()
                    osc.release()
                }
                oscMap.clear()

                while(oscStack.size > 0){
                    var osc = oscStack.pop()
                    osc.toneOff()
                    osc.release()
                }


            }catch(e : Exception){
                Log.e(ToneGeneratorConfig.APP_NAME, e.message!!)
            }
        }
    }
}