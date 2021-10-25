package jp.krohigewagma.tonegenerator.v1_1

import android.util.Log
import java.util.*

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
        fun toneOn(tone : Tone, level: Float, func : Int) : Int{
            if(!ToneGeneratorConfig.isInitialize) {
                Log.w(ToneGeneratorConfig.APP_NAME, "not initialize ToneGenerator")
                return 0
            }

            var osc : OSCObject? = null
            osc = if(oscStack.size > 0){
                oscStack.pop()
            }else{
                OSCObject()
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

    }
}