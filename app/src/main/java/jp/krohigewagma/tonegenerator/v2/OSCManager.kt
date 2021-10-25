package jp.krohigewagma.tonegenerator.v2

import android.util.Log
import java.util.*
import kotlin.math.floor

class OSCManager {

    companion object{
        /**
         * オシレータ管理マップ
         */
        private val oscMap = HashMap<Int, OSCObject>()


        private val oscStack = Stack<OSCObject>()

        /**
         * トーンON
         * @param tone トーン
         * @param level レベル
         * @param fumc オシレータのファンクションコード
         * @return オシレータID
         */
        fun toneOn(tone : Tone, level: Int, func : Int) : Int{
            if(!ToneGeneratorConfig.isInitialize) {
                Log.w(ToneGeneratorConfig.APP_NAME, "not initialize ToneGenerator")
                return 0
            }

            var osc : OSCObject? = null
            osc = if(oscStack.size > 0){
                oscStack.pop()
            }else{
                OSCObject(tone, level, func)
            }
            osc?.toneOn(tone, level, func)
            var id = osc.hashCode()
            oscMap[id] = osc!!
            //Log.i(ToneGeneratorConfig.APP_NAME, "Tone On ID : $id")
            Log.d(ToneGeneratorConfig.APP_NAME, "Tone On ID : $id")
            return id
        }

        /**
         * トーンOFF
         * @param id toneOnで返却されたオシレータID
         */
        fun toneOff(id : Int){
            if(!ToneGeneratorConfig.isInitialize) {
                Log.w(ToneGeneratorConfig.APP_NAME, "not initialize ToneGenerator")
                return
            }

            if(!oscMap.containsKey(id)){
                return
            }
            Log.d(ToneGeneratorConfig.APP_NAME, "Tone Off ID : $id")

            var osc = oscMap[id]
            osc!!.toneOff()
            oscMap.remove(id)
            oscStack.push(osc)
        }

        /**
         * データ生成
         */
        fun generate() : Byte{
            if(!ToneGeneratorConfig.isInitialize) {
                Log.w(ToneGeneratorConfig.APP_NAME, "not initialize ToneGenerator")
                return 0
            }

            /*
            var mix = 0.0
            oscMap.forEach {
                mix += it.value!!.generate() / 32768.0f
            }

            mix *= 0.8
            if(mix > 1.0f ) mix = 1.0
            if(mix < -1.0f ) mix = -1.0
            var outData = mix * 32768.0
            Log.i(ToneGeneratorConfig.APP_NAME, "outData : $outData")
            return floor(outData).toInt().toByte()
            */
            var mix = 0
            oscMap.forEach {
                mix = mix.or(it.value!!.generate())
            }
            return mix.toByte()
        }
    }
}