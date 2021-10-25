package jp.krohigewagma.tonegenerator.v2

internal class Channel {

    private var oscFun : Int = 0

    private val idMap = HashMap<Tone, Int>()

    fun setOSCFunction(program : Int){
        oscFun = program
    }

    fun toneOn(tone : Tone, level : Int){
        var id = OSCManager.toneOn(tone, level, oscFun)
        idMap[tone] = id
    }

    fun toneOff(tone : Tone){
        if(!idMap.containsKey(tone)){
            return
        }

        var id = idMap[tone]
        idMap.remove(tone)
        OSCManager.toneOff(id!!)
    }

}