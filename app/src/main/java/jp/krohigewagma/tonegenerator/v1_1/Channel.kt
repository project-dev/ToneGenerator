package jp.krohigewagma.tonegenerator.v1_1

internal class Channel {

    private var oscFun : Int = 0
    private var level = 0.0f
    private val idMap = HashMap<Tone, Int>()

    fun setOSCFunction(program : Int){
        oscFun = program
    }

    fun toneOn(tone : Tone, level : Float){
        this.level = level
        var id = OSCManager.toneOn(tone, this.level, oscFun)
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